import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {

    private static final int PORT = 5555;
    private static final List<Socket> clients = new ArrayList<>();

    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Сервер запущено на порті " + PORT);

        while (true) {
            Socket socket = serverSocket.accept();
            clients.add(socket);

            System.out.println("Новий клієнт: " + socket.getInetAddress());

            new Thread(() -> handleClient(socket)).start();
        }
    }

    private static void handleClient(Socket socket) {
        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()))
        ) {
            String msg;

            while ((msg = in.readLine()) != null) {

                System.out.println("[ENCRYPTED] " + msg);

                broadcast(msg, socket);
            }

        } catch (Exception e) {
            System.out.println("Клієнт відключився");
        }
    }

    private static void broadcast(String msg, Socket sender) {
        for (Socket s : clients) {
            try {
                if (s != sender && !s.isClosed()) {
                    PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                    out.println(msg);
                }
            } catch (Exception ignored) {}
        }
    }
}