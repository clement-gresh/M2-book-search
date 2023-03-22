package com.searchgutenberg.booksearchengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
public class BooksearchengineApplication {
    //（token，(Book id , key word frequence))
    public static ConcurrentHashMap<String, ConcurrentHashMap<Integer,Integer>> keywordsDictionary;

    public static void main(String[] args) {
        SpringApplication.run(BooksearchengineApplication.class, args);
    }

}
