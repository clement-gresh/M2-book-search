package com.searchgutenberg.booksearchengine.repository;

import com.searchgutenberg.booksearchengine.entity.Book;
import com.searchgutenberg.booksearchengine.entity.IndexTableData;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IndexTableDataRepository extends MongoRepository<IndexTableData,String> {

}
