package com.example.petstore.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
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
import org.springframework.http.HttpStatus;
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
    private Map<String, Object> petDto1;
    private Map<String, Object> petDto2;

    @BeforeEach
    void setUp() {
        pet1 = new Pet();
        pet2 = new Pet();
        petDto1 = Map.of("id", 1L, "name", "Fido");
        petDto2 = Map.of("id", 2L, "name", "Whiskers");
    }

    @Test
    void listAll_whenCalled_shouldReturnListOfPetDtos() {
        // Arrange
        List<Pet> pets = List.of(pet1, pet2);
        when(petService.findAll()).thenReturn(pets);
        when(petMapper.toDto(pet1)).thenReturn(petDto1);
        when(petMapper.toDto(pet2)).thenReturn(petDto2);

        // Act
        List<Map<String, Object>> result = petController.listAll();

        // Assert
        assertThat(result).containsExactly(petDto1, petDto2);
    }

    @Test
    void listAvailable_whenCalled_shouldReturnListOfAvailablePetDtos() {
        // Arrange
        List<Pet> availablePets = List.of(pet1);
        when(petService.findAvailable()).thenReturn(availablePets);
        when(petMapper.toDto(pet1)).thenReturn(petDto1);

        // Act
        List<Map<String, Object>> result = petController.listAvailable();

        // Assert
        assertThat(result).containsExactly(petDto1);
    }

    @Test
    void bySpecies_whenSpeciesExists_shouldReturnListOfPetDtosForSpecies() {
        // Arrange
        String species = "dog";
        List<Pet> petsBySpecies = List.of(pet1);
        when(petService.findBySpecies(species)).thenReturn(petsBySpecies);
        when(petMapper.toDto(pet1)).thenReturn(petDto1);

        // Act
        List<Map<String, Object>> result = petController.bySpecies(species);

        // Assert
        assertThat(result).containsExactly(petDto1);
    }

    @Test
    void getById_whenIdExists_shouldReturnPetDtoInResponseEntity() {
        // Arrange
        Long id = 1L;
        when(petService.findById(id)).thenReturn(pet1);
        when(petMapper.toDto(pet1)).thenReturn(petDto1);

        // Act
        ResponseEntity<Map<String, Object>> response = petController.getById(id);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(petDto1);
    }

    @Test
    void create_whenValidPet_shouldReturnCreatedPetDto() {
        // Arrange
        when(petService.create(pet1)).thenReturn(pet1);
        when(petMapper.toDto(pet1)).thenReturn(petDto1);

        // Act
        Map<String, Object> result = petController.create(pet1);

        // Assert
        assertThat(result).isEqualTo(petDto1);
    }

    @Test
    void update_whenValidIdAndPet_shouldReturnUpdatedPetDto() {
        // Arrange
        Long id = 1L;
        when(petService.update(id, pet1)).thenReturn(pet1);
        when(petMapper.toDto(pet1)).thenReturn(petDto1);

        // Act
        Map<String, Object> result = petController.update(id, pet1);

        // Assert
        assertThat(result).isEqualTo(petDto1);
    }

    @Test
    void delete_whenValidId_shouldInvokeServiceDelete() {
        // Arrange
        Long id = 1L;
        doNothing().when(petService).delete(id);

        // Act / Assert
        assertThatCode(() -> petController.delete(id)).doesNotThrowAnyException();
        verify(petService).delete(id);
    }

    @Test
    void markUnavailable_whenValidId_shouldReturnUpdatedPetDto() {
        // Arrange
        Long id = 1L;
        when(petService.markUnavailable(id)).thenReturn(pet1);
        when(petMapper.toDto(pet1)).thenReturn(petDto1);

        // Act
        Map<String, Object> result = petController.markUnavailable(id);

        // Assert
        assertThat(result).isEqualTo(petDto1);
    }
}
