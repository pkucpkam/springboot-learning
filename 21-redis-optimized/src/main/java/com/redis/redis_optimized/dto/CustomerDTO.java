package com.redis.redis_optimized.dto;


import lombok.Builder;

@Builder
public record CustomerDTO(
    Long id,
    String name,
    String email,
    String address
) {
}
