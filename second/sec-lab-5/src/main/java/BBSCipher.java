public class BBSCipher {
    private long n;
    private long state;

    public BBSCipher(long p, long q, long seed) {
        this.n = p * q;
        this.state = (seed * seed) % n;
    }

    private int nextBit() {
        state = (state * state) % n;
        return (int) (state % 2);
    }

    private byte nextByte() {
        int b = 0;
        for (int i = 0; i < 8; i++) {
            b = (b << 1) | nextBit();
        }
        return (byte) b;
    }

    public byte[] process(byte[] data) {
        byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = (byte) (data[i] ^ nextByte());
        }
        return result;
    }

    public static void main(String[] args) {
        long p = 151;
        long q = 199;
        long seed = 123;

        String originalText = "Лабораторна робота 5";
        System.out.println("Оригінал: " + originalText);

        BBSCipher encryptor = new BBSCipher(p, q, seed);
        byte[] encryptedText = encryptor.process(originalText.getBytes());

        System.out.print("Зашифровано (Hex-код): ");
        for (byte b : encryptedText) {
            System.out.printf("%02X ", b);
        }
        System.out.println();

        BBSCipher decryptor = new BBSCipher(p, q, seed);
        byte[] decryptedText = decryptor.process(encryptedText);

        System.out.println("Розшифровано: " + new String(decryptedText));
    }
}
