package com.likelion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.likelion.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByGoogleSub(String googleSub);

    Optional<User> findByEmail(String email);

    @Query("""
        select u from User u
        left join fetch u.userRoles ur
        left join fetch ur.role r
        where u.id = :id
    """)
    Optional<User> findByIdWithRoles(@Param("id") String id);
}
