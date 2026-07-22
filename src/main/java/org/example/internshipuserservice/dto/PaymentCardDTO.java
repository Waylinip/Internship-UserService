package org.example.internshipuserservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PaymentCardDTO {

    private static final String CARD_NUMBER_REGEX = "\\d{16}";

    private Long id;

    private Long userId;

    @NotBlank
    @Pattern(regexp = CARD_NUMBER_REGEX)
    private String number;

    @NotBlank
    @Size(max = 255)
    private String holder;

    @NotNull
    @FutureOrPresent
    private LocalDate expirationDate;

    @NotNull
    private Boolean active;
}
