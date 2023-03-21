package com.searchgutenberg.booksearchengine.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Document("bookContentMap")
public class BookContent {
    @Id
    private int id;
    private String content;


}
