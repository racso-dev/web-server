package src;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public abstract class Parser {
  protected Path path;

  protected Parser(Path path) {
    this.path = path;
  }

  public abstract HashMap<String, String> parseFile() throws IOException;
}
