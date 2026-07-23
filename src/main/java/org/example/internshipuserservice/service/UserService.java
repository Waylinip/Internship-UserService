package org.example.internshipuserservice.service;

import org.example.internshipuserservice.dto.UserDTO;
import org.example.internshipuserservice.dto.UserWithCardsDTO;
import org.example.internshipuserservice.entity.User;
import org.example.internshipuserservice.exception.NotFoundException;
import org.example.internshipuserservice.mapper.UserMapper;
import org.example.internshipuserservice.repository.UserRepo;
import org.example.internshipuserservice.specification.UserSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private static final String USER_NOT_FOUND = "User not found with id: ";
    private static final String USER_NOT_FOUND_BY_EMAIL = "User not found with email: ";
    private static final String USER_ID_EXCEPTION = "User id can not be null";
    private static final String USER_DTO_EXCEPTION = "User DTO can not be null ";
    public static final String EMAIL_NULL_EXCEPTION = "Email can not be null or empty";

    private final UserMapper userMapper;
    private final UserRepo userRepo;

    public UserService(UserMapper userMapper, UserRepo userRepo) {
        this.userMapper = userMapper;
        this.userRepo = userRepo;
    }

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        if (userDTO == null) {
            throw new IllegalArgumentException(USER_DTO_EXCEPTION);
        }
        User user = userMapper.toEntity(userDTO);
        User newUser = userRepo.save(user);
        return userMapper.toDto(newUser);
    }

    public UserDTO getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException(USER_ID_EXCEPTION);
        }
        return userMapper.toDto(userRepo.findById(id).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND + id)));
    }

    public Page<UserDTO> getAllUsers(String name, String surname, Pageable pageable) {
        Specification<User> spec = UserSpecification.filter(name, surname);
        return userRepo.findAll(spec, pageable)
                .map(userMapper::toDto);
    }

    public UserDTO findByEmail(String email) {
        if ((email == null) || (email.isEmpty())) {
            throw new IllegalArgumentException(EMAIL_NULL_EXCEPTION);
        }
        Optional<User> optionalUser = userRepo.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new NotFoundException(USER_NOT_FOUND_BY_EMAIL + email);
        }

        User user = optionalUser.get();

        UserDTO userDTO = userMapper.toDto(user);

        return userDTO;

    }

    @Transactional
    public UserDTO changeStatus(Long id, boolean active) {
        if (id == null) {
            throw new IllegalArgumentException(USER_ID_EXCEPTION);
        }

        if (userRepo.changeStatus(id, active) == 0) {
            throw new NotFoundException(USER_NOT_FOUND + id);
        }

        return userRepo.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND + id));
    }

    @Transactional
    public UserDTO deleteUser(Long id) {
        if (id == null) {
            throw new IllegalArgumentException(USER_ID_EXCEPTION);
        }
        User user = userRepo.findById(id).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND + id));
        userRepo.delete(user);
        return userMapper.toDto(user);
    }


    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        if (id == null) {
            throw new IllegalArgumentException(USER_ID_EXCEPTION);
        }

        if (userDTO == null) {
            throw new IllegalArgumentException(USER_DTO_EXCEPTION);
        }
        User user = userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND + id));

        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        user.setEmail(userDTO.getEmail());
        userRepo.save(user);
        return userMapper.toDto(user);
    }

    public UserWithCardsDTO getUserWithCards(Long id) {
        User user = userRepo.findByIdWithCards(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));

        return userMapper.toDtoWithCards(user);
    }

}
