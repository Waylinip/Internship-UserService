package org.example.internshipuserservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.internshipuserservice.dto.PaymentCardDTO;
import org.example.internshipuserservice.entity.PaymentCard;
import org.example.internshipuserservice.entity.User;
import org.example.internshipuserservice.exception.CardLimitExceededException;
import org.example.internshipuserservice.exception.NotFoundException;
import org.example.internshipuserservice.mapper.PaymentCardMapper;
import org.example.internshipuserservice.repository.PaymentCardRepo;
import org.example.internshipuserservice.repository.UserRepo;
import org.example.internshipuserservice.specification.PaymentCardSpecification;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private static final String CARD_NOT_FOUND = "PaymentCard not found with id: ";
    private static final String CARD_ID_EXCEPTION = "Card id can not be null";
    private static final String CARD_DTO_EXCEPTION = "Card DTO can not be null";
    private static final String USER_ID_EXCEPTION = "User id can not be null";
    private static final String USER_NOT_FOUND = "User not found with id: ";
    private static final String CARD_NUMBER_EXISTS = "Card with this number already exists";

    private final PaymentCardRepo paymentCardRepo;
    private final PaymentCardMapper cardMapper;
    private final UserRepo userRepo;

    @Transactional
    @CacheEvict(value = "userWithCards", key = "#result.userId")
    public PaymentCardDTO create(PaymentCardDTO cardDTO, Long authUserId, boolean isAdmin) {
        if (cardDTO == null) {
            log.warn("cardDTO is null");
            throw new IllegalArgumentException(CARD_DTO_EXCEPTION);
        }
        if (cardDTO.getUserId() == null) {
            log.warn("userId is null");
            throw new IllegalArgumentException(USER_ID_EXCEPTION);
        }

        log.info("creating card for user {}", cardDTO.getUserId());

        User user = userRepo.findById(cardDTO.getUserId())
                .orElseThrow(() -> {
                    log.warn("user {} not found", cardDTO.getUserId());
                    return new NotFoundException(USER_NOT_FOUND + cardDTO.getUserId());
                });

        if (!isAdmin && !user.getAuthUserId().equals(authUserId)) {
            log.warn("authUserId {} tried to create card for another user {}", authUserId, cardDTO.getUserId());
            throw new AccessDeniedException("You can only create cards for yourself");
        }

        long count = paymentCardRepo.countCards(cardDTO.getUserId());
        if (count >= 5) {
            log.warn("user {} already has 5 cards", cardDTO.getUserId());
            throw new CardLimitExceededException("User with id " + cardDTO.getUserId() + " already has 5 payment cards");
        }

        if (paymentCardRepo.existsByNumber(cardDTO.getNumber())) {
            log.warn("card number already exists");
            throw new IllegalArgumentException(CARD_NUMBER_EXISTS);
        }


        PaymentCard card = cardMapper.toEntity(cardDTO);
        card.setUser(user);

        PaymentCard savedCard = paymentCardRepo.save(card);
        log.info("card {} created for user {}", savedCard.getId(), cardDTO.getUserId());
        return cardMapper.toDTO(savedCard);
    }

    public PaymentCardDTO findById(Long id) {
        if (id == null) {
            log.warn("card id is null");
            throw new IllegalArgumentException(CARD_ID_EXCEPTION);
        }
        log.debug("fetching card {}", id);
        return cardMapper.toDTO(paymentCardRepo.findById(id).orElseThrow(() -> {
            log.warn("card {} not found", id);
            return new NotFoundException(CARD_NOT_FOUND + id);
        }));
    }

    public Page<PaymentCardDTO> findAll(String name, String surname, Pageable pageable) {
        log.debug("fetching cards, name={}, surname={}, page={}", name, surname, pageable);
        Specification<PaymentCard> specification = PaymentCardSpecification.filter(name, surname);
        return paymentCardRepo.findAll(specification, pageable)
                .map(cardMapper::toDTO);
    }

    @Transactional
    @CacheEvict(value = "userWithCards", key = "#result.userId")
    public PaymentCardDTO updateStatus(Long id, boolean active) {
        if (id == null) {
            log.warn("card id is null");
            throw new IllegalArgumentException(CARD_ID_EXCEPTION);
        }

        log.info("card {} status -> {}", id, active);
        int updated = paymentCardRepo.changeStatus(id, active);
        if (updated == 0) {
            log.warn("card {} not found", id);
            throw new NotFoundException(CARD_NOT_FOUND + id);
        }

        return paymentCardRepo.findById(id)
                .map(cardMapper::toDTO)
                .orElseThrow(() -> new NotFoundException(CARD_NOT_FOUND + id));
    }

    public List<PaymentCardDTO> findAllByUserId(Long userId) {
        if (userId == null) {
            log.warn("userId is null");
            throw new IllegalArgumentException(USER_ID_EXCEPTION);
        }

        log.debug("fetching cards for user {}", userId);
        List<PaymentCard> cards = paymentCardRepo.findAllByUserId(userId);
        return cards.stream().map(cardMapper::toDTO).collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "userWithCards", key = "#result.userId")
    public PaymentCardDTO delete(Long id, Long authUserId, boolean isAdmin) {
        if (id == null) {
            throw new IllegalArgumentException(CARD_ID_EXCEPTION);
        }

        PaymentCard card = paymentCardRepo.findById(id)
                .orElseThrow(() -> {
                    log.warn("card {} not found", id);
                    return new NotFoundException(CARD_NOT_FOUND + id);
                });

        if (!isAdmin && !card.getUser().getAuthUserId().equals(authUserId)) {
            log.warn("authUserId {} tried to delete card {} owned by another user", authUserId, id);
            throw new AccessDeniedException("You don't have access to this card");
        }

        paymentCardRepo.delete(card);
        log.info("card {} deleted", id);
        return cardMapper.toDTO(card);
    }

    @Transactional
    @CacheEvict(value = "userWithCards", key = "#result.userId")
    public PaymentCardDTO update(Long id, PaymentCardDTO cardDTO, Long authUserId, boolean isAdmin) {
        if (id == null) {
            throw new IllegalArgumentException(CARD_ID_EXCEPTION);
        }
        if (cardDTO == null) {
            throw new IllegalArgumentException(CARD_DTO_EXCEPTION);
        }

        PaymentCard card = paymentCardRepo.findById(id)
                .orElseThrow(() -> {
                    log.warn("card {} not found", id);
                    return new NotFoundException(CARD_NOT_FOUND + id);
                });

        if (!isAdmin && !card.getUser().getAuthUserId().equals(authUserId)) {
            log.warn("authUserId {} tried to update card {} owned by another user", authUserId, id);
            throw new AccessDeniedException("You don't have access to this card");
        }

        log.info("updating card {}", id);
        card.setNumber(cardDTO.getNumber());
        card.setHolder(cardDTO.getHolder());
        card.setExpirationDate(cardDTO.getExpirationDate());
        paymentCardRepo.save(card);
        return cardMapper.toDTO(card);
    }

    public List<PaymentCardDTO> findMyCards(Long authUserId) {
        User user = userRepo.findByAuthUserId(authUserId)
                .orElseThrow(() -> new NotFoundException("User not found for authUserId: " + authUserId));
        return findAllByUserId(user.getId());
    }

}
