package com.urlshortener.repository;

import com.urlshortener.model.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * This interface serves as our Data Access Layer for the UrlMapping entity.
 * By extending JpaRepository, we inherit a rich set of CURD (Create,
 * Update, Read, Delete) methods and other common data access operations without
 * needing to write any implementation code.
 *
 * Spring Data JPA will automatically create a proxy implementation of this
 * interface a runtime. This implementation can be injected into other
 * Spring Components, like our service classes.
 *
 * The generic parameters <UrlMapping, Long> specifies that this repository is
 * for managing 'UrlMapping' entities, and the type of the entity's
 * primary key is 'Long'.
 */
@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {

    /**
     * THis is a custom derived query method. Spring Data JPA will automatically
     * generate a query based on the method name.
     *
     * How it works:
     * - "find": This is the introductory keyword.
     * - "By": This keyword separates the find operation from the criteria.
     * - "ShortCode": This is the property name in our UrlMapping entity. Spring Data JPA
     *                parses this and understands it needs to create a query that filters
     *                by the 'shortCode' field. The field name must match exactly.
     *
     * The generated SQL query will be equivalent to:
     * "SELECT * FROM url_mapping WHERE short_code = ?"
     *
     * The method returns an Optional<UrlMapping>, which is a modern, robust way
     * to handle cases where a result may or may not be found, preventing NullPointerExceptions.
     *
     * @param shortCode The short code to search for in the database.
     * @return An Optional containing the UrlMapping if found, or an empty Optional otherwise.
     */
    Optional<UrlMapping> findByShortCode(String shortCode);
}