package com.fakerestapi.test.utils;

import com.fakerestapi.test.clients.AuthorClient;
import com.fakerestapi.test.models.Author;
import com.github.javafaker.Faker;

import java.util.Random;

public class AuthorHelper {

    private static final Faker faker = new Faker();
    private final AuthorClient authorClient = new AuthorClient();
    private final Random random = new Random();

    public Author createAuthorWithFakeData() {
        return Author.builder()
                .id(faker.number().numberBetween(1000, 2000))
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .idBook(faker.number().numberBetween(1, 1000))
                .build();
    }

    public Author createAuthorEmptyData() {
        return Author.builder()
                .id(0)
                .idBook(0)
                .firstName(null)
                .lastName(null)
                .build();
    }

    public int getRandomAuthorId() {
        return random.nextInt(authorClient.getAllAuthors().jsonPath().getList("id", Integer.class).size());
    }
}
