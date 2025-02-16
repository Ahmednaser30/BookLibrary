package org.projects.book.bookshop.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistrationRequest {
   @NotEmpty(message = "firstName Required")
    @NotBlank(message = "firstName Required")
    private String firstName;
    @NotEmpty(message = "LastName Required")
    @NotBlank(message = "LastName Required")
   private String lastName;
    @Email
    @NotEmpty(message = "Email Required")
    @NotBlank(message = "Email Required")
    private String email;
    @NotEmpty(message = "password Required")
    @NotBlank(message = "password Required")
    @Size(min = 8,message = "password must be at least 8 characters")
    private String password;
}
