package com.searchgutenberg.booksearchengine.keywords;

import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Data
public class KeyWord implements Comparable<KeyWord>, Serializable {
    private static final long serialVersionUID = 1L;
    private final String root;
    private final Set<String> words = new HashSet<String>();
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
        return Integer.valueOf(o.frequence).compareTo(frequence);
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
/*
    public String getRoot() {
        return root;
    }

    public Set<String> getWords() {
        return words;
    }

    public int getFrequence() {
        return frequence;
    }*/

    @Override
    public String toString() {
        return "KeyWords [root = " + root + ", frequence = " + frequence + "]";
    }


}
