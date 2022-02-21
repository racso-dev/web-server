package src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class ClientHandler implements Runnable {
  protected Socket clientSocket;
  private ServerConfig config;
  private String clientIp;
  private HashMap<String, String> mimeMap;

  public ClientHandler(Socket clientSocket, ServerConfig config, HashMap<String, String> mimeMap) {
    this.clientSocket = clientSocket;
    this.config = config;
    this.mimeMap = mimeMap;
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
        String path = this.config.getDocumentRoot().toString() + request.getUri();
        String extension = null;
        byte[] bytes = Files.readAllBytes(Path.of(path));

        if (path.contains(".")) {
          extension = path.substring(path.lastIndexOf(".") + 1);
        }


        response.setBody(bytes)
          .setStatusCode(Response.statusCodes.get("OK"))
          .setContentType(this.mimeMap.get(extension));
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
