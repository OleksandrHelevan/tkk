import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class ShannonFano {
    Map<Character, String> codes = new HashMap<>();

    public Map<Character, String> encode(String text) {
        Map<Character, Integer> freq = new HashMap<>();
        for(char c : text.toCharArray()) freq.put(c, freq.getOrDefault(c,0)+1);

        List<Map.Entry<Character, Integer>> list = freq.entrySet()
                .stream().sorted((a,b) -> b.getValue() - a.getValue())
                .collect(Collectors.toList());

        build(list, "");
        return codes;
    }

    private void build(List<Map.Entry<Character,Integer>> list, String prefix) {
        if(list.size() == 1) {
            codes.put(list.get(0).getKey(), prefix.isEmpty() ? "0" : prefix);
            return;
        }
        int total = list.stream().mapToInt(Map.Entry::getValue).sum();
        int sum = 0;
        int split = 0;
        for(int i=0; i<list.size(); i++){
            sum += list.get(i).getValue();
            if(sum >= total/2) { split=i+1; break; }
        }
        build(list.subList(0, split), prefix+"0");
        build(list.subList(split, list.size()), prefix+"1");
    }

    public String encodeText(String text, Map<Character,String> codes) {
        StringBuilder sb = new StringBuilder();
        for(char c : text.toCharArray()) sb.append(codes.get(c));
        return sb.toString();
    }

    public String decodeText(String encoded, Map<Character,String> codes) {
        Map<String, Character> rev = new HashMap<>();
        for(Map.Entry<Character,String> e : codes.entrySet()) rev.put(e.getValue(), e.getKey());
        StringBuilder sb = new StringBuilder();
        String tmp="";
        for(char bit : encoded.toCharArray()){
            tmp += bit;
            if(rev.containsKey(tmp)) { sb.append(rev.get(tmp)); tmp=""; }
        }
        return sb.toString();
    }
}