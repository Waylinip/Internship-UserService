package org.example.internshipuserservice.integrationTest;

import org.example.internshipuserservice.dto.PaymentCardDTO;
import org.example.internshipuserservice.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureTestRestTemplate
public class CardIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createCard_shouldReturnConflict_whenUserAlreadyHas5Cards() {
        UserDTO userDto = new UserDTO();
        userDto.setName("Petro");
        userDto.setSurname("Petrenko");
        userDto.setEmail("test_" + System.currentTimeMillis() + "@example.com");
        userDto.setBirthdate(LocalDate.of(1990, 1, 1));
        userDto.setActive(true);

        ResponseEntity<UserDTO> userResponse = restTemplate.postForEntity("/api/users", userDto, UserDTO.class);
        assertEquals(HttpStatus.CREATED, userResponse.getStatusCode());
        Long userId = userResponse.getBody().getId();

        for (int i = 0; i < 5; i++) {
            PaymentCardDTO cardDto = new PaymentCardDTO();
            cardDto.setUserId(userId);
            cardDto.setNumber("100000000000000" + i);
            cardDto.setHolder("Petro Petrenko");
            cardDto.setExpirationDate(LocalDate.of(2030, 1, 1));
            cardDto.setActive(true);

            ResponseEntity<PaymentCardDTO> response = restTemplate.postForEntity("/api/cards", cardDto, PaymentCardDTO.class);
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
        }

        PaymentCardDTO sixthCard = new PaymentCardDTO();
        sixthCard.setUserId(userId);
        sixthCard.setNumber("9999999999999999");
        sixthCard.setHolder("Petro Petrenko");
        sixthCard.setExpirationDate(LocalDate.of(2030, 1, 1));
        sixthCard.setActive(true);

        ResponseEntity<String> sixthResponse = restTemplate.postForEntity("/api/cards", sixthCard, String.class);
        assertEquals(HttpStatus.CONFLICT, sixthResponse.getStatusCode());
    }
}
