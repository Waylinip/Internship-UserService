package org.example.internshipuserservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfileRequest {
    @NotNull
    Long authUserId;

    @NotBlank
    @Size(min = 2, max = 50)
    String name;

    @NotBlank
    @Size(min = 2, max = 50)
    String surname;

    @NotNull
    @Past
    LocalDate birthdate;

    @NotBlank
    @Email
    String email;
}
