package com.likelion.entity;

import java.beans.Transient;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    private String id; // UUID string

    @Column(nullable = false, unique = true)
    private String email;

    private String name;
    private String picture;

    // sub from Google (OIDC subject/google token/token id)
    @Column(unique = true)
    private String googleSub;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRole> userRoles = new HashSet<>();

    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        if (this.id == null)
            this.id = UUID.randomUUID().toString();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = Instant.now();
    }

    @Transient // only JPA do not a persistent property; can be remove if it only a method
    public Set<String> getRoleCodes() {
        return userRoles.stream()
                .map(ur -> ur.getRole().getCode())
                .collect(Collectors.toUnmodifiableSet());
    }

    public void addRole(Role role) {
        // ensure User had id (can save or generate id first)
        UserRole ur = new UserRole(this, role);
        this.getUserRoles().add(ur);
        // if Role have 2 dimension userRoles collection, also add into it
        role.getUserRoles().add(ur);
    }

}
