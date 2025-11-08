import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class Lab11Cipher {

    // Український алфавіт у нижньому регістрі + пробіл
    private static final String ALPHABET = "абвгдеєжзиіїйклмнопрстуфхцчшщьюя ";

    // Шляхи до файлів
    private static final Path BASE_DIR = Paths.get(System.getProperty("user.dir"));
    private static final Path INPUT_FILE =
            BASE_DIR.resolve("lab-11/src/main/resources/input.txt");
    private static final Path PACKET_FILE =
            BASE_DIR.resolve("target/output/encrypted_packet.txt");
    private static final Path DECRYPTED_FILE =
            BASE_DIR.resolve("target/output/decrypted.txt");

    public static void main(String[] args) throws IOException {
        System.out.println("Поточна директорія: " + BASE_DIR.toAbsolutePath());
        System.out.println("Оберіть режим:");
        System.out.println("1 — шифрування (з input.txt у encrypted_packet.txt)");
        System.out.println("2 — розшифрування (з encrypted_packet.txt у decrypted.txt)");
        System.out.print("Ваш вибір: ");

        Scanner sc = new Scanner(System.in);
        int mode = sc.nextInt();
        sc.nextLine();

        if (mode == 1) encryptMode(sc);
        else if (mode == 2) decryptMode();
        else System.out.println("Невірна опція. Потрібно 1 або 2.");
    }

    // ---------------------- ШИФРУВАННЯ ----------------------
    private static void encryptMode(Scanner sc) throws IOException {
        System.out.println("=== РЕЖИМ ШИФРУВАННЯ ===");

        if (!Files.exists(INPUT_FILE)) {
            throw new IOException("Файл відкритого тексту не знайдено: " + INPUT_FILE.toAbsolutePath());
        }

        String openText = Files.readString(INPUT_FILE, StandardCharsets.UTF_8);
        System.out.println("Відкритий текст (з файлу):");
        System.out.println(openText);

        // робимо все малими літерами
        openText = openText.toLowerCase(Locale.ROOT);

        System.out.print("Введіть величину зсуву (ціле число): ");
        int shift = sc.nextInt();
        sc.nextLine();

        System.out.print("Введіть напрям зсуву (l - вліво, r - вправо): ");
        String dirStr = sc.nextLine().trim().toLowerCase(Locale.ROOT);
        int direction = dirStr.startsWith("l") ? -1 : 1;

        int signedShift = shift * direction;

        // 1) Цезар
        String afterCaesar = caesarEncrypt(openText, signedShift);
        System.out.println("\nПісля шифру Цезаря:");
        System.out.println(afterCaesar);

        // 2) Проста заміна
        String substitutionTable = generateRandomSubstitution();
        System.out.println("\nТаблиця простої заміни:");
        System.out.println("оригінал : " + ALPHABET);
        System.out.println("підміна  : " + substitutionTable);

        String finalCipher = substitutionEncrypt(afterCaesar, substitutionTable);
        System.out.println("\nФінальне зашифроване повідомлення:");
        System.out.println(finalCipher);

        // 3) Запис у файл
        Files.createDirectories(PACKET_FILE.getParent());
        List<String> lines = List.of(finalCipher, String.valueOf(signedShift), substitutionTable);
        Files.write(PACKET_FILE, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        System.out.println("\nПакет записано у файл:");
        System.out.println(PACKET_FILE.toAbsolutePath());
    }

    // ---------------------- РОЗШИФРУВАННЯ ----------------------
    private static void decryptMode() throws IOException {
        System.out.println("=== РЕЖИМ РОЗШИФРУВАННЯ ===");

        if (!Files.exists(PACKET_FILE)) {
            throw new IOException("Файл пакету не знайдено: " + PACKET_FILE.toAbsolutePath());
        }

        List<String> lines = Files.readAllLines(PACKET_FILE, StandardCharsets.UTF_8);
        if (lines.size() < 3) {
            throw new IOException("Файл пакету має неправильний формат (менше 3 рядків).");
        }

        String cipherText = lines.get(0);
        int signedShift = Integer.parseInt(lines.get(1).trim());
        String substitutionTable = lines.get(2);

        System.out.println("Зчитано шифрограму:");
        System.out.println(cipherText);
        System.out.println("\nЗчитаний зсув: " + signedShift);
        System.out.println("Зчитана таблиця заміни:");
        System.out.println("оригінал : " + ALPHABET);
        System.out.println("підміна  : " + substitutionTable);

        // 1) зняття простої заміни
        String afterSubstitution = substitutionDecrypt(cipherText, substitutionTable);
        System.out.println("\nПісля зняття простої заміни:");
        System.out.println(afterSubstitution);

        // 2) зняття Цезаря
        String plainText = caesarEncrypt(afterSubstitution, -signedShift);
        System.out.println("\nРозшифрований текст:");
        System.out.println(plainText);

        Files.createDirectories(DECRYPTED_FILE.getParent());
        Files.writeString(DECRYPTED_FILE, plainText, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        System.out.println("\nРозшифрований текст записано у файл:");
        System.out.println(DECRYPTED_FILE.toAbsolutePath());
    }

    // ------------------- ЦЕЗАР -------------------
    private static String caesarEncrypt(String text, int shift) {
        int n = ALPHABET.length();
        StringBuilder sb = new StringBuilder();

        for (char ch : text.toCharArray()) {
            int idx = ALPHABET.indexOf(ch);
            if (idx == -1) {
                sb.append(ch);
            } else {
                int newIdx = Math.floorMod(idx + shift, n);
                sb.append(ALPHABET.charAt(newIdx));
            }
        }
        return sb.toString();
    }

    // ------------------- ПРОСТА ЗАМІНА -------------------
    private static String substitutionEncrypt(String text, String subTable) {
        StringBuilder sb = new StringBuilder();
        for (char ch : text.toCharArray()) {
            int idx = ALPHABET.indexOf(ch);
            if (idx == -1) sb.append(ch);
            else sb.append(subTable.charAt(idx));
        }
        return sb.toString();
    }

    private static String substitutionDecrypt(String text, String subTable) {
        StringBuilder sb = new StringBuilder();
        for (char ch : text.toCharArray()) {
            int idx = subTable.indexOf(ch);
            if (idx == -1) sb.append(ch);
            else sb.append(ALPHABET.charAt(idx));
        }
        return sb.toString();
    }

    // ------------------- ГЕНЕРАЦІЯ ЗАМІНИ -------------------
    private static String generateRandomSubstitution() {
        List<Character> chars = new ArrayList<>();
        for (char c : ALPHABET.toCharArray()) chars.add(c);
        Collections.shuffle(chars, new Random());
        StringBuilder sb = new StringBuilder();
        for (char c : chars) sb.append(c);
        return sb.toString();
    }
}
