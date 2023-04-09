package com.searchgutenberg.booksearchengine.config;

import com.searchgutenberg.booksearchengine.utils.keywords.KeyWord;
import lombok.Data;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class BookGraph {
    // dict(book : dict(book, distance))
    private final ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Float>> adjacencyMatrix ;
    private ConcurrentHashMap<Integer, Float> closenessCentrality ;

   public BookGraph(){
        adjacencyMatrix = new ConcurrentHashMap<>();
        closenessCentrality = new ConcurrentHashMap<>();
    }

    // jaccardDistance must have an entry with the newbook itself at 1
    public void addBook(Integer newBook, ConcurrentHashMap<Integer, Float> jaccardDistance){
           if (!adjacencyMatrix.isEmpty()) {
               for (ConcurrentHashMap.Entry<Integer, ConcurrentHashMap<Integer, Float>> entry : adjacencyMatrix.entrySet()) {
                   entry.getValue().putIfAbsent(newBook, jaccardDistance.get(entry.getKey()));
               }
           }
           adjacencyMatrix.putIfAbsent(newBook, jaccardDistance);
    }
    public void removeBook(Integer bookId){
        adjacencyMatrix.remove(bookId);
        for(Map.Entry<Integer, ConcurrentHashMap<Integer, Float>> entry : adjacencyMatrix.entrySet()){
            entry.getValue().remove(bookId);
        }
    }

    public void addLine(Integer book, ConcurrentHashMap<Integer, Float> jaccardDistance, Float closeness){
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
            float sum = 0F;
            for(float d : value.values()) { sum += d; }
            closenessCentrality.put(key, (size - 1) / sum);
        });
    }

    public SortedSet<Map.Entry<Integer, Float>> booksSortedByDistance(Integer bookId) {
        SortedSet<Map.Entry<Integer, Float>> sortedEntries = new TreeSet<>(
                Map.Entry.comparingByValue()
        );
        sortedEntries.addAll(adjacencyMatrix.get(bookId).entrySet());
//        List<Integer> booksIds = new ArrayList<>();
//        sortedEntries.forEach(e -> booksIds.add(e.getKey()));
//        return booksIds;
        return sortedEntries;
    }

    public SortedSet<Map.Entry<Integer, Float>> closestFromBooks(List<Integer> booksIds) {
        Map<Integer, Float> meanDistances = new ConcurrentHashMap<>();

        SortedSet<Map.Entry<Integer, Float>> sortedEntries = booksSortedByDistance(booksIds.get(0));

        sortedEntries.forEach(e -> {
            float sumDistance = 0;
            for (Integer bookId : booksIds) {
                sumDistance += adjacencyMatrix.get(bookId).get(e.getKey());
            }
            float meanDistance = sumDistance / booksIds.size();
            meanDistances.put(e.getKey(), meanDistance);
        });

        SortedSet<Map.Entry<Integer, Float>> sortedDistances = new TreeSet<>(
                Map.Entry.comparingByValue()
        );
        sortedDistances.addAll(meanDistances.entrySet());
        return sortedDistances;
    }
}
