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

  private void handleGetRequest(ArrayList<String> lines, OutputStream output) {
    try {
      output.write(("HTTP/1.1 200 OK\n\n<h1>salut coucou</h1>\n\r").getBytes());

    } catch (IOException e) {

    }
  }


  private void parseRoute(ArrayList<String> lines, OutputStream output) {
    String[] requestParts = lines.get(0).split("\\s+");

    if (requestParts.length == 3) {
      if (requestParts[0].equals("GET")) {
        handleGetRequest(lines, output);
      } else if (requestParts[0].equals("POST")) {

      }
    }
    // if (parts[0].equals("GET") && parts.length == 3) {
    //   // Path
    // }
  }

  private void parseRequest(BufferedReader input, OutputStream output) throws IOException {
    String line;
    ArrayList<String> lines = new ArrayList<String>();

    do {
      line = input.readLine();
      System.out.println(line);
      lines.add(line);
    // } while (!line.isEmpty());
    } while (line == null);
    parseRoute(lines, output);
  }

  @Override
  public void run() {
    try {
      BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      OutputStream output = clientSocket.getOutputStream();

      parseRequest(input, output);
      clientSocket.close();
    } catch (IOException exception) {
    }
  }

}
