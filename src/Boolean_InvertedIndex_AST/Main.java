package Boolean_InvertedIndex_AST;

import java.io.File;
import java.util.*;

public class Main {
    public static String folderPath = "/files";
    public static HashMap<String, HashSet<Integer>> matrix;
    public static HashMap<Integer, String> fileList;
    public static Preprocessor preProc;
    public static QueryAST handler;

    public static void main(String[] args) {
        System.out.println(new File("").getAbsolutePath()+ folderPath);
        preProc = new Preprocessor(folderPath);
        fileList = preProc.getFilenameList();
        matrix = preProc.getInvertedMatrix();

        Scanner input = new Scanner(System.in);
        System.out.println("Welcome to the best IR system, part two!\nEnter Query:");
        String queryLine = input.nextLine();
        while (!queryLine.equalsIgnoreCase("stop")) {
            handler = new QueryAST(queryLine, matrix, fileList.size());

            TreeNode root = handler.parseQuery();
            HashSet<Integer> result = handler.makeQuery(root);
            printResults(result);
            System.out.println("Enter 'stop' to end, or another query: ");
            queryLine = input.nextLine();
        }
    }

    public static void printResults(HashSet<Integer> resultList) {
        if (resultList.size() != 0) {
            System.out.println("Files found:");
            for (int docID : resultList) {
                System.out.println(fileList.get(docID));
            }
        } else {
            System.out.println("Empty query");
        }
    }
}
