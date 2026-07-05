package com.urlshortener.dto;

import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

/**
 * A Data ATransfer Object (DTO) that represent the request for shortening a URL.
 * We use a Java Record here for its conciseness and immutability. Records automatically
 * provide a constructor, getters for all fields, and proper equals(), hashCode(), and
 * toString() methods.
 *
 * This record defines the JSON contract for our API's shorten endpoint. An incoming
 * request body must be a JSON object with a key named "url".
 * For example:
 * {
 *     "url": "https://www.verylongurlto-make-short.com/with/lots/of/params"
 * }
 *
 * @param url The original, long URL that the user wants to shorten. We add validation
 *            annotations to ensure the URL is not empty and is well-formed.
 */
public record ShortenUrlRequest (
        // The @NotEmpty annotation ensures that the provided URL string is not null and not empty.
        @NotEmpty(message = "URL cannot be empty")
        // THe @URL annotation from Hibernate Validator provides a robust check to ensure
        // the string is a validly formatted URL.
        @URL(message="A valid URL format is required")
        String url
){
}
