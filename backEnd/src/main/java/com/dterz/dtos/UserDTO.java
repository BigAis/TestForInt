package com.dterz.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private long id;

    private String userName;

    private String pass;

    private String fistName;

    private String surName;

    private String comments;
    
    private Integer age;

    @JsonProperty("superAdmin")
    private boolean superAdmin;
}