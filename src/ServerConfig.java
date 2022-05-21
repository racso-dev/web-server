package src;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

public class ServerConfig {
  private Path confPath;

  private Path documentRoot;
  private Path serverRoot;
  private int port = -1;
  private Path logFile;
  private ArrayList<String> directoryIndexes = new ArrayList<String>();

  private ScriptAlias scriptAlias = new ScriptAlias();
  private HashMap<String, Path> aliases = new HashMap<String, Path>();

  private HashMap<String, String> mimeTypes = new HashMap<String, String>();

  private static final ArrayList<String> doubles = new ArrayList<String>() {
    {
      add("ServerRoot");
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

  public ServerConfig(Path confPath, Path mimePath) throws IOException {
    this.confPath = confPath;
    MimeParser mimeParser = new MimeParser(mimePath);
    this.mimeTypes = mimeParser.parseFile();
    this.parseFile();
    System.out.println("\nLoaded configuration:\n" + this.stringify() + "\n");
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
    if (parts[0].equals(doubles.get(0))) { // ServerRoot
      this.serverRoot = Path.of(result);
    } else if (parts[0].equals(doubles.get(1))) { // DocumentRoot
      this.documentRoot = Path.of(result);
    } else if (parts[0].equals(doubles.get(2))) { // Listen
      this.port = Integer.parseInt(result);
    } else if (parts[0].equals(doubles.get(3))) { // LogFile
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

  private void parseDirectoryIndex(String[] parts) {
    for (int i = 1; i < parts.length; i++)
      this.directoryIndexes.add(parts[i].replace('\"', ' ').trim());
  }

  private void parseFile() throws IOException {
    try (Stream<String> lines = Files.lines(confPath)) {
      lines.forEach(line -> {
        if (!line.startsWith("#") && !line.isEmpty()) {
          String[] parts = line.split("\\s+");

          if (parts[0].equals("DirectoryIndex")) {
            this.parseDirectoryIndex(parts);
          } else if (doubles.contains(parts[0])) {
            parseDouble(parts);
          } else if (triples.contains(parts[0])) {
            parseTriple(parts);
          }
        }
      });
    }
    if (this.directoryIndexes.isEmpty()) {
      this.directoryIndexes.add("index.html");
    }
  }

  // Function that return a string with the configuration of the server
  private String stringify() {
    StringBuilder sb = new StringBuilder();

    sb.append("ServerRoot: " + this.serverRoot.toString() + "\n");
    sb.append("DocumentRoot: " + this.documentRoot.toString() + "\n");
    sb.append("Listen: " + this.port + "\n");
    sb.append("LogFile: " + this.logFile.toString() + "\n");
    sb.append("DirectoryIndex: ");
    for (String index : this.directoryIndexes)
      sb.append(index + " ");
    sb.append("\n");
    sb.append("ScriptAlias: " + this.scriptAlias.route + " " +
        this.scriptAlias.path.toString() + "\n");
    for (String key : this.aliases.keySet())
      sb.append("Alias: " + key + " " + this.aliases.get(key).toString() + "\n");
    String result = sb.toString();
    return result.substring(0, result.length() - 1);
  }

  // Getters
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

  public HashMap<String, String> getMimeTypes() {
    return this.mimeTypes;
  }

  public Path getServerRoot() {
    return this.serverRoot;
  }

  public ArrayList<String> getDirectoryIndexes() {
    return this.directoryIndexes;
  }

}
