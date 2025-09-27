import java.util.List;

public class Main {
    public static void main(String[] args) {
        String message = "my name is oleksandr";

        List<Integer> mtf = MoveToFront.encode(message);
        System.out.println("MTF закодоване: " + mtf);

        String decodedMTF = MoveToFront.decode(mtf);
        System.out.println("MTF розкодоване: " + decodedMTF);

        List<int[]> rle = RLE.encode(mtf);

        List<Integer> decodedRLE = RLE.decode(rle);
        String rleDecodedMessage = MoveToFront.decode(decodedRLE);

        List<String> lev = LevenshteinCoding.encode(mtf);
        System.out.println("Levenshtein після: " + lev);

        List<Integer> decodedLev = LevenshteinCoding.decode(lev);
        System.out.println("Levenshtein розкодоване (повертає MTF): " + rleDecodedMessage);
    }
}
