package src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
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

  private void processRequest(Request request, Response response) throws IOException {
    if (request.getUri().startsWith(config.getScriptAlias().route)) {
      // TODO: write cgi code
    } else {
      if (request.getMethod().equals("GET")) {
        byte[] bytes = Files.readAllBytes(Path.of(this.config.getDocumentRoot().toString() + request.getUri()));

        response.setBody(String.valueOf(bytes))
          .setStatusCode(Response.statusCodes.get("OK"));
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
      Response response = new Response(output);

      processRequest(request, response);
      clientSocket.close();
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

}
