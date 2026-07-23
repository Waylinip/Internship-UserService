package org.example.internshipuserservice.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UserWithCardsDTO {
    private Long id;
    private String name;
    private String surname;
    private LocalDate birthdate;
    private String email;
    private Boolean active;
    private List<PaymentCardDTO> cardList;
}
