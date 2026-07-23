package org.example.internshipuserservice.controller;

import jakarta.validation.Valid;
import org.example.internshipuserservice.dto.UserDTO;
import org.example.internshipuserservice.dto.UserWithCardsDTO;
import org.example.internshipuserservice.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> finndById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserDTO> deleteUser(@PathVariable Long id) {
        UserDTO userDTO = userService.deleteUser(id);

        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> findByEmail(@PathVariable String email) {
        UserDTO userDTO = userService.findByEmail(email);
        return ResponseEntity.ok(userDTO);
    }



    @GetMapping
    public ResponseEntity<Page<UserDTO>> findAll(@RequestParam(required = false) String name,
                                                 @RequestParam(required = false) String surname,
                                                 Pageable pageable) {

        Page<UserDTO> users = userService.getAllUsers(name, surname, pageable);
        return ResponseEntity.ok(users);

    }

    @GetMapping("/withCards/{id}")
    public ResponseEntity<UserWithCardsDTO> findUserWithCards(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserWithCards(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(id, userDTO));
    }
    @PatchMapping("/{id}/status")
    public ResponseEntity<UserDTO> changeStatus(@PathVariable Long id, @RequestParam boolean active) {
        return ResponseEntity.ok(userService.changeStatus(id, active));
    }
}
