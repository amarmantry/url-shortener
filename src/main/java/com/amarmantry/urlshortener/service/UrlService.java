package com.amarmantry.urlshortener.service;

import com.amarmantry.urlshortener.model.Url;
import org.springframework.stereotype.Service;

@Service
public interface UrlService {
    Url shortenUrl(String Longurl);
    String  getOriginalUrl(String shortCode);
}
