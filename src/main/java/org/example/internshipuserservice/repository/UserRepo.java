package org.example.internshipuserservice.repository;

import org.example.internshipuserservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Optional;


public interface UserRepo extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE User u SET u.name = :name, u.surname = :surname, u.email = :email WHERE u.id = :id")
    int updateUser(@Param("id") Long id,
                   @Param("name")String name,
                   @Param("surname") String surname,
                   @Param("email") String email);
    @Query("""
        SELECT u
        FROM User u
        LEFT JOIN FETCH u.cardList
        WHERE u.id = :id""")
    Optional<User> findByIdWithCards(@Param("id") Long id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE User u SET u.active = :active WHERE u.id = :id")
    int changeStatus(@Param("id") Long id, @Param("active") boolean active);

}
