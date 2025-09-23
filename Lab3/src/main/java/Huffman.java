import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

class Huffman {
    static class Node implements Comparable<Node>{
        char ch; int freq; Node left, right;
        Node(char ch, int freq){this.ch=ch; this.freq=freq;}
        Node(int freq, Node left, Node right){this.freq=freq; this.left=left; this.right=right;}
        public int compareTo(Node o){ return this.freq - o.freq; }
        public boolean isLeaf(){ return left==null && right==null; }
    }

    Map<Character, String> codes = new HashMap<>();

    public Map<Character, String> encode(String text){
        Map<Character,Integer> freq = new HashMap<>();
        for(char c : text.toCharArray()) freq.put(c, freq.getOrDefault(c,0)+1);

        PriorityQueue<Node> pq = new PriorityQueue<>();
        for(Map.Entry<Character,Integer> e : freq.entrySet()) pq.add(new Node(e.getKey(), e.getValue()));

        while(pq.size() > 1){
            Node a = pq.poll();
            Node b = pq.poll();
            assert b != null;
            pq.add(new Node(a.freq + b.freq, a, b));
        }
        Node root = pq.poll();
        assert root != null;
        build(root, "");
        return codes;
    }

    private void build(Node node, String prefix){
        if(node.isLeaf()){ codes.put(node.ch, prefix.isEmpty()?"0":prefix); return; }
        build(node.left, prefix+"0");
        build(node.right, prefix+"1");
    }

    public String encodeText(String text, Map<Character,String> codes){
        StringBuilder sb = new StringBuilder();
        for(char c : text.toCharArray()) sb.append(codes.get(c));
        return sb.toString();
    }

    public String decodeText(String encoded, Map<Character,String> codes){
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