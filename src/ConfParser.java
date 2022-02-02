package src;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.stream.Stream;

public class ConfParser extends Parser {
  public ConfParser(Path path) {
    super(path);
  }

  public HashMap<String, String> parseFile() throws IOException {
    HashMap<String, String> config = new HashMap<String, String>();

    return config;
  }

}
