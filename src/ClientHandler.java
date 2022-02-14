package src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
  protected Socket clientSocket;

  public ClientHandler(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  private void handleHeadRequest(Request request, Response response) {
    response.setStatusCode("200");
    response.setBody("<h1>HEAD REQUEST</h1>");
    Logger.log(request, response);
    response.send();
  }
  private void handleGetRequest(Request request, Response response) {
    response.setStatusCode("200");
    response.setBody("<h1>GET REQUEST</h1>");
    Logger.log(request, response);
    response.send();
  }
  private void handlePostRequest(Request request, Response response) {
    response.setStatusCode("201");
    response.setBody("<h1>POST REQUEST</h1>");
    Logger.log(request, response);
    response.send();
  }
  private void handlePutRequest(Request request, Response response) {
    response.setStatusCode("200");
    response.setBody("<h1>PUT REQUEST</h1>");
    Logger.log(request, response);
    response.send();
  }
  private void handleDeleteRequest(Request request, Response response) {
    response.setStatusCode("204");
    response.setBody("<h1>DELETE REQUEST</h1>");
    Logger.log(request, response);
  }


  private void processRequest(Request request, Response response) throws IOException{
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
      String ip = clientSocket.getRemoteSocketAddress().toString();
      Request request = new Request(parseRequest(input), ip);
      Response response = new Response(request.getContentType(), request.getContentLength(), clientSocket.getOutputStream());
      processRequest(request, response);
      clientSocket.close();
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

}
