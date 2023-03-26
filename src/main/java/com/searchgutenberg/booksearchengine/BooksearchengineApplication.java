package com.searchgutenberg.booksearchengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@SpringBootApplication
public class BooksearchengineApplication {
    //（token，(Book id , key word frequence))
    public static ConcurrentHashMap<String, ConcurrentHashMap<Integer,Integer>> keywordsDictionary;
    public static ConcurrentHashMap<String, Integer> booksTitle;

    public static ConcurrentHashMap<String, ConcurrentLinkedQueue<Integer> > authorBooks;

    public static ConcurrentHashMap<String,String > term2KeywordDictionary;



    public static void main(String[] args) {
        SpringApplication.run(BooksearchengineApplication.class, args);
    }

}
