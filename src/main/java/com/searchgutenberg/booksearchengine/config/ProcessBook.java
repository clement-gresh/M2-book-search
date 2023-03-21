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

import java.io.*;
import java.util.*;
import java.util.concurrent.Future;

@Service
@Slf4j
public class ProcessBook {
    @Autowired
    @Qualifier("restTemplate")
    private RestTemplate restTemplate;

    private ConcurrentHashMap<String, <ArrayList<String>>> tupleIndex;

    /**
     * set image's and text's URL for a book object
     * download the text to files in /books/id.txt
     * @param book a book object to be completed init
     * @return Future<Entry<Id of Book, Book>> a Future Task of work on the book object
     */

    public void processBook(Book book){
        Format format = book.getFormats();
        String textURL = getTextURL(format);
        if (textURL == null)
            return;
        /*
        if (format.getImage() != null){
            book.setImage(format.getImage().replace("small", "medium"));
        }*/
        book.setText(textURL);

        String text = restTemplate.getForObject(textURL, String.class);
        if (text == null)
            return;
        System.out.println(text);

        return;
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

    public static HashSet<String> splitBookWords(String content, String language) throws IOException {
        Set<Character> alphabet = getAlphabet(language);
        HashSet<String> allWords = new HashSet<>();
        StringBuilder currentWord = new StringBuilder();
        for (int i = 0; i < content.length(); i++) { //browsing the text, char by char
            char c = Character.toLowerCase(content.charAt(i));
            if (alphabet.contains(c)) { //if current char is in the alphabet (which is not a space, a point, etc)
                currentWord.append(c); //it is the next char of the current word
            } else {                   //else we have a word!
                String wordString = currentWord.toString();
                currentWord = new StringBuilder();
                if (!wordString.isEmpty() ) { //if the word is not empty
                    allWords.add(wordString);
                }
            }
        }
        String wordString = currentWord.toString();
        if (!wordString.isEmpty() ) { //if the word is not empty
            allWords.add(wordString);
        }
        return allWords;
    }

    public static Set<Character> getAlphabet(String language){
        Set<Character> alphabet;
        if(language.equals("EN")){
           alphabet = new HashSet<>(Arrays.asList('a','b','c','d','e','f','g','h','i','j','k','l','m','n'
                    ,'o','p','q','r','s','t','u','v','w','x','y','z','\'','’','`'));
        }else{
            alphabet = new HashSet<>(Arrays.asList('a','b','c','d','e','f','g','h','i','j','k','l','m','n'
                    ,'o','p','q','r','s','t','u','v','w','x','y','z','à','â','æ','ç','é','è','ê','ë','î','ï','ô','œ','ù','û','ü','ÿ'));
        }
        return alphabet;
    }
}
