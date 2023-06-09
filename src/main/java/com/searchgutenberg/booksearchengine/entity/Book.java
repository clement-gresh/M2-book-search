package com.searchgutenberg.booksearchengine.entity;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Document("library")
public class Book implements Serializable {
    @Id
    private int id;
    private String title;
    private Format formats;
    // URL of book content
    private String text;
    // URL of image
    private String image;
    private ArrayList<String> subjects;
    private ArrayList<String> bookshelves;
    private ArrayList<Person> authors;
    private ArrayList<Person> translators;
    private ArrayList<String> languages;

}
