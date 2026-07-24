package org.example.internshipuserservice.serviceTest;

import org.example.internshipuserservice.dto.PaymentCardDTO;
import org.example.internshipuserservice.exception.CardLimitExceededException;
import org.example.internshipuserservice.exception.NotFoundException;
import org.example.internshipuserservice.mapper.PaymentCardMapper;
import org.example.internshipuserservice.repository.PaymentCardRepo;
import org.example.internshipuserservice.repository.UserRepo;
import org.example.internshipuserservice.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;


@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentCardRepo paymentCardRepo;

    @Mock
    private PaymentCardMapper cardMapper;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void create_shouldThrowException_whenUserAlreadyHas5Cards() {
        PaymentCardDTO cardDTO = new PaymentCardDTO();
        cardDTO.setUserId(1L);

        when(paymentCardRepo.countCards(1L)).thenReturn(5L);

        assertThrows(CardLimitExceededException.class, () -> paymentService.create(cardDTO));
        verify(paymentCardRepo, never()).save(any());
    }

    @Test
    void updateStatus_shouldThrowNotFoundException_whenCardDoesNotExist() {
        when(paymentCardRepo.changeStatus(99L, false)).thenReturn(0);

        assertThrows(NotFoundException.class, () -> paymentService.updateStatus(99L, false));
    }
}
