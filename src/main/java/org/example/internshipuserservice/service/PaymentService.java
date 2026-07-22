package org.example.internshipuserservice.service;

import org.example.internshipuserservice.dto.PaymentCardDTO;
import org.example.internshipuserservice.entity.PaymentCard;
import org.example.internshipuserservice.entity.User;
import org.example.internshipuserservice.mapper.PaymentCardMapper;
import org.example.internshipuserservice.repository.PaymentCardRepo;
import org.example.internshipuserservice.repository.UserRepo;
import org.example.internshipuserservice.specification.PaymentCardSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    private final PaymentCardRepo paymentCardRepo;
    private final PaymentCardMapper cardMapper;
    private final UserRepo userRepo;

    public PaymentService(PaymentCardRepo paymentCardRepo, PaymentCardMapper cardMapper, UserRepo userRepo) {
        this.paymentCardRepo = paymentCardRepo;
        this.cardMapper = cardMapper;
        this.userRepo = userRepo;
    }

    @Transactional
    public PaymentCardDTO create(PaymentCardDTO cardDTO) {
        if (cardDTO == null) {
            throw new IllegalArgumentException("Card DTO can not be null");
        }
        if (cardDTO.getUserId() == null) {
            throw new IllegalArgumentException("User ID can not be null");
        }

        long count = paymentCardRepo.countCards(cardDTO.getUserId());
        if (count >= 5) {
            throw new IllegalStateException("User already has 5 payment cards. Limit reached!");
        }

        User user = userRepo.findById(cardDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User with id " + cardDTO.getUserId() + " not found"));

        PaymentCard card = cardMapper.toEntity(cardDTO);
        card.setUser(user);

        PaymentCard savedCard = paymentCardRepo.save(card);
        return cardMapper.toDTO(savedCard);
    }

    public PaymentCardDTO findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id can not be null");
        }
        return cardMapper.toDTO(paymentCardRepo.findById(id).orElseThrow(() -> new RuntimeException("Card not found")));
    }

    public Page<PaymentCardDTO> findAll(String name, String surname, Pageable pageable) {
        Specification<PaymentCard> specification = PaymentCardSpecification.filter(name, surname);
        return paymentCardRepo.findAll(specification, pageable)
                .map(cardMapper::toDTO);
    }

    @Transactional
    public PaymentCardDTO updateStatus(Long id, boolean active) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        if (paymentCardRepo.changeStatus(id, active) == 0) {
            throw new RuntimeException("Card not found");
        }

        return paymentCardRepo.findById(id)
                .map(cardMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Card not found"));
    }

    public List<PaymentCardDTO> findAllByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID can not be null");
        }
        List<PaymentCard> cards = paymentCardRepo.findAllByUserId(userId);
        return cards.stream().map(cardMapper::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public PaymentCardDTO delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id can not be null");
        }

        PaymentCard paymentCard = paymentCardRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        paymentCardRepo.delete(paymentCard);

        return cardMapper.toDTO(paymentCard);
    }

    @Transactional
    public PaymentCardDTO update(Long id, PaymentCardDTO cardDTO) {
        if (id == null || cardDTO == null) {
            throw new IllegalArgumentException("Id and Card DTO can not be null");
        }

        PaymentCard card = paymentCardRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        card.setNumber(cardDTO.getNumber());
        card.setHolder(cardDTO.getHolder());
        card.setExpirationDate(cardDTO.getExpirationDate());

        return cardMapper.toDTO(card);
    }

}
