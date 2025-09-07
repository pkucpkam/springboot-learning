package com.likelion.service;

import java.util.Collection;

public interface JwtService {
    String createAccessToken(String userId, String email, Collection<String> roles);
    
}
