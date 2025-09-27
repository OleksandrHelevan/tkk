import java.util.ArrayList;
import java.util.List;

public class RLE {
    public static List<int[]> encode(List<Integer> input) {
        List<int[]> encoded = new ArrayList<>();
        if (input.isEmpty()) return encoded;

        int prev = input.get(0), count = 1;
        for (int i = 1; i < input.size(); i++) {
            int curr = input.get(i);
            if (curr == prev) count++;
            else {
                encoded.add(new int[]{prev, count});
                prev = curr;
                count = 1;
            }
        }
        encoded.add(new int[]{prev, count});
        return encoded;
    }

    public static List<Integer> decode(List<int[]> encoded) {
        List<Integer> decoded = new ArrayList<>();
        for (int[] pair : encoded) {
            for (int i = 0; i < pair[1]; i++) decoded.add(pair[0]);
        }
        return decoded;
    }
}
