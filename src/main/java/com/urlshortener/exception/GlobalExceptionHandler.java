package com.urlshortener.exception;

// NEW: Import necessary classes for the handler
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // --- NEWLY ADDED METHOD START ---

    /**
     * This method is a dedicated handler for the UrlNotFoundException.
     *
     * @ExceptionHandler(UrlNotFoundException.class): This annotation registers this method
     *   as the specific handler for any UrlNotFoundException thrown by any controller.
     *
     * @param ex The actual exception object that was thrown. Spring injects this for us,
     *           so we can use its properties, like the error message.
     * @param request The web request during which the exception occurred. We can use this
     *                to get more context if needed.
     * @return A ResponseEntity that encapsulates the HTTP response. We build a structured
     *         response with a 404 status code and a JSON body containing error details.
     */
    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<Object> handleUrlNotFoundException(UrlNotFoundException ex, WebRequest request) {

        // Create a structured, map-based body for our JSON response.
        // Using a LinkedHashMap preserves the insertion order of the keys.
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value()); // e.g., 404
        body.put("error", "Not Found");
        body.put("message", ex.getMessage()); // Get the specific message from the exception
        body.put("path", request.getDescription(false).replace("uri=", "")); // Get the path where the error occurred

        // Return the ResponseEntity with the body and the HTTP status.
        // Spring's Jackson integration will automatically convert the Map to a JSON string.
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
}
