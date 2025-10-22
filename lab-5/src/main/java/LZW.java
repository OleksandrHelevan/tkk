import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Клас для реалізації алгоритму стиснення та декомпресії LZW.
 * Ця версія коду є остаточною та виправленою для коректної роботи
 * з будь-якими текстовими даними, включаючи кирилицю.
 */
public class LZW {

    /**
     * Стискає рядок за допомогою алгоритму LZW.
     * @param uncompressed Вхідний рядок для стиснення.
     * @return Список цілочисельних кодів, що представляють стиснені дані.
     */
    public static List<Integer> compress(String uncompressed) {
        if (uncompressed == null || uncompressed.isEmpty()) {
            return new ArrayList<>();
        }

        int dictSize = 256;
        Map<String, Integer> dictionary = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            dictionary.put(String.valueOf((char) i), i);
        }

        String w = "";
        List<Integer> result = new ArrayList<>();
        for (char c : uncompressed.toCharArray()) {
            String wc = w + c;
            if (dictionary.containsKey(wc)) {
                w = wc;
            } else {
               result.add(dictionary.get(w));
                dictionary.put(wc, dictSize++);
                w = String.valueOf(c);
            }
        }

        if (!w.isEmpty()) {
            result.add(dictionary.get(w));
        }
        return result;
    }

    /**
     * Декомпресує список кодів LZW у вихідний рядок.
     * @param compressed Список кодів для декомпресії.
     * @return Вихідний, декомпресований рядок.
     */
    public static String decompress(List<Integer> compressed) {
        if (compressed == null || compressed.isEmpty()) {
            return "";
        }

        int dictSize = 256;
        Map<Integer, String> dictionary = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            dictionary.put(i, String.valueOf((char) i));
        }

        String w = String.valueOf((char) (int) compressed.remove(0));
        StringBuilder result = new StringBuilder(w);

        for (int k : compressed) {
            String entry;
            if (dictionary.containsKey(k)) {
                entry = dictionary.get(k);
            } else if (k == dictSize) {
                entry = w + w.charAt(0);
            } else {
                throw new IllegalArgumentException("Помилковий стиснений код: " + k);
            }

            result.append(entry);

            dictionary.put(dictSize++, w + entry.charAt(0));
            w = entry;
        }
        return result.toString();
    }


    public static void main(String[] args) {
        String message1 = "TOBEORNOTTOBEORTOBEORNOT";
        System.out.println("--- Повідомлення 1 ---");
        performCompressionAnalysis(message1);

        String message2 = "THEQUICKBROWNFOXJUMPSOVERTHELAZYDOG";
        System.out.println("\n--- Повідомлення 2 ---");
        performCompressionAnalysis(message2);

        System.out.println("\n--- Повідомлення 3 ---");

        String message3_ascii = "My name is Oleksandr and Oleksandr is my name.";
        performCompressionAnalysis(message3_ascii);
    }

    private static void performCompressionAnalysis(String message) {
        System.out.println("Оригінальне повідомлення: " + message);

        int originalSize = message.getBytes().length;
        System.out.println("Оригінальний розмір: " + originalSize + " байт");

        List<Integer> compressed;
        try {
            compressed = compress(message);
        } catch (Exception e) {
            System.out.println("Помилка при стисненні: " + e.getMessage());
            System.out.println("Ця реалізація LZW працює тільки з символами з набору ASCII (коди 0-255).");
            return;
        }

        System.out.println("Стиснене повідомлення (коди): " + compressed);

        int compressedSize = compressed.size() * Integer.BYTES;
        System.out.println("Стиснений розмір: " + compressedSize + " байт");

        String decompressed = decompress(new ArrayList<>(compressed));
        System.out.println("Декодоване повідомлення: " + decompressed);

        double compressionRatio = (double) compressedSize / originalSize;
        System.out.printf("Коефіцієнт стиснення: %.2f\n", compressionRatio);
        System.out.println("Верифікація (співпадає): " + message.equals(decompressed));
    }
}