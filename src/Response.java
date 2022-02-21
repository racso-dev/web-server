package src;

import java.io.BufferedWriter;
import java.io.IOError;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Response {
  private String server;
  private String date;
  private String contentType;
  private String contentLength;
  private byte[] body;
  private Socket clientSocket;
  private HashMap<String, String> headers = new HashMap<String, String>();
  private final String version = "1.1";
  private String statusCode;
  public static HashMap<String, String> statusCodes = new HashMap<String, String>() {
    {
      put("OK", "200");
      put("Created", "201");
      put("No Content", "204");
      put("Not Modified", "304");
      put("Bad Request", "400");
      put("Unauthorized", "401");
      put("Forbidden", "403");
      put("Not Found", "404");
      put("Internal Server Error", "500");
    }
  };

  private void setContentLength() {
    this.contentLength = String.format("%d", this.body.length);
  }

  public Response(BufferedWriter output, Socket clientSocket) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("E, d MMM YYYY H:m:s z");

    this.server = "ANTOINE-RENIER";
    this.date = dateFormat.format(new Date());
    this.clientSocket = clientSocket;
  }

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

  public byte[] getBody() {
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

  public Response setServer(String server) {
    this.server = server;
    return this;
  }

  public Response setContentType(String contentType) {
    this.contentType = contentType;
    return this;
  }

  public Response setBody(byte[] body) {
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

  public void send() throws IOException {
    OutputStream out = clientSocket.getOutputStream();
    out.write(String.format("HTTP/%s %s%n", version, Response.statusCodes.get(statusCode) + " " + statusCode).getBytes());

    out.write(String.format("Date: %s%n", date).getBytes());
    out.write(String.format("Server: %s%n", server).getBytes());
    out.write(String.format("Content-Type: %s%n", contentType).getBytes());
    out.write(String.format("Connection: close%n").getBytes());
    for (String key : headers.keySet())
      out.write(String.format("%s: %s%n", key, headers.get(key)).getBytes());
    if (this.body != null) {
      out.write(String.format("Content-Length: %s%n%n", contentLength).getBytes());
      out.write(body);
    }

    try {
      out.write("\r\n".getBytes());
      out.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
