import java.io.*;
import java.util.*;

public class Receiver {
    static int p = 3, q = 11, n = p * q, E = 7, privateD = 3;

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

    static String decryptMessage(List<Integer> encrypted) {
        StringBuilder sb = new StringBuilder();
        for (int val : encrypted) sb.append(codeToChar.getOrDefault(modExp(val, privateD, n), ' '));
        return sb.toString();
    }

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("signed_message.txt"));
        String[] encryptedStr = reader.readLine().trim().split(" ");
        List<Integer> encrypted = new ArrayList<>();
        for (String s : encryptedStr) encrypted.add(Integer.parseInt(s));
        int signature = Integer.parseInt(reader.readLine().trim());
        reader.close();

        String decrypted = decryptMessage(encrypted);
        int hash = hashFunction(decrypted);
        int sigHash = modExp(signature, privateD, n);

        System.out.println("Розшифроване повідомлення: " + decrypted);
        System.out.println("Хеш повідомлення: " + hash);
        System.out.println("Розшифрований підпис: " + sigHash);
        System.out.println("Підпис вірний: " + (hash == sigHash));
    }
}