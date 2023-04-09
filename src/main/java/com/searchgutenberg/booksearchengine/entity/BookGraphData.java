package com.searchgutenberg.booksearchengine.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.concurrent.ConcurrentHashMap;

@Data
@Document("BookGraph")
@AllArgsConstructor
public class BookGraphData {
    @Id
    private Integer bookId;
    private ConcurrentHashMap<Integer, Float> jaccardDistance;
    private Float closenessCentrality;
}