package com.amarmantry.urlshortener.mapper;

import com.amarmantry.urlshortener.dto.UrlResponseDto;
import com.amarmantry.urlshortener.model.Url;
import org.springframework.stereotype.Component;

@Component
public class UrlMapper {
    public UrlResponseDto toResponseDto(Url url, String baseUrl){
        return UrlResponseDto.builder()
                .shortCode(url.getShortCode())
                .shortUrl(baseUrl + "/" + url.getShortCode())
                .longUrl(url.getLongUrl())
                .expiresAt(
                        url.getExpiresAt() != null
                        ? url.getExpiresAt().toString()
                        : null
                )
                .build();
    }


}
