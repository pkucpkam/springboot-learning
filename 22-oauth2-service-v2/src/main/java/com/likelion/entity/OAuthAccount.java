package com.likelion.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "oauth_accounts", uniqueConstraints = @UniqueConstraint(columnNames = { "provider", "provider_user_id" }))
public class OAuthAccount {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false)
    private String provider; // 'google'
    @Column(name = "provider_user_id", nullable = false)
    private String providerUserId; // sub
    private String email;
    private String name;
    private String picture;
    @Column(columnDefinition = "text")
    private String scopes;
    @Column(columnDefinition = "jsonb")
    private String rawInfo;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void upd() {
        updatedAt = Instant.now();
    }
}
