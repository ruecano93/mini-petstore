package com.example.petstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.petstore.exception.PetNotFoundException;
import com.example.petstore.model.Pet;
import com.example.petstore.repository.PetRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private PetService petService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_whenCalled_shouldReturnAllPets() {
        // Arrange
        Pet pet1 = new Pet();
        pet1.setId(1L);
        pet1.setName("Buddy");
        Pet pet2 = new Pet();
        pet2.setId(2L);
        pet2.setName("Milo");
        List<Pet> expectedPets = List.of(pet1, pet2);
        when(petRepository.findAll()).thenReturn(expectedPets);

        // Act
        List<Pet> actualPets = petService.findAll();

        // Assert
        assertThat(actualPets).isEqualTo(expectedPets);
    }

    @Test
    void findAvailable_whenCalled_shouldReturnAvailablePets() {
        // Arrange
        Pet pet1 = new Pet();
        pet1.setId(1L);
        pet1.setAvailable(true);
        Pet pet2 = new Pet();
        pet2.setId(2L);
        pet2.setAvailable(true);
        List<Pet> expectedPets = List.of(pet1, pet2);
        when(petRepository.findByAvailableTrue()).thenReturn(expectedPets);

        // Act
        List<Pet> actualPets = petService.findAvailable();

        // Assert
        assertThat(actualPets).isEqualTo(expectedPets);
    }

    @Test
    void findBySpecies_whenSpeciesIsValid_shouldReturnPetsOfThatSpecies() {
        // Arrange
        String species = "Dog";
        Pet pet1 = new Pet();
        pet1.setId(1L);
        pet1.setSpecies(species);
        Pet pet2 = new Pet();
        pet2.setId(2L);
        pet2.setSpecies(species);
        List<Pet> expectedPets = List.of(pet1, pet2);
        when(petRepository.findBySpeciesIgnoreCase(species)).thenReturn(expectedPets);

        // Act
        List<Pet> actualPets = petService.findBySpecies(species);

        // Assert
        assertThat(actualPets).isEqualTo(expectedPets);
    }

    @Test
    void findBySpecies_whenSpeciesIsNull_shouldThrowIllegalArgumentException() {
        // Arrange
        String species = null;

        // Act / Assert
        assertThatThrownBy(() -> petService.findBySpecies(species))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Species must not be blank");
    }

    @Test
    void findBySpecies_whenSpeciesIsBlank_shouldThrowIllegalArgumentException() {
        // Arrange
        String species = " ";

        // Act / Assert
        assertThatThrownBy(() -> petService.findBySpecies(species))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Species must not be blank");
    }

    @Test
    void findAvailableUnderPrice_whenMaxPricePositive_shouldReturnPetsUnderPrice() {
        // Arrange
        double maxPrice = 100.0;
        Pet pet1 = new Pet();
        pet1.setId(1L);
        pet1.setPrice(50.0);
        Pet pet2 = new Pet();
        pet2.setId(2L);
        pet2.setPrice(80.0);
        List<Pet> expectedPets = List.of(pet1, pet2);
        when(petRepository.findAvailableByMaxPrice(maxPrice)).thenReturn(expectedPets);

        // Act
        List<Pet> actualPets = petService.findAvailableUnderPrice(maxPrice);

        // Assert
        assertThat(actualPets).isEqualTo(expectedPets);
    }

    @Test
    void findAvailableUnderPrice_whenMaxPriceZeroOrNegative_shouldThrowIllegalArgumentException() {
        // Arrange
        double maxPriceZero = 0.0;
        double maxPriceNegative = -10.0;

        // Act / Assert
        assertThatThrownBy(() -> petService.findAvailableUnderPrice(maxPriceZero))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Max price must be positive");

        assertThatThrownBy(() -> petService.findAvailableUnderPrice(maxPriceNegative))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Max price must be positive");
    }

    @Test
    void findById_whenIdExists_shouldReturnPet() {
        // Arrange
        Long id = 1L;
        Pet pet = new Pet();
        pet.setId(id);
        when(petRepository.findById(id)).thenReturn(Optional.of(pet));

        // Act
        Pet actualPet = petService.findById(id);

        // Assert
        assertThat(actualPet).isEqualTo(pet);
    }

    @Test
    void findById_whenIdNotExists_shouldThrowPetNotFoundException() {
        // Arrange
        Long id = 1L;
        when(petRepository.findById(id)).thenReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> petService.findById(id))
                .isInstanceOf(PetNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void create_whenPetNameAndSpeciesNotExists_shouldSaveAndReturnPet() {
        // Arrange
        Pet pet = new Pet();
        pet.setName("Bella");
        pet.setSpecies("Cat");
        Pet savedPet = new Pet();
        savedPet.setId(1L);
        savedPet.setName("Bella");
        savedPet.setSpecies("Cat");
        when(petRepository.existsByNameAndSpecies("Bella", "Cat")).thenReturn(false);
        when(petRepository.save(pet)).thenReturn(savedPet);

        // Act
        Pet actualPet = petService.create(pet);

        // Assert
        assertThat(actualPet).isEqualTo(savedPet);
    }

    @Test
    void create_whenPetNameAndSpeciesExists_shouldThrowIllegalStateException() {
        // Arrange
        Pet pet = new Pet();
        pet.setName("Bella");
        pet.setSpecies("Cat");
        when(petRepository.existsByNameAndSpecies("Bella", "Cat")).thenReturn(true);

        // Act / Assert
        assertThatThrownBy(() -> petService.create(pet))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void update_whenIdExists_shouldUpdateAndReturnPet() {
        // Arrange
        Long id = 1L;
        Pet existingPet = new Pet();
        existingPet.setId(id);
        existingPet.setName("OldName");
        existingPet.setSpecies("OldSpecies");
        existingPet.setBreed("OldBreed");
        existingPet.setBirthDate(LocalDate.of(2020, 1, 1));
        existingPet.setPrice(100.0);
        existingPet.setAvailable(true);

        Pet updatedPet = new Pet();
        updatedPet.setName("NewName");
        updatedPet.setSpecies("NewSpecies");
        updatedPet.setBreed("NewBreed");
        updatedPet.setBirthDate(LocalDate.of(2021, 2, 2));
        updatedPet.setPrice(200.0);
        updatedPet.setAvailable(false);

        Pet savedPet = new Pet();
        savedPet.setId(id);
        savedPet.setName("NewName");
        savedPet.setSpecies("NewSpecies");
        savedPet.setBreed("NewBreed");
        savedPet.setBirthDate(LocalDate.of(2021, 2, 2));
        savedPet.setPrice(200.0);
        savedPet.setAvailable(false);

        when(petRepository.findById(id)).thenReturn(Optional.of(existingPet));
        when(petRepository.save(existingPet)).thenReturn(savedPet);

        // Act
        Pet actualPet = petService.update(id, updatedPet);

        // Assert
        assertThat(actualPet.getName()).isEqualTo(updatedPet.getName());
        assertThat(actualPet.getSpecies()).isEqualTo(updatedPet.getSpecies());
        assertThat(actualPet.getBreed()).isEqualTo(updatedPet.getBreed());
        assertThat(actualPet.getBirthDate()).isEqualTo(updatedPet.getBirthDate());
        assertThat(actualPet.getPrice()).isEqualTo(updatedPet.getPrice());
        assertThat(actualPet.isAvailable()).isEqualTo(updatedPet.isAvailable());
    }

    @Test
    void update_whenIdNotExists_shouldThrowPetNotFoundException() {
        // Arrange
        Long id = 1L;
        Pet updatedPet = new Pet();
        when(petRepository.findById(id)).thenReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> petService.update(id, updatedPet))
                .isInstanceOf(PetNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void delete_whenIdExists_shouldDeletePet() {
        // Arrange
        Long id = 1L;
        Pet pet = new Pet();
        pet.setId(id);
        when(petRepository.findById(id)).thenReturn(Optional.of(pet));

        // Act
        petService.delete(id);

        // Assert
        verify(petRepository).delete(pet);
    }

    @Test
    void delete_whenIdNotExists_shouldThrowPetNotFoundException() {
        // Arrange
        Long id = 1L;
        when(petRepository.findById(id)).thenReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> petService.delete(id))
                .isInstanceOf(PetNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void markUnavailable_whenIdExists_shouldSetAvailableFalseAndReturnPet() {
        // Arrange
        Long id = 1L;
        Pet pet = new Pet();
        pet.setId(id);
        pet.setAvailable(true);
        Pet savedPet = new Pet();
        savedPet.setId(id);
        savedPet.setAvailable(false);
        when(petRepository.findById(id)).thenReturn(Optional.of(pet));
        when(petRepository.save(pet)).thenReturn(savedPet);

        // Act
        Pet actualPet = petService.markUnavailable(id);

        // Assert
        assertThat(actualPet.isAvailable()).isFalse();
    }

    @Test
    void markUnavailable_whenIdNotExists_shouldThrowPetNotFoundException() {
        // Arrange
        Long id = 1L;
        when(petRepository.findById(id)).thenReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> petService.markUnavailable(id))
                .isInstanceOf(PetNotFoundException.class)
                .hasMessageContaining(id.toString());
    }
}
