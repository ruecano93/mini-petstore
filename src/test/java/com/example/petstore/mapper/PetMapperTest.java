package com.example.petstore.mapper;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.petstore.model.Pet;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PetMapperTest {

    private PetMapper petMapper;

    @BeforeEach
    void setUp() {
        petMapper = new PetMapper();
    }

    @Test
    void fromDto_mapWithNonNumberPrice_throwsClassCastException() {
        // Arrange
        Map<String, Object> dto = Map.of(
                "name", "Buddy",
                "species", "Dog",
                "breed", "Beagle",
                "price", "notANumber",
                "available", true
        );

        // Act & Assert
        assertThatThrownBy(() -> petMapper.fromDto(dto))
                .isInstanceOf(ClassCastException.class);
    }

    @Test
    void fromDto_mapWithNonBooleanAvailable_throwsClassCastException() {
        // Arrange
        Map<String, Object> dto = Map.of(
                "name", "Buddy",
                "species", "Dog",
                "breed", "Beagle",
                "price", 100.0,
                "available", "notABoolean"
        );

        // Act & Assert
        assertThatThrownBy(() -> petMapper.fromDto(dto))
                .isInstanceOf(ClassCastException.class);
    }
}
