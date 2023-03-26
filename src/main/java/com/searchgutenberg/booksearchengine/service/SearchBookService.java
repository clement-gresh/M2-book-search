package com.searchgutenberg.booksearchengine.service;

import com.searchgutenberg.booksearchengine.entity.Book;
import com.searchgutenberg.booksearchengine.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.searchgutenberg.booksearchengine.BooksearchengineApplication.*;


@Service
public class SearchBookService {

    @Autowired
    private BookRepository bookRepository;
    public HashMap<Integer,Integer> getBooksByContent(String keyword){
        HashMap<Integer,Integer> bookIdsKeyFrequence=new HashMap<>();
        for(String token:keywordsDictionary.keySet()){
            if(token.contains(keyword.toLowerCase())){
                bookIdsKeyFrequence.putAll(keywordsDictionary.get(keyword));
            }
        }

         return bookIdsKeyFrequence;
    }

    public HashSet<Integer> getBooksByTitle(String input){
        HashSet<Integer> booksId = new HashSet<>();

        for (String title : booksTitle.keySet()) {
            if(title.contains(input)) {
                booksId.add(booksTitle.get(title));
            }
        }
        return booksId;
    }

    public HashSet<Integer> getBooksByAuthor(String input){
        HashSet<Integer> booksId = new HashSet<>();

        for (String author :authorBooks.keySet()) {
            if(author.contains(input)) {
                booksId.addAll(authorBooks.get(author));
            }
        }
        return booksId;
    }


    public List<Book> getSortedBooks(HashMap<Integer, Integer>bookIdsKeyFrequence, Boolean closenessFlag) {
       List<Book> books=new ArrayList<>();
        if(! closenessFlag){
            bookIdsKeyFrequence=bookIdsKeyFrequence.entrySet().stream()
                    .sorted((e1, e2) -> - (e1.getValue().compareTo(e2.getValue())))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
            for(Integer bookId:bookIdsKeyFrequence.keySet()){
                Book book=bookRepository.findBookById(bookId);
                if(book!=null){
                    books.add(book);
                }

            }
        }
       return  books;
    }

    public List<Book> getSortedBooks(HashSet<Integer>bookIds, Boolean closenessFlag) {
        List<Book> books=new ArrayList<>();
        if(! closenessFlag){
            for(Integer bookId:bookIds){
                Book book=bookRepository.findBookById(bookId);
                if(book!=null){
                    books.add(book);
                }

            }
        }
        return  books;
    }

}
