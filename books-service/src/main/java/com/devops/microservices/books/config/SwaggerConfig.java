package com.devops.microservices.books.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.responses.ApiResponse;

@Configuration
public class SwaggerConfig {
	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info().title("Books API").version("1.0").description("API documentation for Books API"))
				.components(new Components()
						.addResponses("200Response", new ApiResponse().description("Success").content(new Content()))
						.addResponses("400Response", new ApiResponse().description("Bad Request").content(new Content()))
						.addResponses("404Response", new ApiResponse().description("Not Found").content(new Content()))
						.addResponses("500Response", new ApiResponse().description("Internal Server Error").content(new Content())));

	}
}
