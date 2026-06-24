package com.amarmantry.urlshortener.service;

import com.amarmantry.urlshortener.exception.UrlExpiredException;
import com.amarmantry.urlshortener.exception.UrlNotFoundException;
import com.amarmantry.urlshortener.model.Url;
import com.amarmantry.urlshortener.repository.UrlRepository;
import com.amarmantry.urlshortener.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;

    @Override
    public Url shortenUrl(String LongUrl) {
        Url url = Url.builder()
                .longUrl(LongUrl)
                .shortCode("")
                .clickCount(0L)
                .build();
        Url saved = urlRepository.save(url);
        String shortCode = Base62Encoder.encodeBase62(saved.getId());
        saved.setShortCode(shortCode);
        return urlRepository.save(saved);
    }

    @Override
    public String getOriginalUrl(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Url not found for shortCode : " + shortCode));
        if(url.getExpiresAt()!=null && url.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UrlExpiredException("Url has expired");
        }
        url.setClickCount(url.getClickCount() + 1);
        urlRepository.save(url);
        return url.getLongUrl();
    }
}
