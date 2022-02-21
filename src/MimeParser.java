package src;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.stream.Stream;

public class MimeParser extends Parser {
  public MimeParser(Path path) {
    super(path);
  }

  public HashMap<String, String> parseFile() throws IOException {
    HashMap<String, String> result = new HashMap<String, String>();

    try (Stream<String> lines = Files.lines(path)) {
      lines.forEach(line -> {
        if (!line.startsWith("#")) {
          String[] parts = line.split("\\s+");
          String value = parts[0];
          for (int i = 1; i < parts.length; i++) {
            result.put(parts[i], value);
          }
        }
      });
    }
    return result;
  }

}
