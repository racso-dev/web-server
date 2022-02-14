package src;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Response {
    private BufferedWriter output;
    private String server;
    private String date;
    private String contentType;
    private String contentLength;
    private String body = "";
    private final String version = "1.1";
    private String statusCode;
    private HashMap<String, String> statusCodes = new HashMap<String,String>() {{
        put("200", "OK");
        put("201", "Created");
        put("204", "No Content");
        put("304", "Not Modified");
        put("400", "Bad Request");
        put("401", "Unauthorized");
        put("403", "Forbidden");
        put("404", "Not Found");
        put("500", "Internal Server Error");
    }};

    private void setContentLength() {
        this.contentLength = String.format("%d", this.body.length());
    }

    public Response(String contentType, String contentLength, BufferedWriter output) {
        this.output = output;
        this.server = "ANTOINE-RENIER";
        SimpleDateFormat dateFormat = new SimpleDateFormat("E, d MMM YYYY H:m:s z");
        this.date = dateFormat.format(new Date());
        this.contentType = contentType;
        this.contentLength = contentLength;
    }

    // Getters
    public String getServer() {
        return server;
    }

    public String getDate() {
        return date;
    }

    public String getContentType() {
        return contentType;
    }

    public String getContentLength() {
        return contentLength;
    }

    public String getBody() {
        return body;
    }

    public String getVersion() {
        return version;
    }

    public String getStatusCode() {
        return statusCode;
    }

    // Setters
    public Response setServer(String server) {
        this.server = server;
        return this;
    }

    public Response setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public Response setBody(String body) {
        this.body = body;
        this.setContentLength();
        return this;
    }

    public Response setStatusCode(String statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public void send() {
        String response = String.format("HTTP/%s %s %s\r\n", version, statusCode, statusCodes.get(statusCode));
        response += String.format("Date: %s\r\n", date);
        response += String.format("Server: %s\r\n", server);
        response += String.format("Content-Type: %s\r\n", contentType);
        if (body.length() > 0) {
            response += String.format("Content-Length: %s\r\n", contentLength);
        }
        response += "\r\n";
        response += body;
        try {
            output.write(response);
            output.newLine();
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
