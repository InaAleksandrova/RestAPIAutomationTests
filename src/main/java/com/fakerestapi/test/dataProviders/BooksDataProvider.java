package com.fakerestapi.test.dataProviders;

import org.apache.http.HttpStatus;
import org.testng.annotations.DataProvider;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BooksDataProvider {

    @DataProvider(name = "createBookWithInvalidIds")
    public static Object[][] createBookWithInvalidIds() {
        return new Object[][] {
                {-1, HttpStatus.SC_BAD_REQUEST, "Negative number for id is not accepted"},
                {null, HttpStatus.SC_BAD_REQUEST, "Null value for id is not accepted"},
                {"id", HttpStatus.SC_BAD_REQUEST, "String value for id is not accepted"}
        };
    }

    @DataProvider(name = "createBookWithInvalidPageCountValue")
    public static Object[][] createBookWithInvalidPageCountValue() {
        return new Object[][] {
                {-1, HttpStatus.SC_BAD_REQUEST, "Negative number for page count is not accepted"},
                {null, HttpStatus.SC_BAD_REQUEST, "Null value for page count not accepted"},
                {"page count", HttpStatus.SC_BAD_REQUEST, "String value for page count is not accepted"}
        };
    }

    @DataProvider(name = "createBookWithInvalidPublishDate")
    public static Object[][] createBookWithInvalidPublishDate() {
        return new Object[][] {
                {"date", HttpStatus.SC_BAD_REQUEST, "Publish date field accepts only date format values"},
                {LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), HttpStatus.SC_BAD_REQUEST, "Publish date accepts date format of pattern \"yyyy-MM-dd'T'HH:mm:ss\""}
        };
    }
}
