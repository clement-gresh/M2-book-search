package com.searchgutenberg.booksearchengine.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.ArrayList;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GutendexEntity {
    private int count;
    private String next;
    private String previous;
    private ArrayList<Book> results;
}