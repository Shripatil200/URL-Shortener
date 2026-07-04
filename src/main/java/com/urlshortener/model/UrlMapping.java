package com.urlshortener.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * This class represents core data model for our URL Shortener.
 *
 * The @Entity annotation is the most fundamental JPA annotation. It marks this
 * Java class as manageable entity for the persistence framework (Hibernate).
 * This means hibernate will be responsible for mapping instances of this class
 * to rows in a database table.
 *
 * By convention, Hibernate will create a table named after the class name in snake_case,
 * so this entity will be mapped to a table named 'url_mapping'.
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UrlMapping {


    /**
    * The unique identifier for each URL mapping. This will serve as Private Key
    * in our database table. A Primary Key is a special column that uniquely
    * identifies each record (row) in a table.
    * We use 'Long' wrapper class instead of the primitive 'long'. This allows the
    * id to be 'null' before the entity is first saved to the database. JPA
    * uses this null state to determine if an entity is new or already exists.
     *
     * @Id: This annotation, from jakarta.persistence, explicitly marks this field
     *      as the Primary Key of the entity. Every entity MUST have a primary key.
     *
     * @GeneratedValue: This annotation specifies that the primary key value will be
     *                  generated automatically. We don't need to set it manually.
     *      strategy = GenerationType.IDENTITY: This strategy tells Hibernate to rely
     *      on an auto incrementing column in the database. When we save a new entity,
     *      the database assigns the next available ID. This is a common and efficient
     *      strategy for many databases, incliding H2, MySQL, and PostgreSQL.
    */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The original, full-length URL that the user want to shorten.
     *
     * @Lob: This annotation specifies that the field should be persisted as a
     *       Large Object. For a String field like this one, it tells Hibernate
     *       to use a database column type suitable for storing very long strings,
     *       such as CLOB (Character Large Object) or TEXT, instead of standard
     *       VARCHAR which has a size limit. This makes our application robust
     *       against error caused by exceptionally long URLs.
     */
    @Lob
    @Column(nullable = false)
    private String originalUrl;

    /**
     * The generated unique short code that maps to the original URL.
     * This is a core part of our short link, for example, the 'xyz123' in
     * a URL like '<a href="http://sho.rt/xyz123">...</a>'.
     *
     * @Column(unique = true): This is a critical instruction for data integrity.
     * It tells the persistence provider (Hibernate) to generate a database schema
     * where the 'short_code' column has a UNIQUE constraint. This means that database
     * itself will enforce the rule that no two rows can have the same shotCode.
     * This ius the ultimate safeguard against duplicate short links, which would
     * break the functionality of our application.
     */
    @Column(unique = true)
    private String shortCode;

    /**
     * The timestamp indicating when this URL mapping was created.
     * We use java.time.LocalDateTime, which is the modern, standard Java API
     * for representing date and time without a time zone. JPA has excellent
     * built-in support for persisting this type to an appropriate database column
     * (e.g., TIMESTAMP).
     */
    private LocalDateTime creationDate;

    /**
     * A counter to track how many times the short link has been assessed or clicked.
     * We use primitive 'long' type here. Unlike 'Long', a primitive 'long' cannot
     * be null and default to 0. This is perfect for a counter, as a link that
     * has never been clicked should have count of 0, not null.
     */

    private long clickCount;


}