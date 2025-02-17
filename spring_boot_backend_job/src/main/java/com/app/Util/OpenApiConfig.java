package com.app.Util;
import java.util.*;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.media.Schema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        String securitySchemeName = "BearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Hotel Management API")
                        .description("Comprehensive documentation for the Hotel Management API.")
                        .version("1.0")
                        .termsOfService("https://www.goibigo.com/terms")
                        .contact(new io.swagger.v3.oas.models.info.Contact()
                                .name("Hotel Support")
                                .url("https://www.goibigo.com/contact")
                                .email("support@goibigo.com")
                        )
                )
                .addTagsItem(new Tag()
                        .name("Hotel Management")
                        .description("Operations related to hotel management, including rooms, bookings, and customers.")
                )
                .addTagsItem(new Tag()
                        .name("User Management")
                        .description("Operations for user registration, authentication, and role management.")
                )
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token to authenticate users")
                        )
                        .addResponses("200", new ApiResponse().description("Successful operation"))
                        .addResponses("400", new ApiResponse().description("Bad Request"))
                        .addResponses("401", new ApiResponse().description("Unauthorized"))
                        .addResponses("404", new ApiResponse().description("Resource not found"))
                        .addResponses("500", new ApiResponse().description("Internal Server Error"))
                        .addParameters("X-Request-ID", new Parameter()
                                .in("header")
                                .name("X-Request-ID")
                                .description("Unique request identifier for tracking purposes.")
                                .required(true)
                                .schema(new StringSchema())
                        )
                        .addSchemas("Room", new Schema<>()
                                .type("object")
                                .addProperties("id", new IntegerSchema())
                                .addProperties("name", new StringSchema())
                                .addProperties("status", new StringSchema()._enum(Arrays.asList("available", "booked")))
                        )
                )

                .externalDocs(new ExternalDocumentation()
                        .description("Find out more about the Hotel Management API")
                        .url("https://www.goibigo.com/docs")
                );
    }
}
