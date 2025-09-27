import java.util.ArrayList;
import java.util.List;

public class LevenshteinCoding {

    public static String encodeNumber(int n) {
        if (n == 0) return "0";
        String code = "";
        int c = 0;
        while (n > 0) {
            String bin = Integer.toBinaryString(n);
            bin = bin.substring(1);
            code = bin + code;
            n = bin.length();
            c++;
        }
        StringBuilder prefix = new StringBuilder();
        for (int i = 0; i < c; i++) prefix.append('1');
        prefix.append('0');
        return prefix + code;
    }

    public static List<String> encode(List<Integer> numbers) {
        List<String> result = new ArrayList<>();
        for (int n : numbers) result.add(encodeNumber(n));
        return result;
    }

    public static int decodeNumber(String code) {
        if (code.equals("0")) return 0;
        int i = 0;
        while (i < code.length() && code.charAt(i) == '1') i++;
        int c = i;
        i++;
        String k = code.substring(i);
        int n = 1;
        while (c > 0) {
            if (k.length() < n - 1) break;
            String bits = "1" + k.substring(0, n - 1);
            n = Integer.parseInt(bits, 2);
            k = k.substring(n - 1);
            c--;
        }
        return n;
    }

    public static List<Integer> decode(List<String> codes) {
        List<Integer> numbers = new ArrayList<>();
        for (String code : codes) numbers.add(decodeNumber(code));
        return numbers;
    }
}
