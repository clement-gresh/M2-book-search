package com.searchgutenberg.booksearchengine.repository;

import com.searchgutenberg.booksearchengine.entity.IndexTitleData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IndexTitleDataRepository extends MongoRepository<IndexTitleData,String> {

}
