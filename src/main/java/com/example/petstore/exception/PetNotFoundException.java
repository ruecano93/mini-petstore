package com.example.petstore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a pet with the given ID cannot be found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class PetNotFoundException extends RuntimeException {

    public PetNotFoundException(Long id) {
        super("Pet not found with id: " + id);
    }

    public PetNotFoundException(String message) {
        super(message);
    }
}
