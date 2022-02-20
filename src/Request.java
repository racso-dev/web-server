package src;

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
    private HashMap<String, String> headers;
    private ArrayList<String> requestParts;

    public Request(ArrayList<String> requestParts, String clientIp) {
        // bodyLines.forEach(entry -> {
        //     System.out.println("BODY LINE =" + entry);
        // });
        this.requestParts = requestParts;
        this.clientIp = clientIp;
        String[] requestLine = requestParts.get(0).split(" ");
        this.method = requestLine[0];
        this.uri = requestLine[1];
        this.version = requestLine[2];
        this.queryString = this.uri.contains("?") ? this.uri.split("?")[1] : "";
        // Set headers
        this.headers = new HashMap<String, String>();
        for (int i = 1; i < requestParts.size() - 1; i++) {
            String[] parts = requestParts.get(i).split(": ");
            this.headers.put(parts[0], parts[1]);
        }
        this.logHeaders();
    }

    public void logHeaders() {
        System.out.println("HEADERS:");
        this.headers.entrySet().forEach(entry -> {
            System.out.println(entry.getKey() + " " + entry.getValue());
        });
    }
    // Getters
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
    public String getHost() {
        return this.headers.get("Host");
    };
    public String getUserAgent() {
        return this.headers.get("User-Agent");
    };
    public String getAccept() {
        return this.headers.get("Accept");
    };
    public String getAcceptEncoding() {
        return this.headers.get("Accept-Encoding");
    };
    public String getAcceptLanguage() {
        return this.headers.get("Accept-Language");
    };
    public String getAcceptCharset() {
        return this.headers.get("Accept-Charset");
    };
    public String getConnection() {
        return this.headers.get("Connection");
    };
    public String getContentType() {
        return this.headers.get("Content-Type");
    };
    public String getContentLength() {
        return this.headers.get("Content-Length");
    };
    public String getCookie() {
        return this.headers.get("Cookie");
    };
    public String getBody() {
        return body;
    };
    public ArrayList<String> getRequestParts() {
        return requestParts;
    };
    public String getQueryString() {
        return queryString;
    };

    public HashMap<String, String> getHeaders() {
        return headers;
    };

    public Request setBody(String body) {
        this.body = body;
        return this;
    };
}
