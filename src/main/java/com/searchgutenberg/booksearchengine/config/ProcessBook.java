package com.searchgutenberg.booksearchengine.config;

import com.searchgutenberg.booksearchengine.entity.Book;
import com.searchgutenberg.booksearchengine.entity.Format;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.concurrent.Future;

@Service
@Slf4j
public class ProcessBook {
    @Autowired
    @Qualifier("restTemplate")
    private RestTemplate restTemplate;

    /**
     * set image's and text's URL for a book object
     * download the text to files in /books/id.txt
     * @param book a book object to be completed init
     * @return Future<Entry<Id of Book, Book>> a Future Task of work on the book object
     */
    @Async("asyncTaskExecutor")
    public Future<Book> getBook(Book book){
        Format format = book.getFormats();
        String textURL = getTextURL(format);
        if (textURL == null)
            return new AsyncResult<>(null);
        if (format.getImage() != null){
            book.setImage(format.getImage().replace("small", "medium"));
        }
        book.setText(textURL);
        try {
            String text = restTemplate.getForObject(textURL, String.class);
            if (text == null)
                return new AsyncResult<>(null);
            if (text.split("\\s+").length > 10000){
                PrintWriter printWriter = new PrintWriter(new FileOutputStream("books/" + book.getId() + ".txt"));
                printWriter.println(text);
                printWriter.flush();
                printWriter.close();
                return new AsyncResult<>(book);
            }else {
                return new AsyncResult<>(null);
            }
        }catch (HttpClientErrorException | FileNotFoundException ignored){
        }
        return new AsyncResult<>(null);
    }

    /**
     * get at least one valid URL to download the text of a book
     * @param format the URLs given by "gutendex" api
     * @return a URL to download txt
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
}
