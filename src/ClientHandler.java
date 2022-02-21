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

  // private void handleHeadRequest(Request request, Response response) {
  //   response.setStatusCode(Response.statusCodes.get("OK")).setBody("<h1>HEAD REQUEST</h1>").send();
  //   Logger.log(request, response, config);
  // }
  // private void handleGetRequest(Request request, Response response) {
  //   response.setStatusCode(Response.statusCodes.get("OK")).setBody("<h1>GET REQUEST</h1>").send();
  //   Logger.log(request, response, config);
  // }
  // private void handlePostRequest(Request request, Response response) {
  //   response.setStatusCode(Response.statusCodes.get("Created")).setBody("<h1>POST REQUEST</h1>").send();
  //   Logger.log(request, response, config);
  // }
  // private void handlePutRequest(Request request, Response response) {
  //   response.setStatusCode(Response.statusCodes.get("OK")).setBody("<h1>PUT REQUEST</h1>").send();
  //   Logger.log(request, response, config);
  // }
  // private void handleDeleteRequest(Request request, Response response) {
  //   response.setStatusCode(Response.statusCodes.get("No Content")).setBody("<h1>DELETE REQUEST</h1>").send();
  //   Logger.log(request, response, config);
  // }

  private void handleCgi(Request request, Response response) throws IOException {
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

  }

  private void handleFiles(Request request, Response response) {
    File file = new File(config.getDocumentRoot() + request.getUri());
      if (file.exists()) {
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
        String extension = file.getName().split("\\.")[1];
        response.setBody(fileContent).setStatusCode("200").setContentType(config.getMimeTypes().get(extension)).send();
      } else {
        response.setStatusCode("404").setBody("<h1>Not Found</h1>").send();
      }
  }

  private void processRequest(Request request, Response response) throws IOException {
    if (request.getUri().startsWith(config.getScriptAlias().route)) {
      // TODO: write cgi code
      handleCgi(request, response);
    } else {
      // Check if the file exists
      handleFiles(request, response);
    }
  }

  @Override
  public void run() {
    try {
      BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      BufferedWriter output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
      Request request = new Request(input, clientIp);
      Response response = new Response(output);

      processRequest(request, response);
      clientSocket.close();
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

}
