package src;

import java.io.BufferedWriter;
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
    private HashMap<String, String> headers = new HashMap<String, String>();
    private final String version = "1.1";
    private String statusCode;
    public static HashMap<String, String> statusCodes = new HashMap<String,String>() {{
        put("OK", "200");
        put("Created", "201");
        put("No Content", "204");
        put("Not Modified", "304");
        put("Bad Request", "400");
        put("Unauthorized", "401");
        put("Forbidden", "403");
        put("Not Found", "404");
        put("Internal Server Error", "500");
    }};

    private void setContentLength() {
        this.contentLength = String.format("%d", this.body.length());
    }

    public Response(BufferedWriter output) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("E, d MMM YYYY H:m:s z");

        this.output = output;
        this.server = "ANTOINE-RENIER";
        this.date = dateFormat.format(new Date());
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

    public HashMap<String, String> getHeaders() {
        return headers;
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

    public Response setHeaders(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public void send() {
        String response = String.format("HTTP/%s %s %s%n", version, statusCode, statusCodes.get(statusCode));

        response += String.format("Date: %s%n", date);
        response += String.format("Server: %s%n", server);
        response += String.format("Content-Type: %s%n", contentType);
        if (body.length() > 0)
            response += String.format("Content-Length: %s%n%n", contentLength);
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
