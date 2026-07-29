package com.amarmantry.urlshortener.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Data
public class UrlUpdateDto {
    private String longUrl;
    private LocalDateTime expiresAt;
}
