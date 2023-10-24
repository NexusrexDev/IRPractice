package Boolean_TermIncidence;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class Preprocessor {
    private String folderPath;
    private HashSet<String> distinctTerms = new HashSet<>();
    private HashMap<Integer, ArrayList<String>> termsPerDocument = new HashMap<>();
    private HashMap<Integer, String> filenameList = new HashMap<>();
    private HashMap<String, ArrayList<Boolean>> incidenceMatrix = new HashMap<>();

    Preprocessor(String filePath) {
        folderPath = new File("").getAbsolutePath() + filePath;
        readFiles();
        createMatrix();
    }

    private void readFiles(){
        File folder = new File(folderPath);
        File[] fileList = folder.listFiles();
        int fileCounter = 0;
        for (File file : fileList) {
            if (file.isFile()) {
                filenameList.put(fileCounter, file.getName());

                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
                    ArrayList<String> termsList = new ArrayList<>();

                    String line = reader.readLine();

                    while (line != null) {
                        String[] words = line.split(" ");
                        for (String word : words) {
                            word = word.trim();
                            distinctTerms.add(word);
                            termsList.add(word);
                        }
                        line = reader.readLine();
                    }

                    termsPerDocument.put(fileCounter, termsList);

                    reader.close();
                } catch (Exception e) {}

            }
            fileCounter++;
        }
    }

    private void createMatrix() {
        for (String term : distinctTerms) {
            ArrayList<Boolean> incidenceVector = new ArrayList<>();

            for (Entry<Integer, ArrayList<String>> entry : termsPerDocument.entrySet())
                incidenceVector.add(entry.getValue().contains(term));

            incidenceMatrix.put(term, incidenceVector);
        }
    }

    public HashMap<Integer, String> getFilenameList() {
        return filenameList;
    }

    public HashMap<String, ArrayList<Boolean>> getIncidenceMatrix() {
        return incidenceMatrix;
    }
}
