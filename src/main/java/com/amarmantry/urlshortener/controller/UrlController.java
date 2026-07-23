package com.amarmantry.urlshortener.controller;

import com.amarmantry.urlshortener.dto.UrlRequestDto;
import com.amarmantry.urlshortener.dto.UrlResponseDto;
import com.amarmantry.urlshortener.mapper.UrlMapper;
import com.amarmantry.urlshortener.model.Url;
import com.amarmantry.urlshortener.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;
    private final UrlMapper urlMapper;
    @PostMapping("/api/shorten")
    public ResponseEntity<UrlResponseDto> saveUrl(@Valid @RequestBody UrlRequestDto url, Authentication authentication) {
        final String baseUrl = "http://localhost:8080";
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
    @GetMapping("/urls/mine")
    public ResponseEntity<List<UrlResponseDto>> mineUrls(Authentication authentication) {
        final String baseUrl = "http://localhost:8080";
        List<Url> urls = urlService.getAllUrls(authentication.getName());
        List<UrlResponseDto> response = urls.stream()
                        .map(url -> urlMapper.toResponseDto(url, baseUrl))
                                .toList();
        return ResponseEntity.ok(response);
    }
}
