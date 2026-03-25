import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Client2 {

    private static final String HOST = "localhost";
    private static final int PORT = 5555;

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORM = "AES/CBC/PKCS5Padding";

    private static final byte[] SECRET_KEY = "MySecretKey12345".getBytes();
    private static final byte[] INIT_VECTOR = "InitVector123456".getBytes();

    public static void main(String[] args) throws Exception {

        SecretKeySpec key = new SecretKeySpec(SECRET_KEY, ALGORITHM);
        IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR);

        Socket socket = new Socket(HOST, PORT);
        System.out.println("Підключено до сервера");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        BufferedReader console = new BufferedReader(
                new InputStreamReader(System.in));

        // читання з сервера
        new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {

                    String decrypted = decrypt(line, key, iv);
                    System.out.println("[Отримано]: " + decrypted);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // відправка на сервер
        String msg;
        while ((msg = console.readLine()) != null) {

            String encrypted = encrypt(msg, key, iv);
            out.println(encrypted);
        }

        socket.close();
    }

    static String encrypt(String text, SecretKeySpec key, IvParameterSpec iv)
            throws Exception {

        Cipher cipher = Cipher.getInstance(TRANSFORM);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        byte[] enc = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(enc);
    }

    static String decrypt(String text, SecretKeySpec key, IvParameterSpec iv)
            throws Exception {

        Cipher cipher = Cipher.getInstance(TRANSFORM);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);

        byte[] dec = Base64.getDecoder().decode(text);
        return new String(cipher.doFinal(dec), StandardCharsets.UTF_8);
    }
}