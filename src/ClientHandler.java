package src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Map;
import java.util.Optional;

public class ClientHandler implements Runnable {
  protected Socket clientSocket;
  private ServerConfig config;
  private String clientIp;

  public ClientHandler(Socket clientSocket, ServerConfig config) {
    this.clientSocket = clientSocket;
    this.config = config;
    this.clientIp = clientSocket.getRemoteSocketAddress().toString().replace("/", "").split(":")[0];
  }

  private void handleHeadRequest(Request request, Response response) throws IOException {
    String path = this.config.getDocumentRoot().toString() + request.getUri();
    String extension = null;

    if (!Files.exists(Path.of(path))) {
      response.setStatusCode("Not Found");
      return;
    }
    FileTime lastMod = Files.getLastModifiedTime(Path.of(path));
    if (request.getHeaders().containsKey("Last-Modified")) {
        if (lastMod.toString().compareTo(request.getHeaders().get("Last-Modified")) == 0) {
            response.setStatusCode("Not Modified");
            return;
        }
    }
    byte[] bytes = Files.readAllBytes(Path.of(path));

    if (path.contains("."))
      extension = path.substring(path.lastIndexOf(".") + 1);
    response.setBody(bytes)
        .setHeaders("Last-Modified", lastMod.toString())
        .setStatusCode("OK")
        .setContentType(this.config.getMimeTypes().get(extension));
  }

  private void handleGetRequest(Request request, Response response) throws IOException {
    String path = this.config.getDocumentRoot().toString() + request.getUri();
    String extension = null;

    if (!Files.exists(Path.of(path))) {
      response.setStatusCode("Not Found").setBody("<h1>404 Not Found</h1>".getBytes());
      return;
    }
    FileTime lastMod = Files.getLastModifiedTime(Path.of(path));
    if (request.getHeaders().containsKey("Last-Modified")) {
        if (lastMod.toString().compareTo(request.getHeaders().get("Last-Modified")) == 0) {
            response.setStatusCode("Not Modified");
            return;
        }
    }
    byte[] bytes = Files.readAllBytes(Path.of(path));

    if (path.contains("."))
      extension = path.substring(path.lastIndexOf(".") + 1);
    response.setBody(bytes)
        .setHeaders("Last-Modified", lastMod.toString())
        .setStatusCode("OK")
        .setContentType(this.config.getMimeTypes().get(extension));
  }
  
  private void handlePostRequest(Request request, Response response) throws IOException {
    this.handleGetRequest(request, response);  
  }
  
  private void handlePutRequest(Request request, Response response) throws IOException {
    String path = this.config.getDocumentRoot().toString() + request.getUri();

    if (request.getBody() == null || request.getBody().isEmpty()) {
      response.setStatusCode("Bad Request");
      return;
    }
    PrintWriter writer = new PrintWriter(path, "UTF-8");
    writer.write(request.getBody());
    writer.flush();
    writer.close();
    response.setStatusCode("Created");
  }

  private void handleDeleteRequest(Request request, Response response) throws IOException {
    String path = this.config.getDocumentRoot().toString() + request.getUri();

    if (!Files.exists(Path.of(path))) {
      response.setStatusCode("Not Found").setBody("<h1>404 Not Found</h1>".getBytes());
      return;
    }
    Files.delete(Path.of(path));
    response.setStatusCode("No Content");
  }

  private void handleCgi(Request request, Response response) throws IOException {
    String path = this.config.getScriptAlias().path + request.getUri().replace(config.getScriptAlias().route, "/");
    char[] output = new char[1];
    String result = "";
    int bytesRead = 0;
    ProcessBuilder builder = new ProcessBuilder(path);
    builder.redirectErrorStream(true);
    for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
      builder.environment().put("HTTP_" + entry.getKey(), entry.getValue());
    }
    builder.environment().put("QUERY_STRING", request.getQueryString());
    builder.environment().put("SERVER_PROTOCOL", request.getVersion());
    builder.environment().put("REQUEST_METHOD", request.getMethod());

    Process process = builder.start();
    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    if ((request.getMethod().equals("PUT") || request.getMethod().equals("POST")) &&
        !request.getBody().isEmpty()) {
      writer.write(request.getBody());
      writer.flush();
      writer.close();
    }
    while (true) {
      bytesRead = reader.read(output);
      if (bytesRead == -1) {
        break;
      } else {
        result += new String(output);
      }
    }
    reader.close();
    try {
      process.waitFor();
    } catch (InterruptedException ie) {
      System.err.println(ie);
    }
    int processReturnValue = process.exitValue();
    if (processReturnValue != 0) {
        response.setStatusCode("Internal Server Error")
          .setBody("<h1>Process exited with non-zero exit code</h1>".getBytes());
    } else {
        response.setBody(result.getBytes())
          .setStatusCode("OK")
          .setContentType(this.config.getMimeTypes().get("html"));
    }
  }

  private void handleFiles(Request request, Response response) throws IOException {
    switch (request.getMethod()) {
      case "GET":
        this.handleGetRequest(request, response);
        break;
      case "POST":
        this.handlePostRequest(request, response);
        break;
      case "PUT":
        this.handlePutRequest(request, response);
        break;
      case "DELETE":
        this.handleDeleteRequest(request, response);
        break;
      default:
        this.handleHeadRequest(request, response);
        break;
    }
  }

  private void handleAliases(Request request, Response response) throws IOException {
    if (request.getMethod().equals("PUT")) {
      return;
    }
    Optional<String> alias = config.getAliases().keySet().stream()
        .filter(uri -> request.getUri().startsWith(uri))
        .findFirst();
    if (alias.isPresent()) {
      request.setUri(request.getUri().replace(
          alias.get(), config.getAliases().get(alias.get()).toString() + "/")
          .replace(config.getDocumentRoot().toString(), ""));
    }

    if (Files.isDirectory(Path.of(this.config.getDocumentRoot().toString() + request.getUri()))) {
      for (String index : this.config.getDirectoryIndexes()) {
        if (Files.exists(Path.of(this.config.getDocumentRoot().toString() + request.getUri() + "/" + index))) {
          request.setUri(request.getUri() + index);
          break;
        }
      }
    } else {
      response.setStatusCode("Not Found").setBody("<h1>404 Not Found</h1>".getBytes());
    }
  }

  private void processRequest(Request request, Response response) throws IOException {
    this.handleAliases(request, response);
    if (request.getUri().startsWith(config.getScriptAlias().route))
      this.handleCgi(request, response);
    else
      this.handleFiles(request, response);
    response.send();
    Logger.log(request, response, config);
  }

  @Override
  public void run() {
    try {
      BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      BufferedWriter output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

      Request request = new Request(input, this.clientIp);
      Response response = new Response(output, clientSocket);

      processRequest(request, response);
      clientSocket.close();
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

}
