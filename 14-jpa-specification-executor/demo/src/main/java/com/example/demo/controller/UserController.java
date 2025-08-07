package com.example.demo.controller;

import com.example.demo.dto.UserSearchRequest;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/search")
    public List<User> searchUsers(@RequestBody UserSearchRequest request) {
        return userService.searchUsers(request);
    }

    @PostMapping("/search/paginated")
    public Page<User> searchUsersWithPagination(@RequestBody UserSearchRequest request, Pageable pageable) {
        return userService.searchUsersWithPagination(request, pageable);
    }
}