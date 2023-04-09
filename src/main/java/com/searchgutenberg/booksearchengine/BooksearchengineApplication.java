package com.searchgutenberg.booksearchengine;

import com.searchgutenberg.booksearchengine.config.BookGraph;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@SpringBootApplication
public class BooksearchengineApplication {
    //（token，(Book id , key word frequence))
    public static ConcurrentHashMap<String, ConcurrentHashMap<Integer,Integer>> keywordsDictionary;
    //(Book title , book id )
    public static ConcurrentHashMap<String, Integer> booksTitle;
    //(author , list of book id )
    public static ConcurrentHashMap<String, ConcurrentLinkedQueue<Integer> > authorBooks;
    //(term , stem of the term )
    public static ConcurrentHashMap<String,String > term2KeywordDictionary;

    public static BookGraph bookGraph ;



    public static void main(String[] args) {
        SpringApplication.run(BooksearchengineApplication.class, args);
    }

}
