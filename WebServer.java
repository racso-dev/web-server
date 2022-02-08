import src.MimeParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

final class WorkerRunnable implements Runnable {

  protected Socket clientSocket;

  public WorkerRunnable(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  @Override
  public void run() {
    try {
      // InputStream input = clientSocket.getInputStream();
      OutputStream output = clientSocket.getOutputStream();

      output.write(("HTTP/1.1 200 OK\n\n<h1>salut coucou</h1>\n\r").getBytes());
      clientSocket.close();
    } catch (IOException exception) {
    }
  }

}

class WebServer {
  public static final int DEFAULT_PORT = 8080;

  public static void main(String[] args) throws IOException {
    ExecutorService threadPool = Executors.newFixedThreadPool(10);
    ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT);

    while (true) {
      Socket clientSocket = null;
      clientSocket = serverSocket.accept();
      threadPool.execute(new WorkerRunnable(clientSocket));

      // clientSocket = serverSocket.accept();
      // BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      // PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
      // System.out.println(br.readLine());
      // out.println("END CONNECTION");
    }

    // Path path = Path.of("./conf/mim.types");
    // MimeParser mimeParser = new MimeParser(path);

    // HashMap<String, String> mimeMap = mimeParser.parseFile();

    // System.out.println(mimeMap.toString());

  }
}
