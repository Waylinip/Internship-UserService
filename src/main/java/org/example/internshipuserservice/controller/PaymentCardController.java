package org.example.internshipuserservice.controller;

import jakarta.validation.Valid;
import org.example.internshipuserservice.dto.PaymentCardDTO;
import org.example.internshipuserservice.service.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class PaymentCardController {

    private final PaymentService paymentService;

    public PaymentCardController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentCardDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PaymentCardDTO> createCard(@Valid @RequestBody PaymentCardDTO cardDTO) {
        PaymentCardDTO createdCard = paymentService.create(cardDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCard);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PaymentCardDTO> deleteCard(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.delete(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentCardDTO> updateCard(@PathVariable Long id, @Valid @RequestBody PaymentCardDTO cardDTO) {
        return ResponseEntity.ok(paymentService.update(id, cardDTO));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PaymentCardDTO> changeStatus(@PathVariable Long id, @RequestParam boolean active) {
        return ResponseEntity.ok(paymentService.updateStatus(id, active));
    }

    @GetMapping
    public ResponseEntity<Page<PaymentCardDTO>> findAll(@RequestParam(required = false) String name,
                                                        @RequestParam(required = false) String surname,
                                                        Pageable pageable) {
        Page<PaymentCardDTO> cards = paymentService.findAll(name, surname, pageable);
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentCardDTO>> findAllByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(paymentService.findAllByUserId(userId));
    }
}