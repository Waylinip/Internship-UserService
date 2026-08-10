package org.example.internshipuserservice.controller;

import jakarta.validation.Valid;
import org.example.internshipuserservice.dto.PaymentCardDTO;
import org.example.internshipuserservice.service.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class PaymentCardController {

    private final PaymentService paymentService;

    public PaymentCardController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<PaymentCardDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PaymentCardDTO> createCard(@Valid @RequestBody PaymentCardDTO cardDTO,
                                                     @AuthenticationPrincipal Long authUserId) {
        boolean isAdmin = hasRole("ROLE_ADMIN");
        PaymentCardDTO createdCard = paymentService.create(cardDTO, authUserId, isAdmin);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCard);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PaymentCardDTO> deleteCard(@PathVariable Long id,
                                                     @AuthenticationPrincipal Long authUserId) {
        boolean isAdmin = hasRole("ROLE_ADMIN");
        return ResponseEntity.ok(paymentService.delete(id, authUserId, isAdmin));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentCardDTO> updateCard(@PathVariable Long id,
                                                     @Valid @RequestBody PaymentCardDTO cardDTO,
                                                     @AuthenticationPrincipal Long authUserId) {
        boolean isAdmin = hasRole("ROLE_ADMIN");
        return ResponseEntity.ok(paymentService.update(id, cardDTO, authUserId, isAdmin));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<PaymentCardDTO> changeStatus(@PathVariable Long id, @RequestParam boolean active) {
        return ResponseEntity.ok(paymentService.updateStatus(id, active));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<PaymentCardDTO>> findAll(@RequestParam(required = false) String name,
                                                        @RequestParam(required = false) String surname,
                                                        Pageable pageable) {
        Page<PaymentCardDTO> cards = paymentService.findAll(name, surname, pageable);
        return ResponseEntity.ok(cards);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentCardDTO>> findAllByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(paymentService.findAllByUserId(userId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<PaymentCardDTO>> findMyCards(@AuthenticationPrincipal Long authUserId) {
        return ResponseEntity.ok(paymentService.findMyCards(authUserId));
    }

    private boolean hasRole(String role) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }
}