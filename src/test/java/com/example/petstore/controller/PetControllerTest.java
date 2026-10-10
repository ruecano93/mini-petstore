package com.example.petstore.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.petstore.mapper.PetMapper;
import com.example.petstore.model.Pet;
import com.example.petstore.service.PetService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class PetControllerTest {

    @Mock
    private PetService petService;

    @Mock
    private PetMapper petMapper;

    @InjectMocks
    private PetController petController;

    private Pet pet1;
    private Pet pet2;
    private Map<String, Object> dto1;
    private Map<String, Object> dto2;

    @BeforeEach
    void setUp() {
        pet1 = new Pet("Buddy", "dog", "Labrador", null, 150.0);
        pet2 = new Pet("Mittens", "cat", "Siamese", null, 100.0);

        dto1 = Map.of("name", "Buddy", "species", "dog");
        dto2 = Map.of("name", "Mittens", "species", "cat");
    }

    @Test
    void listAll_whenCalled_returnsListOfPetDtos() {
        // Arrange
        List<Pet> pets = List.of(pet1, pet2);
        doReturn(pets).when(petService).findAll();
        when(petMapper.toDto(pet1)).thenReturn(dto1);
        when(petMapper.toDto(pet2)).thenReturn(dto2);

        // Act
        List<Map<String, Object>> result = petController.listAll();

        // Assert
        assertThat(result).containsExactly(dto1, dto2);
    }

    @Test
    void listAvailable_whenCalled_returnsListOfAvailablePetDtos() {
        // Arrange
        List<Pet> availablePets = List.of(pet1);
        doReturn(availablePets).when(petService).findAvailable();
        when(petMapper.toDto(pet1)).thenReturn(dto1);

        // Act
        List<Map<String, Object>> result = petController.listAvailable();

        // Assert
        assertThat(result).containsExactly(dto1);
    }

    @Test
    void bySpecies_whenSpeciesExists_returnsListOfPetDtos() {
        // Arrange
        String species = "dog";
        List<Pet> petsBySpecies = List.of(pet1);
        doReturn(petsBySpecies).when(petService).findBySpecies(species);
        when(petMapper.toDto(pet1)).thenReturn(dto1);

        // Act
        List<Map<String, Object>> result = petController.bySpecies(species);

        // Assert
        assertThat(result).containsExactly(dto1);
    }

    @Test
    void getById_whenIdExists_returnsPetDtoInResponseEntity() {
        // Arrange
        Long id = 1L;
        doReturn(pet1).when(petService).findById(id);
        when(petMapper.toDto(pet1)).thenReturn(dto1);

        // Act
        ResponseEntity<Map<String, Object>> response = petController.getById(id);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(dto1);
    }

    @Test
    void create_whenPetValid_returnsCreatedPetDto() {
        // Arrange
        doReturn(pet1).when(petService).create(pet1);
        when(petMapper.toDto(pet1)).thenReturn(dto1);

        // Act
        Map<String, Object> result = petController.create(pet1);

        // Assert
        assertThat(result).isEqualTo(dto1);
    }

    @Test
    void update_whenIdExistsAndPetValid_returnsUpdatedPetDto() {
        // Arrange
        Long id = 1L;
        doReturn(pet1).when(petService).update(id, pet1);
        when(petMapper.toDto(pet1)).thenReturn(dto1);

        // Act
        Map<String, Object> result = petController.update(id, pet1);

        // Assert
        assertThat(result).isEqualTo(dto1);
    }

    @Test
    void delete_whenIdExists_invokesServiceDelete() {
        // Arrange
        Long id = 1L;

        // Act
        petController.delete(id);

        // Assert
        verify(petService).delete(id);
    }

    @Test
    void markUnavailable_whenIdExists_returnsUpdatedPetDto() {
        // Arrange
        Long id = 1L;
        doReturn(pet1).when(petService).markUnavailable(id);
        when(petMapper.toDto(pet1)).thenReturn(dto1);

        // Act
        Map<String, Object> result = petController.markUnavailable(id);

        // Assert
        assertThat(result).isEqualTo(dto1);
    }
}
