package com.searchgutenberg.booksearchengine.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("TitleIndex")
@AllArgsConstructor
public class IndexTitleData {
    String title;
    Integer bookId;
}
