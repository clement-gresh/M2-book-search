package com.searchgutenberg.booksearchengine.config;

import com.searchgutenberg.booksearchengine.entity.Book;
import com.searchgutenberg.booksearchengine.entity.GutendexEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Component
@Slf4j
@EnableAsync
public class DownloadBooks {
    @Autowired
    private ProcessBooks processBooks;

    /**
     * loading books from Gutenberg project, or from file
     * @param restTemplate a modified RestTemplate
     * @param httpHeaders HttpHeaders witt accept JSON
     * @return ArrayList of Book
     */
    @Bean
    public Vector<Book> library(RestTemplate httpRequest, HttpEntity<String> httpEntity) throws IOException, ClassNotFoundException {
        Vector<Book> library = new Vector<Book>();

        // if the books.ser file already exists, load the information of books into a map
        if (new File("downloadBooks.ser").exists()){
            log.info("Loading books from file to memory...");
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("downloadBooks.ser"));
            library = (Vector<Book>) inputStream.readObject();
            inputStream.close();
            return library;
        }

        // else, download the 1664 books information into a .ser file and download the text of each book into /books/<id>.txt
        log.info("First time use, Downloading 1664 books ...");
        ResponseEntity<GutendexEntity> result = httpRequest.exchange("http://gutendex.com/books?mime_type=text&languages=en", HttpMethod.GET, httpEntity, GutendexEntity.class);
        ArrayList<Book> allBooks;
        while (library.size() < 1664){
            allBooks = Objects.requireNonNull(result.getBody()).getResults();
            allBooks = allBooks.stream().filter(Objects::nonNull).collect(Collectors.toCollection(ArrayList::new));
            List<Future<Book>> futures = new ArrayList<>();
            for (Book book: allBooks){
                futures.add(processBooks.getBook(book));
            }
            Iterator<Future<Book>> iterator = futures.iterator();
            while (iterator.hasNext()){
                Future<Book> future = iterator.next();
                if (future.isDone()){
                    try {
                        iterator.remove();
                        Book b = future.get();
                        if (b != null)
                            library.add(b);
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                if (!iterator.hasNext()) {
                    iterator = futures.iterator();
                }
            }
            log.info("progress: " + library.size());
            String nextURL = result.getBody().getNext();
            result = httpRequest.exchange(nextURL, HttpMethod.GET, httpEntity, GutendexEntity.class);
        }
        System.out.println();

        log.info("Saving " + library.size() + " books from memory to local file...");
        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("books.ser"));
        outputStream.writeObject(library);
        outputStream.flush();
        outputStream.close();
        return library;
    }

    /**
     * separate the books library to many lists (pageable), each list contains 20 books
     * @return the PagedListHolder of books ordered by closeness centrality, each page containing 20 books
     */

    /*
    @Bean
    public PagedListHolder<Book> pagedLibrary(Map<Integer, Book> library, Map<Integer, Double> closenessCentrality){
        List<Book> books = new ArrayList<>();
        List<Integer> orderedIds = new ArrayList<>(closenessCentrality.keySet());
        for (Integer id: orderedIds) {
            books.add(library.get(id));
        }
        PagedListHolder<Book> pagedLibrary = new PagedListHolder<>(books);
        pagedLibrary.setPageSize(20);
        return pagedLibrary;
    }

    @Bean
    public List<Pair<Integer, String>> top100BooksPreview(Map<Integer, Book> library, Map<Integer, Double> closenessCentrality){
        List<Pair<Integer, String>> result = new ArrayList<>();
        Set<Integer> ids = closenessCentrality.keySet();
        int i = 0;
        for (Integer id: ids){
            result.add(new Pair<>(id, library.get(id).getTitle()));
            i++;
            if (i == 100) break;
        }
        return result;
    }
     */
}
