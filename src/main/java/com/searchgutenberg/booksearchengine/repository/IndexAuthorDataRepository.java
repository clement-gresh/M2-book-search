package com.searchgutenberg.booksearchengine.repository;

import com.searchgutenberg.booksearchengine.entity.IndexAuthorData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IndexAuthorDataRepository extends MongoRepository<IndexAuthorData,String> {

}
