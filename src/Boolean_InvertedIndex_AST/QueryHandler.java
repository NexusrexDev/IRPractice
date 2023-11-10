
package Boolean_InvertedIndex_AST;
import java.util.*;

public class QueryHandler {
    private static HashMap<String, HashSet<Integer>> invertedMatrix;
    private Queue<String> termQueue;
    private Stack<HashSet<Integer>> matrixStack;
    private HashSet<Integer> documentSet;

    QueryHandler(HashMap<String, HashSet<Integer>> matrix, int docNumber) {
        invertedMatrix = matrix;
        documentSet = new HashSet<>();
        for (int i = 0; i < docNumber; i++)
            documentSet.add(i);
    }

    public HashSet<Integer> makeQuery(String query) {
        termQueue = new LinkedList<>();
        matrixStack = new Stack<>();
        String[] termList = query.split(" ");

        for (String term : termList) {
            termQueue.offer(term.trim());
        }

        while (!termQueue.isEmpty()) {
            String term = termQueue.poll();

            switch (term) {
                case "and":
                    andOperation();
                    break;
                case "or":
                    orOperation();
                    break;
                case "not":
                    notOperation();
                    break;
                default:
                    HashSet<Integer> termDocuments = invertedMatrix.get(term);
                    if (termDocuments == null)
                        matrixStack.push(new HashSet<>());
                    else matrixStack.push(termDocuments);
            }
        }

        return matrixStack.pop();
    }

    private void andOperation() {
        String nextTerm = termQueue.poll();
        ArrayList<Integer> mat1 = new ArrayList<>(matrixStack.pop()), mat2;
        HashSet<Integer> result = new HashSet<>();
        int p1 = 0, p2 = 0;

        if (nextTerm.equals("not")) {
            notOperation();
            mat2 = new ArrayList<>(matrixStack.pop());
        } else {
            mat2 = new ArrayList<>(invertedMatrix.get(nextTerm));
        }

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
        String nextTerm = termQueue.poll();
        ArrayList<Integer> mat1 = new ArrayList<>(matrixStack.pop()), mat2;
        HashSet<Integer> result = new HashSet<>();

        if (nextTerm.equals("not")) {
            notOperation();
            mat2 = new ArrayList<>(matrixStack.pop());
        } else {
            mat2 = new ArrayList<>(invertedMatrix.get(nextTerm));
        }

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
        HashSet<Integer> termMatrix = invertedMatrix.get(nextTerm);

        //Doing a 'not -term-' is basically removing the posting list of said document from the total
        for (int document : termMatrix)
            result.remove(document);

        matrixStack.push(result);
    }
}
