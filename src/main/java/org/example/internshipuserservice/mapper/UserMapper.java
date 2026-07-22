package org.example.internshipuserservice.mapper;

import org.example.internshipuserservice.dto.UserDTO;
import org.example.internshipuserservice.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDto(User user);

    User toEntity(UserDTO userDto);

}
