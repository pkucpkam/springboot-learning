package com.likelion.dto;

import com.likelion.entity.User;

public record RotateResult(String sessionId, String newRawRefresh, User user) {
}
