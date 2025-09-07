package com.likelion.service;

import com.likelion.dto.GoogleUser;
import com.likelion.entity.User;

public interface UserService {
    User upsertGoogleUser(GoogleUser guser); // find-or-create + link googleSub

    User getById(String id);
}
