package com.searchgutenberg.booksearchengine.config;

import com.searchgutenberg.booksearchengine.entity.*;
import com.searchgutenberg.booksearchengine.repository.*;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import java.util.stream.Collectors;

import static com.searchgutenberg.booksearchengine.BooksearchengineApplication.*;

@Component
@Slf4j
@EnableAsync
public class DownloadBooks {
    @Autowired
    private ProcessBook processBooks;
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookGraphRepository bookGraphRepository;

    @Autowired
    private IndexTableDataRepository indexTableDataRepository;

    @Autowired
    private IndexTitleDataRepository indexTitleDataRepository;


    @Autowired
    private IndexAuthorDataRepository indexAuthorDataRepository;

    @Autowired
    private Term2KeywordRepository term2keywordRepository;


    @Bean
    public List<Book> library(RestTemplate httpRequest, HttpEntity<String> httpEntity) throws IOException, ClassNotFoundException {

        // if book already stocked in the data base, get them
        List<Book>library=bookRepository.findAll();
        //debug change the number of the while to get the number of the book you want
        if(library.size()>=5 ){
           // if(keywordsDictionary!=null){return library;}
            List<IndexTableData> indexTable= indexTableDataRepository.findAll();
            List<IndexTitleData> TitleIndex=indexTitleDataRepository.findAll();
            List<IndexAuthorData> AuthorIndex=indexAuthorDataRepository.findAll();
            List<Term2Keyword> term2KeywordList= term2keywordRepository.findAll();
            keywordsDictionary=new ConcurrentHashMap<String,ConcurrentHashMap<Integer,Integer>>();
            booksTitle=new ConcurrentHashMap<String, Integer>();
            authorBooks=new ConcurrentHashMap<String, ConcurrentLinkedQueue<Integer>>();
            term2KeywordDictionary=new ConcurrentHashMap<>();
            for(IndexTableData line:indexTable){
               keywordsDictionary.put(line.getToken(),line.getBookIdsKeyFrequence());
            }
            for(IndexTitleData line: TitleIndex){
                booksTitle.put(line.getTitle(), line.getBookId());
            }
            for(IndexAuthorData line:AuthorIndex){
                authorBooks.put(line.getAuthor(),line.getBooksId());
            }
            for(Term2Keyword line:term2KeywordList){
                term2KeywordDictionary.put(line.getTerm(),line.getKeyword());
            }




            return library;
        }

        // else, download the 1664 books information and stock them in DB
        keywordsDictionary=new ConcurrentHashMap<String,ConcurrentHashMap<Integer,Integer>>();
        booksTitle=new ConcurrentHashMap<String, Integer>();
        authorBooks=new ConcurrentHashMap<String, ConcurrentLinkedQueue<Integer>>();
        term2KeywordDictionary=new ConcurrentHashMap<>();

        log.info("First time use, Downloading 1664 books ...");
        ResponseEntity<GutendexEntity> result = httpRequest.exchange("http://gutendex.com/books?mime_type=text&languages=en", HttpMethod.GET, httpEntity, GutendexEntity.class);
        ArrayList<Book> allBooks;
        //debug change the number of the while to get the number of the book you want
        while (library.size() < 1){
            allBooks = Objects.requireNonNull(result.getBody()).getResults();
            allBooks = allBooks.stream().filter(Objects::nonNull).collect(Collectors.toCollection(ArrayList::new));
            List<CompletableFuture<Boolean>> futures = new ArrayList<>();
            for (Book book: allBooks){
                futures.add(processBooks.processBook(book));
            }

            for (int i=0;i<futures.size();i++){
                CompletableFuture<Boolean> future=futures.get(i);
                Boolean resultat=future.join();
                if(resultat){
                    library.add(allBooks.get(i));
                    System.out.println("success load book "+allBooks.get(i).getId());
                }
            }

            // Saving Jaccard Graph in the DB
            bookGraph.getAdjacencyMatrix().forEach((key, value) -> {
                BookGraphData line = new BookGraphData(key, value);
//                System.out.println(
//                        "New book id: " + line.getBookId()
//                                + " (id: " + 1513 + ", "
//                                + line.getJaccardDistance().get(1513) + ")"
//                );
                bookGraphRepository.save(line);
            });

            log.info("progress: " + library.size());
            keywordsDictionary.forEach((token,bookIdsKeyFrequence)->{
                IndexTableData line=new IndexTableData(token,bookIdsKeyFrequence);
                indexTableDataRepository.save(line);

            });
            booksTitle.forEach( (title,id)->{
                IndexTitleData line=new IndexTitleData(title,id);
                indexTitleDataRepository.save(line);
            });
            authorBooks.forEach( (author,booksId)->{
                IndexAuthorData line=new IndexAuthorData(author,booksId);
                indexAuthorDataRepository.save(line);
            });

            term2KeywordDictionary.forEach( (term,keyword)->{
                Term2Keyword line=new Term2Keyword(term,keyword);
                term2keywordRepository.save(line);
            });


            String nextURL = result.getBody().getNext();
            result = httpRequest.exchange(nextURL, HttpMethod.GET, httpEntity, GutendexEntity.class);
        }


        log.info("Saving " + library.size() + " books from memory to local file...");
        return library;
    }


}
