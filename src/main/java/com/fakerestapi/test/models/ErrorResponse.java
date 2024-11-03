package com.fakerestapi.test.models;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ErrorResponse {

    private String type;
    private String title;
    private int status;
    private String traceId;
    private Map<String, List<String>> errors;
}
