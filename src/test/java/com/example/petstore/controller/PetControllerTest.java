package com.example.petstore.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.example.petstore.mapper.PetMapper;
import com.example.petstore.model.Pet;
import com.example.petstore.service.PetService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
        Map<String, Object> dto1 = Map.of("id", 1L);
        Map<String, Object> dto2 = Map.of("id", 2L);

        when(petService.findAll()).thenReturn(pets);
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
        List<Pet> pets = List.of(pet1, pet2);
        Map<String, Object> dto1 = Map.of("available", true);
        Map<String, Object> dto2 = Map.of("available", true);

        when(petService.findAvailable()).thenReturn(pets);
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
    void bySpecies_whenSpeciesExists_returnsListOfPetDtos() {
        // Arrange
        String species = "dog";
        Pet pet1 = new Pet();
        Pet pet2 = new Pet();
        List<Pet> pets = List.of(pet1, pet2);
        Map<String, Object> dto1 = Map.of("species", species);
        Map<String, Object> dto2 = Map.of("species", species);

        when(petService.findBySpecies(species)).thenReturn(pets);
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
        Long id = 1L;
        Pet pet = new Pet();
        Map<String, Object> dto = Map.of("id", id);

        when(petService.findById(id)).thenReturn(pet);
        when(petMapper.toDto(pet)).thenReturn(dto);

        // Act
        ResponseEntity<Map<String, Object>> response = petController.getById(id);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(dto);
        verify(petService).findById(id);
        verify(petMapper).toDto(pet);
    }

    @Test
    void create_whenValidPet_returnsCreatedPetDto() {
        // Arrange
        Pet pet = new Pet();
        Pet createdPet = new Pet();
        Map<String, Object> dto = Map.of("id", 1L);

        when(petService.create(pet)).thenReturn(createdPet);
        when(petMapper.toDto(createdPet)).thenReturn(dto);

        // Act
        Map<String, Object> result = petController.create(pet);

        // Assert
        assertThat(result).isEqualTo(dto);
        verify(petService).create(pet);
        verify(petMapper).toDto(createdPet);
    }

    @Test
    void update_whenIdAndPetValid_returnsUpdatedPetDto() {
        // Arrange
        Long id = 1L;
        Pet pet = new Pet();
        Pet updatedPet = new Pet();
        Map<String, Object> dto = Map.of("id", id);

        when(petService.update(id, pet)).thenReturn(updatedPet);
        when(petMapper.toDto(updatedPet)).thenReturn(dto);

        // Act
        Map<String, Object> result = petController.update(id, pet);

        // Assert
        assertThat(result).isEqualTo(dto);
        verify(petService).update(id, pet);
        verify(petMapper).toDto(updatedPet);
    }

    @Test
    void delete_whenIdExists_invokesServiceDelete() {
        // Arrange
        Long id = 1L;
        doNothing().when(petService).delete(id);

        // Act
        petController.delete(id);

        // Assert
        verify(petService, times(1)).delete(id);
    }

    @Test
    void markUnavailable_whenIdExists_returnsUpdatedPetDto() {
        // Arrange
        Long id = 1L;
        Pet updatedPet = new Pet();
        Map<String, Object> dto = Map.of("available", false);

        when(petService.markUnavailable(id)).thenReturn(updatedPet);
        when(petMapper.toDto(updatedPet)).thenReturn(dto);

        // Act
        Map<String, Object> result = petController.markUnavailable(id);

        // Assert
        assertThat(result).isEqualTo(dto);
        verify(petService).markUnavailable(id);
        verify(petMapper).toDto(updatedPet);
    }
}
