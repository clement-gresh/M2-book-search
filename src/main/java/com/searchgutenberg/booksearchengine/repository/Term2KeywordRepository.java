package com.searchgutenberg.booksearchengine.repository;

import com.searchgutenberg.booksearchengine.entity.Term2Keyword;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface Term2KeywordRepository extends MongoRepository<Term2Keyword,String> {
}
