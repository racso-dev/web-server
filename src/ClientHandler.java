package src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

public class ClientHandler implements Runnable {
  protected Socket clientSocket;
  private ServerConfig config;
  private String clientIp;

  public ClientHandler(Socket clientSocket, ServerConfig config) {
    this.clientSocket = clientSocket;
    this.config = config;
    this.clientIp = clientSocket.getRemoteSocketAddress().toString().replace("/", "").split(":")[0];
  }

  private void handleHeadRequest(Request request, Response response) {
    response.setStatusCode(Response.statusCodes.get("OK")).setBody("<h1>HEAD REQUEST</h1>").send();
    Logger.log(request, response, config);
  }
  private void handleGetRequest(Request request, Response response) {
    response.setStatusCode(Response.statusCodes.get("OK")).setBody("<h1>GET REQUEST</h1>").send();
    Logger.log(request, response, config);
  }
  private void handlePostRequest(Request request, Response response) {
    response.setStatusCode(Response.statusCodes.get("Created")).setBody("<h1>POST REQUEST</h1>").send();
    Logger.log(request, response, config);
  }
  private void handlePutRequest(Request request, Response response) {
    response.setStatusCode(Response.statusCodes.get("OK")).setBody("<h1>PUT REQUEST</h1>").send();
    Logger.log(request, response, config);
  }
  private void handleDeleteRequest(Request request, Response response) {
    response.setStatusCode(Response.statusCodes.get("No Content")).setBody("<h1>DELETE REQUEST</h1>").send();
    Logger.log(request, response, config);
  }


  private void processRequest(Request request, Response response) throws IOException{
    if (request.getUri().startsWith(config.getScriptAlias().route)) {
      String[] command = request.getUri().split("/");
      String scriptPath = config.getScriptAlias().path + "/" + command[command.length - 1];
      ProcessBuilder builder = new ProcessBuilder(scriptPath);
      Map<String, String> env = builder.environment();
      for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
        env.put("HTTP_" + entry.getKey(), entry.getValue());
      }
      env.put("QUERY_STRING", request.getQueryString());
      env.put("SERVER_PROTOCOL", "HTTP/" + request.getVersion());
      if (request.getMethod().equals("POST") || request.getMethod().equals("PUT")) {
        builder.redirectInput(ProcessBuilder.Redirect.PIPE);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(builder.start().getOutputStream()));
        writer.write(request.getBody());
        writer.flush();
        writer.close();
      }
      builder.redirectErrorStream(true);
      Process process = builder.start();

      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line;
      ArrayList<String> lines = new ArrayList<String>();

      while ((line = reader.readLine()) != null)
        lines.add(line);
      response.setBody(String.join("\r\n", lines)).setStatusCode("200").send();
    } else {
      // Check if the file exists
      File file = new File(config.getDocumentRoot() + request.getUri());
      if (file.exists()) {
        // Set the response headers
        // Read file content and store it in a string
        String fileContent = "";
        try {
          BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
          String line;
          while ((line = reader.readLine()) != null) {
            fileContent += line + "\r\n";
          }
          reader.close();
        } catch (IOException e) {
          e.printStackTrace();
          response.setStatusCode("500").setBody("<h1>Internal Server Error</h1>").send();
          return;
        }
        // Set the response body
        response.setBody(fileContent).setStatusCode("200").setContentType("text/html").send();
      } else {
        response.setStatusCode("404").setBody("<h1>Not Found</h1>").send();
      }
    }
    switch (request.getMethod()) {    
      case "GET":
        handleGetRequest(request, response);
        break;
    
      case "POST":
        handlePostRequest(request, response);
        break;
    
      case "PUT":
        handlePutRequest(request, response);
        break;
    
      case "DELETE":
        handleDeleteRequest(request, response);
        break;
    
      default:
        handleHeadRequest(request, response);
        break;
    }
  }

  private ArrayList<String> parseRequest(BufferedReader input) throws IOException {
    String line;
    ArrayList<String> lines = new ArrayList<String>();

    do {
      line = input.readLine();
      lines.add(line);
    } while (!line.isEmpty());
    return lines;
  }

  private String parseBody(BufferedReader input, int contentLength) throws IOException {
    char[] buffer = new char[Integer.valueOf(contentLength)];
    input.read(buffer, 0, contentLength);
    String body  = new String(buffer, 0, contentLength);
    return body;
  }

  @Override
  public void run() {
    try {
      BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      BufferedWriter output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
      Request request = new Request(parseRequest(input), clientIp);
      if (request.getContentLength() != null)
        request.setBody(parseBody(input, Integer.valueOf(request.getContentLength())));
      Response response = new Response(request.getContentType(), request.getContentLength(), output);
      processRequest(request, response);
      clientSocket.close();
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

}
