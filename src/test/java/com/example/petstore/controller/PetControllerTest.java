package com.example.petstore.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
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
        pet1 = new Pet("Fido", "dog", "bulldog", LocalDate.of(2020, 1, 1), 150.0);
        pet1.setId(1L);
        pet1.setAvailable(true);

        pet2 = new Pet("Whiskers", "cat", "siamese", LocalDate.of(2019, 5, 5), 100.0);
        pet2.setId(2L);
        pet2.setAvailable(true);

        petDto1 = Map.of(
                "id", pet1.getId(),
                "name", pet1.getName(),
                "species", pet1.getSpecies(),
                "breed", pet1.getBreed(),
                "birthDate", pet1.getBirthDate().toString(),
                "price", pet1.getPrice(),
                "available", pet1.isAvailable()
        );

        petDto2 = Map.of(
                "id", pet2.getId(),
                "name", pet2.getName(),
                "species", pet2.getSpecies(),
                "breed", pet2.getBreed(),
                "birthDate", pet2.getBirthDate().toString(),
                "price", pet2.getPrice(),
                "available", pet2.isAvailable()
        );
    }

    @Test
    void listAll_whenCalled_returnsListOfPetDtos() {
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
    void listAvailable_whenCalled_returnsListOfAvailablePetDtos() {
        // Arrange
        List<Pet> pets = List.of(pet1, pet2);
        when(petService.findAvailable()).thenReturn(pets);
        when(petMapper.toDto(pet1)).thenReturn(petDto1);
        when(petMapper.toDto(pet2)).thenReturn(petDto2);

        // Act
        List<Map<String, Object>> result = petController.listAvailable();

        // Assert
        assertThat(result).containsExactly(petDto1, petDto2);
    }

    @Test
    void bySpecies_whenSpeciesExists_returnsListOfPetDtos() {
        // Arrange
        String species = "dog";
        List<Pet> pets = List.of(pet1);
        when(petService.findBySpecies(species)).thenReturn(pets);
        when(petMapper.toDto(pet1)).thenReturn(petDto1);

        // Act
        List<Map<String, Object>> result = petController.bySpecies(species);

        // Assert
        assertThat(result).containsExactly(petDto1);
    }

    @Test
    void getById_whenIdExists_returnsPetDtoInResponseEntity() {
        // Arrange
        Long id = 1L;
        when(petService.findById(id)).thenReturn(pet1);
        when(petMapper.toDto(pet1)).thenReturn(petDto1);

        // Act
        ResponseEntity<Map<String, Object>> response = petController.getById(id);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(petDto1);
    }

    @Test
    void create_whenPetValid_returnsCreatedPetDto() {
        // Arrange
        when(petService.create(any(Pet.class))).thenReturn(pet1);
        when(petMapper.toDto(pet1)).thenReturn(petDto1);

        // Act
        Map<String, Object> result = petController.create(pet1);

        // Assert
        assertThat(result).isEqualTo(petDto1);
    }

    @Test
    void update_whenIdAndPetValid_returnsUpdatedPetDto() {
        // Arrange
        Long id = 1L;
        when(petService.update(eq(id), any(Pet.class))).thenReturn(pet1);
        when(petMapper.toDto(pet1)).thenReturn(petDto1);

        // Act
        Map<String, Object> result = petController.update(id, pet1);

        // Assert
        assertThat(result).isEqualTo(petDto1);
    }

    @Test
    void delete_whenIdExists_invokesServiceDelete() {
        // Arrange
        Long id = 1L;
        // No exception expected on delete
        assertThatCode(() -> petController.delete(id)).doesNotThrowAnyException();

        // Act
        petController.delete(id);

        // Assert
        verify(petService, times(1)).delete(id);
    }

    @Test
    void markUnavailable_whenIdExists_returnsUpdatedPetDto() {
        // Arrange
        Long id = 1L;
        Pet unavailablePet = new Pet("Fido", "dog", "bulldog", LocalDate.of(2020, 1, 1), 150.0);
        unavailablePet.setId(id);
        unavailablePet.setAvailable(false);
        Map<String, Object> unavailablePetDto = Map.of(
                "id", unavailablePet.getId(),
                "name", unavailablePet.getName(),
                "species", unavailablePet.getSpecies(),
                "breed", unavailablePet.getBreed(),
                "birthDate", unavailablePet.getBirthDate().toString(),
                "price", unavailablePet.getPrice(),
                "available", unavailablePet.isAvailable()
        );

        when(petService.markUnavailable(id)).thenReturn(unavailablePet);
        when(petMapper.toDto(unavailablePet)).thenReturn(unavailablePetDto);

        // Act
        Map<String, Object> result = petController.markUnavailable(id);

        // Assert
        assertThat(result).isEqualTo(unavailablePetDto);
    }
}
