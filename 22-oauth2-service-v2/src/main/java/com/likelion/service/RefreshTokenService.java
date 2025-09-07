package com.likelion.service;

import org.springframework.lang.Nullable;

import com.likelion.dto.IssueResult;
import com.likelion.dto.RotateResult;
import com.likelion.entity.RefreshToken;
import com.likelion.entity.User;

public interface RefreshTokenService {
    IssueResult issue(User user, @Nullable String parentHash);

    RefreshToken validateBySession(String sessionId);

    RotateResult rotateBySession(String sessionId);
}
