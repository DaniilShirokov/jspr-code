package ru.netology;

public class NewMain {
    public static void main(String[] args) {
        final var server = new Server();

        // Добавление обработчиков
        server.addHandler("GET", "/messages", (request, responseStream) -> {
            String responseBody = "{\"messages\": [\"Hello, World!\"]}";
            responseStream.write(("HTTP/1.1 200 OK\r\n" +
                    "Content-Type: application/json\r\n" +
                    "Content-Length: " + responseBody.length() + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n").getBytes());
            responseStream.write(responseBody.getBytes());
            responseStream.flush();
        });

        server.addHandler("POST", "/messages", (request, responseStream) -> {
            // Здесь вы можете читать из request.getBody() и обрабатывать данные
            responseStream.write(("HTTP/1.1 200 OK\r\n" +
                    "Connection: close\r\n" +
                    "\r\n").getBytes());
            responseStream.flush();
        });

        server.start(9999);  // Порт 9999
    }
}
