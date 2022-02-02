import src.MimeParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.HashMap;

class WebServer {
  public static final int DEFAULT_PORT = 8080;

  public static void main(String[] args) throws IOException {
    Path path = Path.of("./conf/mim.types");
    MimeParser mimeParser = new MimeParser(path);

    HashMap<String, String> mimeMap = mimeParser.parseFile();

    System.out.println(mimeMap.toString());
  }
}
