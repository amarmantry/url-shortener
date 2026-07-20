package com.amarmantry.urlshortener.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "url")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String longUrl;
    @Column(unique = true, length = 10, nullable = false)
    private String shortCode;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column
    private LocalDateTime expiresAt;
    @Column(nullable = false)
    private Long clickCount = 0L;
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
}
