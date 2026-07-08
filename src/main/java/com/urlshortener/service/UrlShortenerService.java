package com.urlshortener.service;

// The 'stereotype' package contains annotations that define the role of beans.
import com.urlshortener.dto.UrlStatsResponse;
import com.urlshortener.exception.UrlNotFoundException;
import com.urlshortener.model.UrlMapping;
import com.urlshortener.repository.UrlMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


/**
 * This class represents core business logic layer for our URL shortener.
 *
 * The @Service annotation marks this class as a Spring service component.
 * It's a specialized form of the @Component annotation, used to indicate that
 * it's holding business logic. As a Spring-managed bean, an instance of this
 * class will be created by the Spring container and can be injected into
 * other components (like our controllers).
 *
 * The @RequiredArgConstructor is a Lombok annotation that generates a constructor
 * at compile-time for all fields that required special attention when
 * the object is created. It specifically targets final fields (which must be
 * initialized when the object is created.) and fields marked with @NonNull
 * (which must be checked for nullability).
 *
 * This service will be responsible for orchestrating the creation of short URLs:
 * which involves interacting with the repository to persist URL mappings.
 */

@Service
@RequiredArgsConstructor
public class UrlShortenerService {

    /**
     * This field will hold the reference to our repository.
     * 'private': It's an internal details of this service, so we keep it private.
     * 'final': We declare it as 'final' because it's a required dependency that
     *          will not change after the service is created. This is a best practice
     *          for immutability and thread safety.
     */

    private final UrlMappingRepository urlMappingRepository;

    //A constant holding all the characters for our base-62 encoding.
    // It is 'static' and 'final' because it's a constant value that never changes
    // and is shared across all instances of this service.
    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";


    /**
     * Shortens a given URL.
     *
     * If a customAlias is provided, it will be used as the shortCode.
     * If customAlias is null or empty, a unique shortCode will be generated.
     *
     * @param originalUrl The long URL to shorten.
     * @param customAlias An optional user-defined short code.
     * @return The final shortCode (either the custom alias or the generated one).
     */
    @Transactional
    public String shortenUrl(String originalUrl, String customAlias) {

        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setOriginalUrl(originalUrl);
        urlMapping.setCreationDate(LocalDateTime.now());

        UrlMapping savedEntity = urlMappingRepository.save(urlMapping);

        // This generated code will be our fallback if no custom alias is provided.
        String shortCode = encodeBase62(savedEntity.getId());
        savedEntity.setShortCode(shortCode);

        urlMappingRepository.save(savedEntity);

        return shortCode;
    }



    /**
     * Finds the original URL for a given short code and increments its click count.
     * The @Transactional annotation ensures this is an atomic operation.
     *
     * @param shortCode The unique code representing the shortened URL.
     * @return The original, long URL to redirect to.
     * @throws UrlNotFoundException if the short code does not exist in the database.
     */
    @Transactional
    public String getOriginalUrlAndIncrementClicks(String shortCode) {
        UrlMapping urlMapping = urlMappingRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("URL not found for short code: " + shortCode));

        urlMapping.setClickCount(urlMapping.getClickCount() + 1);
        urlMappingRepository.save(urlMapping);

        return urlMapping.getOriginalUrl();
    }


    /**
     * Retrieves statistics for a given short code.
     * This is a read-only operation and doesn't need to be @Transactional by itself,
     * but adding it is harmless and keeps it consistent with other data-access methods.
     *
     * @param shortCode The unique code to look up.
     * @return A UrlStatsResponse DTO containing the statistics.
     * @throws UrlNotFoundException if the short code does not exist.
     */
    public UrlStatsResponse getStats(String shortCode) {
        UrlMapping urlMapping = urlMappingRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("No statistics found for short code: " + shortCode));

        String fullShortUrl = "http://localhost:8080/" + urlMapping.getShortCode();

        return new UrlStatsResponse(
                urlMapping.getOriginalUrl(),
                fullShortUrl,
                urlMapping.getCreationDate(),
                urlMapping.getClickCount()
        );
    }

    // --- NEWLY ADDED METHOD END ---


    /**
     * A private utility method to convert a base-10 number (our database ID)
     * into a base-62 string.
     * Base-62 uses characters 0-9, A-Z, a-z, making it URL-safe and compact.
     *
     * @param number The unique ID of the URL mapping (a positive long).
     * @return The base-62 encoded string.
     */

    private String encodeBase62(Long number) {
        if (number == 0) {
            return String.valueOf(BASE62_CHARS.charAt(0));
        }

        StringBuilder sb = new StringBuilder();
        long num = number;

        while (num > 0) {
            int remainder = (int) (num % 62);
            sb.append(BASE62_CHARS.charAt(remainder));
            num /= 62;
        }

        return sb.reverse().toString();
    }

}
