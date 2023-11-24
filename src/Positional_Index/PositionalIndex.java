package Positional_Index;

import java.util.ArrayList;
import java.util.HashMap;

public class PositionalIndex {
    private HashMap<Integer, ArrayList<Integer>> postingList;
    private int documentFrequency;

    PositionalIndex() {
        postingList = new HashMap<>();
        documentFrequency = 0;
    }

    public void addPosition(int docID, int position) {
        if (postingList.get(docID) == null) {
            ArrayList<Integer> docPostingList = new ArrayList<>();
            docPostingList.add(position);
            postingList.put(docID, docPostingList);
            documentFrequency++;
        } else {
            postingList.get(docID).add(position);
        }
    }

    public void addPosition(int docID, ArrayList<Integer> positionList) {
        if (postingList.get(docID) == null) {
            postingList.put(docID, positionList);
            documentFrequency++;
        }
    }

    public HashMap<Integer, ArrayList<Integer>> getPostingList() {
        return postingList;
    }

    public int getDocumentFrequency() {
        return documentFrequency;
    }
}
