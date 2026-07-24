package org.example.internshipuserservice.integrationTest;


import org.example.internshipuserservice.dto.PaymentCardDTO;
import org.example.internshipuserservice.dto.UserDTO;
import org.example.internshipuserservice.dto.UserWithCardsDTO;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
public class UserIntegrationTest {

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
    void getUserWithCards_shouldReturnUserAndCreatedCard() {

        UserDTO userDto = new UserDTO();
        userDto.setName("Kevin");
        userDto.setSurname("Duranr");
        userDto.setEmail("test_" + System.currentTimeMillis() + "@example.com");
        userDto.setBirthdate(LocalDate.of(1995, 5, 10));
        userDto.setActive(true);

        ResponseEntity<UserDTO> userResponse = restTemplate.postForEntity("/api/users", userDto, UserDTO.class);
        assertEquals(HttpStatus.CREATED, userResponse.getStatusCode());
        Long userId = userResponse.getBody().getId();

        PaymentCardDTO cardDto = new PaymentCardDTO();
        cardDto.setUserId(userId);
        cardDto.setNumber("1234567890123456");
        cardDto.setHolder("Kevin Durant");
        cardDto.setExpirationDate(LocalDate.of(2030, 1, 1));
        cardDto.setActive(true);

        ResponseEntity<PaymentCardDTO> cardResponse = restTemplate.postForEntity("/api/cards", cardDto, PaymentCardDTO.class);
        assertEquals(HttpStatus.CREATED, cardResponse.getStatusCode());

        ResponseEntity<UserWithCardsDTO> withCardsResponse = restTemplate.getForEntity(
                "/api/users/withCards/" + userId, UserWithCardsDTO.class);

        assertEquals(HttpStatus.OK, withCardsResponse.getStatusCode());
        UserWithCardsDTO body = withCardsResponse.getBody();
        assertNotNull(body);
        assertEquals(1, body.getCardList().size());
        assertEquals("1234567890123456", body.getCardList().get(0).getNumber());
    }
}