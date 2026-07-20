package org.example.internshipuserservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Table(name = "payment_cards", indexes = {
        @Index(name = "idx_payment_cards_user_id", columnList = "user_id")
})
public class PaymentCard extends Audit{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)

    private String number;

    @Column(nullable = false)
    @ToString.Include
    private String holder;

    @Column(name = "expiration_date", nullable = false)
    @ToString.Include
    private LocalDate expirationDate;

    @Column(nullable = false)
    @ToString.Include
    private Boolean active;


}
