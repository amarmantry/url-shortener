package com.amarmantry.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UrlRequestDto {
    @NotBlank(message = "URL cannot be blank")
    @Pattern(
            regexp="^(https?://).*",
            message="Url must start with http:// or https://"
    )
    private String longUrl;

    private LocalDateTime expiresAt;
}
