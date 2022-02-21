import src.MimeParser;
import src.ServerConfig;
import src.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class WebServer {
  public static final int DEFAULT_PORT = 8080;
  public static final Path CONF_PATH = Path.of("./conf/httpd.conf");

  public static void main(String[] args) throws IOException {
    ServerConfig config = new ServerConfig(CONF_PATH);
    Path path = Path.of("./conf/mime.types");
    MimeParser mimeParser = new MimeParser(path);

    HashMap<String, String> mimeMap = mimeParser.parseFile();

    ExecutorService threadPool = Executors.newFixedThreadPool(10);
    ServerSocket serverSocket = new ServerSocket(config.getPort() == -1 ? DEFAULT_PORT : config.getPort());
    boolean closeServer = false;

    while (!closeServer) {
      Socket clientSocket = null;
      clientSocket = serverSocket.accept();
      threadPool.execute(new ClientHandler(clientSocket, config, mimeMap));
    }
    serverSocket.close();


    // System.out.println(mimeMap.toString());

  }
}
