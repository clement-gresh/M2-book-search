package com.searchgutenberg.booksearchengine.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Document("BookGraph")
@AllArgsConstructor
public class BookGraphData {
    @Id
    private Integer bookId;
    private Map<Integer, Float> jaccardDistance;
}