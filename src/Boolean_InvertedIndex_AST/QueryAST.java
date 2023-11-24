package Boolean_InvertedIndex_AST;


import java.util.*;
import Stemmer.Porter;

class TreeNode {
    String value;
    TreeNode left;
    TreeNode right;

    public TreeNode(String value) {
        this.value = value;
        this.left = null;
        this.right = null;
    }
}

class QueryAST {
    private static HashMap<String, HashSet<Integer>> invertedMatrix;
    private Queue<String> termQueue;
    private Stack<HashSet<Integer>> matrixStack;
    private HashSet<Integer> documentSet;
    private String query;
    private int index;

    private Porter stemmer = new Porter();
    public QueryAST(String query, HashMap<String, HashSet<Integer>> matrix, int docNumber) {
        this.query = query;
        invertedMatrix = matrix;
        documentSet = new HashSet<>();
        matrixStack = new Stack<>();
        for (int i = 0; i < docNumber; i++)
            documentSet.add(i);
        this.index = 0;
    }

    public TreeNode parseQuery() {
        String[] tokens = query.split("\\s+");
        String[] stemmedTokens = new String[tokens.length];
        for(int i = 0; i < tokens.length; i++) {
            stemmedTokens[i] = stemmer.stem(tokens[i].toLowerCase());
        }
        return parseExpression(stemmedTokens);
    }

    private TreeNode parseExpression(String[] tokens) {
        TreeNode node = parseTerm(tokens);
        while (index < tokens.length && (tokens[index].equals("AND") || tokens[index].equals("OR"))) {
            String operator = tokens[index];
            index++;
            TreeNode right = parseTerm(tokens);
            TreeNode newNode = new TreeNode(operator);
            newNode.left = node;
            newNode.right = right;
            node = newNode;
        }
        return node;
    }

    private TreeNode parseTerm(String[] tokens) {
        if (index < tokens.length && tokens[index].equals("NOT")) {
            index++;
            TreeNode node = new TreeNode("NOT");
            node.right = parseFactor(tokens);
            return node;
        } else {
            return parseFactor(tokens);
        }
    }

    private TreeNode parseFactor(String[] tokens) {
        if (index < tokens.length && tokens[index].matches("[a-zA-Z]+")) {
            TreeNode node = new TreeNode(tokens[index]);
            index++;
            return node;
        } else {
            throw new IllegalArgumentException("Invalid query syntax.");
        }
    }

    public HashSet<Integer> makeQuery(TreeNode root) {

        traverseTreeHelper(root);

        return matrixStack.pop();
    }
    private void traverseTreeHelper(TreeNode node){
        if (node == null)
            return;

        traverseTreeHelper(node.left);
        traverseTreeHelper(node.right);

        switch (node.value) {
            case "AND":
                andOperation();
                break;
            case "OR":
                orOperation();
                break;
            case "NOT":
                notOperation();
                break;
            default:
                HashSet<Integer> termDocuments = invertedMatrix.get(node.value);
                if (termDocuments == null)
                    matrixStack.push(new HashSet<>());
                else matrixStack.push(termDocuments);
        }
    }

    private void andOperation() {
        int p1 = 0, p2 = 0;

        HashSet<Integer> result = new HashSet<>();
        ArrayList<Integer> mat1 = new ArrayList<>(matrixStack.pop());
        ArrayList<Integer> mat2 = new ArrayList<>(matrixStack.pop());

        //Same implementation as the lecture
        while (p1 != mat1.size() && p2 != mat2.size()) {
            int i1 = mat1.get(p1), i2 = mat2.get(p2);
            if (i1 == i2) {
                result.add(mat1.get(p1));
                p1++; p2++;
            } else if (i1 > i2) {
                p2++;
            } else {
                p1++;
            }
        }

        matrixStack.push(result);
    }

    private void orOperation() {
        HashSet<Integer> result = new HashSet<>();

        ArrayList<Integer> mat1 = new ArrayList<>(matrixStack.pop());
        ArrayList<Integer> mat2 = new ArrayList<>(matrixStack.pop());

        //Adding all values of each term, hashsets will handle the repetition
        for (int i : mat1)
            result.add(i);

        for (int i : mat2)
            result.add(i);

        matrixStack.push(result);
    }

    private void notOperation() {
        String nextTerm = termQueue.poll();
        HashSet<Integer> result = (HashSet<Integer>) documentSet.clone();
        HashSet<Integer> termMatrix = matrixStack.pop();

        //Doing a 'not -term-' is basically removing the posting list of said document from the total
        for (int document : termMatrix)
            result.remove(document);

        matrixStack.push(result);
    }
}

