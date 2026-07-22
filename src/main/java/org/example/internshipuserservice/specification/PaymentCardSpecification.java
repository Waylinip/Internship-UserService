package org.example.internshipuserservice.specification;


import org.example.internshipuserservice.entity.PaymentCard;
import org.springframework.data.jpa.domain.Specification;


public class PaymentCardSpecification {

    public static Specification<PaymentCard> hasUserName(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(
                    cb.lower(root.join("user").get("name")),
                    "%" + name.toLowerCase() + "%"
            );
        };
    }

    public static Specification<PaymentCard> hasUserSurname(String surname) {
        return (root, query, cb) -> {
            if (surname == null || surname.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(
                    cb.lower(root.join("user").get("surname")),
                    "%" + surname.toLowerCase() + "%"
            );
        };
    }

    public static Specification<PaymentCard> filter(String name, String surname) {
        return Specification.where(hasUserName(name)).and(hasUserSurname(surname));
    }
}
