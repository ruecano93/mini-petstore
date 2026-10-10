package com.example.petstore.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.petstore.mapper.PetMapper;
import com.example.petstore.model.Pet;
import com.example.petstore.service.PetService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PetControllerTest {

    @Mock
    private PetService petService;

    @Mock
    private PetMapper petMapper;

    @InjectMocks
    private PetController petController;

    private Pet samplePet;
    private Map<String, Object> sampleDto;

    @BeforeEach
    void setUp() {
        samplePet = new Pet("Buddy", "Dog", "Beagle", LocalDate.of(2020, 1, 1), 150.0);
        samplePet.setAvailable(true);
        sampleDto = Map.of(
                "name", "Buddy",
                "species", "Dog",
                "breed", "Beagle",
                "birthDate", LocalDate.of(2020, 1, 1),
                "price", 150.0,
                "available", true
        );
    }

    @Test
    void listAll_whenCalled_returnsListOfPetDtos() {
        // Arrange
        List<Pet> pets = List.of(samplePet);
        when(petService.findAll()).thenReturn(pets);
        doReturn(sampleDto).when(petMapper).toDto(samplePet);

        // Act
        List<Map<String, Object>> result = petController.listAll();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(sampleDto);
    }

    @Test
    void update_whenIdAndPetValid_returnsUpdatedPetDto() {
        // Arrange
        Long id = 1L;
        Pet updatedPet = new Pet("Buddy", "Dog", "Beagle", LocalDate.of(2020, 1, 1), 150.0);
        updatedPet.setAvailable(true);
        when(petService.update(id, samplePet)).thenReturn(updatedPet);
        doReturn(sampleDto).when(petMapper).toDto(updatedPet);

        // Act
        Map<String, Object> result = petController.update(id, samplePet);

        // Assert
        assertThat(result).isEqualTo(sampleDto);
    }

    @Test
    void delete_whenIdExists_invokesServiceDelete() {
        // Arrange
        Long id = 1L;

        // Act
        petController.delete(id);

        // Assert
        verify(petService, times(1)).delete(id);
    }

    @Test
    void markUnavailable_whenIdExists_returnsUpdatedPetDto() {
        // Arrange
        Long id = 1L;
        Pet unavailablePet = new Pet("Buddy", "Dog", "Beagle", LocalDate.of(2020, 1, 1), 150.0);
        unavailablePet.setAvailable(false);
        when(petService.markUnavailable(id)).thenReturn(unavailablePet);
        doReturn(sampleDto).when(petMapper).toDto(unavailablePet);

        // Act
        Map<String, Object> result = petController.markUnavailable(id);

        // Assert
        assertThat(result).isEqualTo(sampleDto);
    }
}
