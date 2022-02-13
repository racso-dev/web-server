package src;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

class ScriptAlias {
  public String route;
  public Path path;
}

public class ServerConf {
  private Path confPath;

  private Path documentRoot;
  private int port;
  private Path logFile;

  private ScriptAlias scriptAlias;
  private HashMap<String, Path> aliases;

  private static final ArrayList<String> doubles = new ArrayList<String>() {
    {
      add("DocumentRoot");
      add("Listen");
      add("LogFile");
    }
  };

  private static final ArrayList<String> triples = new ArrayList<String>() {
    {
      add("ScriptAlias");
      add("Alias");
    }
  };


  public ServerConf(Path path) {
    this.confPath = path;
  }

  void parseDouble(String[] parts) {
    if (parts[0].equals("DocumentRoot") && parts.length == 2) {
      if (parts[1].charAt(0) == '\"' &&
      parts[1].charAt(parts[1].length() - 1) == '\"') {
        String path = parts[1].substring(1, parts[1].length() - 1);

        this.documentRoot = Path.of(path);
      } else {
        this.documentRoot = Path.of(parts[1]);
      }
    }
  }

  void parseTriple(String[] parts) {
  }

  // void checkConfiguration() {
  //   if ()
  // }

  public void parseFile() throws IOException {
    try (Stream<String> lines = Files.lines(confPath)) {
      lines.forEach(line -> {
        if (!line.startsWith("#") && !line.isEmpty()) {
          String[] parts = line.split("\\s+");

          if (doubles.contains(parts[0])) {
            parseDouble(parts);
          } else if (triples.contains(parts[0])) {
            parseTriple(parts);
          } else {
          }
        }
      });
    }
  }


  // public HashMap<String, Path> getAliases() {
  // }

  // public ScriptAlias getScriptAlias() {
  // }

  // public Path getLogFile() {
  // }

  // public int getPort() {
  // }

  public Path getDocumentRoot() {
    return this.documentRoot;
  }

}
