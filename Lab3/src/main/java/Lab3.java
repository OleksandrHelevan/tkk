import java.util.*;

public class Lab3 {

    public static void main(String[] args) {
        String text = "My name is Oleksandr";
        System.out.println("=== Original Text ===");
        System.out.println(text);
        System.out.println();

        ShannonFano shannon = new ShannonFano();
        Map<Character, String> sfCodes = shannon.encode(text);
        String sfEncoded = shannon.encodeText(text, sfCodes);

        System.out.println("=== Shannon-Fano Codes ===");
        sfCodes.forEach((k,v) -> System.out.println(k + " -> " + v));
        System.out.println("\nEncoded Text (Shannon-Fano): " + sfEncoded);

        Huffman huffman = new Huffman();
        Map<Character, String> huffCodes = huffman.encode(text);
        String huffEncoded = huffman.encodeText(text, huffCodes);

        System.out.println("\n=== Huffman Codes ===");
        huffCodes.forEach((k,v) -> System.out.println(k + " -> " + v));
        System.out.println("\nEncoded Text (Huffman): " + huffEncoded);

        String sfDecoded = shannon.decodeText(sfEncoded, sfCodes);
        String huffDecoded = huffman.decodeText(huffEncoded, huffCodes);

        System.out.println("\nDecoded (Shannon-Fano): " + sfDecoded);
        System.out.println("Decoded (Huffman): " + huffDecoded);

        double Hmax = Utils.maxEntropy(sfCodes.size());
        double H = Utils.entropy(text);
        double l_sf = Utils.avgCodeLength(sfCodes, text);
        double l_huff = Utils.avgCodeLength(huffCodes, text);

        System.out.println("\n--- Information Characteristics ---");
        System.out.printf("Shannon-Fano: l_avg=%.3f, KCC=%.3f, Kve=%.3f\n",
                l_sf, Hmax / l_sf, H / l_sf);
        System.out.printf("Huffman: l_avg=%.3f, KCC=%.3f, Kve=%.3f\n",
                l_huff, Hmax / l_huff, H / l_huff);
    }
}
