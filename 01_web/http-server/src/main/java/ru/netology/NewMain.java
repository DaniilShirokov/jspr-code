package ru.netology;

public class NewMain {
    public static void main(String[] args) {
        var server = new Server(9999, 64);
        server.start();
    }
}
