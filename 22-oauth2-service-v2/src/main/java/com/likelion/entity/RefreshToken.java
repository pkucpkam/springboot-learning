package com.likelion.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "refresh_tokens", indexes = {
        @Index(name = "uq_refresh_session", columnList = "session_id", unique = true)
})
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // CHAR(36)
    private User user;

    @Column(name = "session_id", nullable = false, unique = true, length = 36)
    private String sessionId; // UUID cho session (opaque ID)

    @Column(name = "token_hash", nullable = false, unique = true, length = 88)
    private String tokenHash; // SHA-256 hash (Base64)

    private Instant expiresAt;
    private boolean revoked;
    private String parentTokenHash; // support rotation chain (optional)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
