package com.fakerestapi.test;

import com.fakerestapi.test.clients.BookClient;
import com.fakerestapi.test.constants.ErrorMessagesConstants;
import com.fakerestapi.test.constants.JsonPathConstants;
import com.fakerestapi.test.dataProviders.BooksDataProvider;
import com.fakerestapi.test.models.Book;
import com.fakerestapi.test.utils.BooksHelper;
import com.fakerestapi.test.validators.ErrorResponseValidator;
import com.fakerestapi.test.validators.ResponseValidator;
import com.google.gson.Gson;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.fakerestapi.test.constants.ErrorMessagesConstants.ERROR_MESSAGE_NOT_FOUND;
import static com.fakerestapi.test.constants.ErrorMessagesConstants.ERROR_MESSAGE_VALIDATION_ERRORS;

public class BooksTests extends BaseTests {

    private BookClient bookClient;
    private BooksHelper booksHelper;
    private Gson gson;

    @BeforeClass
    public void setUpBooksTests() {
        bookClient = new BookClient();
        booksHelper = new BooksHelper();
        gson = new Gson();
    }

    @Test(description = "Verify that all books are successfully displayed")
    public void getAllBooksTest() {
        Response response = bookClient.getAllBooks();
        ResponseValidator.validateStatusCode(response, HttpStatus.SC_OK);
        ResponseValidator.validateListOfIdsNotEmpty(response);
    }

    @Test(description = "Verify that a book with valid id is displayed")
    public void getBookByValidIdTest() {
        Response allBooksResponse = bookClient.getAllBooks();
        ResponseValidator.validateStatusCode(allBooksResponse, HttpStatus.SC_OK);
        ResponseValidator.validateListOfIdsNotEmpty(allBooksResponse);

        int randomBookId = booksHelper.getRandomIdFromAllBooks();

        Response bookResponse = bookClient.getBookById(randomBookId);
        ResponseValidator.validateStatusCode(bookResponse, HttpStatus.SC_OK);

        Book retrievedBook = bookResponse.as(Book.class);

        ResponseValidator.validateFieldValue(retrievedBook.getId(), JsonPathConstants.BOOK_ID, randomBookId);
        ResponseValidator.validateFieldValueIsNotEmpty(retrievedBook.getTitle(), JsonPathConstants.TITLE);
        ResponseValidator.validateFieldValueIsNotEmpty(retrievedBook.getDescription(), JsonPathConstants.DESCRIPTION);
        ResponseValidator.validateFieldValueIsNotEmpty(retrievedBook.getPageCount(), JsonPathConstants.PAGE_COUNT);
        ResponseValidator.validateFieldValueIsNotEmpty(retrievedBook.getExcerpt(), JsonPathConstants.EXCERPT);
        ResponseValidator.validateFieldValueIsNotEmpty(retrievedBook.getPublishDate(), JsonPathConstants.PUBLISH_DATE);
    }

    @Test(description = "Verify that a book with id bigger than the total count of the ids is not displayed")
    public void getBookByOutOfRangeIdTest() {
        int invalidBookId = bookClient.getAllBooks().jsonPath().getList(JsonPathConstants.BOOK_ID, Integer.class).size() + 1;
        Response response = bookClient.getBookById(invalidBookId);
        ResponseValidator.validateStatusCode(response, HttpStatus.SC_NOT_FOUND);
        ErrorResponseValidator.validateErrorMessageTitle(response, ERROR_MESSAGE_NOT_FOUND);
    }

    @Test(description = "Verify that a new book with valid data is successfully added")
    public void addNewBookWithValidDataTest() {
        Book newBook = booksHelper.createBookWithFakeData();
        String newBookJson = gson.toJson(newBook);

        Response postResponse = bookClient.addBook(newBookJson);
        ResponseValidator.validateStatusCode(postResponse, HttpStatus.SC_OK);
        ResponseValidator.validateFieldNotNull(newBookJson, JsonPathConstants.BOOK_ID);

        Book createdBook = postResponse.as(Book.class);

        ResponseValidator.validateFieldValue(createdBook.getId(), JsonPathConstants.BOOK_ID, newBook.getId());
        ResponseValidator.validateFieldValue(createdBook.getTitle(), JsonPathConstants.TITLE, newBook.getTitle());
        ResponseValidator.validateFieldValue(createdBook.getDescription(), JsonPathConstants.DESCRIPTION, newBook.getDescription());
        ResponseValidator.validateFieldValue(createdBook.getPageCount(), JsonPathConstants.PAGE_COUNT, newBook.getPageCount());
        ResponseValidator.validateFieldValue(createdBook.getExcerpt(), JsonPathConstants.EXCERPT, newBook.getExcerpt());
        ResponseValidator.validateFieldValue(createdBook.getPublishDate(), JsonPathConstants.PUBLISH_DATE, newBook.getPublishDate());
    }

