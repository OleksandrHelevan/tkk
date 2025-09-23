import java.util.*;

public class ChannelMetrics {

    private static final String ALPHABET = "абвгґдеєжзиіїйклмнопрстуфхцчшщьюя";

    public static void main(String[] args) {
        double errorProb = 0.05;
        double nu = 1000;

        Map<Character, Double> sourceProb = generateSourceProbabilities();

        // ентропія джерела
        double Hx = calculateEntropy(sourceProb);
        System.out.printf("Ентропія джерела H(X): %.4f біт/символ%n", Hx);

        // матриця каналу зі збоями
        Map<Character, Map<Character, Double>> channelMatrix = createChannelMatrix(sourceProb, errorProb);

        // ентропія завад
        double Hyx = calculateChannelEntropy(sourceProb, channelMatrix);
        System.out.printf("Ентропія завад H(Y|X): %.4f біт/символ%n", Hyx);

        // швидкість передачі, пропускну здатність та коефіцієнт використання
        double R = calculateTransmissionRate(nu, Hx, Hyx);
        double C = calculateChannelCapacity(nu);
        double eta = calculateEfficiency(R, C);

        System.out.printf("Швидкість передачі R: %.2f біт/сек%n", R);
        System.out.printf("Пропускна спроможність C: %.2f біт/сек%n", C);
        System.out.printf("Коефіцієнт використання η: %.4f%n", eta);
    }

    // Генерація ймовірностей джерела
    private static Map<Character, Double> generateSourceProbabilities() {
        Map<Character, Double> sourceProb = new LinkedHashMap<>();
        Random rand = new Random();
        for (char c : ALPHABET.toCharArray()) {
            sourceProb.put(c, 0.01 + rand.nextDouble() * 0.05);
        }
        normalize(sourceProb);
        return sourceProb;
    }

    // Нормалізація ймовірностей, щоб їх сума = 1
    private static void normalize(Map<Character, Double> probs) {
        double sum = probs.values().stream().mapToDouble(Double::doubleValue).sum();
        for (Map.Entry<Character, Double> entry : probs.entrySet()) {
            entry.setValue(entry.getValue() / sum);
        }
    }

    // 1 Обчислення ентропії джерела
    private static double calculateEntropy(Map<Character, Double> probs) {
        double H = 0.0;
        for (double p : probs.values()) {
            if (p > 0) H += -p * log2(p);
        }
        return H;
    }

    // Створення матриці каналу зі збоями
    private static Map<Character, Map<Character, Double>> createChannelMatrix(
            Map<Character, Double> sourceProb, double errorProb) {

        Map<Character, Map<Character, Double>> matrix = new LinkedHashMap<>();
        int m = ALPHABET.length();

        for (char xi : ALPHABET.toCharArray()) {
            Map<Character, Double> row = new LinkedHashMap<>();
            for (char yj : ALPHABET.toCharArray()) {
                if (xi == yj) {
                    row.put(yj, 1 - errorProb);
                } else {
                    row.put(yj, errorProb / (m - 1));
                }
            }
            matrix.put(xi, row);
        }
        return matrix;
    }

    // 2 Обчислення ентропії завад H(Y|X)
    private static double calculateChannelEntropy(Map<Character, Double> sourceProb,
                                                  Map<Character, Map<Character, Double>> matrix) {
        double Hyx = 0.0;
        for (char xi : ALPHABET.toCharArray()) {
            double px = sourceProb.getOrDefault(xi, 0.0);
            for (char yj : ALPHABET.toCharArray()) {
                double pyx = matrix.get(xi).getOrDefault(yj, 0.0);
                if (pyx > 0) {
                    Hyx += px * (-pyx * log2(pyx));
                }
            }
        }
        return Hyx;
    }

    // 3 Обчислення швидкості передачі R
    private static double calculateTransmissionRate(double nu, double Hx, double Hyx) {
        return nu * (Hx - Hyx);
    }

    // Обчислення пропускної здатності C
    private static double calculateChannelCapacity(double nu) {
        return nu * log2(ALPHABET.length());
    }

    // 4 Коефіцієнт використання каналу η
    private static double calculateEfficiency(double R, double C) {
        return R / C;
    }

    private static double log2(double x) {
        return Math.log(x) / Math.log(2);
    }
}
