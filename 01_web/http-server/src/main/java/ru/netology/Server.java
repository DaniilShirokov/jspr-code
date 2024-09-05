package ru.netology;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class Server {
    private  Map<String, Map<String, Handler>> handlers = new HashMap<>();
    private  ExecutorService executorService;

    public Server() {
        this.executorService = Executors.newFixedThreadPool(64);
        this.handlers = new ConcurrentHashMap<>();
    }

    public void addHandler(String method, String path, Handler handler) {
        handlers.computeIfAbsent(method, k -> new HashMap<>()).put(path, handler);
    }



    public void start(int port) {
        try (var serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен на порту: " + port);
            while (true) {
                var socket = serverSocket.accept();
                executorService.submit(() -> handleConnection(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }

    private void handleConnection(Socket socket) {
        try (var in = new BufferedInputStream(socket.getInputStream());
             var out = new BufferedOutputStream(socket.getOutputStream())) {
             var requestLine = readLine(in);
             var parts = requestLine.split(" ");
            if (parts.length != 3) {
                socket.close();
                return;
            }

             var method = parts[0];
             var path = parts[1];
             var headers = new HashMap<String, String>();

             var newPath = path.contains("?") ? path.substring(0, path.indexOf("?")) : path;

            String headerLine;
            while (!(headerLine = readLine(in)).isEmpty()) {
                var headerParts = headerLine.split(": ", 2);
                if (headerParts.length == 2) {
                    headers.put(headerParts[0], headerParts[1]);
                }
            }

            InputStream body = null;
            int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
            if (contentLength > 0) {
                body = new ByteArrayInputStream(in.readNBytes(contentLength));
            }

            Request request = new Request(method, path, headers, body);

            Handler handler = handlers.getOrDefault(method, new HashMap<>()).get(newPath);
            if (handler != null) {
                handler.handle(request, out);
            } else {
                out.write(("HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n\r\n").getBytes());
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String readLine(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        int ch;
        while ((ch = in.read()) != -1 && ch != '\n') {
            if (ch != '\r') {
                sb.append((char) ch);
            }
        }
        return sb.toString();
    }
}