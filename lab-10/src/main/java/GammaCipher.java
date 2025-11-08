import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class GammaCipher {

    // === Таблиця простих замін (табл. 1) ===
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz .,!?";

    // === Сеансовий ключ ===
    private static final int K1 = 5;
    private static final int K2 = 9;
    private static final int K3 = 13;

    // === Шляхи до файлів ===
    private static final Path BASE_DIR = Paths.get(System.getProperty("user.dir"));
    private static final Path INPUT_FILE = BASE_DIR.resolve("lab-10/src/main/resources/text.txt");
    private static final Path ENCRYPTED_FILE = BASE_DIR.resolve("target/output/lab-10-decoded.txt");
    private static final Path DECRYPTED_FILE = BASE_DIR.resolve("target/output/lab-10-decrypted.txt");

    public static void main(String[] args) throws IOException {
        System.out.println("Поточна директорія: " + BASE_DIR.toAbsolutePath());
        System.out.println("Оберіть режим:");
        System.out.println("1 — Шифрування (з text.txt у decoded.txt)");
        System.out.println("2 — Розшифрування (з decoded.txt у decrypted.txt)");
        System.out.print("Ваш вибір: ");

        Scanner scanner = new Scanner(System.in);
        int mode = scanner.nextInt();

        if (mode == 1) {
            encryptMode();
        } else if (mode == 2) {
            decryptMode();
        } else {
            System.out.println("Невірна опція. Оберіть 1 або 2.");
        }
    }

    // ---------------------- ШИФРУВАННЯ ----------------------
    private static void encryptMode() throws IOException {
        System.out.println("=== РЕЖИМ ШИФРУВАННЯ ===");
        if (!Files.exists(INPUT_FILE)) {
            throw new IOException("Файл не знайдено: " + INPUT_FILE.toAbsolutePath());
        }

        String openText = Files.readString(INPUT_FILE);
        System.out.println("Відкритий текст:");
        System.out.println(openText);

        String cipherText = encrypt(openText, K1, K2, K3);
        Files.createDirectories(ENCRYPTED_FILE.getParent());
        Files.writeString(ENCRYPTED_FILE, cipherText);

        System.out.println("\nЗашифрований текст записано у файл:");
        System.out.println(ENCRYPTED_FILE.toAbsolutePath());
    }

    // ---------------------- РОЗШИФРУВАННЯ ----------------------
    private static void decryptMode() throws IOException {
        System.out.println("=== РЕЖИМ РОЗШИФРУВАННЯ ===");
        if (!Files.exists(ENCRYPTED_FILE)) {
            throw new IOException("Шифрограма не знайдена: " + ENCRYPTED_FILE.toAbsolutePath());
        }

        String cipherText = Files.readString(ENCRYPTED_FILE);
        System.out.println("Зчитано шифрограму:");
        System.out.println(cipherText);

        String plainText = decrypt(cipherText, K1, K2, K3);
        Files.createDirectories(DECRYPTED_FILE.getParent());
        Files.writeString(DECRYPTED_FILE, plainText);

        System.out.println("\nРозшифрований текст записано у файл:");
        System.out.println(DECRYPTED_FILE.toAbsolutePath());
        System.out.println("\nВідновлений текст:");
        System.out.println(plainText);
    }

    // ------------------- Формула (1): Шифрування -------------------
    private static String encrypt(String openText, int k1, int k2, int k3) {
        int n = ALPHABET.length();
        int[] gamma = generateGamma(openText.length(), k1, k2, k3, n);

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < openText.length(); i++) {
            char ch = openText.charAt(i);
            int index = ALPHABET.indexOf(ch);
            if (index == -1) {
                result.append(ch);
            } else {
                result.append(ALPHABET.charAt((index + gamma[i]) % n));
            }
        }
        return result.toString();
    }

    // ------------------- Формула (3): Розшифрування -------------------
    private static String decrypt(String cipherText, int k1, int k2, int k3) {
        int n = ALPHABET.length();
        int[] gamma = generateGamma(cipherText.length(), k1, k2, k3, n);

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < cipherText.length(); i++) {
            char ch = cipherText.charAt(i);
            int index = ALPHABET.indexOf(ch);
            if (index == -1) {
                result.append(ch);
            } else {
                result.append(ALPHABET.charAt((index - gamma[i] + n) % n));
            }
        }
        return result.toString();
    }

    // ------------------- Формула (2): Генерація гамми -------------------
    private static int[] generateGamma(int length, int k1, int k2, int k3, int modulus) {
        int[] gamma = new int[length];
        if (length <= 0) return gamma;
        gamma[0] = Math.floorMod(k1, modulus);
        if (length == 1) return gamma;
        gamma[1] = Math.floorMod(k2, modulus);
        if (length == 2) return gamma;
        gamma[2] = Math.floorMod(k3, modulus);
        for (int i = 3; i < length; i++) {
            gamma[i] = (gamma[i - 1] + gamma[i - 2] + gamma[i - 3]) % modulus;
        }
        return gamma;
    }
}
