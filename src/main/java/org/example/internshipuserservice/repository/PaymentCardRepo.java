package org.example.internshipuserservice.repository;

import org.example.internshipuserservice.entity.PaymentCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface PaymentCardRepo extends JpaRepository<PaymentCard, Long>, JpaSpecificationExecutor<PaymentCard> {

    List<PaymentCard> findAllByUserId(Long userId);

    @Query(value = """
            SELECT COUNT(*)
            FROM payment_cards
            WHERE user_id = :userId
            """, nativeQuery = true)
    long countCards(@Param("userId") Long userId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE PaymentCard c SET c.active = :active WHERE c.id = :id")
    int changeStatus(@Param("id") Long id, @Param("active") boolean active);

}
