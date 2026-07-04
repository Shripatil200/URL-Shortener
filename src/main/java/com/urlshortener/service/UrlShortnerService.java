package com.urlshortener.service;

// The 'stereotype' package contains annotations that define the role of beans.
import com.urlshortener.model.UrlMapping;
import com.urlshortener.repository.UrlMappingRepository;
import lombok.AllArgsConstructor;
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
public class UrlShortnerService {

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
     *  The core business logic for creating a short URL.
     *  This method will orchestrate the entire process:
     *  1. Create a UrlMapping entity.
     *  2. Save it to the database to generate a unique primary key (ID).
     *  3. Convert this ID into a unique base-62 short code.
     *  4. Update the entity with the new short code.
     *  5. Return the generated short code.
     *
     *  By default, repository methods are transactional. However, our method orchestrates
     *  multiple database operations. Wrapping it in @Transactional ensures that these
     *  operations are executed as a single, atomic unit. If any part fails, all
     *  previous operations in the method are rolled back.
     *
     * @param originalUrl The long URL that needs to be shortened. This is the input from the user.
     * @return The generated unique short code (e.g., "aB1cDe") corresponding to the originalUrl.
     */

    @Transactional
    public String shortenUrl(String originalUrl){

        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setOriginalUrl(originalUrl);
        urlMapping.setCreationDate(LocalDateTime.now());

        UrlMapping savedEntity = urlMappingRepository.save(urlMapping);

        /*
        Now we take unique ID from the saved entity and convert it
        into our base-62 short code.
         */
        String shortCode = encodedBase62(savedEntity.getId());

        /*
        We are now updating our managed entity instance with the generated short code.
        The 'setShortCode()' method was automatically generated for us by lombok's @Setter annotation.
        By calling this method, we are changing the state of the 'savedEntity' object in memory.
        JPA's Persistence Context will detect this change (a process called dirty checking).
         */

        savedEntity.setShortCode(shortCode);

        urlMappingRepository.save(savedEntity);
        return shortCode;

    }


    /**
     * A private utility method to convert a base-10 number (our database ID)
     * into a base-62 string.
     * Base-62 uses characters 0-9, A-Z, a-z, making it URL-safe and compact.
     *
     * @param number The unique ID of the URL mapping (a positive long).
     * @return The base-62 encoded string.
     */

    private String encodedBase62(Long number){
        //if number is 0 return the first character of our character set.
        if(0 == number){
            return String.valueOf(BASE62_CHARS.charAt(0));
        }

        // StringBuilder is highly efficient for concatenating string in a loop.
        StringBuilder sb = new StringBuilder();
        long num = number; // Use a mutable copy of the number for our calculations.

        // This is the core base conversion algorithm
        while(num > 0) {
            // The modulo operator (%) gives the reminder of a division.
            // This reminder is our index into the BASE62_CHARS string.

            int reminder = (int) (num % 62);

            // append the character corresponding to the reminder to our result.
            sb.append(BASE62_CHARS.charAt(reminder));

            // Perform integer division by 62 to prepare for the next iteration.
            num /= 62;
        }

        // The algorithm builds the string in reverse order (from lest significant
        // "digit" to most significant). We must reverse it before returning.
        // For example, the number 62 would produce "01" during the loop, which
        // needs to be reversed to the corrected "10" representation.
        return sb.reverse().toString();
    }

}
