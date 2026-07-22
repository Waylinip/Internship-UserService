package org.example.internshipuserservice.service;

import lombok.RequiredArgsConstructor;
import org.example.internshipuserservice.dto.PaymentCardDTO;
import org.example.internshipuserservice.dto.UserDTO;
import org.example.internshipuserservice.entity.PaymentCard;
import org.example.internshipuserservice.entity.User;
import org.example.internshipuserservice.mapper.UserMapper;
import org.example.internshipuserservice.repository.UserRepo;
import org.example.internshipuserservice.specification.UserSpecification;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final UserRepo userRepo;

    public UserService(UserMapper userMapper, UserRepo userRepo) {
        this.userMapper = userMapper;
        this.userRepo = userRepo;
    }

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        if (userDTO == null) {
            throw new IllegalArgumentException("User DTO can not be null");
        }
        User user = userMapper.toEntity(userDTO);
        User newUser = userRepo.save(user);
        return userMapper.toDto(newUser);
    }

    public UserDTO getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id can not be null");
        }
        return userMapper.toDto(userRepo.findById(id).orElseThrow(() -> new RuntimeException("User with id " + id + " not fount")));
    }

    public Page<UserDTO> getAllUsers(String name, String surname, Pageable pageable) {
        Specification<User> spec = UserSpecification.filter(name, surname);
        return userRepo.findAll(spec, pageable)
                .map(userMapper::toDto);
    }

    public UserDTO findByEmail(String email) {
        if ((email == null) || (email.isEmpty())) {
            throw new IllegalArgumentException("Email can not be null or empty");
        }
        Optional<User> optionalUser = userRepo.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = optionalUser.get();

        UserDTO userDTO = userMapper.toDto(user);

        return userDTO;

    }

    @Transactional
    public UserDTO changeStatus(Long id, boolean active) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        if (userRepo.changeStatus(id, active) == 0) {
            throw new RuntimeException("USer not found");
        }

        return userRepo.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public UserDTO deleteUser(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id can not be null");
        }
        User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        userRepo.delete(user);
        return userMapper.toDto(user);
    }


    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        if (userDTO == null) {
            throw new IllegalArgumentException("User DTO cannot be null");
        }
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        user.setEmail(userDTO.getEmail());

        return userMapper.toDto(user);
    }


}
