package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

// Class that handles the client requests and stores all the metadata of the requestParts
// It is used by the server to handle the requests
// It is also used by the client to send the requests to the server
public class Request {
  private String clientIp;
  private String method;
  private String uri;
  private String version;
  private String body;
  private String queryString;
  private HashMap<String, String> headers = new HashMap<String, String>();
  private ArrayList<String> requestParts = new ArrayList<String>();

  private void parseRequestLine() {
    String[] requestLine = requestParts.get(0).split("\\s+");

    this.method = requestLine[0];
    this.uri = requestLine[1].contains("?") ? requestLine[1].split("\\?")[0] : requestLine[1];
    this.version = requestLine[2];
    this.queryString = requestLine[1].contains("?") ? requestLine[1].split("\\?")[1] : "";
  }

  private void parseHeaders() {
    if (requestParts.size() > 1) {
      for (int i = 1; i < requestParts.size(); i += 1) {
        String[] parts = requestParts.get(i).split(": ", 2);

        this.headers.put(parts[0], parts[1]);
      }
    }
  }

  private void parseBody(BufferedReader input) throws IOException {
    int contentLength = Integer.valueOf(this.headers.get("Content-Length"));
    char[] buffer = new char[contentLength];

    input.read(buffer, 0, contentLength);
    this.body = new String(buffer);
  }

  public Request(BufferedReader input, String clientIp) throws IOException {
    String line;

    this.clientIp = clientIp;
    while (true) {
      line = input.readLine();
      if (line == null || line.isEmpty()) {
        break;
      } else {
        this.requestParts.add(line);
      }
    }
    this.parseRequestLine();
    this.parseHeaders();
    if (this.method.equals("POST") || this.method.equals("PUT")) {
      this.parseBody(input);
    }
  }

  public Request setUri(String uri) {
    this.uri = uri;
    return this;

  }

  public String getClientIp() {
    return clientIp;
  }

  public String getMethod() {
    return method;
  };

  public String getUri() {
    return uri;
  };

  public String getVersion() {
    return version;
  };

  public String getBody() {
    return body;
  };

  public String getQueryString() {
    return queryString;
  };

  public HashMap<String, String> getHeaders() {
    return headers;
  };
}
