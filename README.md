# Rest API automation tests

## Overview
This project is designed for automating API tests for an online bookstore using the FakeRestAPI. The project uses TestNG for structuring the test cases, RestAssured for making HTTP calls to the API, and Allure for generating comprehensive reports. Additionally, the project is configured with GitHub Actions for continuous integration and deployment (CI/CD).

***

**Project Structure**

Here is a brief overview of the folder structure in the project:
```
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com.fakerestapi.test/
│   │   │       ├── clients/
│   │   │       │   ├── AuthorClient.java
│   │   │       │   ├── BaseClient.java
│   │   │       │   └── BookClient.java
│   │   │       ├── config/
│   │   │       │   └── ApiConfig.java
│   │   │       ├── constants/
│   │   │       │   ├── ErrorMessagesConstants.java
│   │   │       │   └── JsonPathConstants.java
│   │   │       ├── dataProviders/
│   │   │       │   ├── AuthorsDataProvider.java
│   │   │       │   └── BooksDataProvider.java
│   │   │       ├── models/
│   │   │       │   ├── Author.java
│   │   │       │   ├── Book.java
│   │   │       │   └── ErrorResponse.java
│   │   │       ├── utils/
│   │   │       │   ├── AuthorHelper.java
│   │   │       │   └── BooksHelper.java
│   │   │       └── validators/
│   │   │           ├── ErrorResponseValidator.java
│   │   │           └── ResponseValidator.java
│
├── test/
│   ├── java/
│   │   └── com.fakerestapi.test/
│   │       ├── AuthorsTests.java
│   │       ├── BaseTests.java
│   │       └── BooksTests.java
│   ├── resources/
│   │   └── testng.xml
│
├── pom.xml
├── README.md
```

***

**Project Features:**
* API Test automation using TestNG and RestAssured
* Allure reports integration for visualizing test results
* CI/CD pipeline using GitHub Actions
* Validations for both success and error scenarios
* Usage of Java Lombok for model simplification

***

**Technologies Used**
* Java: Programming language used for the test implementation
* TestNG: Testing framework for organizing the test cases
* RestAssured: Library for HTTP calls and API automation
* Allure: Reporting tool for generating test execution reports
* Maven: Build tool for managing dependencies and running tests
* GitHub Actions: CI/CD for continuous integration and test execution
* Java Faker: Used for generating random test data

***

**Prerequisites**
* Java 11+: Ensure that JDK is installed on your machine.
* Maven: Make sure you have Maven installed.
* Allure: Install Allure for report generation if you plan to view reports locally.

***
**Setup**
* Step 1: Clone the repository
```
git clone https://github.com/InaAleksandrova/RestAPIAutomationTests.git
cd RestAPIAutomationTests
```
* Step 2: Install Dependencies
    * Run the following Maven command to install all dependencies:
 ```
mvn clean install
```
This will also download the necessary libraries like TestNG, RestAssured, Lombok, and Allure.

**Running the Tests**
 * To run the tests locally, you can use Maven's test command:
```
mvn clean test
```
**TestNG Suite Execution**
 * You can also run a specific suite or individual test cases via the testng.xml file:
```
mvn test -DsuiteXmlFile=testng.xml
```
***
**Allure Reporting**

**Generating Allure Reports**
1. Run the tests using Maven:
```
mvn clean test
```

2. Generate the Allure report:
```
allure serve target/allure-results
```
This will start a local server and open the Allure report in your default browser.

**CI/CD with GitHub Actions**
 * The project uses GitHub Actions for continuous integration. The configuration is defined in the .github/workflows/github-actions.yml file.

Steps:

1. On every push to the main branch, the tests will automatically execute.
2. If all tests pass, the results will be displayed under the "Actions" tab in the repository.
