package com.fakerestapi.test;

import com.fakerestapi.test.clients.AuthorClient;
import com.fakerestapi.test.constants.ErrorMessagesConstants;
import com.fakerestapi.test.constants.JsonPathConstants;
import com.fakerestapi.test.dataProviders.AuthorsDataProvider;
import com.fakerestapi.test.models.Author;
import com.fakerestapi.test.utils.AuthorHelper;
import com.fakerestapi.test.validators.ErrorResponseValidator;
import com.fakerestapi.test.validators.ResponseValidator;
import com.google.gson.Gson;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static com.fakerestapi.test.constants.ErrorMessagesConstants.ERROR_MESSAGE_NOT_FOUND;
import static com.fakerestapi.test.constants.ErrorMessagesConstants.ERROR_MESSAGE_VALIDATION_ERRORS;

public class AuthorsTests extends BaseTests {

    private AuthorClient authorClient;
    private AuthorHelper authorHelper;
    private Gson gson;

    @BeforeClass
    public void setUpAuthorsTests() {
        authorClient = new AuthorClient();
        authorHelper = new AuthorHelper();
        gson = new Gson();
    }

    @Test(description = "Verify a list of all authors is displayed")
    public void getAllAuthorsTest() {
        Response response = authorClient.getAllAuthors();
        ResponseValidator.validateStatusCode(response, HttpStatus.SC_OK);
        ResponseValidator.validateListOfIdsNotEmpty(response);
    }

    @Test(description = "Verify an author can be retrieved by a valid id")
    public void getAuthorByIdTest() {
        Response allAuthorsResponse = authorClient.getAllAuthors();
        ResponseValidator.validateStatusCode(allAuthorsResponse, HttpStatus.SC_OK);
        ResponseValidator.validateListOfIdsNotEmpty(allAuthorsResponse);

        int randomId = authorHelper.getRandomAuthorId();

        Response response = authorClient.getAuthorById(randomId);
        ResponseValidator.validateStatusCode(allAuthorsResponse, HttpStatus.SC_OK);

        Author retrievedAuthor = response.as(Author.class);

        Assert.assertEquals(retrievedAuthor.getId(), randomId);
        ResponseValidator.validateFieldValue(retrievedAuthor.getId(), JsonPathConstants.AUTHOR_ID, randomId);
        ResponseValidator.validateFieldValueIsNotEmpty(retrievedAuthor.getFirstName(), "First name field should not be empty");
        ResponseValidator.validateFieldValueIsNotEmpty(retrievedAuthor.getLastName(), "Last name field should not be empty");
    }

    @Test(description = "Verify that an author cannot be retrieved using an id bigger than the authors list size")
    public void getAuthorByOutOfBoundsId() {
        List<Integer> authors = authorClient.getAllAuthors().jsonPath().getList("id", Integer.class);
        int invalidAuthorId = authors.size() + 1;

        Response response = authorClient.getAuthorById(invalidAuthorId);
        ResponseValidator.validateStatusCode(response, HttpStatus.SC_NOT_FOUND);
        ErrorResponseValidator.validateErrorMessageTitle(response, ERROR_MESSAGE_NOT_FOUND);
    }

    @Test(description = "Verify that new author with valid data can be added")
    public void addNewAuthorTest() {;
        Author newAuthor = authorHelper.createAuthorWithFakeData();
        String newAuthorJson = gson.toJson(newAuthor);

        Response postResponse = authorClient.addAuthor(newAuthorJson).then().extract().response();

        ResponseValidator.validateStatusCode(postResponse, HttpStatus.SC_OK);
        ResponseValidator.validateFieldNotNull(newAuthorJson, JsonPathConstants.AUTHOR_ID);

        Author createdAuthor = postResponse.as(Author.class);

        ResponseValidator.validateFieldValue(createdAuthor.getId(), JsonPathConstants.AUTHOR_ID, newAuthor.getId());
        ResponseValidator.validateFieldValue(createdAuthor.getIdBook(), JsonPathConstants.AUTHOR_BOOK_ID, newAuthor.getIdBook());
        ResponseValidator.validateFieldValue(createdAuthor.getFirstName(), JsonPathConstants.FIRST_NAME, newAuthor.getFirstName());
        ResponseValidator.validateFieldValue(createdAuthor.getLastName(), JsonPathConstants.LAST_NAME, newAuthor.getLastName());
    }

    @Test(description = "Verify that a new author with empty values cannot be created")
    public void addNewAuthorWithEmptyDataTest() {
        Author newAuthor = authorHelper.createAuthorEmptyData();
        String newBookJson = gson.toJson(newAuthor);

        Response response = authorClient.addAuthor(newBookJson);
        ResponseValidator.validateStatusCode(response, HttpStatus.SC_BAD_REQUEST);
        ErrorResponseValidator.validateErrorMessageTitle(response, ERROR_MESSAGE_VALIDATION_ERRORS);
    }

