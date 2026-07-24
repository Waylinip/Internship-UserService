package org.example.internshipuserservice.serviceTest;

import org.example.internshipuserservice.dto.UserWithCardsDTO;
import org.example.internshipuserservice.entity.User;
import org.example.internshipuserservice.exception.NotFoundException;
import org.example.internshipuserservice.mapper.UserMapper;
import org.example.internshipuserservice.repository.UserRepo;
import org.example.internshipuserservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private UserService userService;

    @Test
    void findByEmail_shouldThrowNotFoundException_whenUserDoesNotExist() {
        when(userRepo.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findByEmail("missing@example.com"));
    }

    @Test
    void getUserWithCards_shouldReturnDtoWithCards_whenUserExists() {
        User user = new User();
        user.setId(1L);

        UserWithCardsDTO withCardsDTO = new UserWithCardsDTO();
        withCardsDTO.setId(1L);

        when(userRepo.findByIdWithCards(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDtoWithCards(user)).thenReturn(withCardsDTO);

        UserWithCardsDTO result = userService.getUserWithCards(1L);

        assertEquals(1L, result.getId());
    }
}
