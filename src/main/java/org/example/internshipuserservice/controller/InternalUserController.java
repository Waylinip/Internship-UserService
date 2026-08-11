package org.example.internshipuserservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.internshipuserservice.dto.ProfileRequest;
import org.example.internshipuserservice.dto.UserDTO;
import org.example.internshipuserservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/users")
@RequiredArgsConstructor
public class InternalUserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDTO> createFromRegistration(
            @Valid @RequestBody ProfileRequest request) {

        UserDTO createdUser = userService.createFromRegistration(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> findByEmail(@PathVariable String email) {
        UserDTO userDTO = userService.findByEmail(email);
        return ResponseEntity.ok(userDTO);
    }

}
