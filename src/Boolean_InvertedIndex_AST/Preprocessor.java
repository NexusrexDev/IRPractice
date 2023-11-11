
package Boolean_InvertedIndex_AST;
import java.io.*;
import java.util.*;

public class Preprocessor {
    private String folderPath;
    private HashMap<Integer, String> filenameList = new HashMap<>();
    private HashMap<String, HashSet<Integer>> invertedMatrix = new HashMap<>();

    Preprocessor(String filePath) {
        folderPath = new File("").getAbsolutePath() + filePath;
        readFiles();
    }

    private void readFiles(){
        File folder = new File(folderPath);
        File[] fileList = folder.listFiles();
        int fileCounter = 0;
        for (File file : fileList) {
            if (file.isFile()) {
                //Identify each file with an ID number
                filenameList.put(fileCounter, file.getName());

                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));

                    String line = reader.readLine();

                    while (line != null) {
                        String[] words = line.split(" ");
                        for (String word : words) {
                            word = word.trim();

                            if (invertedMatrix.get(word) == null) {
                                //If the word isn't captured before, add it to the matrix
                                HashSet<Integer> postingList = new HashSet<>();
                                postingList.add(fileCounter);
                                invertedMatrix.put(word, postingList);
                            } else {
                                //Otherwise, add the current file ID to this word's posting list
                                invertedMatrix.get(word).add(fileCounter);
                            }
                        }
                        line = reader.readLine();
                    }
                    reader.close();
                } catch (Exception e) {}

            }
            fileCounter++;
        }
    }

    public HashMap<Integer, String> getFilenameList() {
        return filenameList;
    }

    public HashMap<String, HashSet<Integer>> getInvertedMatrix() {
        return invertedMatrix;
    }
}
