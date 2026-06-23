package com.amarmantry.urlshortener.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hl")
public class HealthCheck {
    @GetMapping
    public ResponseEntity<String> hl(){
        String hl="k";
        return new ResponseEntity<>(hl, HttpStatus.OK);
    }
}
