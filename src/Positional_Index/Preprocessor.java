package Positional_Index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import Stemmer.Porter;

public class Preprocessor {
    private String folderPath;
    private HashMap<Integer, String> filenameList = new HashMap<>();
    private HashMap<String, PositionalIndex> positionalIndex = new HashMap<>();
    private Porter stemmer = new Porter();

    Preprocessor(String filePath) {
        folderPath = new File("").getAbsolutePath() + filePath;
        readFiles();
    }

    private void readFiles(){
        File folder = new File(folderPath);
        File[] fileList = folder.listFiles();
        int fileCounter = 1;
        for (File file : fileList) {
            if (file.isFile()) {
                //Identify each file with an ID number
                filenameList.put(fileCounter, file.getName());

                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));

                    String line = reader.readLine();

                    int wordCounter = 0;

                    while (line != null) {
                        String[] words = line.split(" ");
                        for (String word : words) {
                            word = word.trim();
                            word = stemmer.stem(word.toLowerCase());

                            if (positionalIndex.get(word) == null) {
                                //If the word isn't captured before, add it to the matrix
                                PositionalIndex pos = new PositionalIndex();
                                pos.addPosition(fileCounter, wordCounter);
                                positionalIndex.put(word, pos);
                            } else {
                                //Otherwise, add the current file ID to this word's posting list
                                positionalIndex.get(word).addPosition(fileCounter, wordCounter);
                            }

                            wordCounter++;
                        }
                        line = reader.readLine();
                    }
                    fileCounter++;
                    reader.close();
                } catch (Exception e) {}
            }
        }
    }

    public HashMap<Integer, String> getFilenameList() {
        return filenameList;
    }

    public HashMap<String, PositionalIndex> getPositionalIndex() { return positionalIndex; }
}
