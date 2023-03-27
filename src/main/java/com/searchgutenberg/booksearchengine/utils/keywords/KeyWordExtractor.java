package com.searchgutenberg.booksearchengine.utils.keywords;


import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.ClassicFilter;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.core.StopFilter;


import java.io.*;
import java.util.*;
import java.util.stream.Collectors;




public class KeyWordExtractor {

    public static String tokenizer(String word) throws IOException {

        TokenStream tokenStream = null;
        try {
            // tokenize the input
            tokenStream = new ClassicTokenizer(Version.LUCENE_40, new StringReader(word));
            // get the stem of the input as token
            tokenStream = new PorterStemFilter(tokenStream);
            // Add each token in a set, so that duplicates are removed
            Set<String> tokens = new HashSet<String>();
            CharTermAttribute charToken = tokenStream.getAttribute(CharTermAttribute.class);
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                tokens.add(charToken.toString());
            }
            // if no stem or 2+ stems have been found, return null
            if (tokens.size() != 1) {
                return null;
            }
            String token = tokens.iterator().next();
            // if the stem has non-alphanumerical chars, return null
            if (!token.matches("[a-zA-Z0-9-]+")) {
                return null;
            }

            return token;

        } finally {
            if (tokenStream != null) {
                tokenStream.close();
            }
        }
    }

    /**
     * Get the keyword list  of a book using english stop words
     */
    public static List<KeyWord> getBookKeyWords(String input) throws IOException {

        TokenStream tokenStream = null;
        try {

            // Hack to keep dashed words (e.g. "non-specific" rather than "non" and
            // "specific")
            input = input.replaceAll("-+", "-0");

            // Replace any punctuation char but apostrophes and dashes by a space
            input = input.replaceAll("[\\p{Punct}&&[^'-]]+", " ");

            // Replace most common english contractions
            input = input.replaceAll("(?:'(?:[tdsm]|[vr]e|ll))+\\b", "");


            // tokenize input
            tokenStream = new ClassicTokenizer(Version.LUCENE_40, new StringReader(input));

            // to lowercase
            tokenStream = new LowerCaseFilter(Version.LUCENE_40, tokenStream);

            // remove dots from acronyms (and "'s" but already done manually above)
            tokenStream = new ClassicFilter(tokenStream);

            // convert any char to ASCII
            tokenStream = new ASCIIFoldingFilter(tokenStream);

            // remove the english stop words (common words which don't have special meaning )
            Scanner s = new Scanner(new File("stopWords.txt"));
            ArrayList<String> stopWordsList = new ArrayList<>();
            while(s.hasNextLine()) {
                stopWordsList.add(s.nextLine());
            }
            s.close();

            final CharArraySet cas = new CharArraySet(Version.LUCENE_40, stopWordsList, true);
            final CharArraySet defaultSet = EnglishAnalyzer.getDefaultStopSet();
            cas.addAll(defaultSet);
            tokenStream = new StopFilter(Version.LUCENE_40, tokenStream, cas);

            List<KeyWord> keywords = new LinkedList<KeyWord>();
            CharTermAttribute token = tokenStream.getAttribute(CharTermAttribute.class);
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                String term = token.toString();

                // remove the word which are too short
                if(term.length()<3){continue;}

                String stem = tokenizer(term);

                if (stem != null) {
                    // Create the keyword or get the existing one if any
                    KeyWord keyword = find(keywords, new KeyWord(stem.replaceAll("-0", "-")));
                    // Add its corresponding initial token
                    keyword.add(term.replaceAll("-0", "-"));
                }
            }

            // sort the keyword by its frequency
            Collections.sort(keywords);
            // We keep the 50 most used keywords per book
            keywords = keywords.stream().limit(50).collect(Collectors.toList());
            return keywords;

        } finally {
            if (tokenStream != null) {
                tokenStream.close();
            }
        }

    }

    /**
     * Generic algorithm to search through a collection
     */
    public static <T> T find(Collection<T> collection, T target) {
        for (T element : collection) {
            if (element.equals(target)) {
                return element;
            }
        }
        collection.add(target);
        return target;
    }




}
