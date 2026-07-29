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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private final UserRepository userRepository;
    private static final long DEFAULT_EXPIRY_DAYS = 30;
    @Override
    public Url shortenUrl(String LongUrl, String username, LocalDateTime requestedExpiry) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found : " + username));
        LocalDateTime expiresAt = (requestedExpiry != null) ? requestedExpiry : LocalDateTime.now().plusDays(DEFAULT_EXPIRY_DAYS);
        if(expiresAt.isBefore(LocalDateTime.now())){
            throw new IllegalArgumentException("Invalid time entered : " + expiresAt);
        }
        Url url = Url.builder()
                .longUrl(LongUrl)
                .shortCode("")
                .clickCount(0L)
                .expiresAt(expiresAt)
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
        if(url.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UrlExpiredException("Url has expired");
        }
        url.setClickCount(url.getClickCount() + 1);
        urlRepository.save(url);
        return url.getLongUrl();
    }

    @Override
    public List<Url> getAllUrlsByOwner(String username) {
       return urlRepository.findAllByOwnerUsername(username);
    }

    @Override
    @Transactional
    public Url updateUrl(String longUrl, String shortCode, LocalDateTime requestedExpiry, String username) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Url not found for code : " + shortCode));
        if(!url.getOwner().getUsername().equals(username)) {
            throw new UrlNotFoundException("Url not found for code : " + shortCode);
        }
        if(longUrl!=null){
            url.setLongUrl(longUrl);
        }
        if(requestedExpiry!=null) {
            if(requestedExpiry.isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Invalid time entered : " + requestedExpiry);
            }
            url.setExpiresAt(requestedExpiry);
        }
        return urlRepository.save(url);
    }

    @Override
    @Transactional
    public void deleteUrl(String shortCode, String username) {
        Url url=urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("URL not found for shortcode : " + shortCode));
        if(!url.getOwner().getUsername().equals(username)) {
            throw new UrlNotFoundException("Url not found for code : " + shortCode);
        }
        urlRepository.delete(url);
    }
}
