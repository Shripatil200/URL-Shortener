package com.urlshortener.controller;

import com.urlshortener.dto.ShortenUrlRequest;
import com.urlshortener.dto.ShortenUrlResponse;
import com.urlshortener.service.UrlShortenerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * This class wil serve as the REST Controller for all URL-related operations.
 * A controller in Spring is responsible for handling web requests.
 *
 * This class will act as the entry point for our API, receiving request from users,
 * delegating the business logic to the UrlShortenerService, and returning the
 * result.
 */

@RequiredArgsConstructor
@RestController
public class UrlController {

    private final UrlShortenerService urlShortenerService;

    /**
     * This method handles the creation of a new short URL.
     *
     * @PostMapping("api/v1/url/shorten"): Maps HTTP POST requests sent to /api/v1/url/shorten to this method.
     * @param request The incoming request body, which Spring automatically deserializes from JSON
     *                into our ShortenUrlRequest DTO.
     * @Valid: This annotation triggers the validation rules we defined in the ShortenUrlRequest
     *         record (e.g., @NotEmpty, @URL). If validation fails, Spring automatically
     *         returns a 400 Bad Request error before our method is even called.
     * @return A ResponseEntity containing the ShortenUrlResponse DTO and an HTTP status of 201 Created.
     */
    @PostMapping("api/v1/url/shorten")
    public ResponseEntity<ShortenUrlResponse> shortenUrl(@Valid @RequestBody ShortenUrlRequest request) {
        String shortCode = urlShortenerService.shortenUrl(request.url());

        String fullShortUrl = "http://localhost:8080/" + shortCode;

        ShortenUrlResponse response = new ShortenUrlResponse(fullShortUrl);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    /**
     * This method handles the redirection from a short URL to the original URL.
     *
     * @GetMapping("/{shortCode}"): This annotation maps HTTP GET requests to this method.
     * The path "/{shortCode}" contains a path variable. This means it will match any GET
     * request to the root path followed by some value, e.g., /aB1cDe, /xYz123.
     *
     * @PathVariable String shortCode: This annotation tells Spring to take the value from the
     * {shortCode} placeholder in the URL and inject it into the 'shortCode' method parameter.
     * For a request to /aB1cDe, the value of the 'shortCode' parameter will be "aB1cDe".
     *
     * @return A ResponseEntity<Void>. For a redirect, we don't send a body, so the generic type
     * is Void. The entire response is communicated through the HTTP status code (302 Found)
     * and the 'Location' header, which we will implement in a later task.
     */
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String originalUrl = urlShortenerService.getOriginalUrlAndIncrementClicks(shortCode);

        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(originalUrl)).build();
    }
}
