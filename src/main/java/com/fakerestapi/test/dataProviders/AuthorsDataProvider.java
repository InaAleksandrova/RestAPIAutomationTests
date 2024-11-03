package com.fakerestapi.test.dataProviders;

import org.apache.http.HttpStatus;
import org.testng.annotations.DataProvider;

public class AuthorsDataProvider {

    @DataProvider(name = "createAuthorWithInvalidIds")
    public static Object[][] createAuthorWithInvalidIds() {
        return new Object[][] {
                {-1, HttpStatus.SC_BAD_REQUEST, "Negative number for id is not accepted"},
                {null, HttpStatus.SC_BAD_REQUEST, "Null value for id is not accepted"},
                {"id", HttpStatus.SC_BAD_REQUEST, "String value for id is not accepted"}
        };
    }

    @DataProvider(name = "updateAuthorWithInvalidIds")
    public static Object[][] updateAuthorWithInvalidIds() {
        return new Object[][] {
                {-1, HttpStatus.SC_BAD_REQUEST, "Negative number for id is not accepted"},
                {"id", HttpStatus.SC_BAD_REQUEST, "String value for id is not accepted"}
        };
    }

}
