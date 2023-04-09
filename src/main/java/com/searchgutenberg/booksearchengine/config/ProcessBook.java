package com.searchgutenberg.booksearchengine.config;

import com.searchgutenberg.booksearchengine.entity.Book;
import com.searchgutenberg.booksearchengine.entity.Format;
import com.searchgutenberg.booksearchengine.entity.Person;
import com.searchgutenberg.booksearchengine.utils.keywords.KeyWord;
import com.searchgutenberg.booksearchengine.utils.keywords.KeyWordExtractor;
import com.searchgutenberg.booksearchengine.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.searchgutenberg.booksearchengine.BooksearchengineApplication.keywordsDictionary;
import static com.searchgutenberg.booksearchengine.BooksearchengineApplication.*;

@Service
@Slf4j
public class ProcessBook {
    @Autowired
    @Qualifier("restTemplate")
    private RestTemplate restTemplate;
    @Autowired
    private BookRepository bookRepository;


    /**
     * set image's and text's URL for a book object
     * index the book,and stock it in the DB
     * @param book a book object to be completed init
     * @return a future task to indicate if the book is processed successfully
     */
    @Async("ProcessBookExecutor")
    public CompletableFuture<Boolean> processBook(Book book) throws IOException {

        Format format = book.getFormats();
        String textURL = getTextURL(format);
        if (textURL == null)
            return CompletableFuture.completedFuture(false);
        book.setText(textURL);
        if (format.getImage() != null){
            book.setImage(format.getImage().replace("small", "medium"));
        }

        String text = restTemplate.getForObject(textURL, String.class);
        if (text == null)
            return CompletableFuture.completedFuture(false);
        
        List<KeyWord> keyWordList = KeyWordExtractor.getBookKeyWords(text);

        // Creating Jaccard Graph
        ConcurrentHashMap<Integer, Float> jaccardDistance = new ConcurrentHashMap<>();
        jaccardDistance.put(book.getId(), 1F);
        bookGraph.getAdjacencyMatrix().forEach((key, value) -> {
            float distance = BookGraph.computeDistance(keyWordList, keywordsDictionary, key);
            jaccardDistance.put(key, distance);
        });
        bookGraph.addBook(book.getId(), jaccardDistance);
//        System.out.println(
//                "New book id: " + book.getId()
//                + " (id: " + 1513 + ", " + bookGraph.getAdjacencyMatrix().get(book.getId()).get(1513) + ")"
//        );

        for (KeyWord kword : keyWordList) {
            ConcurrentHashMap<Integer,Integer> bookIdsKeyFrequence = keywordsDictionary.get(kword.getRoot());
            for(String word :kword.getWords()){
                term2KeywordDictionary.put(word,kword.getRoot());
            }


            if (bookIdsKeyFrequence != null) {
                bookIdsKeyFrequence.put(book.getId(),kword.getFrequence());
            } else {
                bookIdsKeyFrequence = new ConcurrentHashMap<>();
                bookIdsKeyFrequence.put(book.getId(),kword.getFrequence());
                keywordsDictionary.put(kword.getRoot(), bookIdsKeyFrequence);
            }
        }
        indexTitleAuthor(book);
        bookRepository.save(book);

        return CompletableFuture.completedFuture(true);
    }

    /**
     * get at least one valid URL to download the text of a book
     * @param format the URLs given by "gutendex" api
     * @return a valid URL to download txt
     */
    private String getTextURL(Format format){
        if (format.getTextUtf() != null){
            if (format.getTextUtf().endsWith(".txt") || format.getTextUtf().endsWith(".txt.utf-8"))
                return format.getTextUtf();
        }
        if (format.getTextAscii() != null) {
            if (format.getTextAscii().endsWith(".txt") || format.getTextAscii().endsWith(".txt.utf-8"))
                return format.getTextAscii();
        }
        if (format.getTextPlain() != null) {
            if (format.getTextPlain().endsWith(".txt") || format.getTextPlain().endsWith(".txt.utf-8"))
                return format.getTextPlain();
        }
        return null;
    }

    /**
     * stock the information of a book in index table of title and author
     * @param book the book to be processed
     */
    public void indexTitleAuthor(Book book)  {

            booksTitle.put(book.getTitle().toLowerCase(),book.getId());
            List<Person>authors=book.getAuthors();
            for(Person a:authors){
                ConcurrentLinkedQueue<Integer> booksId=authorBooks.get(a.getName().toLowerCase());
                if(booksId==null){
                    booksId=new ConcurrentLinkedQueue<>();
                    booksId.add(book.getId());
                    authorBooks.put(a.getName().toLowerCase(),booksId);
                }else {
                    booksId.add(book.getId());
                }
            }

    }


}
