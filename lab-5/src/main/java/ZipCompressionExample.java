import java.io.IOException;

public class ZipCompressionExample {


    public static byte[] compressStringToZip(String input, String entryName) throws IOException {
        return CompressionChart.zipCompress(input, entryName);
    }

    public static void main(String[] args) throws IOException {
        String message = "TOBEORNOTTOBEORTOBEORNOT";

        System.out.println("Оригінальне повідомлення: " + message);
        int originalSize = message.getBytes().length;
        System.out.println("Оригінальний розмір: " + originalSize + " байт");

        byte[] compressedZip = compressStringToZip(message, "message.txt");
        int zipSize = compressedZip.length;
        System.out.println("Розмір ZIP: " + zipSize + " байт");

        double zipCompressionRatio = (double) zipSize / originalSize;
        System.out.printf("Коефіцієнт стиснення ZIP: %.2f\n", zipCompressionRatio);
    }
}
