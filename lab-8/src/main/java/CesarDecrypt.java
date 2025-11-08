import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class CesarDecrypt {

    private static final int SHIFT = 3;

    public static void main(String[] args) {

        try {
            var resource = CesarDecrypt.class.getClassLoader().getResource("encrypted.txt");
            if (resource == null) {
                System.err.println("Файл encrypted.txt не знайдено в ресурсах!");
                return;
            }

            Path encryptedFile = Path.of(resource.toURI());

            if (!Files.exists(encryptedFile)) {
                System.err.println("Вхідний файл не існує за шляхом: " + encryptedFile);
                return;
            }

            Path outputDir = Path.of("target/output");
            Files.createDirectories(outputDir);
            Path outputFile = outputDir.resolve("decrypted.txt");

            String encryptedText = Cesar.getText("encrypted.txt");

            printFrequency(encryptedText);

            String decryptedText = decrypt(encryptedText, SHIFT);
            Files.writeString(outputFile, decryptedText);

        } catch (IOException | URISyntaxException e) {
            System.err.println("Помилка при читанні або записі файлу:");
            System.err.println(e.getMessage());
        }
    }

    private static String decrypt(String text, int shift) {
        StringBuilder result = new StringBuilder();
        for (char ch : text.toCharArray()) {
            if (Character.isLetter(ch)) {
                char base = Character.isUpperCase(ch) ? 'A' : 'a';
                int dec = ((ch - base) - shift + 26) % 26;
                result.append((char) (dec + base));
            } else result.append(ch);
        }
        return result.toString();
    }

    private static void printFrequency(String text) {
        Map<Character, Integer> countMap = new HashMap<>();
        int totalLetters = 0;

        for (char ch : text.toCharArray()) {
            if (Character.isLetter(ch)) {
                ch = Character.toLowerCase(ch);
                countMap.put(ch, countMap.getOrDefault(ch, 0) + 1);
                totalLetters++;
            }
        }

        int finalTotalLetters = totalLetters;
        countMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    double freq = (entry.getValue() * 100.0) / finalTotalLetters;
                    System.out.printf("   %c : %5.2f%% (%d)%n", entry.getKey(), freq, entry.getValue());
                });

        System.out.println("Всього літер: " + totalLetters);
    }
}
