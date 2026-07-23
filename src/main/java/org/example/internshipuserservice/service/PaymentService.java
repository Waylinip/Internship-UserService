package org.example.internshipuserservice.service;

import org.example.internshipuserservice.dto.PaymentCardDTO;
import org.example.internshipuserservice.entity.PaymentCard;
import org.example.internshipuserservice.entity.User;
import org.example.internshipuserservice.exception.CardLimitExceededException;
import org.example.internshipuserservice.exception.NotFoundException;
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

    private static final String CARD_NOT_FOUND = "PaymentCard not found with id: ";
    private static final String CARD_ID_EXCEPTION = "Card id can not be null";
    private static final String CARD_DTO_EXCEPTION = "Card DTO can not be null";
    private static final String USER_ID_EXCEPTION = "User id can not be null";
    private static final String USER_NOT_FOUND = "User not found with id: ";

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
            throw new IllegalArgumentException(CARD_DTO_EXCEPTION);
        }
        if (cardDTO.getUserId() == null) {
            throw new IllegalArgumentException(USER_ID_EXCEPTION);
        }

        long count = paymentCardRepo.countCards(cardDTO.getUserId());
        if (count >= 5) {
            throw new CardLimitExceededException("User with id " + cardDTO.getUserId() + " already has 5 payment cards");
        }

        User user = userRepo.findById(cardDTO.getUserId())
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND + cardDTO.getUserId()));

        PaymentCard card = cardMapper.toEntity(cardDTO);
        card.setUser(user);

        PaymentCard savedCard = paymentCardRepo.save(card);
        return cardMapper.toDTO(savedCard);
    }

    public PaymentCardDTO findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException(CARD_ID_EXCEPTION);
        }
        return cardMapper.toDTO(paymentCardRepo.findById(id).orElseThrow(() -> new NotFoundException(CARD_NOT_FOUND + id)));
    }

    public Page<PaymentCardDTO> findAll(String name, String surname, Pageable pageable) {
        Specification<PaymentCard> specification = PaymentCardSpecification.filter(name, surname);
        return paymentCardRepo.findAll(specification, pageable)
                .map(cardMapper::toDTO);
    }

    @Transactional
    public PaymentCardDTO updateStatus(Long id, boolean active) {
        if (id == null) {
            throw new IllegalArgumentException(CARD_ID_EXCEPTION);
        }

        if (paymentCardRepo.changeStatus(id, active) == 0) {
            throw new NotFoundException(CARD_NOT_FOUND + id);
        }


        return paymentCardRepo.findById(id)
                .map(cardMapper::toDTO)
                .orElseThrow(() -> new NotFoundException(CARD_NOT_FOUND + id));
    }

    public List<PaymentCardDTO> findAllByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException(USER_ID_EXCEPTION);
        }
        List<PaymentCard> cards = paymentCardRepo.findAllByUserId(userId);
        return cards.stream().map(cardMapper::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public PaymentCardDTO delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException(CARD_ID_EXCEPTION);
        }

        PaymentCard paymentCard = paymentCardRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(CARD_NOT_FOUND + id));

        paymentCardRepo.delete(paymentCard);

        return cardMapper.toDTO(paymentCard);
    }

    @Transactional
    public PaymentCardDTO update(Long id, PaymentCardDTO cardDTO) {
        if (id == null) {
            throw new IllegalArgumentException(CARD_ID_EXCEPTION);
        }
        if (cardDTO == null) {
            throw new IllegalArgumentException(CARD_DTO_EXCEPTION);
        }

        PaymentCard card = paymentCardRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(CARD_NOT_FOUND + id));

        card.setNumber(cardDTO.getNumber());
        card.setHolder(cardDTO.getHolder());
        card.setExpirationDate(cardDTO.getExpirationDate());
        paymentCardRepo.save(card);
        return cardMapper.toDTO(card);
    }

}
