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
            Path outputFile = Paths.get(Objects.requireNonNull(Cesar.class.getClassLoader().getResource("encrypted.txt")).toURI());

            String encryptedText = encrypt(getText("input.txt"));
            Files.write(outputFile, encryptedText.getBytes());

        } catch (IOException | URISyntaxException e) {
            System.err.println("Помилка при читанні або записі файлу: " + e.getMessage());
        }
    }

    private static String encrypt(String text) {
        StringBuilder result = new StringBuilder();
        for (char ch : text.toCharArray()) {
            if (Character.isLetter(ch)) {
                char base = Character.isUpperCase(ch) ? 'A' : 'a';
                result.append((char) ((ch - base + Cesar.SHIFT) % 26 + base));
            }else result.append(ch);
        }
        return result.toString();
    }

    public static String getText(String fileName) throws IOException, URISyntaxException {
        Path inputFile = Paths.get(Objects.requireNonNull(Cesar.class.getClassLoader().getResource(fileName)).toURI());

        List<String> lines = Files.readAllLines(inputFile);
        StringBuilder text = new StringBuilder();
        for (String line : lines) {
            text.append(line).append(System.lineSeparator());
        }
        return text.toString();
    }
}
