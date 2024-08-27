package ru.netology;

import java.io.InputStream;
import java.util.Map;

public class Request {
    private final String method;
    private final String path;
    private final String headers;
    private final String body;

    public Request(String requestLine, String headers, String body) {
        String[] parts = requestLine.split(" ");
        this.method = parts[0];
        this.path = parts[1];
        this.headers = headers;
        this.body = body;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
