package com.amarmantry.urlshortener.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UrlResponseDto {
    private String longUrl;
    private String shortUrl;
    private String shortCode;
    private String expiresAt;
}
