package com.example.petstore.exception;

import static org.assertj.core.api.Assertions.assertThat;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PetNotFoundExceptionTest {

    @Test
    void constructorWithId_shouldCreateExceptionWithMessageContainingId() {
        // Arrange
        Long id = 123L;

        // Act
        PetNotFoundException exception = new PetNotFoundException(id);

        // Assert
        assertThat(exception).isInstanceOf(PetNotFoundException.class);
        assertThat(exception.getMessage()).contains(String.valueOf(id));
    }

    @Test
    void constructorWithMessage_shouldCreateExceptionWithGivenMessage() {
        // Arrange
        String message = "Custom not found message";

        // Act
        PetNotFoundException exception = new PetNotFoundException(message);

        // Assert
        assertThat(exception).isInstanceOf(PetNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(message);
    }
}
