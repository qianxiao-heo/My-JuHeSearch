package com.search.springbootinit.model.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Image implements Serializable {

    private String title;

    private String url;

    private String tUrl;

    private static final long serialVersionUID = 1L;
}
