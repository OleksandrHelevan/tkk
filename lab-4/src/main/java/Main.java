import java.util.List;

public class Main {
    public static void main(String[] args) {
        String message = "швидка лисиця";

        List<Integer> mtf = MoveToFront.encode(message);
        System.out.println("MTF закодоване: " + mtf);

        String decodedMTF = MoveToFront.decode(mtf);
        System.out.println("MTF розкодоване: " + decodedMTF);

        List<int[]> rle = RLE.encode(mtf);

        List<Integer> decodedRLE = RLE.decode(rle);
        String rleDecodedMessage = MoveToFront.decode(decodedRLE);

        List<String> lev = LevenshteinCoding.encode(mtf);
        System.out.println("Levenshtein після: " + lev);

        System.out.println("Levenshtein розкодоване (повертає MTF): " + rleDecodedMessage);
        System.out.println(calculate(33, message.length(), rleDecodedMessage.length()));
        System.out.println(calculate(33, message.length(), lev.size()));

    }

    public static double calculate(int m,int N, int n){
        return (Math.log(m)*N)/n;
    }

}
