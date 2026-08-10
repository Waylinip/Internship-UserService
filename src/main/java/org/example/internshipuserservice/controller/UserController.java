package org.example.internshipuserservice.controller;

import jakarta.validation.Valid;
import org.example.internshipuserservice.dto.UserDTO;
import org.example.internshipuserservice.dto.UserWithCardsDTO;
import org.example.internshipuserservice.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> finndById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserDTO> deleteUser(@PathVariable Long id,
                                              @AuthenticationPrincipal Long authUserId) {
        boolean isAdmin = hasRole("ROLE_ADMIN");
        UserDTO userDTO = userService.deleteUser(id, authUserId, isAdmin);
        return ResponseEntity.ok(userDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> findByEmail(@PathVariable String email) {
        UserDTO userDTO = userService.findByEmail(email);
        return ResponseEntity.ok(userDTO);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserDTO>> findAll(@RequestParam(required = false) String name,
                                                 @RequestParam(required = false) String surname,
                                                 Pageable pageable) {

        Page<UserDTO> users = userService.getAllUsers(name, surname, pageable);
        return ResponseEntity.ok(users);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/withCards/{id}")
    public ResponseEntity<UserWithCardsDTO> findUserWithCards(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserWithCards(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id,
                                              @Valid @RequestBody UserDTO userDTO,
                                              @AuthenticationPrincipal Long authUserId) {
        boolean isAdmin = hasRole("ROLE_ADMIN");
        return ResponseEntity.ok(userService.updateUser(id, userDTO, authUserId, isAdmin));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<UserDTO> changeStatus(@PathVariable Long id, @RequestParam boolean active) {
        return ResponseEntity.ok(userService.changeStatus(id, active));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMyProfile(@AuthenticationPrincipal Long authUserId) {
        return ResponseEntity.ok(userService.getByAuthUserId(authUserId));
    }

    private boolean hasRole(String role) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }
}