    @Test(description = "Verify that a new book with empty values cannot be created")
    public void addNewBookWithEmptyDataTest() {
        Book newBook = booksHelper.createBookWithEmptyData();
        String newBookJson = gson.toJson(newBook);

        Response response = bookClient.addBook(newBookJson);
        ResponseValidator.validateStatusCode(response, HttpStatus.SC_BAD_REQUEST);
        ErrorResponseValidator.validateErrorMessageTitle(response, ERROR_MESSAGE_VALIDATION_ERRORS);
    }

    @Test(description = "Verify that a new book with different invalid values for id cannot be added ",
            dataProvider = "createBookWithInvalidIds",
            dataProviderClass = BooksDataProvider.class)
    public void addNewBookWithInvalidIdsTest(Object id, int expectedStatusCode, String errorMessage) {
        Book book = booksHelper.createBookWithFakeData();
        book.setId(id);

        String bookJson = gson.toJson(book);
        Response response = bookClient.addBook(bookJson);

        ResponseValidator.validateStatusCode(response, expectedStatusCode);
        ErrorResponseValidator.validateErrorMessageTitle(response, ERROR_MESSAGE_VALIDATION_ERRORS);
        ErrorResponseValidator.validateErrorResponse(response, ErrorMessagesConstants.ERROR_ID);
    }


    @Test(description = "Verify that a new book with different invalid values for page count cannot be added ",
            dataProvider = "createBookWithInvalidPageCountValue",
            dataProviderClass = BooksDataProvider.class)
    public void addNewBookWithInvalidPageCountTest(Object pageCount, int expectedStatusCode, String errorMessage) {
        Book book = booksHelper.createBookWithFakeData();
        book.setPageCount(pageCount);

        String bookJson = gson.toJson(book);
        Response response = bookClient.addBook(bookJson).then().extract().response();

        ResponseValidator.validateStatusCode(response, expectedStatusCode);
        ErrorResponseValidator.validateErrorMessageTitle(response, ERROR_MESSAGE_VALIDATION_ERRORS);
        ErrorResponseValidator.validateErrorResponse(response, ErrorMessagesConstants.ERROR_PAGE_COUNT);
    }

    @Test(description = "Verify that a new book with different invalid values for publish date cannot be added",
            dataProvider = "createBookWithInvalidPublishDate",
            dataProviderClass = BooksDataProvider.class)
    public void addNewBookWithInvalidPublishDatesTest(String publishDate, int expectedStatusCode, String errorMessage) {
        Book book = booksHelper.createBookWithFakeData();
        book.setPublishDate(publishDate);

        String bookJson = gson.toJson(book);
        Response response = bookClient.addBook(bookJson).then().extract().response();

        ResponseValidator.validateStatusCode(response, expectedStatusCode);
        ErrorResponseValidator.validateErrorMessageTitle(response, ERROR_MESSAGE_VALIDATION_ERRORS);
        ErrorResponseValidator.validateErrorResponse(response, ErrorMessagesConstants.ERROR_PUBLISH_DATE);
    }

    @Test(description = "Update an existing book with valid id")
    public void updateBookByValidIdTest() {
        Response getAllBooksResponse = bookClient.getAllBooks();
        ResponseValidator.validateStatusCode(getAllBooksResponse, 200);

        int id = booksHelper.getRandomIdFromAllBooks();

        Response response = bookClient.getBookById(id);
        Book newBook = response.as(Book.class);

        Book book = booksHelper.createBookWithFakeData();
        String bookJson = gson.toJson(book);

        Response updateBookResponse = bookClient.updateBook(id, bookJson);

        Book updatedBook = updateBookResponse.as(Book.class);

        ResponseValidator.validateStatusCode(updateBookResponse, HttpStatus.SC_OK);
        ResponseValidator.validateFieldValue(updatedBook.getId(), JsonPathConstants.BOOK_ID, newBook.getId());
        ResponseValidator.validateFieldValue(updatedBook.getTitle(), JsonPathConstants.TITLE, newBook.getTitle());
        ResponseValidator.validateFieldValue(updatedBook.getDescription(), JsonPathConstants.DESCRIPTION, newBook.getDescription());
        ResponseValidator.validateFieldValue(updatedBook.getPageCount(), JsonPathConstants.PAGE_COUNT, newBook.getPageCount());
        ResponseValidator.validateFieldValue(updatedBook.getExcerpt(), JsonPathConstants.EXCERPT, newBook.getExcerpt());
        ResponseValidator.validateFieldValue(updatedBook.getPublishDate(), JsonPathConstants.PUBLISH_DATE, newBook.getPublishDate());
    }

