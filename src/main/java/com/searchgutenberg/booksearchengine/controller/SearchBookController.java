package com.searchgutenberg.booksearchengine.controller;

import com.searchgutenberg.booksearchengine.entity.Book;
import com.searchgutenberg.booksearchengine.utils.keywords.KeyWordExtractor;
import com.searchgutenberg.booksearchengine.service.SearchBookService;
import com.sun.istack.internal.NotNull;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.*;

@CrossOrigin
@RestController
public class SearchBookController {
    @Autowired
    private SearchBookService searchBookService = new SearchBookService();
    @GetMapping("/searchbycontent/{word}")
    @ResponseBody
    public ResponseEntity<List<Book>> searchBookByContent(@PathVariable String word) {

        String[] keywords = word.split("\\s+");
        HashMap<Integer,Integer>bookIdsKeyFrequence=new HashMap<>();
        Arrays.asList(keywords).forEach(keyword -> {
            try {
                keyword =  KeyWordExtractor.tokenizer(keyword);
            } catch (IOException e) {

            }
            bookIdsKeyFrequence.putAll(searchBookService.getBooksByContent(keyword.toLowerCase()));
        });

        List<Book> books= searchBookService.getSortedBooks(bookIdsKeyFrequence,false);
        return ResponseEntity.ok(books);
    }



    @GetMapping(value = "/searchbyregex")
    @ResponseBody
    public ResponseEntity<List<Book>> searchBookByRegex(@RequestBody Map<String,String >requestBody) {
        String input=requestBody.get("regex");

        String[] keywords = input.split("\\s+");
        HashMap<Integer,Integer>bookIdsKeyFrequence=new HashMap<>();
        Arrays.asList(keywords).forEach(keyword -> {
            try {
                System.out.println("debug "+keyword.toLowerCase());
                bookIdsKeyFrequence.putAll(searchBookService.getBooksByRegex(keyword.toLowerCase()));
            } catch (IOException e) {

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });

        List<Book> books= searchBookService.getSortedBooks(bookIdsKeyFrequence,false);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/searchbytitle/{title}")
    @ResponseBody
    public ResponseEntity<List<Book>> searchBookByTitle(@PathVariable String title)  {

        String[] keywords = title.split("\\s+");
        HashSet<Integer> bookIds = new HashSet<>();
        Arrays.asList(keywords).forEach(k -> {
            bookIds.addAll(searchBookService.getBooksByTitle(k.toLowerCase()));
        });

        List<Book> books= searchBookService.getSortedBooks(bookIds,false);
        return ResponseEntity.ok(books);
    }


    @GetMapping("/searchbyauthor/{author}")
    @ResponseBody
    public ResponseEntity<List<Book>> searchBookByAuthor(@PathVariable String author)  {

        String[] keywords = author.split("\\s+");
        HashSet<Integer> bookIds = new HashSet<>();
        Arrays.asList(keywords).forEach(k -> {
            bookIds.addAll(searchBookService.getBooksByAuthor(k.toLowerCase()));
        });
        List<Book> books= searchBookService.getSortedBooks(bookIds,false);
        return ResponseEntity.ok(books);
    }





}
