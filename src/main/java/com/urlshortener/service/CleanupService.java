package com.urlshortener.service;

import com.urlshortener.repository.UrlMappingRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * A service dedicated to performing background, scheduled tasks for the application.
 *
 * This service is responsible for system maintenance duties, such as cleaning up
 * expired data. Creating a separate service for this adheres to the Separation
 * of Concerns principle, keeping the user-facing logic in UrlShortenerService
 * separate from the internal, automated logic here.
 *
 * @Service: This annotation marks the class as a Spring-managed service bean.
 * Spring's component scanner will detect it and register it in the application
 * context, making it eligible for features like dependency injection and scheduling.
 */
@Service
@RequiredArgsConstructor
public class CleanupService {

    private static final Logger logger = LoggerFactory.getLogger(CleanupService.class);

    private final UrlMappingRepository urlMappingRepository;

    /**
     * A scheduled job that runs automatically to clean up expired URL mappings.
     *
     * @Scheduled: This annotation marks the method as a scheduled task.
     *   - cron = "0 0 1 * * ?": This cron expression configures the task to run
     *     every day at 1:00 AM.
     *     - Second: 0
     *     - Minute: 0
     *     - Hour: 1
     *     - Day of Month: * (any)
     *     - Month: * (any)
     *     - Day of Week: ? (not specified)
     *
     * This method will be executed by Spring's task scheduler in a background thread.
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void cleanupExpiredUrls() {
        logger.info("Starting scheduled job: Cleaning up expired URL mappings...");

        LocalDateTime now = LocalDateTime.now();

        long deletedCount = urlMappingRepository.deleteByExpirationDateBefore(now);

        if (deletedCount > 0) {
            logger.info("Finished scheduled job: Successfully deleted {} expired URL mappings.", deletedCount);
        } else {
            logger.info("Finished scheduled job: No expired URL mappings found to delete.");
        }
    }

}