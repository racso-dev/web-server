package src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

public class ClientHandler implements Runnable {
  protected Socket clientSocket;
  private ServerConfig config;
  private String clientIp;

  public ClientHandler(Socket clientSocket, ServerConfig config) {
    this.clientSocket = clientSocket;
    this.config = config;
    this.clientIp = clientSocket.getRemoteSocketAddress().toString().replace("/", "").split(":")[0];
  }

  // private void handleHeadRequest(Request request, Response response) {
  // response.setStatusCode(Response.statusCodes.get("OK")).setBody("<h1>HEAD
  // REQUEST</h1>").send();
  // Logger.log(request, response, config);
  // }
  // private void handleGetRequest(Request request, Response response) {
  // response.setStatusCode(Response.statusCodes.get("OK")).setBody("<h1>GET
  // REQUEST</h1>").send();
  // Logger.log(request, response, config);
  // }
  // private void handlePostRequest(Request request, Response response) {
  // response.setStatusCode(Response.statusCodes.get("Created")).setBody("<h1>POST
  // REQUEST</h1>").send();
  // Logger.log(request, response, config);
  // }
  // private void handlePutRequest(Request request, Response response) {
  // response.setStatusCode(Response.statusCodes.get("OK")).setBody("<h1>PUT
  // REQUEST</h1>").send();
  // Logger.log(request, response, config);
  // }
  // private void handleDeleteRequest(Request request, Response response) {
  // response.setStatusCode(Response.statusCodes.get("No
  // Content")).setBody("<h1>DELETE REQUEST</h1>").send();
  // Logger.log(request, response, config);
  // }

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
    builder.environment().put("SERVER_PROTOCOL", "HTTP/" + request.getVersion());

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
    response.setBody(result.getBytes())
        .setStatusCode(Response.statusCodes.get("OK"))
        .setContentType(this.config.getMimeTypes().get("html"));
    Logger.log(request, response, config);
    response.send();

  }

  private void handleFiles(Request request, Response response) throws IOException {
    String path = this.config.getDocumentRoot().toString() + request.getUri();
    String extension = null;

    if (!Files.exists(Path.of(path))) {
      response.setStatusCode(Response.statusCodes.get("Not Found")).setBody("<h1>404 Not Found</h1>".getBytes());
      return;
    }
    byte[] bytes = Files.readAllBytes(Path.of(path));

    if (path.contains("."))
      extension = path.substring(path.lastIndexOf(".") + 1);

    response.setBody(bytes)
        .setStatusCode(Response.statusCodes.get("OK"))
        .setContentType(this.config.getMimeTypes().get(extension));
  }

  private void handleAliases(Request request, Response response) throws IOException {
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
      response.setStatusCode(Response.statusCodes.get("Not Found")).setBody("<h1>404 Not Found</h1>".getBytes());
    }
  }

  private void processRequest(Request request, Response response) throws IOException {
    this.handleAliases(request, response);
    if (request.getUri().startsWith(config.getScriptAlias().route)) {
      this.handleCgi(request, response);
    } else {
      if (request.getMethod().equals("GET")) {
        this.handleFiles(request, response);
      }

      Logger.log(request, response, config);
      response.send();
    }
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
