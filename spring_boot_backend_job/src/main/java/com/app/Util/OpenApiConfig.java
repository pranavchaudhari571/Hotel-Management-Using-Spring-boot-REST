package com.app.Util;

import java.util.*;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.ExternalDocumentation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        String securitySchemeName = "BearerAuth";

        return new OpenAPI()
                .info(apiInfo())
                .tags(apiTags())
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(apiComponents(securitySchemeName))
                .externalDocs(externalDocs());
    }

    private Info apiInfo() {
        return new Info()
                .title("Hotel Management API")
                .description("Comprehensive documentation for the Hotel Management API.")
                .version("1.0")
                .termsOfService("https://www.goibigo.com/terms")
                .contact(new Contact()
                        .name("Hotel Support")
                        .url("https://www.goibigo.com/contact")
                        .email("support@goibigo.com")
                );
    }

    private List<Tag> apiTags() {
        return Arrays.asList(
                new Tag()
                        .name("Hotel Management")
                        .description("Operations related to hotel management, including rooms, bookings, and customers."),
                new Tag()
                        .name("User Management")
                        .description("Operations for user registration, authentication, and role management.")
        );
    }

    private Components apiComponents(String securitySchemeName) {
        return new Components()
                .addSecuritySchemes(securitySchemeName, securityScheme(securitySchemeName))
                .addResponses("200", apiResponse("Successful operation"))
                .addResponses("400", apiResponse("Bad Request"))
                .addResponses("401", apiResponse("Unauthorized"))
                .addResponses("404", apiResponse("Resource not found"))
                .addResponses("500", apiResponse("Internal Server Error"))
                .addParameters("X-Request-ID", requestIDParameter())
                .addSchemas("Room", roomSchema());
    }

    private SecurityScheme securityScheme(String securitySchemeName) {
        return new SecurityScheme()
                .name(securitySchemeName)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT token to authenticate users");
    }

    private ApiResponse apiResponse(String description) {
        return new ApiResponse().description(description);
    }

    private Parameter requestIDParameter() {
        return new Parameter()
                .in("header")
                .name("X-Request-ID")
                .description("Unique request identifier for tracking purposes.")
                .required(true)
                .schema(new StringSchema());
    }

    private Schema<?> roomSchema() {
        return new Schema<>()
                .type("object")
                .properties(Map.of(
                        "id", new IntegerSchema(),
                        "name", new StringSchema(),
                        "status", new StringSchema()._enum(Arrays.asList("available", "booked"))
                ));
    }

    private ExternalDocumentation externalDocs() {
        return new ExternalDocumentation()
                .description("Find out more about the Hotel Management API")
                .url("https://www.goibigo.com/docs");
    }
}
