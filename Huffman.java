import java.util.*;

public class Huffman {

    // Node class for the Huffman Tree
    static class Node implements Comparable<Node> {
        char ch;
        int freq;
        Node left, right;

        Node(char ch, int freq) {
            this.ch = ch;
            this.freq = freq;
        }

        Node(int freq, Node left, Node right) {
            this.ch = '\0';  // internal node
            this.freq = freq;
            this.left = left;
            this.right = right;
        }

        @Override
        public int compareTo(Node other) {
            return this.freq - other.freq;
        }

        // Check if it's a leaf node
        boolean isLeaf() {
            return left == null && right == null;
        }
    }

    // Compression: build codes and encode
    public static Map<Character, String> buildCodes(Node root) {
        Map<Character, String> codes = new HashMap<>();
        buildCodesHelper(root, "", codes);
        return codes;
    }

    private static void buildCodesHelper(Node node, String code, Map<Character, String> codes) {
        if (node != null) {
            if (node.isLeaf()) {
                codes.put(node.ch, code.length() > 0 ? code : "0"); // Edge case: single character
            }
            buildCodesHelper(node.left, code + "0", codes);
            buildCodesHelper(node.right, code + "1", codes);
        }
    }

    public static Node buildHuffmanTree(String text) {
        Map<Character, Integer> freqMap = new HashMap<>();
        for (char ch : text.toCharArray()) {
            freqMap.put(ch, freqMap.getOrDefault(ch, 0) + 1);
        }

        PriorityQueue<Node> pq = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
            pq.add(new Node(entry.getKey(), entry.getValue()));
        }

        while (pq.size() > 1) {
            Node left = pq.poll();
            Node right = pq.poll();
            Node parent = new Node(left.freq + right.freq, left, right);
            pq.add(parent);
        }

        return pq.peek();
    }

    public static class CompressionResult {
        Map<Character, String> codesTable;
        String compressedData;

        CompressionResult(Map<Character, String> codesTable, String compressedData) {
            this.codesTable = codesTable;
            this.compressedData = compressedData;
        }
    }

    public static CompressionResult compress(String text) {
        Node root = buildHuffmanTree(text);
        Map<Character, String> codesTable = buildCodes(root);

        StringBuilder encoded = new StringBuilder();
        for (char ch : text.toCharArray()) {
            encoded.append(codesTable.get(ch));
        }

        return new CompressionResult(codesTable, encoded.toString());
    }

    public static String decompress(String compressedData, Map<Character, String> codesTable) {
        // Build reverse map
        Map<String, Character> reverseCodes = new HashMap<>();
        for (Map.Entry<Character, String> entry : codesTable.entrySet()) {
            reverseCodes.put(entry.getValue(), entry.getKey());
        }

        StringBuilder decoded = new StringBuilder();
        StringBuilder currentCode = new StringBuilder();

        for (char bit : compressedData.toCharArray()) {
            currentCode.append(bit);
            if (reverseCodes.containsKey(currentCode.toString())) {
                decoded.append(reverseCodes.get(currentCode.toString()));
                currentCode.setLength(0); // reset
            }
        }

        return decoded.toString();
    }

    // Example usage
    public static void main(String[] args) {
        String text = "Hello World";

        CompressionResult result = compress(text);

        System.out.println("Codes Table:");
        for (Map.Entry<Character, String> entry : result.codesTable.entrySet()) {
            System.out.println("'" + entry.getKey() + "': " + entry.getValue());
        }

        System.out.println("\nCompressed Data:");
        System.out.println(result.compressedData);

        String decompressedText = decompress(result.compressedData, result.codesTable);

        System.out.println("\nDecompressed Text:");
        System.out.println(decompressedText);
    }
}
