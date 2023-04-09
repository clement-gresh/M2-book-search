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
import java.time.Duration;
import java.time.Instant;
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
    public List<Book> library(RestTemplate httpRequest, HttpEntity<String> httpEntity) throws IOException {

        keywordsDictionary=new ConcurrentHashMap<String,ConcurrentHashMap<Integer,Integer>>();
        booksTitle=new ConcurrentHashMap<String, Integer>();
        authorBooks=new ConcurrentHashMap<String, ConcurrentLinkedQueue<Integer>>();
        term2KeywordDictionary=new ConcurrentHashMap<>();
        bookGraph=new BookGraph();
        // if the books already stocked in the database, get them
        List<Book>library=bookRepository.findAll();

        if(library.size()>=60 ){

            List<IndexTableData> indexTable= indexTableDataRepository.findAll();
            List<IndexTitleData> TitleIndex=indexTitleDataRepository.findAll();
            List<IndexAuthorData> AuthorIndex=indexAuthorDataRepository.findAll();
            List<Term2Keyword> term2KeywordList= term2keywordRepository.findAll();
            List<BookGraphData> bookGraphList= bookGraphRepository.findAll();

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
            for(BookGraphData line: bookGraphList){
                bookGraph.addLine(line.getBookId(), line.getJaccardDistance(), line.getClosenessCentrality());
            }

            return library;
        }

        // else, download the 1664 books and stock them in DB

        log.info("Use for the first time, Downloading 1664 books ...");
        ResponseEntity<GutendexEntity> result = httpRequest.exchange("http://gutendex.com/books?mime_type=text&languages=en", HttpMethod.GET, httpEntity, GutendexEntity.class);
        ArrayList<Book> gotBooks;

        while (library.size() < 60){
            List<CompletableFuture<Boolean>> futures = new ArrayList<>();
            gotBooks = Objects.requireNonNull(result.getBody()).getResults();
            gotBooks = gotBooks.stream().filter(Objects::nonNull).collect(Collectors.toCollection(ArrayList::new));

            for (Book book: gotBooks){
                futures.add(processBooks.processBook(book));
            }
            for (int i=0;i<futures.size();i++){
                CompletableFuture<Boolean> future=futures.get(i);
                Boolean resultat=future.join();
                if(resultat){
                    library.add(gotBooks.get(i));
                    System.out.println("success load book "+gotBooks.get(i).getId());
                }
            }


            String nextURL = result.getBody().getNext();
            result = httpRequest.exchange(nextURL, HttpMethod.GET, httpEntity, GutendexEntity.class);
        }

       
        // Saving Jaccard Graph in the DB
        bookGraph.computeCloseness();;

        bookGraph.getAdjacencyMatrix().forEach((key, value) -> {
            float closenessCentrality = bookGraph.getClosenessCentrality().get(key);
            BookGraphData line = new BookGraphData(key, value, closenessCentrality);
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


        log.info("Saving " + library.size() + " books in DB");
        return library;
    }


}
