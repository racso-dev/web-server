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
  public static final Path MIME_PATH = Path.of("./conf/mime.types");

  public static void main(String[] args) throws IOException {
    ServerConfig config = new ServerConfig(CONF_PATH, MIME_PATH);

    ExecutorService threadPool = Executors.newFixedThreadPool(32);
    ServerSocket serverSocket = new ServerSocket(config.getPort() == -1 ? DEFAULT_PORT : config.getPort());
    boolean closeServer = false;

    while (!closeServer) {
      Socket clientSocket = null;
      clientSocket = serverSocket.accept();
      threadPool.execute(new ClientHandler(clientSocket, config));
    }
    serverSocket.close();
  }
}
