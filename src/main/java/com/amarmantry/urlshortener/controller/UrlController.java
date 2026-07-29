package com.amarmantry.urlshortener.controller;

import com.amarmantry.urlshortener.dto.UrlRequestDto;
import com.amarmantry.urlshortener.dto.UrlResponseDto;
import com.amarmantry.urlshortener.dto.UrlUpdateDto;
import com.amarmantry.urlshortener.mapper.UrlMapper;
import com.amarmantry.urlshortener.model.Url;
import com.amarmantry.urlshortener.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;
    private final UrlMapper urlMapper;
    @Value("${app.base-url}")
    private String baseUrl;
    @PostMapping("/api/shorten")
    public ResponseEntity<UrlResponseDto> saveUrl(@Valid @RequestBody UrlRequestDto url, Authentication authentication) {
        Url saved = urlService.shortenUrl(url.getLongUrl(),authentication.getName(),url.getExpiresAt());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(urlMapper.toResponseDto(saved, baseUrl));
    }
    @GetMapping("/{shortcode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortcode) {
        String longUrl = urlService.getOriginalUrl(shortcode);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(longUrl))
                .build();
    }
    @GetMapping("/api/urls/mine")
    public ResponseEntity<List<UrlResponseDto>> mineUrls(Authentication authentication) {
        List<Url> urls = urlService.getAllUrlsByOwner(authentication.getName());
        List<UrlResponseDto> response = urls.stream()
                        .map(url -> urlMapper.toResponseDto(url, baseUrl))
                                .toList();
        return ResponseEntity.ok(response);
    }
    @PutMapping("/api/urls/{shortCode}")
    public ResponseEntity<UrlResponseDto> updateUrl(@RequestBody UrlUpdateDto url, @PathVariable String shortCode, Authentication authentication) {
        String longUrl = url.getLongUrl();
        LocalDateTime expiresAt = url.getExpiresAt();
        Url updated = urlService.updateUrl(longUrl, shortCode, expiresAt, authentication.getName());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(urlMapper.toResponseDto(updated, baseUrl));
    }
    @DeleteMapping("/api/urls/{shortCode}")
    public ResponseEntity<Void> deleteUrl(@PathVariable String shortCode, Authentication authentication) {
        urlService.deleteUrl(shortCode, authentication.getName());
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
