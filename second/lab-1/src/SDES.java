public class SDES {
    private static final int[] P10 = {3, 5, 2, 7, 4, 10, 1, 9, 8, 6};
    private static final int[] P8 = {6, 3, 7, 4, 8, 5, 10, 9};
    private static final int[] IP = {2, 6, 3, 1, 4, 8, 5, 7};
    private static final int[] IP_INV = {4, 1, 3, 5, 7, 2, 8, 6};
    private static final int[] EP = {4, 1, 2, 3, 2, 3, 4, 1};
    private static final int[] P4 = {2, 4, 3, 1};

    private static final int[][] S0 = {
            {1, 0, 3, 2},
            {3, 2, 1, 0},
            {3, 1, 2, 0},
            {3, 1, 3, 1}
    };

    private static final int[][] S1 = {
            {0, 1, 2, 3},
            {2, 0, 1, 3},
            {3, 0, 1, 0},
            {0, 1, 2, 3}
    };

    public static int[][] generateKeys(String key) {
        int[] k = new int[10];
        for (int i = 0; i < 10; i++) k[i] = key.charAt(i) - '0';

        int[] p10Key = permute(k, P10);

        int[] left = new int[5];
        int[] right = new int[5];
        System.arraycopy(p10Key, 0, left, 0, 5);
        System.arraycopy(p10Key, 5, right, 0, 5);

        left = leftShift(left, 1);
        right = leftShift(right, 1);
        int[] k1 = permute(concat(left, right), P8);

        left = leftShift(left, 2);
        right = leftShift(right, 2);

        int[] k2 = permute(concat(left, right), P8);

        return new int[][]{k1, k2};
    }

    public static int[] encryptBlock(int[] block, int[] k1, int[] k2) {
        return decryptBlock(block, k2, k1);
    }

    public static int[] decryptBlock(int[] block, int[] k1, int[] k2) {
        int[] temp = permute(block, IP);
        temp = fk(temp, k2);
        temp = swapHalves(temp);
        temp = fk(temp, k1);
        temp = permute(temp, IP_INV);
        return temp;
    }

    private static int[] fk(int[] block, int[] key) {
        int[] left = new int[4];
        int[] right = new int[4];
        System.arraycopy(block, 0, left, 0, 4);
        System.arraycopy(block, 4, right, 0, 4);

        int[] temp = permute(right, EP);
        for (int i = 0; i < 8; i++) temp[i] ^= key[i];

        int[] left4 = sBox(temp, S0);
        int[] right4 = sBox(new int[]{temp[4], temp[5], temp[6], temp[7]}, S1);
        int[] fResult = permute(concat(left4, right4), P4);

        for (int i = 0; i < 4; i++) left[i] ^= fResult[i];

        return concat(left, right);
    }

    private static int[] sBox(int[] input, int[][] s) {
        int row = (input[0] << 1) | input[3];
        int col = (input[1] << 1) | input[2];
        int val = s[row][col];
        return new int[]{(val >> 1) & 1, val & 1};
    }

    private static int[] permute(int[] bits, int[] table) {
        int[] res = new int[table.length];
        for (int i = 0; i < table.length; i++) res[i] = bits[table[i] - 1];
        return res;
    }

    private static int[] leftShift(int[] bits, int n) {
        int[] res = new int[bits.length];
        for (int i = 0; i < bits.length; i++) res[i] = bits[(i + n) % bits.length];
        return res;
    }

    private static int[] concat(int[] a, int[] b) {
        int[] res = new int[a.length + b.length];
        System.arraycopy(a, 0, res, 0, a.length);
        System.arraycopy(b, 0, res, a.length, b.length);
        return res;
    }

    private static int[] swapHalves(int[] block) {
        return concat(
                java.util.Arrays.copyOfRange(block, 4, 8),
                java.util.Arrays.copyOfRange(block, 0, 4)
        );
    }

    public static int[] stringToBits(String s) {
        int[] bits = new int[s.length()];
        for (int i = 0; i < s.length(); i++) bits[i] = s.charAt(i) - '0';
        return bits;
    }

    public static String bitsToString(int[] bits) {
        StringBuilder sb = new StringBuilder();
        for (int b : bits) sb.append(b);
        return sb.toString();
    }

    public static void main(String[] args) {
        String plaintext = "01010101";
        String realKey = "1010011011";

        int[][] keys = generateKeys(realKey);
        int[] encrypted = encryptBlock(stringToBits(plaintext), keys[0], keys[1]);
        String ciphertext = bitsToString(encrypted);

        bruteForceAttack(plaintext, ciphertext);
    }

    private static void runExample(String plaintext, String key) {
        int[][] keys = SDES.generateKeys(key);
        int[] block = SDES.stringToBits(plaintext);

        int[] encrypted = SDES.encryptBlock(block, keys[0], keys[1]);
        int[] decrypted = SDES.decryptBlock(encrypted, keys[0], keys[1]);

        System.out.println("Plaintext:  " + plaintext);
        System.out.println("Key:        " + key);
        System.out.println("Encrypted:  " + SDES.bitsToString(encrypted));
        System.out.println("Decrypted:  " + SDES.bitsToString(decrypted));
        System.out.println("---------------------------");
    }

    public static void bruteForceAttack(String plaintext, String targetCiphertext) {
        long startTime = System.nanoTime();
        int attempts = 0;
        String foundKey = null;
        for (int i = 0; i < 1024; i++) {
            String key = String.format("%10s", Integer.toBinaryString(i))
                    .replace(' ', '0');
            int[][] keys = generateKeys(key);
            int[] block = stringToBits(plaintext);
            int[] encrypted = encryptBlock(block, keys[0], keys[1]);
            String encryptedStr = bitsToString(encrypted);
            attempts++;
            if (encryptedStr.equals(targetCiphertext)) {
                foundKey = key;
                break;
            }
        }
        long endTime = System.nanoTime();
        double timeMs = (endTime - startTime) / 1_000_000.0;
        System.out.println("Brute Force Results:");
        System.out.println("Found key: " + foundKey);
        System.out.println("Keys checked: " + attempts);
        System.out.println("Total possible keys: 1024");
        System.out.println("Time taken: " + timeMs + " ms");
    }
}