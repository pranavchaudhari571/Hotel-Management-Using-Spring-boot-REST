package com.app.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)  // Exclude null fields from JSON serialization
public class EmailMessage {

    @NotBlank(message = "Email cannot be blank")  // Ensure email is not blank
    @Email(message = "Invalid email format")  // Ensure email is in valid format
    private String email;

    @NotBlank(message = "Subject cannot be blank")  // Ensure subject is not blank
    private String subject;

    @NotBlank(message = "Body cannot be blank")  // Ensure body is not blank
    private String body;

    // Constructor for JSON deserialization
    @JsonCreator
    public EmailMessage(@JsonProperty("email") String email,
                        @JsonProperty("subject") String subject,
                        @JsonProperty("body") String body) {
        this.email = email;
        this.subject = subject;
        this.body = body;
    }

    // Optional: You can add custom getters/setters if needed, but Lombok handles them for you
}
