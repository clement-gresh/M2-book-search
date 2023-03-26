package com.searchgutenberg.booksearchengine.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.concurrent.ConcurrentLinkedQueue;

@Data
@Document("AuthorIndex")
@AllArgsConstructor
public class IndexAuthorData {
    String author;
    ConcurrentLinkedQueue<Integer> booksId;


}
