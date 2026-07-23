package org.example.internshipuserservice.mapper;

import org.example.internshipuserservice.dto.UserDTO;
import org.example.internshipuserservice.dto.UserWithCardsDTO;
import org.example.internshipuserservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = PaymentCardMapper.class)
public interface UserMapper {

    UserDTO toDto(User user);
    @Mapping(target = "cardList", ignore = true)
    User toEntity(UserDTO userDto);

    UserWithCardsDTO toDtoWithCards(User user);

}
