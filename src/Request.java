package src;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

// Class that handles the client requests and stores all the metadata of the requestParts
// It is used by the server to handle the requests
// It is also used by the client to send the requests to the server
public class Request {
    private String clientIp;
    private String method;
    private String uri;
    private String version;
    private String host;
    private String userAgent;
    private String accept;
    private String acceptEncoding;
    private String acceptLanguage;
    private String acceptCharset;
    private String connection;
    private String contentType;
    private String contentLength;
    private String cookie;
    private String body;
    private ArrayList<String> requestParts;

    public Request(ArrayList<String> requestParts, String clientIp) {
        this.requestParts = requestParts;
        this.clientIp = clientIp;
        String[] requestLine = requestParts.get(0).split(" ");
        this.method = requestLine[0];
        this.uri = requestLine[1];
        this.version = requestLine[2];
        for (int i = 1; i < requestParts.size(); i++) {
            String[] header = requestParts.get(i).split(": ");
            if (header[0].equals("Host")) {
                host = header[1];
            } else if (header[0].equals("User-Agent")) {
                userAgent = header[1];
            } else if (header[0].equals("Accept")) {
                accept = header[1];
            } else if (header[0].equals("Accept-Encoding")) {
                acceptEncoding = header[1];
            } else if (header[0].equals("Accept-Language")) {
                acceptLanguage = header[1];
            } else if (header[0].equals("Accept-Charset")) {
                acceptCharset = header[1];
            } else if (header[0].equals("Connection")) {
                connection = header[1];
            } else if (header[0].equals("Content-Type")) {
                contentType = header[1];
            } else if (header[0].equals("Content-Length")) {
                contentLength = header[1];
            } else if (header[0].equals("Cookie")) {
                cookie = header[1];
            }
        }
        // if (requestParts.length > 1) {
        //     body = requestParts;
        // }
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
        return host;
    };
    public String getUserAgent() {
        return userAgent;
    };
    public String getAccept() {
        return accept;
    };
    public String getAcceptEncoding() {
        return acceptEncoding;
    };
    public String getAcceptLanguage() {
        return acceptLanguage;
    };
    public String getAcceptCharset() {
        return acceptCharset;
    };
    public String getConnection() {
        return connection;
    };
    public String getContentType() {
        return contentType;
    };
    public String getContentLength() {
        return contentLength;
    };
    public String getCookie() {
        return cookie;
    };
    public String getBody() {
        return body;
    };
    public ArrayList<String> getRequestParts() {
        return requestParts;
    };
}
