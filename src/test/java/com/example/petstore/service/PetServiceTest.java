package com.example.petstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    void findAll_whenCalled_shouldReturnListOfPets() {
        // Arrange
        List<Pet> pets = List.of(
                new Pet(1L, "Fido", "Dog", "Bulldog", LocalDate.of(2020, 1, 1), 300.0, true),
                new Pet(2L, "Whiskers", "Cat", "Siamese", LocalDate.of(2019, 5, 15), 150.0, false));
        when(petRepository.findAll()).thenReturn(pets);

        // Act
        List<Pet> result = petService.findAll();

        // Assert
        assertThat(result).isEqualTo(pets);
        verify(petRepository).findAll();
    }

    @Test
    void findAvailable_whenCalled_shouldReturnListOfAvailablePets() {
        // Arrange
        List<Pet> availablePets = List.of(
                new Pet(1L, "Fido", "Dog", "Bulldog", LocalDate.of(2020, 1, 1), 300.0, true));
        when(petRepository.findByAvailableTrue()).thenReturn(availablePets);

        // Act
        List<Pet> result = petService.findAvailable();

        // Assert
        assertThat(result).allMatch(Pet::isAvailable);
        assertThat(result).isEqualTo(availablePets);
        verify(petRepository).findByAvailableTrue();
    }

    @Test
    void findBySpecies_whenSpeciesIsValid_shouldReturnPetsOfThatSpecies() {
        // Arrange
        String species = "dog";
        List<Pet> pets = List.of(
                new Pet(1L, "Fido", "Dog", "Bulldog", LocalDate.of(2020, 1, 1), 300.0, true));
        when(petRepository.findBySpeciesIgnoreCase(species)).thenReturn(pets);

        // Act
        List<Pet> result = petService.findBySpecies(species);

        // Assert
        assertThat(result).allMatch(p -> p.getSpecies().equalsIgnoreCase(species));
        assertThat(result).isEqualTo(pets);
        verify(petRepository).findBySpeciesIgnoreCase(species);
    }

    @Test
    void findBySpecies_whenSpeciesIsNull_shouldThrowIllegalArgumentException() {
        // Arrange
        String species = null;

        // Act & Assert
        assertThatThrownBy(() -> petService.findBySpecies(species))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Species must not be blank");
        verifyNoInteractions(petRepository);
    }

    @Test
    void findBySpecies_whenSpeciesIsBlank_shouldThrowIllegalArgumentException() {
        // Arrange
        String species = "  ";

        // Act & Assert
        assertThatThrownBy(() -> petService.findBySpecies(species))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Species must not be blank");
        verifyNoInteractions(petRepository);
    }

    @Test
    void findAvailableUnderPrice_whenMaxPricePositive_shouldReturnAvailablePetsUnderPrice() {
        // Arrange
        double maxPrice = 200.0;
        List<Pet> pets = List.of(
                new Pet(1L, "Whiskers", "Cat", "Siamese", LocalDate.of(2019, 5, 15), 150.0, true));
        when(petRepository.findAvailableByMaxPrice(maxPrice)).thenReturn(pets);

        // Act
        List<Pet> result = petService.findAvailableUnderPrice(maxPrice);

        // Assert
        assertThat(result).allMatch(p -> p.isAvailable() && p.getPrice() <= maxPrice);
        assertThat(result).isEqualTo(pets);
        verify(petRepository).findAvailableByMaxPrice(maxPrice);
    }

    @Test
    void findAvailableUnderPrice_whenMaxPriceZeroOrNegative_shouldThrowIllegalArgumentException() {
        // Arrange
        double maxPriceZero = 0.0;
        double maxPriceNegative = -10.0;

        // Act & Assert
        assertThatThrownBy(() -> petService.findAvailableUnderPrice(maxPriceZero))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Max price must be positive");
        assertThatThrownBy(() -> petService.findAvailableUnderPrice(maxPriceNegative))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Max price must be positive");
        verifyNoInteractions(petRepository);
    }

    @Test
    void findById_whenIdExists_shouldReturnPet() {
        // Arrange
        Long id = 1L;
        Pet pet = new Pet(id, "Fido", "Dog", "Bulldog", LocalDate.of(2020, 1, 1), 300.0, true);
        when(petRepository.findById(id)).thenReturn(Optional.of(pet));

        // Act
        Pet result = petService.findById(id);

        // Assert
        assertThat(result).isEqualTo(pet);
        verify(petRepository).findById(id);
    }

    @Test
    void findById_whenIdDoesNotExist_shouldThrowPetNotFoundException() {
        // Arrange
        Long id = 99L;
        when(petRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> petService.findById(id))
                .isInstanceOf(PetNotFoundException.class)
                .hasMessage("Pet not found with id " + id);
        verify(petRepository).findById(id);
    }

    @Test
    void create_whenPetNameAndSpeciesNotExists_shouldSaveAndReturnPet() {
        // Arrange
        Pet pet = new Pet(null, "Fido", "Dog", "Bulldog", LocalDate.of(2020, 1, 1), 300.0, true);
        when(petRepository.existsByNameAndSpecies(pet.getName(), pet.getSpecies())).thenReturn(false);
        Pet savedPet = new Pet(1L, "Fido", "Dog", "Bulldog", LocalDate.of(2020, 1, 1), 300.0, true);
        when(petRepository.save(pet)).thenReturn(savedPet);

        // Act
        Pet result = petService.create(pet);

        // Assert
        assertThat(result).isEqualTo(savedPet);
        verify(petRepository).existsByNameAndSpecies(pet.getName(), pet.getSpecies());
        verify(petRepository).save(pet);
    }

    @Test
    void create_whenPetNameAndSpeciesExists_shouldThrowIllegalStateException() {
        // Arrange
        Pet pet = new Pet(null, "Fido", "Dog", "Bulldog", LocalDate.of(2020, 1, 1), 300.0, true);
        when(petRepository.existsByNameAndSpecies(pet.getName(), pet.getSpecies())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> petService.create(pet))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("A pet named 'Fido' of species 'Dog' already exists");
        verify(petRepository).existsByNameAndSpecies(pet.getName(), pet.getSpecies());
        verify(petRepository, never()).save(any());
    }

    @Test
    void update_whenIdExists_shouldUpdateAndReturnUpdatedPet() {
        // Arrange
        Long id = 1L;
        Pet existingPet = new Pet(id, "Fido", "Dog", "Bulldog", LocalDate.of(2020, 1, 1), 300.0, true);
        Pet updatedPet = new Pet(null, "Max", "Dog", "Labrador", LocalDate.of(2021, 2, 2), 350.0, false);
        when(petRepository.findById(id)).thenReturn(Optional.of(existingPet));
        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Pet result = petService.update(id, updatedPet);

        // Assert
        ArgumentCaptor<Pet> petCaptor = ArgumentCaptor.forClass(Pet.class);
        verify(petRepository).save(petCaptor.capture());
        Pet savedPet = petCaptor.getValue();

        assertThat(savedPet.getId()).isEqualTo(id);
        assertThat(savedPet.getName()).isEqualTo(updatedPet.getName());
        assertThat(savedPet.getSpecies()).isEqualTo(updatedPet.getSpecies());
        assertThat(savedPet.getBreed()).isEqualTo(updatedPet.getBreed());
        assertThat(savedPet.getBirthDate()).isEqualTo(updatedPet.getBirthDate());
        assertThat(savedPet.getPrice()).isEqualTo(updatedPet.getPrice());
        assertThat(savedPet.isAvailable()).isEqualTo(updatedPet.isAvailable());

        assertThat(result).isEqualTo(savedPet);
    }

    @Test
    void update_whenIdDoesNotExist_shouldThrowPetNotFoundException() {
        // Arrange
        Long id = 99L;
        Pet updatedPet = new Pet(null, "Max", "Dog", "Labrador", LocalDate.of(2021, 2, 2), 350.0, false);
        when(petRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> petService.update(id, updatedPet))
                .isInstanceOf(PetNotFoundException.class)
                .hasMessage("Pet not found with id " + id);
        verify(petRepository).findById(id);
        verify(petRepository, never()).save(any());
    }

    @Test
    void delete_whenIdExists_shouldDeletePet() {
        // Arrange
        Long id = 1L;
        Pet existingPet = new Pet(id, "Fido", "Dog", "Bulldog", LocalDate.of(2020, 1, 1), 300.0, true);
        when(petRepository.findById(id)).thenReturn(Optional.of(existingPet));
        doNothing().when(petRepository).delete(existingPet);

        // Act
        petService.delete(id);

        // Assert
        verify(petRepository).findById(id);
        verify(petRepository).delete(existingPet);
    }

    @Test
    void delete_whenIdDoesNotExist_shouldThrowPetNotFoundException() {
        // Arrange
        Long id = 99L;
        when(petRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> petService.delete(id))
                .isInstanceOf(PetNotFoundException.class)
                .hasMessage("Pet not found with id " + id);
        verify(petRepository).findById(id);
        verify(petRepository, never()).delete(any());
    }

    @Test
    void markUnavailable_whenIdExists_shouldSetAvailableFalseAndSave() {
        // Arrange
        Long id = 1L;
        Pet existingPet = new Pet(id, "Fido", "Dog", "Bulldog", LocalDate.of(2020, 1, 1), 300.0, true);
        when(petRepository.findById(id)).thenReturn(Optional.of(existingPet));
        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Pet result = petService.markUnavailable(id);

        // Assert
        ArgumentCaptor<Pet> petCaptor = ArgumentCaptor.forClass(Pet.class);
        verify(petRepository).save(petCaptor.capture());
        Pet savedPet = petCaptor.getValue();

        assertThat(savedPet.isAvailable()).isFalse();
        assertThat(result).isEqualTo(savedPet);
    }

    @Test
    void markUnavailable_whenIdDoesNotExist_shouldThrowPetNotFoundException() {
        // Arrange
        Long id = 99L;
        when(petRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> petService.markUnavailable(id))
                .isInstanceOf(PetNotFoundException.class)
                .hasMessage("Pet not found with id " + id);
        verify(petRepository).findById(id);
        verify(petRepository, never()).save(any());
    }
}
