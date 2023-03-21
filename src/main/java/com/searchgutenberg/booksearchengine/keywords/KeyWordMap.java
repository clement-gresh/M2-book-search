package com.searchgutenberg.booksearchengine.keywords;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class KeyWordMap implements Serializable {
    private static final long serialVersionUID = 1L;

    private HashMap<String, List<Integer>> keyWordMap;

    public KeyWordMap() {
        this.keyWordMap = new HashMap<>();
    }

    public HashMap<String, List<Integer>> getMotCleMap() {
        return keyWordMap;
    }

    public void setMotCleMap(HashMap<String, List<Integer>> keyWordMap) {
        this.keyWordMap = keyWordMap;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String stem : keyWordMap.keySet()) {
            sb.append(stem + " -> " + keyWordMap.get(stem).size());
            sb.append("\n");
        }
        return sb.toString();
    }
}
