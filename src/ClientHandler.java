package src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

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
    response.setStatusCode("200").setBody("<h1>HEAD REQUEST</h1>").send();
    Logger.log(request, response, config);
  }
  private void handleGetRequest(Request request, Response response) {
    response.setStatusCode("200").setBody("<h1>GET REQUEST</h1>").send();
    Logger.log(request, response, config);
  }
  private void handlePostRequest(Request request, Response response) {
    response.setStatusCode("201").setBody("<h1>POST REQUEST</h1>").send();
    Logger.log(request, response, config);
  }
  private void handlePutRequest(Request request, Response response) {
    response.setStatusCode("200").setBody("<h1>PUT REQUEST</h1>").send();
    Logger.log(request, response, config);
  }
  private void handleDeleteRequest(Request request, Response response) {
    response.setStatusCode("204").setBody("<h1>DELETE REQUEST</h1>").send();
    Logger.log(request, response, config);
  }


  private void processRequest(Request request, Response response) throws IOException{
    if (request.getUri().endsWith(".cgi")) {
      String[] command = request.getUri().split("/");
      String scriptName = command[command.length - 1];

      ProcessBuilder builder = new ProcessBuilder(config.getScriptAlias() + scriptName);
      builder.environment().put("REQUEST_METHOD", request.getMethod());
      builder.environment().put("REQUEST_URI", request.getUri());
      builder.environment().put("QUERY_STRING", request.getQueryString());
      builder.environment().put("CONTENT_TYPE", request.getContentType());
      builder.environment().put("CONTENT_LENGTH", request.getContentLength());
      builder.environment().put("REMOTE_ADDR", clientIp);

      if (request.getMethod().equals("POST")) {
        builder.environment().put("REQUEST_BODY", request.getBody());
      }
      builder.redirectErrorStream(true);
      Process process = builder.start();

      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line;
      ArrayList<String> lines = new ArrayList<String>();

      do {
        line = reader.readLine();
        lines.add(line);
      } while (!line.isEmpty());
      response.setBody(String.join("\r\n", lines)).setStatusCode("200").send();
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

  @Override
  public void run() {
    try {
      BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      BufferedWriter output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
      Request request = new Request(parseRequest(input), clientIp);
      Response response = new Response(request.getContentType(), request.getContentLength(), output);
      processRequest(request, response);
      clientSocket.close();
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

}
