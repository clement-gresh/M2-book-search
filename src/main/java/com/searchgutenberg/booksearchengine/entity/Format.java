package com.searchgutenberg.booksearchengine.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Format implements Serializable {
    @JsonProperty(value = "image/jpeg")
    private String image;
    @JsonProperty(value = "text/html")
    private String html;
    @JsonProperty(value = "text/plain; charset=utf-8")
    private String textUtf;
    @JsonProperty(value = "text/plain; charset=us-ascii")
    private String textAscii;
    @JsonProperty(value = "text/plain")
    private String textPlain;

}
