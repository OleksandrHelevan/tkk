import java.util.ArrayList;
import java.util.List;

public class MoveToFront {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz ";

    public static List<Integer> encode(String input) {
        input = input.toLowerCase();
        List<Character> symbols = new ArrayList<>();
        for (char c : ALPHABET.toCharArray()) symbols.add(c);

        List<Integer> encoded = new ArrayList<>();
        for (char c : input.toCharArray()) {
            int index = symbols.indexOf(c);
            if (index == -1) continue;
            encoded.add(index);
            symbols.remove(index);
            symbols.add(0, c);
        }
        return encoded;
    }

    public static String decode(List<Integer> encoded) {
        List<Character> symbols = new ArrayList<>();
        for (char c : ALPHABET.toCharArray()) symbols.add(c);

        StringBuilder sb = new StringBuilder();
        for (int index : encoded) {
            char c = symbols.get(index);
            sb.append(c);
            symbols.remove(index);
            symbols.add(0, c);
        }
        return sb.toString();
    }
}
