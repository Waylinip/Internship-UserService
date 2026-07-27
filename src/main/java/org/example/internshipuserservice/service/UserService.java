package org.example.internshipuserservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.internshipuserservice.dto.UserDTO;
import org.example.internshipuserservice.dto.UserWithCardsDTO;
import org.example.internshipuserservice.entity.User;
import org.example.internshipuserservice.exception.NotFoundException;
import org.example.internshipuserservice.mapper.UserMapper;
import org.example.internshipuserservice.repository.UserRepo;
import org.example.internshipuserservice.specification.UserSpecification;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private static final String USER_NOT_FOUND = "User not found with id: ";
    private static final String USER_NOT_FOUND_BY_EMAIL = "User not found with email: ";
    private static final String USER_ID_EXCEPTION = "User id can not be null";
    private static final String USER_DTO_EXCEPTION = "User DTO can not be null ";
    public static final String EMAIL_NULL_EXCEPTION = "Email can not be null or empty";

    private final UserMapper userMapper;
    private final UserRepo userRepo;

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        if (userDTO == null) {
            log.warn("userDTO is null");
            throw new IllegalArgumentException(USER_DTO_EXCEPTION);
        }
        log.info("creating user with email {}", userDTO.getEmail());
        User user = userMapper.toEntity(userDTO);
        User newUser = userRepo.save(user);
        log.info("user {} created", newUser.getId());
        return userMapper.toDto(newUser);
    }

    public UserDTO getById(Long id) {
        if (id == null) {
            log.warn("user id is null");
            throw new IllegalArgumentException(USER_ID_EXCEPTION);
        }
        log.debug("fetching user {}", id);
        return userMapper.toDto(userRepo.findById(id)
                .orElseThrow(() -> {
                    log.warn("user {} not found", id);
                    return new NotFoundException(USER_NOT_FOUND + id);
                }));
    }

    public Page<UserDTO> getAllUsers(String name, String surname, Pageable pageable) {
        log.debug("fetching users, name={}, surname={}, page={}", name, surname, pageable);
        Specification<User> spec = UserSpecification.filter(name, surname);
        return userRepo.findAll(spec, pageable)
                .map(userMapper::toDto);
    }

    public UserDTO findByEmail(String email) {
        if ((email == null) || (email.isEmpty())) {
            log.warn("email is null or empty");
            throw new IllegalArgumentException(EMAIL_NULL_EXCEPTION);
        }
        log.debug("fetching user by email {}", email);
        Optional<User> optionalUser = userRepo.findByEmail(email);

        if (optionalUser.isEmpty()) {
            log.warn("user with email {} not found", email);
            throw new NotFoundException(USER_NOT_FOUND_BY_EMAIL + email);
        }

        return userMapper.toDto(optionalUser.get());
    }

    @Transactional
    @CacheEvict(value = "userWithCards", key = "#id")
    public UserDTO changeStatus(Long id, boolean active) {
        if (id == null) {
            log.warn("user id is null");
            throw new IllegalArgumentException(USER_ID_EXCEPTION);
        }

        log.info("user {} status -> {}", id, active);
        int updated = userRepo.changeStatus(id,active);
        if (updated == 0) {
            log.warn("user {} not found", id);
            throw new NotFoundException(USER_NOT_FOUND + id);
        }

        return userRepo.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND + id));
    }

    @Transactional
    @CacheEvict(value = "userWithCards", key = "#id")
    public UserDTO deleteUser(Long id) {
        if (id == null) {
            log.warn("user id is null");
            throw new IllegalArgumentException(USER_ID_EXCEPTION);
        }
        User user = userRepo.findById(id)
                .orElseThrow(() -> {
                    log.warn("user {} not found", id);
                    return new NotFoundException(USER_NOT_FOUND + id);
                });
        userRepo.delete(user);
        log.info("user {} deleted", id);
        return userMapper.toDto(user);
    }

    @Transactional
    @CacheEvict(value = "userWithCards", key = "#id")
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        if (id == null) {
            log.warn("user id is null");
            throw new IllegalArgumentException(USER_ID_EXCEPTION);
        }

        if (userDTO == null) {
            log.warn("userDTO is null, id={}", id);
            throw new IllegalArgumentException(USER_DTO_EXCEPTION);
        }
        User user = userRepo.findById(id)
                .orElseThrow(() -> {
                    log.warn("user {} not found", id);
                    return new NotFoundException(USER_NOT_FOUND + id);
                });

        log.info("updating user {}", id);
        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        user.setEmail(userDTO.getEmail());
        userRepo.save(user);
        return userMapper.toDto(user);
    }

    @Cacheable(value = "userWithCards", key = "#id")
    public UserWithCardsDTO getUserWithCards(Long id) {
        log.debug("fetching user with cards, id {}", id);
        User user = userRepo.findByIdWithCards(id)
                .orElseThrow(() -> {
                    log.warn("user {} not found", id);
                    return new NotFoundException("User with id " + id + " not found");
                });

        return userMapper.toDtoWithCards(user);
    }
}