package com.urlshortener.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

/**
 * The DTO (Data Transfer Object) for incoming URL shortening requests.
 * As a Java 'record', it's an immutable carrier for our request data.
 *
 * @param url         The original, long URL to be shortened. This is mandatory.
 *                    - @NotEmpty ensures the URL is not null and not an empty string.
 *                    - @URL ensures the string is a well-formed URL.
 * @param customAlias An OPTIONAL user-defined alias for the short URL.
 *                    If this is null or empty, the service will generate a random short code.
 *                    If it's provided, the service will attempt to use it as the short code.
 * @param hoursToExpire An OPTIONAL time-to-live (TTL) in hours. If provided, the link
 *                      will expire after this many hours. If null, the link is permanent.
 */
public record ShortenUrlRequest (
        @NotEmpty(message = "URL cannot be empty")

        @URL(message="A valid URL format is required")
        String url,
        String customAlias,
        @Min(value = 1, message = "Hours to expire must be a positive number")
        Integer hoursToExpire
){
}
