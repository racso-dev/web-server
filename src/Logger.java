package src;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    public static void log(Request request, Response response) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d/MMM/YYYY:H:m:s Z");
        String message = String.format("%s - - [%s] %s %s -", request.getClientIp(), dateFormat.format(new Date()), String.format("\"%s %s %s\"", request.getMethod(), request.getUri(), request.getVersion()), response.getStatusCode());
        System.out.println(message);
    }
}
