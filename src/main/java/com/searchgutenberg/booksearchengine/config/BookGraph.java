package com.searchgutenberg.booksearchengine.config;

import com.searchgutenberg.booksearchengine.utils.keywords.KeyWord;
import lombok.Data;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class BookGraph {
    // dict(book : dict(book, distance))
    private Map<Integer, Map<Integer, Float>> adjacencyMatrix = new HashMap<>();
    private Map<Integer, Float> closenessCentrality = new HashMap<>();

    // jaccardDistance must have an entry with the newbook itself at 1
    public void addBook(Integer newBook, Map<Integer, Float> jaccardDistance){
        adjacencyMatrix.putIfAbsent(newBook, jaccardDistance);
        for(Map.Entry<Integer, Map<Integer, Float>> entry : adjacencyMatrix.entrySet()){
            entry.getValue().putIfAbsent(newBook, jaccardDistance.get(entry.getKey()));
        }
    }
    public void removeBook(Integer bookId){
        adjacencyMatrix.remove(bookId);
        for(Map.Entry<Integer, Map<Integer, Float>> entry : adjacencyMatrix.entrySet()){
            entry.getValue().remove(bookId);
        }
    }

    public void addLine(Integer book, Map<Integer, Float> jaccardDistance, Float closeness){
        adjacencyMatrix.put(book, jaccardDistance);
        closenessCentrality.put(book, closeness);
    }

    public static float  computeDistance(List<KeyWord> keyWordList,
                                        ConcurrentHashMap<String, ConcurrentHashMap<Integer,Integer>> keywordsDictionary,
                                          Integer book){

        int intersection = (int) keyWordList.stream().filter(keyWord -> {
            ConcurrentHashMap<Integer,Integer> keywordOccurences = keywordsDictionary.get(keyWord.getRoot());
            Integer bookId = (keywordOccurences != null) ? keywordOccurences.get(book) : null;
            return bookId != null;
        }).count();

        AtomicInteger bookKeywordNb = new AtomicInteger();
        keywordsDictionary.forEach((key, value) -> {if(value.containsKey(book)) bookKeywordNb.getAndIncrement();});

        int union = keyWordList.size() + bookKeywordNb.get() - intersection;

        return 1 - (float) intersection/union;
    }

    public void computeCloseness(){
        int size = adjacencyMatrix.size();
        adjacencyMatrix.forEach((key, value) -> {
            double sum = value.values().stream().mapToDouble(e -> Double.parseDouble(String.valueOf(e))).sum();
            closenessCentrality.put(key, (float) ((size - 1) / sum));
        });
    }

    SortedSet<Map.Entry<Integer, Float>> booksSortedByDistance(Integer bookId) {
        SortedSet<Map.Entry<Integer, Float>> sortedEntries = new TreeSet<>(
                Map.Entry.comparingByValue()
        );
        sortedEntries.addAll(adjacencyMatrix.get(bookId).entrySet());
        return sortedEntries;
    }
}
