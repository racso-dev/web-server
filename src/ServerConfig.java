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


public class ServerConfig {
  private Path confPath;

  private Path documentRoot;
  private int port = -1;
  private Path logFile;

  private ScriptAlias scriptAlias = new ScriptAlias();
  private HashMap<String, Path> aliases = new HashMap<String, Path>();

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


  public ServerConfig(Path path) throws IOException {
    this.confPath = path;
    this.parseFile();
  }

  void parseDouble(String[] parts) {
    String result = "";

    if (parts.length == 2) {
      if (parts[1].charAt(0) == '\"' &&
      parts[1].charAt(parts[1].length() - 1) == '\"') {
        String data = parts[1].substring(1, parts[1].length() - 1);

        result = data;
      } else {
        result = parts[1];
      }
    }
    if (parts[0].equals(doubles.get(0))) { // DocumentRoot
      this.documentRoot = Path.of(result);
    } else if (parts[0].equals(doubles.get(1))) { // Listen
      this.port = Integer.parseInt(result);
    } else if (parts[0].equals(doubles.get(2))) { // LogFile
      this.logFile = Path.of(result);
    }
  }

  void parseTriple(String[] parts) {
    String key = "";
    String value = "";

    if (parts.length == 3) {
      if (parts[1].charAt(0) == '\"' &&
      parts[1].charAt(parts[1].length() - 1) == '\"') {
        String data = parts[1].substring(1, parts[1].length() - 1);

        key = data;
      } else {
        key = parts[1];
      }
      if (parts[2].charAt(0) == '\"' &&
      parts[2].charAt(parts[2].length() - 1) == '\"') {
        String data = parts[2].substring(1, parts[2].length() - 1);

        value = data;
      } else {
        value = parts[1];
      }
    }
    if (parts[0].equals(triples.get(0))) { // ScriptAlias
      this.scriptAlias.route = key;
      this.scriptAlias.path = Path.of(value);
    } else if (parts[0].equals(triples.get(1))) { // Alias
      this.aliases.put(key, Path.of(value));
    }
  }

  private void parseFile() throws IOException {
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

  public Path getDocumentRoot() {
    return this.documentRoot;
  }

  public int getPort() {
    return this.port;
  }

  public Path getLogFile() {
    return this.logFile;
  }

  public ScriptAlias getScriptAlias() {
    return this.scriptAlias;
  }

  public HashMap<String, Path> getAliases() {
    return this.aliases;
  }

}
