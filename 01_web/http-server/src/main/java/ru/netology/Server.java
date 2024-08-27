package ru.netology;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class Server {
    private static final List<String> VALID_PATHS = List.of(
            "/index.html", "/spring.svg", "/spring.png", "/resources.html",
            "/styles.css", "/app.js", "/links.html", "/forms.html",
            "/classic.html", "/events.html", "/events.js"
    );

    private final ExecutorService executorService;
    private final Map<String, Map<String, Handler>> handlers;

    public Server() {
        this.executorService = Executors.newFixedThreadPool(64);
        this.handlers = new ConcurrentHashMap<>();
    }

    public void start(int port) {
        try (var serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен на порту: " + port);
            while (true) {
                var socket = serverSocket.accept();
                executorService.submit(() -> handleClient(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }

    public void addHandler(String method, String path, Handler handler) {
        handlers.computeIfAbsent(method, k -> new ConcurrentHashMap<>()).put(path, handler);
    }

    private void handleClient(Socket socket) {
        try (var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             var out = new BufferedOutputStream(socket.getOutputStream())) {

            String requestLine = in.readLine();
            StringBuilder headers = new StringBuilder();
            String line;

            while (!(line = in.readLine()).isEmpty()) {
                headers.append(line).append("\r\n");
            }

            String body = in.toString();
            Request request = new Request(requestLine, headers.toString(), body);

            String method = request.getMethod();
            String path = request.getPath();

            Handler handler = handlers.getOrDefault(method, Map.of()).get(path);
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
}