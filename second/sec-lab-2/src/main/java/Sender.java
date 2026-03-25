import java.io.*;
import java.util.*;

public class Sender {
    static int p = 3, q = 11, n = p * q, publicE = 7, D = 3;

    static Map<Character, Integer> charToCode = new HashMap<>();
    static Map<Integer, Character> codeToChar = new HashMap<>();

    static {
        String chars = "АБВГДЕЄЖЗИІЇЙКЛМНОПРСТУФХЦЧШЩЬЮЯ ";
        for (int i = 0; i < chars.length(); i++) {
            charToCode.put(chars.charAt(i), i);
            codeToChar.put(i, chars.charAt(i));
        }
    }

    static int hashFunction(String msg) {
        int sum = 0;
        for (char c : msg.toCharArray()) sum += charToCode.getOrDefault(c, 0);
        return sum % 33;
    }

    static int modExp(int base, int exp, int mod) {
        int result = 1;
        base %= mod;
        while (exp > 0) {
            if ((exp & 1) == 1) result = (result * base) % mod;
            base = (base * base) % mod;
            exp >>= 1;
        }
        return result;
    }

    static List<Integer> encryptMessage(String msg) {
        List<Integer> encrypted = new ArrayList<>();
        for (char c : msg.toCharArray())
            encrypted.add(modExp(charToCode.getOrDefault(c, 0), publicE, n));
        return encrypted;
    }

    public static void main(String[] args) throws IOException {
        InputStream is = Sender.class.getResourceAsStream("/message.txt");
        if (is == null) { System.out.println("Файл ресурсу не знайдено!"); return; }

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line.trim().toUpperCase());
        reader.close();
        String message = sb.toString();

        List<Integer> encrypted = encryptMessage(message);
        int hash = hashFunction(message);
        System.out.println("Хеш повідомлення: " + hash);
        int signature = modExp(hash, publicE, n);

        try (PrintWriter out = new PrintWriter("signed_message.txt")) {
            for (int val : encrypted) out.print(val + " ");
            out.println();
            out.println(signature);
        }

        System.out.println("Повідомлення зашифровано і підписано.");
    }
}