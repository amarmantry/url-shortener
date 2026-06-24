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
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;
    private final UrlMapper urlMapper;
    @PostMapping("/api/shorten")
    public ResponseEntity<UrlResponseDto> saveUrl(@Valid @RequestBody UrlRequestDto url) {
        final String baseUrl = "http://localhost:8080";
        Url saved = urlService.shortenUrl(url.getLongUrl());
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
}
