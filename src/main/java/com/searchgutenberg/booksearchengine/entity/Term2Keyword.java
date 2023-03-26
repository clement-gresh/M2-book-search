package com.searchgutenberg.booksearchengine.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("Term2Keyword")
@AllArgsConstructor
public class Term2Keyword {
   @Id
    String term;
    String keyword;
}
