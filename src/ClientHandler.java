package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
  protected Socket clientSocket;

  private static final ArrayList<String> methods = new ArrayList<String>() {
    {
      add("GET");
      add("POST");
      add("PUT");
      add("DELETE");
      // add("");
    }
  };

  public ClientHandler(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  private void parseRoute(String line) {
    String[] parts = line.split("\\s+");

    if (parts[0].equals("GET") && parts.length == 3) {
      // Path
    }
  }

  private void parseRequest(BufferedReader input) throws IOException {
    String line;

    do {
      line = input.readLine();

      if (!line.isEmpty())
        parseRoute(line);
    } while (line != null);
  }

  @Override
  public void run() {
    try {
      // InputStream input = clientSocket.getInputStream();
      BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      OutputStream output = clientSocket.getOutputStream();
      boolean isRequestDone = false;


      parseRequest(input);
      // output.write(("HTTP/1.1 200 OK\n\n<h1>salut coucou</h1>\n\r").getBytes());
      clientSocket.close();
    } catch (IOException exception) {
    }
  }

}
