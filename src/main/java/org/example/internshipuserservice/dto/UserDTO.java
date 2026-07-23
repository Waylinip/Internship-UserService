package org.example.internshipuserservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.security.SecureRandom;
import java.time.LocalDate;

@Data
public class UserDTO {

    private Long id;

    @NotBlank
    @Size(min = 2, max = 50)
    private String name;

    @NotBlank
    @Size(min = 2, max = 50)
    private String surname;

    @NotNull
    @Past
    private LocalDate birthdate;

    @NotBlank
    @Email
    private String email;

    @NotNull
    private Boolean active;
}
