package com.likelion.service;

import com.likelion.dto.GoogleUser;
import com.likelion.dto.response.GoogleTokenResponse;

public interface GoogleTokenService {
    GoogleTokenResponse exchangeCode(String code);
                                     
    GoogleUser parseAndVerify(String idToken);
}
