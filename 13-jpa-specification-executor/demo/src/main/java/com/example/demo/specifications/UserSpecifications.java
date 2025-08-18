package com.example.demo.specifications;


import com.example.demo.entity.User;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class UserSpecifications {
    public static Specification<User> hasName(String name) {
        return (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (name == null || name.isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<User> hasEmail(String email) {
        return (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (email == null || email.isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
        };
    }

    public static Specification<User> hasAgeGreaterThan(Integer age) {
        return (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (age == null) {
                return cb.conjunction();
            }
            return cb.greaterThan(root.get("age"), age);
        };
    }
}
