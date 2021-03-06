package src;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
  public static void log(Request request, Response response, ServerConfig config) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("d/MMM/YYYY:H:m:s Z");
    String bodyLength = response.getBody() == null ? "-" : String.format("%d", response.getBody().length);
    String message = String.format("%s - - [%s] %s %s %s\n", request.getClientIp(), dateFormat.format(new Date()),
        String.format("\"%s %s %s\"", request.getMethod(), request.getUri(), request.getVersion()),
        response.getStatusCode(), bodyLength);
    System.out.print(message);
    try {
      Files.write(config.getLogFile(), message.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
