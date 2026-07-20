package com.amarmantry.urlshortener.service;

import com.amarmantry.urlshortener.exception.UrlExpiredException;
import com.amarmantry.urlshortener.exception.UrlNotFoundException;
import com.amarmantry.urlshortener.model.Url;
import com.amarmantry.urlshortener.model.User;
import com.amarmantry.urlshortener.repository.UrlRepository;
import com.amarmantry.urlshortener.repository.UserRepository;
import com.amarmantry.urlshortener.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private final UserRepository userRepository;

    @Override
    public Url shortenUrl(String LongUrl, String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found : " + username));
        Url url = Url.builder()
                .longUrl(LongUrl)
                .shortCode("")
                .clickCount(0L)
                .owner(owner)
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

    @Override
    public List<Url> getAllUrls(String username) {
       return urlRepository.findAllByOwnerUsername(username);
    }
}
