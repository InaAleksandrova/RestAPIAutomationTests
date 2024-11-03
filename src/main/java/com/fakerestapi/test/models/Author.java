package com.fakerestapi.test.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Author {

    private Object id;
    private Object idBook;
    private String firstName;
    private String lastName;
}