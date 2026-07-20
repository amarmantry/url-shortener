package com.amarmantry.urlshortener.service;

import com.amarmantry.urlshortener.model.Url;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UrlService {
    Url shortenUrl(String Longurl, String username);
    String  getOriginalUrl(String shortCode);
    List<Url> getAllUrls(String username);
}
