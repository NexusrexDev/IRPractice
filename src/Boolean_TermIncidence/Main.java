package Boolean_TermIncidence;

import java.util.*;

public class Main {
    public static String folderPath = "\\files";
    public static HashMap<String, ArrayList<Boolean>> matrix;
    public static HashMap<Integer, String> fileList;
    public static Preprocessor preProc;
    public static QueryHandler handler;
    public static void main(String[] args) {
        preProc = new Preprocessor(folderPath);
        matrix = preProc.getIncidenceMatrix();
        fileList = preProc.getFilenameList();

        handler = new QueryHandler(matrix);
        Scanner input = new Scanner(System.in);


        System.out.println("Welcome to the best IR system!\nEnter Query:");
        String queryLine = input.nextLine();

        while (!queryLine.equalsIgnoreCase("stop")) {
            ArrayList<Boolean> result = handler.makeQuery(queryLine.toLowerCase(Locale.ROOT));
            printResults(result);
            System.out.println("Enter 'stop' to end, or another query: ");
            queryLine = input.nextLine();
        }
    }

    public static void printResults(ArrayList<Boolean> resultList) {
        if (resultList != null) {
            System.out.println("Files found:");
            for (int i = 0; i < resultList.size(); i++) {
                if (resultList.get(i))
                    System.out.println(fileList.get(i));
            }
        } else {
            System.out.println("Wrong query");
        }
    }
}
