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
            tokenStream = new ClassicTokenizer(Version.LUCENE_40, new StringReader(word));
            tokenStream = new PorterStemFilter(tokenStream);
            Set<String> tokens = new HashSet<String>();
            CharTermAttribute charToken = tokenStream.getAttribute(CharTermAttribute.class);
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                tokens.add(charToken.toString());
            }
            if (tokens.size() != 1) {
                return null;
            }
            String token = tokens.iterator().next();
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

            input = input.replaceAll("-+", "-0");
            input = input.replaceAll("[\\p{Punct}&&[^'-]]+", " ");
            input = input.replaceAll("(?:'(?:[tdsm]|[vr]e|ll))+\\b", "");
            tokenStream = new ClassicTokenizer(Version.LUCENE_40, new StringReader(input));
            tokenStream = new LowerCaseFilter(Version.LUCENE_40, tokenStream);
            tokenStream = new ClassicFilter(tokenStream);
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
                    KeyWord keyword = find(keywords, new KeyWord(stem.replaceAll("-0", "-")));
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
