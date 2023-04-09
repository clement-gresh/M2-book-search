package com.searchgutenberg.booksearchengine.controller;

import com.searchgutenberg.booksearchengine.entity.Book;
import com.searchgutenberg.booksearchengine.service.SearchBookService;
import com.searchgutenberg.booksearchengine.utils.keywords.KeyWordExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.searchgutenberg.booksearchengine.BooksearchengineApplication.bookGraph;

@CrossOrigin
@RestController
public class SearchBookController {
    @Autowired
    private SearchBookService searchBookService = new SearchBookService();
    @GetMapping("/searchbycontent/{word}")
    @ResponseBody
    public ResponseEntity<List<Book>> searchBookByContent(@PathVariable String word) {
        Instant start = Instant.now(); // test
        String[] keywords = word.split("\\s+");
        HashMap<Integer,Integer> booksIdNbOfKeywords = new HashMap<>();
        Arrays.asList(keywords).forEach(keyword -> {
            try {
                keyword =  KeyWordExtractor.tokenizer(keyword);
            } catch (IOException e) {

            }
            searchBookService.getBooksByContent(keyword.toLowerCase()).forEach((key, value) -> {
                int nbOfKeywords = (booksIdNbOfKeywords.containsKey(key)) ? booksIdNbOfKeywords.get(key) + 1 : 1;
                booksIdNbOfKeywords.put(key, nbOfKeywords);
            });
        });

        List<Book> books= searchBookService.sortBooksByCloseness(booksIdNbOfKeywords);
        Instant finish = Instant.now(); // test
        long timeElapsed = Duration.between(start, finish).toMillis(); // test
        System.out.println("time for treating \"search by content request\" (ms): " + timeElapsed); // test
        return ResponseEntity.ok(books);
    }


    @PostMapping(value = "/searchbyregex")
    /*
    @GetMapping(value = "/searchbyregex")
    public ResponseEntity<List<Book>> searchBookByRegex(@NotNull @NotBlank @RequestParam(name="regex", required = true) String input) {

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
    }*/

    public ResponseEntity<List<Book>> searchBookByRegex(@RequestBody Map<String,String >requestBody) {
        String input=requestBody.get("regex");

        String[] keywords = input.split("\\s+");
        HashMap<Integer, Integer> booksIdNbOfKeywords = new HashMap<>();
        Arrays.asList(keywords).forEach(keyword -> {
            try {
                searchBookService.getBooksByRegex(keyword.toLowerCase()).forEach((key, value) -> {
                    int nbOfKeywords = (booksIdNbOfKeywords.containsKey(key)) ? booksIdNbOfKeywords.get(key) + 1 : 1;
                    booksIdNbOfKeywords.put(key, nbOfKeywords);
                });
            } catch (IOException e) {

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        List<Book> books= searchBookService.sortBooksByCloseness(booksIdNbOfKeywords);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/searchbytitle/{title}")
    @ResponseBody
    public ResponseEntity<List<Book>> searchBookByTitle(@PathVariable String title)  {

        String[] keywords = title.split("\\s+");
        HashMap<Integer, Integer> booksIdNbOfKeywords = new HashMap<>();
        Arrays.asList(keywords).forEach(k -> {
            searchBookService.getBooksByTitle(k.toLowerCase()).forEach(id -> {
                int nbOfKeywords = (booksIdNbOfKeywords.containsKey(id)) ? booksIdNbOfKeywords.get(id) + 1 : 1;
                booksIdNbOfKeywords.put(id, nbOfKeywords);
            });
        });

        List<Book> books= searchBookService.sortBooksByCloseness(booksIdNbOfKeywords);
        return ResponseEntity.ok(books);
    }


    @GetMapping("/searchbyauthor/{author}")
    @ResponseBody
    public ResponseEntity<List<Book>> searchBookByAuthor(@PathVariable String author)  {

        String[] keywords = author.split("\\s+");
        HashMap<Integer, Integer> booksIdNbOfKeywords = new HashMap<>();
        Arrays.asList(keywords).forEach(k -> {
            searchBookService.getBooksByAuthor(k.toLowerCase()).forEach(id -> {
                int nbOfKeywords = (booksIdNbOfKeywords.containsKey(id)) ? booksIdNbOfKeywords.get(id) + 1 : 1;
                booksIdNbOfKeywords.put(id, nbOfKeywords);
            });
        });
        List<Book> books= searchBookService.sortBooksByCloseness(booksIdNbOfKeywords);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/gettopbooks/")
    @ResponseBody
    public ResponseEntity<List<Book>> getTopBooks() {

        List<Book> books= searchBookService.getTop20Books();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/getbook/{id}")
    @ResponseBody
    public ResponseEntity<Book> searchBookById(@PathVariable String id)  {

        Book book= searchBookService.getBookById(Integer.parseInt(id));
        return ResponseEntity.ok(book);

    }

    @GetMapping("/suggestions/{id}")
    @ResponseBody
    public ResponseEntity<List<Book>> getSuggestions(@PathVariable String id)  {
        List<Book> books = new ArrayList<>();
        SortedSet<Map.Entry<Integer, Float>> sortedEntries = bookGraph.booksSortedByDistance(Integer.valueOf(id));
        sortedEntries.forEach(e -> books.add(searchBookService.getBookById(e.getKey())));
//        for(Book book:books){   // DEBUG
//            System.out.println("distance: " + bookGraph.getAdjacencyMatrix().get(1513).get(book.getId()));
//        }
        return ResponseEntity.ok(books);
    }


    @GetMapping("/suggestionsfromresults/{idList}")
    @ResponseBody
    public ResponseEntity<List<Book>> getSuggestionsFromResults(@PathVariable String idList)  {
        List<Book> books = new ArrayList<>();
        List<String> idsString = Arrays.asList(idList.split("\\s+"));
        List<Integer> idsInt = idsString.stream().map(Integer::parseInt).collect(Collectors.toList());
        SortedSet<Map.Entry<Integer, Float>> closestFromBooks = bookGraph.closestFromBooks(idsInt);

        closestFromBooks.forEach(e -> books.add(searchBookService.getBookById(e.getKey())));
//        for(Book book:books){   // DEBUG
//            System.out.println("distance: " + bookGraph.getAdjacencyMatrix().get(1513).get(book.getId()));
//        }
        return ResponseEntity.ok(books);
    }
}
