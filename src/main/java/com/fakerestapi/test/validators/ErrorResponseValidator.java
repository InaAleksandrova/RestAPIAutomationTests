package com.fakerestapi.test.validators;

import com.fakerestapi.test.models.ErrorResponse;
import io.restassured.response.Response;
import org.testng.Assert;

import java.util.List;
import java.util.Map;

import static com.fakerestapi.test.constants.ErrorMessagesConstants.ERROR_MESSAGE_COULD_NOT_CONVERT_TO_SYSTEM;

public class ErrorResponseValidator {


    public static void validateErrorMessageTitle(Response response, String errorMessage) {
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        Assert.assertEquals(errorResponse.getTitle(), errorMessage, "The expected error message is not found");
    }

    public static void validateErrorResponse(Response response, String field) {
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        Map<String, List<String>> errors = errorResponse.getErrors();

        if (errors == null) {
            Assert.fail("The 'errors' map is null.");
            return;
        }

        List<String> fieldErrors = errors.get(field);
        if (fieldErrors == null || fieldErrors.isEmpty()) {
            Assert.fail(String.format("Expected '%s' errors but none were found.", field));
            return;
        }

        String errorMessage = fieldErrors.get(0);
        Assert.assertTrue(errorMessage.contains(ERROR_MESSAGE_COULD_NOT_CONVERT_TO_SYSTEM),
                String.format("The '%s' error message is not as expected: '%s'", field, errorMessage));
    }
}
