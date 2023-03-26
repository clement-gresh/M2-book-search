package com.searchgutenberg.booksearchengine.utils.keywords;

import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Data
public class KeyWord implements Comparable<KeyWord>, Serializable {
    private static final long serialVersionUID = 1L;
    private final String root;
    private final Set<String> words = new HashSet<>();
    private int frequence = 0;

    public KeyWord(String root) {
        this.root = root;
    }

    public void add(String term) {
        words.add(term);
        frequence++;
    }


    @Override
    public int compareTo(KeyWord o) {
        return Integer.compare(o.frequence, frequence);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof KeyWord)) {
            return false;
        } else {
            return root.equals(((KeyWord) obj).root);
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] { root });
    }


    @Override
    public String toString() {
        return "KeyWords [root = " + root + ", frequence = " + frequence + "]";
    }


}
