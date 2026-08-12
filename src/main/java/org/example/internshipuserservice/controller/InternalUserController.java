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

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
        UserDTO userDTO = userService.getById(id);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/auth/{authUserId}")
    public ResponseEntity<UserDTO> findByAuthUserId(@PathVariable Long authUserId) {
        return ResponseEntity.ok(userService.getByAuthUserId(authUserId));
    }

}
