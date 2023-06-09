package com.searchgutenberg.booksearchengine.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.concurrent.ConcurrentHashMap;

@Data
@Document("IndexTable")
@AllArgsConstructor
public class IndexTableData {
    @Id
    private String token;
    private ConcurrentHashMap<Integer,Integer> bookIdsKeyFrequence;




}
