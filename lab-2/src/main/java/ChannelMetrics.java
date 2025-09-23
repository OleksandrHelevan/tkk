import java.util.*;

public class ChannelMetrics {
    private static final String ALPHABET = "абвгґдеєжзиіїйклмнопрстуфхцчшщьюя";

    public static void main(String[] args) {
        Map<Character, Double> sourceProb = new LinkedHashMap<>();
        Random rand = new Random();
        for (char c : ALPHABET.toCharArray()) {
            sourceProb.put(c, 0.01 + rand.nextDouble() * 0.05);
        }
        normalize(sourceProb);

        double Hx = entropy(sourceProb);
        System.out.printf("Ентропія джерела H(X): %.4f біт/символ%n", Hx);

        double errorProb = 0.05;
        Map<Character, Map<Character, Double>> channelMatrix = createChannelMatrix(sourceProb, errorProb);

        double Hyx = channelEntropy(sourceProb, channelMatrix);
        System.out.printf("Ентропія завад H(Y/X): %.4f біт/символ%n", Hyx);

        double nu = 1000;
        double R = nu * (Hx - Hyx);
        System.out.printf("Швидкість передачі R: %.2f біт/сек%n", R);

        double C = nu * log2(ALPHABET.length());
        System.out.printf("Пропускна спроможність C: %.2f біт/сек%n", C);

        double eta = R / C;
        System.out.printf("Коефіцієнт використання η: %.4f%n", eta);
    }

    private static double log2(double x) {
        return Math.log(x) / Math.log(2);
    }

    private static void normalize(Map<Character, Double> probs) {
        double sum = probs.values().stream().mapToDouble(Double::doubleValue).sum();
        for (Map.Entry<Character, Double> entry : probs.entrySet()) {
            entry.setValue(entry.getValue() / sum);
        }
    }

    private static double entropy(Map<Character, Double> probs) {
        double H = 0.0;
        for (double p : probs.values()) {
            if (p > 0) H += -p * log2(p);
        }
        return H;
    }

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

    private static double channelEntropy(Map<Character, Double> sourceProb,
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
}
