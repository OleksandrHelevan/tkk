import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class Cesar {

    private static final int SHIFT = 3;

    public static void main(String[] args) {
        try {
            Path inputFile = Paths.get(Objects.requireNonNull(Cesar.class.getClassLoader().getResource("input.txt")).toURI());
            Path outputFile = Paths.get(Objects.requireNonNull(Cesar.class.getClassLoader().getResource("encrypted.txt")).toURI());

            List<String> lines = Files.readAllLines(inputFile);
            StringBuilder text = new StringBuilder();
            for (String line : lines) {
                text.append(line).append(System.lineSeparator());
            }

            String encryptedText = encrypt(text.toString(), SHIFT);
            Files.write(outputFile, encryptedText.getBytes());
            System.out.println("Текст зашифровано та записано у crypto_text.txt");

        } catch (IOException | URISyntaxException e) {
            System.err.println("❌ Помилка при читанні або записі файлу: " + e.getMessage());
        }
    }

    private static String encrypt(String text, int shift) {
        StringBuilder result = new StringBuilder();
        for (char ch : text.toCharArray()) {
            if (Character.isLetter(ch)) {
                char base = Character.isUpperCase(ch) ? 'A' : 'a';
                result.append((char) ((ch - base + shift) % 26 + base));
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }
}
