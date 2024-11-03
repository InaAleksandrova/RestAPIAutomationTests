package com.fakerestapi.test.clients;

import com.fakerestapi.test.config.ApiConfig;
import com.fakerestapi.test.constants.JsonPathConstants;
import io.restassured.response.Response;

public class AuthorClient extends BaseClient {

    public Response getAllAuthors() {
        return getAll(ApiConfig.AUTHORS);
    }

    public Response getAuthorById(Object id) {
        return getWithPathParam(ApiConfig.AUTHOR_BY_ID, JsonPathConstants.AUTHOR_ID, id);
    }

    public Response addAuthor(String authorJson) {
        return post(ApiConfig.AUTHORS, authorJson);
    }

    public Response updateAuthor(Object id, String authorJson) {
        return putWithPathParam(ApiConfig.AUTHOR_BY_ID, JsonPathConstants.AUTHOR_ID, id, authorJson);
    }

    public Response deleteAuthor(Object id) {
        return deleteWithPathParam(ApiConfig.AUTHOR_BY_ID, JsonPathConstants.AUTHOR_ID, id);
    }
}
