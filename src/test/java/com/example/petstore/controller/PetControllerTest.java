package com.example.petstore.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;

import com.example.petstore.mapper.PetMapper;
import com.example.petstore.model.Pet;
import com.example.petstore.service.PetService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class PetControllerTest {

    @Mock
    private PetService petService;

    @Mock
    private PetMapper petMapper;

    @InjectMocks
    private PetController petController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listAll_whenCalled_returnsListOfPetDtos() {
        // Arrange
        Pet pet1 = new Pet();
        Pet pet2 = new Pet();
        List<Pet> pets = List.of(pet1, pet2);
        when(petService.findAll()).thenReturn(pets);
        Map<String, Object> dto1 = Map.of("id", 1);
        Map<String, Object> dto2 = Map.of("id", 2);
        when(petMapper.toDto(pet1)).thenReturn(dto1);
        when(petMapper.toDto(pet2)).thenReturn(dto2);

        // Act
        List<Map<String, Object>> result = petController.listAll();

        // Assert
        assertThat(result).containsExactly(dto1, dto2);
        verify(petService).findAll();
        verify(petMapper).toDto(pet1);
        verify(petMapper).toDto(pet2);
    }

    @Test
    void listAvailable_whenCalled_returnsListOfAvailablePetDtos() {
        // Arrange
        Pet pet1 = new Pet();
        Pet pet2 = new Pet();
        List<Pet> availablePets = List.of(pet1, pet2);
        when(petService.findAvailable()).thenReturn(availablePets);
        Map<String, Object> dto1 = Map.of("id", 10);
        Map<String, Object> dto2 = Map.of("id", 20);
        when(petMapper.toDto(pet1)).thenReturn(dto1);
        when(petMapper.toDto(pet2)).thenReturn(dto2);

        // Act
        List<Map<String, Object>> result = petController.listAvailable();

        // Assert
        assertThat(result).containsExactly(dto1, dto2);
        verify(petService).findAvailable();
        verify(petMapper).toDto(pet1);
        verify(petMapper).toDto(pet2);
    }

    @Test
    void bySpecies_whenSpeciesExists_returnsListOfPetDtosForSpecies() {
        // Arrange
        String species = "dog";
        Pet pet1 = new Pet();
        Pet pet2 = new Pet();
        List<Pet> petsBySpecies = List.of(pet1, pet2);
        when(petService.findBySpecies(species)).thenReturn(petsBySpecies);
        Map<String, Object> dto1 = Map.of("species", species, "id", 100);
        Map<String, Object> dto2 = Map.of("species", species, "id", 101);
        when(petMapper.toDto(pet1)).thenReturn(dto1);
        when(petMapper.toDto(pet2)).thenReturn(dto2);

        // Act
        List<Map<String, Object>> result = petController.bySpecies(species);

        // Assert
        assertThat(result).containsExactly(dto1, dto2);
        verify(petService).findBySpecies(species);
        verify(petMapper).toDto(pet1);
        verify(petMapper).toDto(pet2);
    }

    @Test
    void getById_whenIdExists_returnsPetDtoInResponseEntity() {
        // Arrange
        Long id = 5L;
        Pet pet = new Pet();
        when(petService.findById(id)).thenReturn(pet);
        Map<String, Object> dto = Map.of("id", id);
        when(petMapper.toDto(pet)).thenReturn(dto);

        // Act
        ResponseEntity<Map<String, Object>> response = petController.getById(id);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dto);
        verify(petService).findById(id);
        verify(petMapper).toDto(pet);
    }

    @Test
    void create_whenPetValid_returnsCreatedPetDto() {
        // Arrange
        Pet petToCreate = new Pet();
        Pet createdPet = new Pet();
        when(petService.create(petToCreate)).thenReturn(createdPet);
        Map<String, Object> dto = Map.of("created", true);
        when(petMapper.toDto(createdPet)).thenReturn(dto);

        // Act
        Map<String, Object> result = petController.create(petToCreate);

        // Assert
        assertThat(result).isEqualTo(dto);
        verify(petService).create(petToCreate);
        verify(petMapper).toDto(createdPet);
    }

    @Test
    void update_whenIdAndPetValid_returnsUpdatedPetDto() {
        // Arrange
        Long id = 7L;
        Pet petToUpdate = new Pet();
        Pet updatedPet = new Pet();
        when(petService.update(id, petToUpdate)).thenReturn(updatedPet);
        Map<String, Object> dto = Map.of("updated", true);
        when(petMapper.toDto(updatedPet)).thenReturn(dto);

        // Act
        Map<String, Object> result = petController.update(id, petToUpdate);

        // Assert
        assertThat(result).isEqualTo(dto);
        verify(petService).update(id, petToUpdate);
        verify(petMapper).toDto(updatedPet);
    }

    @Test
    void delete_whenIdExists_deletesPetWithoutReturn() {
        // Arrange
        Long id = 9L;
        doNothing().when(petService).delete(id);

        // Act / Assert
        assertThatCode(() -> petController.delete(id)).doesNotThrowAnyException();
        verify(petService).delete(id);
    }

    @Test
    void markUnavailable_whenIdExists_returnsUpdatedPetDto() {
        // Arrange
        Long id = 11L;
        Pet updatedPet = new Pet();
        when(petService.markUnavailable(id)).thenReturn(updatedPet);
        Map<String, Object> dto = Map.of("available", false);
        when(petMapper.toDto(updatedPet)).thenReturn(dto);

        // Act
        Map<String, Object> result = petController.markUnavailable(id);

        // Assert
        assertThat(result).isEqualTo(dto);
        verify(petService).markUnavailable(id);
        verify(petMapper).toDto(updatedPet);
    }
}
