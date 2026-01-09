package com.devops.microservices.books.model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Retention(RetentionPolicy.RUNTIME)
@ApiResponses({
        @ApiResponse(responseCode = "200", ref = "#/components/responses/200Response"),
        @ApiResponse(responseCode = "400", ref = "#/components/responses/400Response"),
        @ApiResponse(responseCode = "404", ref = "#/components/responses/404Response"),
        @ApiResponse(responseCode = "500", ref = "#/components/responses/500Response")
})
public @interface StandardResponses { }

