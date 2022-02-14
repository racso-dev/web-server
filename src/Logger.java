package src;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    public static void log(Request request, Response response, ServerConfig config) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d/MMM/YYYY:H:m:s Z");
        String message = String.format("%s - - [%s] %s %s %d", request.getClientIp(), dateFormat.format(new Date()), String.format("\"%s %s %s\"", request.getMethod(), request.getUri(), request.getVersion()), response.getStatusCode(), response.getBody().getBytes().length);
        System.out.println(message);
        // try {
        //     Files.write(config.getLogFile(), message.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
    }
}