    @Test(description = "Verify that a book with invalid values for id cannot be updated",
            dataProvider = "createBookWithInvalidIds",
            dataProviderClass = BooksDataProvider.class)
    public void updateBookWithInvalidIdsTests(Object id, int expectedStatusCode, String errorMessage) {
        int idForUpdate = booksHelper.getRandomIdFromAllBooks();

        Book book = booksHelper.createBookWithFakeData();
        book.setId(id);

        String bookJson = gson.toJson(book);
        Response response = bookClient.updateBook(idForUpdate, bookJson).then().extract().response();

        ResponseValidator.validateStatusCode(response, expectedStatusCode);
        ErrorResponseValidator.validateErrorMessageTitle(response, ERROR_MESSAGE_VALIDATION_ERRORS);
        ErrorResponseValidator.validateErrorResponse(response, ErrorMessagesConstants.ERROR_ID);
    }

    @Test(description = "Verify that a book with invalid values for page count cannot be updated",
            dataProvider = "createBookWithInvalidPageCountValue",
            dataProviderClass = BooksDataProvider.class)
    public void updateBookWithInvalidPageCountTests(Object pageCount, int expectedStatusCode, String errorMessage) {
        Response getAllBooksResponse = bookClient.getAllBooks();
        ResponseValidator.validateStatusCode(getAllBooksResponse, HttpStatus.SC_OK);

        int id = booksHelper.getRandomIdFromAllBooks();

        Book book = booksHelper.createBookWithFakeData();
        book.setPageCount(pageCount);

        String bookJson = gson.toJson(book);
        Response response = bookClient.updateBook(id, bookJson).then().extract().response();

        ResponseValidator.validateStatusCode(response, expectedStatusCode);
        ErrorResponseValidator.validateErrorMessageTitle(response, ERROR_MESSAGE_VALIDATION_ERRORS);
        ErrorResponseValidator.validateErrorResponse(response, ErrorMessagesConstants.ERROR_PAGE_COUNT);
    }

    @Test(description = "Verify that a book with different invalid values for page count cannot be updated",
            dataProvider = "createBookWithInvalidPublishDate",
            dataProviderClass = BooksDataProvider.class)
    public void updateBookWithInvalidPublishDatesTests(String publishDate, int expectedStatusCode, String errorMessage) {
        Response getAllBooksResponse = bookClient.getAllBooks();
        ResponseValidator.validateStatusCode(getAllBooksResponse, HttpStatus.SC_OK);

        int id = booksHelper.getRandomIdFromAllBooks();

        Book book = booksHelper.createBookWithFakeData();
        book.setPublishDate(publishDate);

        String bookJson = gson.toJson(book);
        Response response = bookClient.updateBook(id, bookJson);

        ResponseValidator.validateStatusCode(response, expectedStatusCode);
        ErrorResponseValidator.validateErrorMessageTitle(response, ERROR_MESSAGE_VALIDATION_ERRORS);
        ErrorResponseValidator.validateErrorResponse(response, ErrorMessagesConstants.ERROR_PUBLISH_DATE);
    }

    @Test(description = "Verify that a book is deleted after using an existing id")
    public void deleteBookByIdTest() {
        int id = booksHelper.getRandomIdFromAllBooks();
        Response responseDelete = bookClient.deleteBook(id);
        ResponseValidator.validateStatusCode(responseDelete, HttpStatus.SC_OK);

        Response responseGet = bookClient.getBookById(id);
        ResponseValidator.validateStatusCode(responseGet, HttpStatus.SC_NOT_FOUND);
        ErrorResponseValidator.validateErrorMessageTitle(responseGet, ERROR_MESSAGE_NOT_FOUND);
    }

    @Test(description = "Verify that a book with invalid id cannot be deleted")
    public void deleteBookByInvalidIdTest() {
        int numberOutOfRange = bookClient.getAllBooks().jsonPath().getList("id", Integer.class).size() + 1;

        Response response = bookClient.deleteBook(numberOutOfRange);
        ResponseValidator.validateStatusCode(response, HttpStatus.SC_NOT_FOUND);
        ErrorResponseValidator.validateErrorMessageTitle(response, ERROR_MESSAGE_NOT_FOUND);
    }
}
