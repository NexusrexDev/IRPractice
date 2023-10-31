package Boolean_InvertedIndex;

import java.util.*;

public class Main {
    public static String folderPath = "\\files";
    public static HashMap<String, HashSet<Integer>> matrix;
    public static HashMap<Integer, String> fileList;
    public static Preprocessor preProc;
    public static QueryHandler handler;

    public static void main(String[] args) {
        preProc = new Preprocessor(folderPath);
        fileList = preProc.getFilenameList();
        matrix = preProc.getInvertedMatrix();

        handler = new QueryHandler(matrix, fileList.size());
        Scanner input = new Scanner(System.in);

        System.out.println("Welcome to the best IR system, part two!\nEnter Query:");
        String queryLine = input.nextLine();

        while (!queryLine.equalsIgnoreCase("stop")) {
            HashSet<Integer> result = handler.makeQuery(queryLine.toLowerCase(Locale.ROOT));
            printResults(result);
            System.out.println("Enter 'stop' to end, or another query: ");
            queryLine = input.nextLine();
        }
    }

    public static void printResults(HashSet<Integer> resultList) {
        if (resultList != null) {
            System.out.println("Files found:");
            for (int docID : resultList) {
                System.out.println(fileList.get(docID));
            }
        } else {
            System.out.println("Wrong query");
        }
    }
}
