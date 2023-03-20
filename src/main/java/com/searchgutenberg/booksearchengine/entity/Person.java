package com.searchgutenberg.booksearchengine.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Person implements Serializable {
    private String name;
    private int birth_year;
    private int death_year;
}
