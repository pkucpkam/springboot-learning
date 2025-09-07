package com.likelion.exception.error;

import java.time.Instant;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ApiError {
    private final Instant timestamp = Instant.now();
    private final int status;
    private final String error;
    private final String message;
    private final String path;

    public ApiError(HttpStatus status, String message, String path) {
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
        this.path = path;
    }
}
