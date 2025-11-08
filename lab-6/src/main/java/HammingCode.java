import java.util.*;

public class HammingCode {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Введіть текстове повідомлення:");
        String input = sc.nextLine();
        System.out.println("\nВхідне повідомлення: " + input);

        String binary = textToBinary(input);
        System.out.println("\nКрок 1 — Перетворення у бінарне представлення:");
        for (char c : input.toCharArray()) {
            System.out.printf("%c -> %s%n", c, String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0'));
        }
        System.out.println("\nПовне бінарне повідомлення:");
        System.out.println(binary);

        int[] dataBits = stringToBitArray(binary);
        int[] encoded = encodeHamming(dataBits);
        System.out.println("\nКрок 2 — Кодування методом Хеммінга:");
        System.out.println("Закодоване повідомлення (з доданими контрольними бітами):");
        printBits(encoded);

        Random rand = new Random();
        int errorPos = rand.nextInt(encoded.length);
        encoded[errorPos] ^= 1;
        System.out.println("\nКрок 3 — Імітація помилки:");
        System.out.println("Помилка внесена у позицію #" + (errorPos + 1));
        printBits(encoded);

        System.out.println("\nКрок 4 — Виявлення та виправлення помилки:");
        int[] decoded = decodeHamming(encoded);
        System.out.println("Відновлений (виправлений) бітовий рядок:");
        printBits(decoded);

        String decodedBinary = bitsToString(decoded);
        System.out.println("\nКрок 5 — Декодування:");
        System.out.println("Бінарне повідомлення після декодування:");
        System.out.println(decodedBinary);

        String decodedText = binaryToText(decodedBinary);
        System.out.println("\nВідновлений текст після декодування:");
        System.out.println(decodedText);

        double redundancy = (double) encoded.length / dataBits.length;
        System.out.printf("\nКрок 6 — Коефіцієнт надмірності: %.3f%n", redundancy);
    }

    public static int[] encodeHamming(int[] dataBits) {
        int m = dataBits.length;
        int r = 0;
        while (Math.pow(2, r) < m + r + 1) r++;

        int[] encoded = new int[m + r];
        int j = 0;

        for (int i = 1; i <= encoded.length; i++) {
            if (isPowerOfTwo(i)) encoded[i - 1] = 0;
            else encoded[i - 1] = dataBits[j++];
        }

        for (int i = 0; i < r; i++) {
            int parityPos = (int) Math.pow(2, i);
            int parity = 0;
            for (int bit = 1; bit <= encoded.length; bit++) {
                if (((bit >> i) & 1) == 1)
                    parity ^= encoded[bit - 1];
            }
            encoded[parityPos - 1] = parity;
        }
        return encoded;
    }

    public static int[] decodeHamming(int[] received) {
        int r = 0;
        while (Math.pow(2, r) < received.length + 1) r++;

        int errorPos = 0;

        for (int i = 0; i < r; i++) {
            int parity = 0;
            for (int bit = 1; bit <= received.length; bit++) {
                if (((bit >> i) & 1) == 1)
                    parity ^= received[bit - 1];
            }
            if (parity != 0)
                errorPos += (int) Math.pow(2, i);
        }

        if (errorPos != 0) {
            System.out.println("Виявлено помилку у біті #" + errorPos);
            received[errorPos - 1] ^= 1;
            System.out.println("Помилка виправлена.");
        } else {
            System.out.println("Помилок не виявлено.");
        }

        List<Integer> decodedBits = new ArrayList<>();
        for (int i = 1; i <= received.length; i++) {
            if (!isPowerOfTwo(i))
                decodedBits.add(received[i - 1]);
        }

        return decodedBits.stream().mapToInt(i -> i).toArray();
    }

    private static boolean isPowerOfTwo(int n) {
        return (n & (n - 1)) == 0;
    }

    private static int[] stringToBitArray(String s) {
        int[] bits = new int[s.length()];
        for (int i = 0; i < s.length(); i++) bits[i] = s.charAt(i) - '0';
        return bits;
    }

    private static String bitsToString(int[] bits) {
        StringBuilder sb = new StringBuilder();
        for (int b : bits) sb.append(b);
        return sb.toString();
    }

    private static void printBits(int[] bits) {
        for (int i = 0; i < bits.length; i++) {
            if ((i + 1) % 8 == 0) System.out.print(bits[i] + " ");
            else System.out.print(bits[i]);
        }
        System.out.println();
    }

    private static String textToBinary(String text) {
        StringBuilder binary = new StringBuilder();
        for (char c : text.toCharArray()) {
            binary.append(String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0'));
        }
        return binary.toString();
    }

    private static String binaryToText(String binary) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i + 8 <= binary.length(); i += 8) {
            String byteStr = binary.substring(i, i + 8);
            int charCode = Integer.parseInt(byteStr, 2);
            text.append((char) charCode);
        }
        return text.toString();
    }
}
