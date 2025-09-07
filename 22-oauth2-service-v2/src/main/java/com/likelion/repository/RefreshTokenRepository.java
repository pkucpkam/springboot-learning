package com.likelion.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.likelion.entity.RefreshToken;

import jakarta.persistence.LockModeType;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    Optional<RefreshToken> findBySessionIdAndRevokedFalse(String sessionId);

    Optional<RefreshToken> findBySessionId(String sessionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
                select r from RefreshToken r
                where r.user.id = :userId and r.sessionId = :sid
            """)
    Optional<RefreshToken> findByUserIdAndSessionIdForUpdate(
            @Param("userId") String userId,
            @Param("sid") String sid);

    @Modifying
    @Query("update RefreshToken t set t.revoked = true where t.user.id = :uid")
    int revokeAll(String uid);
}
