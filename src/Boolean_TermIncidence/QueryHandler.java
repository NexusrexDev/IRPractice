package Boolean_TermIncidence;

import java.util.*;

public class QueryHandler {
    private static HashMap<String, ArrayList<Boolean>> incidenceMatrix;
    private Queue<String> termQueue;
    private Stack<ArrayList<Boolean>> matrixStack;

    QueryHandler(HashMap<String, ArrayList<Boolean>> matrix) {
        incidenceMatrix = matrix;
    }

    public ArrayList<Boolean> makeQuery(String query) {
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
                    ArrayList<Boolean> termIncidence = incidenceMatrix.get(term);
                    if (termIncidence == null)
                        matrixStack.push(new ArrayList<>());
                    else matrixStack.push(termIncidence);
            }
        }

        return matrixStack.pop();
    }

    private void andOperation() {
        String nextTerm = termQueue.poll();
        ArrayList<Boolean> mat1 = matrixStack.pop(), mat2, result = new ArrayList<>();

        if (nextTerm.equals("not")) {
            notOperation();
            mat2 = matrixStack.pop();
        } else {
            mat2 = incidenceMatrix.get(nextTerm);
        }

        for (int i = 0; i < mat1.size(); i++) {
            result.add(mat1.get(i) & mat2.get(i));
        }

        matrixStack.push(result);
    }

    private void orOperation() {
        String nextTerm = termQueue.poll();
        ArrayList<Boolean> mat1 = matrixStack.pop(), mat2, result = new ArrayList<>();

        if (nextTerm.equals("not")) {
            notOperation();
            mat2 = matrixStack.pop();
        } else {
            mat2 = incidenceMatrix.get(nextTerm);
        }

        for (int i = 0; i < mat1.size(); i++) {
            result.add(mat1.get(i) | mat2.get(i));
        }

        matrixStack.push(result);
    }

    private void notOperation() {
        String nextTerm = termQueue.poll();
        ArrayList<Boolean> matrix = incidenceMatrix.get(nextTerm);

        for (int i = 0; i < matrix.size(); i++) {
            matrix.set(i, !matrix.get(i));
        }

        matrixStack.push(matrix);
    }
}
