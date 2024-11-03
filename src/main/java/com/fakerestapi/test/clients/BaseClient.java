package com.fakerestapi.test.clients;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class BaseClient {

    public Response getAll(String endpoint) {
        return given().contentType(ContentType.JSON).when().get(endpoint);
    }

    public Response getWithPathParam(String endpoint, String paramName, Object paramValue) {
        return given().contentType(ContentType.JSON).pathParam(paramName, paramValue).when().get(endpoint).then().extract().response();
    }

    public Response post(String endpoint, String bodyJson) {
        return given().contentType(ContentType.JSON).body(bodyJson).when().post(endpoint);
    }

    public Response putWithPathParam(String endpoint, String paramName, Object paramValue, String bodyJson) {
        return given().contentType(ContentType.JSON).pathParam(paramName, paramValue).body(bodyJson).when().put(endpoint);
    }

    public Response deleteWithPathParam(String endpoint, String paramName, Object paramValue) {
        return given().contentType(ContentType.JSON).given().pathParam(paramName, paramValue).when().delete(endpoint);

    }
}
