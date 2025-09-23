import java.util.HashMap;
import java.util.Map;

class Utils {
    public static double entropy(String text){
        Map<Character,Integer> freq = new HashMap<>();
        for(char c : text.toCharArray()) freq.put(c,freq.getOrDefault(c,0)+1);
        double H=0.0;
        int N = text.length();
        for(int f : freq.values()){
            double p = (double)f/N;
            H -= p*Math.log(p)/Math.log(2);
        }
        return H;
    }

    public static double avgCodeLength(Map<Character,String> codes, String text){
        Map<Character,Integer> freq = new HashMap<>();
        for(char c : text.toCharArray()) freq.put(c,freq.getOrDefault(c,0)+1);
        double l=0;
        int N = text.length();
        for(Map.Entry<Character,String> e : codes.entrySet()){
            double p = freq.get(e.getKey())/(double)N;
            l += p * e.getValue().length();
        }
        return l;
    }

    public static double maxEntropy(int alphabetSize){
        return Math.log(alphabetSize)/Math.log(2);
    }
}
