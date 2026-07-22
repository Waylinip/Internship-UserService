package org.example.internshipuserservice.mapper;


import org.example.internshipuserservice.dto.PaymentCardDTO;
import org.example.internshipuserservice.entity.PaymentCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentCardMapper {

    @Mapping(source = "user.id", target = "userId")
    PaymentCardDTO toDTO(PaymentCard paymentCard);

    @Mapping(source = "userId", target = "user.id")
    PaymentCard toEntity(PaymentCardDTO paymentCardDTO);
}
