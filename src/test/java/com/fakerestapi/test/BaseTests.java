package com.fakerestapi.test;

import com.fakerestapi.test.config.ApiConfig;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import org.testng.annotations.BeforeClass;

public class BaseTests {

    @BeforeClass
    public void setup() {
        RestAssured.config = RestAssured.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", 5000)
                        .setParam("http.socket.timeout", 5000)
                        .setParam("http.connection-manager.timeout", 5000));
        RestAssured.baseURI = ApiConfig.BASE_URL;
    }
}