    @Test(description = "Verify that a new author with invalid ids cannot be added",
    dataProvider = "createAuthorWithInvalidIds",
    dataProviderClass = AuthorsDataProvider.class)
    public void addNewAuthorWithInvalidIdsTest(Object id, int expectedStatusCode, String errorMessage) {
        Author author = authorHelper.createAuthorWithFakeData();
        author.setId(id);

        String authorJson = gson.toJson(author);
        Response response = authorClient.addAuthor(authorJson);

        ResponseValidator.validateStatusCode(response, expectedStatusCode);
        ErrorResponseValidator.validateErrorMessageTitle(response, ERROR_MESSAGE_VALIDATION_ERRORS);
        ErrorResponseValidator.validateErrorResponse(response, ErrorMessagesConstants.ERROR_ID);
    }

    @Test(description = "Verify that a new author with invalid book ids cannot be added",
            dataProvider = "createAuthorWithInvalidIds",
            dataProviderClass = AuthorsDataProvider.class)
    public void addNewAuthorWithInvalidBookIdsTest(Object id, int expectedStatusCode, String errorMessage) {
        Author author = authorHelper.createAuthorWithFakeData();
        author.setIdBook(id);

        String authorJson = gson.toJson(author);
        Response response = authorClient.addAuthor(authorJson);

        ResponseValidator.validateStatusCode(response, expectedStatusCode);
        ErrorResponseValidator.validateErrorMessageTitle(response, ERROR_MESSAGE_VALIDATION_ERRORS);
        ErrorResponseValidator.validateErrorResponse(response, ErrorMessagesConstants.ERROR_ID_BOOK);
    }


    @Test(description = "Verify that an existing author data can be updated")
    public void updateAuthorById() {
        Response getAllAuthorsResponse = authorClient.getAllAuthors();
        ResponseValidator.validateStatusCode(getAllAuthorsResponse, HttpStatus.SC_OK);

        int id = authorHelper.getRandomAuthorId();

        Author author = authorHelper.createAuthorWithFakeData();
        String authorJson = gson.toJson(author);

        Response updateAuthorResponse = authorClient.updateAuthor(id, authorJson);

        Author updatedAuthor = updateAuthorResponse.as(Author.class);

        ResponseValidator.validateStatusCode(updateAuthorResponse, HttpStatus.SC_OK);
        ResponseValidator.validateFieldValue(updatedAuthor.getId(), JsonPathConstants.AUTHOR_ID, author.getId());
        ResponseValidator.validateFieldValue(updatedAuthor.getIdBook(), JsonPathConstants.AUTHOR_BOOK_ID, author.getIdBook());
        ResponseValidator.validateFieldValue(updatedAuthor.getFirstName(), JsonPathConstants.FIRST_NAME, author.getFirstName());
        ResponseValidator.validateFieldValue(updatedAuthor.getLastName(), JsonPathConstants.LAST_NAME, author.getLastName());
    }

    @Test(description = "Verify that an author with invalid ids cannot be updated",
            dataProvider = "updateAuthorWithInvalidIds",
            dataProviderClass = AuthorsDataProvider.class)
    public void updateNewAuthorWithInvalidIdsTest(Object id, int expectedStatusCode, String errorMessage) {
        Author author = authorHelper.createAuthorWithFakeData();
        author.setId(id);

        String authorJson = gson.toJson(author);
        Response response = authorClient.updateAuthor(id, authorJson);

        ResponseValidator.validateStatusCode(response, expectedStatusCode);
        ErrorResponseValidator.validateErrorMessageTitle(response, ERROR_MESSAGE_VALIDATION_ERRORS);
        ErrorResponseValidator.validateErrorResponse(response, ErrorMessagesConstants.ERROR_ID);
    }

    @Test(description = "Verify that an author with invalid book ids cannot be updated",
            dataProvider = "updateAuthorWithInvalidIds",
            dataProviderClass = AuthorsDataProvider.class)
    public void updateAuthorWithInvalidBookIdsTest(Object id, int expectedStatusCode, String errorMessage) {
        Author author = authorHelper.createAuthorWithFakeData();
        author.setIdBook(id);

        String authorJson = gson.toJson(author);
        Response response = authorClient.updateAuthor(id, authorJson);

        ResponseValidator.validateStatusCode(response, expectedStatusCode);
        ErrorResponseValidator.validateErrorMessageTitle(response, ERROR_MESSAGE_VALIDATION_ERRORS);
        ErrorResponseValidator.validateErrorResponse(response, ErrorMessagesConstants.ERROR_ID_BOOK);
    }

    @Test(description = "Delete an author")
    public void deleteAuthorById() {
        int id = authorHelper.getRandomAuthorId();
        Response response = authorClient.deleteAuthor(id);
        ResponseValidator.validateStatusCode(response, HttpStatus.SC_OK);

        Response getResponse = authorClient.getAuthorById(id);
        ResponseValidator.validateStatusCode(getResponse, HttpStatus.SC_NOT_FOUND);
    }

    @Test(description = "Verify that a author with invalid id cannot be deleted")
    public void deleteBookByInvalidIdTest() {
        int numberOutOfRange = authorClient.getAllAuthors().jsonPath().getList("id", Integer.class).size() + 1;

        Response response = authorClient.deleteAuthor(numberOutOfRange);
        ResponseValidator.validateStatusCode(response, HttpStatus.SC_NOT_FOUND);
        ErrorResponseValidator.validateErrorMessageTitle(response, ERROR_MESSAGE_NOT_FOUND);
    }

}
