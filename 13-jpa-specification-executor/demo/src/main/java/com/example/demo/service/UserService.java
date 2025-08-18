package com.example.demo.service;

import com.example.demo.dto.UserSearchRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.specifications.UserSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> searchUsers(UserSearchRequest request) {
        Specification<User> spec = UserSpecifications.hasName(request.getName());
        spec = spec.and(UserSpecifications.hasEmail(request.getEmail()));
        spec = spec.and(UserSpecifications.hasAgeGreaterThan(request.getAge()));

        return userRepository.findAll(spec);
    }

    public Page<User> searchUsersWithPagination(UserSearchRequest request, Pageable pageable) {
        Specification<User> spec = UserSpecifications.hasName(request.getName());
        spec = spec.and(UserSpecifications.hasEmail(request.getEmail()));
        spec = spec.and(UserSpecifications.hasAgeGreaterThan(request.getAge()));

        return userRepository.findAll(spec, pageable);
    }
}