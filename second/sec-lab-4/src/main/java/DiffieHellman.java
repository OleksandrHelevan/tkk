import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

public class DiffieHellman {

    private static final SecureRandom random = new SecureRandom();

    public static void main(String[] args) {
        BigInteger p = BigInteger.probablePrime(16, random);
        System.out.println("p = " + p);

        BigInteger g = findPrimitiveRoot(p);
        System.out.println("g = " + g);

        BigInteger a = new BigInteger(10, random);
        BigInteger b = new BigInteger(10, random);

        System.out.println("Secret a = " + a);
        System.out.println("Secret b = " + b);

        assert g != null;
        BigInteger A = g.modPow(a, p);
        BigInteger B = g.modPow(b, p);

        System.out.println("Public A = " + A);
        System.out.println("Public B = " + B);

        BigInteger keyA = B.modPow(a, p);
        BigInteger keyB = A.modPow(b, p);

        System.out.println("Key from A = " + keyA);
        System.out.println("Key from B = " + keyB);

        if (keyA.equals(keyB)) {
            System.out.println("Ключі співпадають!");
        } else {
            System.out.println("Ключі НЕ співпадають!");
        }
    }

    public static BigInteger findPrimitiveRoot(BigInteger p) {
        BigInteger phi = p.subtract(BigInteger.ONE);
        Set<BigInteger> factors = primeFactors(phi);

        for (BigInteger g = BigInteger.TWO; g.compareTo(p) < 0; g = g.add(BigInteger.ONE)) {
            boolean isPrimitive = true;

            for (BigInteger factor : factors) {
                if (g.modPow(phi.divide(factor), p).equals(BigInteger.ONE)) {
                    isPrimitive = false;
                    break;
                }
            }
            if (isPrimitive) {
                return g;
            }
        }
        return null;
    }

    public static Set<BigInteger> primeFactors(BigInteger n) {
        Set<BigInteger> factors = new HashSet<>();
        BigInteger two = BigInteger.TWO;

        while (n.mod(two).equals(BigInteger.ZERO)) {
            factors.add(two);
            n = n.divide(two);
        }

        for (BigInteger i = BigInteger.valueOf(3);
             i.multiply(i).compareTo(n) <= 0;
             i = i.add(two)) {

            while (n.mod(i).equals(BigInteger.ZERO)) {
                factors.add(i);
                n = n.divide(i);
            }
        }

        if (n.compareTo(BigInteger.TWO) > 0) {
            factors.add(n);
        }

        return factors;
    }
}