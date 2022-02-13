import src.MimeParser;
import src.ServerConf;
import src.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class WebServer {
  public static final int DEFAULT_PORT = 8080;
  public static final Path CONF_PATH = Path.of("./conf/httpd.conf");

  public static void main(String[] args) throws IOException {
    ServerConf conf = new ServerConf(CONF_PATH);
    ExecutorService threadPool = Executors.newFixedThreadPool(10);
    ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT);
    boolean closeServer = false;

    conf.parseFile();
    while (!closeServer) {
      Socket clientSocket = null;
      clientSocket = serverSocket.accept();
      threadPool.execute(new ClientHandler(clientSocket));

      // clientSocket = serverSocket.accept();
      // BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      // PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
      // System.out.println(br.readLine());
      // out.println("END CONNECTION");
    }
    serverSocket.close();

    // Path path = Path.of("./conf/mim.types");
    // MimeParser mimeParser = new MimeParser(path);

    // HashMap<String, String> mimeMap = mimeParser.parseFile();

    // System.out.println(mimeMap.toString());

  }
}
