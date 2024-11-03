package com.fakerestapi.test.validators;

import io.restassured.response.Response;
import org.testng.Assert;

import java.util.List;

public class ResponseValidator {


    public static void validateStatusCode(Response response, int expectedStatusCode) {
        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode, expectedStatusCode,
                String.format("Expected status code: %s, but got: %s", expectedStatusCode, actualStatusCode));
    }

    public static void validateListOfIdsNotEmpty(Response response) {
        List<Integer> ids = response.jsonPath().getList("id", Integer.class);
        Assert.assertFalse(ids.isEmpty(), "List of items is empty");
    }

    public static void validateFieldValue(Object actualValue, String jsonPath, Object expectedValue) {
        Assert.assertEquals(actualValue, expectedValue,
                String.format("Expected value for  '%s': %s , but got: %s", jsonPath, expectedValue, actualValue));
    }

    public static void validateFieldNotNull(Object actualValue, String jsonPath) {
        Assert.assertNotNull(actualValue,
                String.format("The book %s should not be null.", jsonPath));
    }

    public static void validateFieldValueIsNotEmpty(Object actualValue, String jsonPath) {
        Assert.assertFalse(actualValue.toString().isEmpty(),
                String.format("Expected field '%s' is empty.", jsonPath));
    }

}