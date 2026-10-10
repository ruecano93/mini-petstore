package com.example.petstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private PetService petService;

    private Pet samplePet1;
    private Pet samplePet2;

    @BeforeEach
    void setUp() {
        samplePet1 = new Pet("Buddy", "dog", "Labrador", LocalDate.of(2020, 1, 1), 150.0);
        samplePet1.setId(1L);
        samplePet1.setAvailable(true);

        samplePet2 = new Pet("Mittens", "cat", "Siamese", LocalDate.of(2019, 5, 5), 100.0);
        samplePet2.setId(2L);
        samplePet2.setAvailable(true);
    }

    @Test
    void findAll_whenCalled_returnsListOfPets() {
        // Arrange
        List<Pet> pets = List.of(samplePet1, samplePet2);
        doReturn(pets).when(petRepository).findAll();

        // Act
        List<Pet> result = petService.findAll();

        // Assert
        assertThat(result).isEqualTo(pets);
    }

    @Test
    void findAvailable_whenCalled_returnsListOfAvailablePets() {
        // Arrange
        List<Pet> availablePets = List.of(samplePet1);
        doReturn(availablePets).when(petRepository).findByAvailableTrue();

        // Act
        List<Pet> result = petService.findAvailable();

        // Assert
        assertThat(result).isEqualTo(availablePets);
    }

    @Test
    void findBySpecies_whenSpeciesIsValid_returnsListOfPets() {
        // Arrange
        List<Pet> dogs = List.of(samplePet1);
        doReturn(dogs).when(petRepository).findBySpeciesIgnoreCase("dog");

        // Act
        List<Pet> result = petService.findBySpecies("dog");

        // Assert
        assertThat(result).isEqualTo(dogs);
    }

    @Test
    void findBySpecies_whenSpeciesIsBlank_throwsIllegalArgumentException() {
        // Act & Assert
        assertThatThrownBy(() -> petService.findBySpecies("")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> petService.findBySpecies("   ")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> petService.findBySpecies(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findAvailableUnderPrice_whenMaxPriceIsPositive_returnsListOfPets() {
        // Arrange
        List<Pet> petsUnderPrice = List.of(samplePet1);
        doReturn(petsUnderPrice).when(petRepository).findAvailableByMaxPrice(100.0);

        // Act
        List<Pet> result = petService.findAvailableUnderPrice(100.0);

        // Assert
        assertThat(result).isEqualTo(petsUnderPrice);
    }

    @Test
    void findAvailableUnderPrice_whenMaxPriceIsZeroOrNegative_throwsIllegalArgumentException() {
        // Act & Assert
        assertThatThrownBy(() -> petService.findAvailableUnderPrice(0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> petService.findAvailableUnderPrice(-1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findById_whenIdExists_returnsPet() {
        // Arrange
        doReturn(Optional.of(samplePet1)).when(petRepository).findById(1L);

        // Act
        Pet result = petService.findById(1L);

        // Assert
        assertThat(result).isEqualTo(samplePet1);
    }

    @Test
    void findById_whenIdNotExists_throwsPetNotFoundException() {
        // Arrange
        doReturn(Optional.empty()).when(petRepository).findById(99L);

        // Act & Assert
        assertThatThrownBy(() -> petService.findById(99L)).isInstanceOf(PetNotFoundException.class);
    }

    @Test
    void create_whenPetNameAndSpeciesNotExists_savesAndReturnsPet() {
        // Arrange
        Pet newPet = new Pet("Charlie", "dog", "Beagle", LocalDate.of(2021, 3, 3), 120.0);
        when(petRepository.existsByNameAndSpecies(newPet.getName(), newPet.getSpecies())).thenReturn(false);
        when(petRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // Act
        Pet result = petService.create(newPet);

        // Assert
        assertThat(result).isEqualTo(newPet);
    }

    @Test
    void create_whenPetNameAndSpeciesExists_throwsIllegalStateException() {
        // Arrange
        Pet existingPet = new Pet("Buddy", "dog", "Labrador", LocalDate.of(2020, 1, 1), 150.0);
        when(petRepository.existsByNameAndSpecies(existingPet.getName(), existingPet.getSpecies())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> petService.create(existingPet)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void update_whenIdExists_updatesAndReturnsPet() {
        // Arrange
        Pet updatedPet = new Pet("BuddyUpdated", "dog", "Golden Retriever", LocalDate.of(2020, 1, 1), 200.0);
        updatedPet.setAvailable(false);
        doReturn(Optional.of(samplePet1)).when(petRepository).findById(1L);
        when(petRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // Act
        Pet result = petService.update(1L, updatedPet);

        // Assert
        assertThat(result.getName()).isEqualTo(updatedPet.getName());
        assertThat(result.getSpecies()).isEqualTo(updatedPet.getSpecies());
        assertThat(result.getBreed()).isEqualTo(updatedPet.getBreed());
        assertThat(result.getBirthDate()).isEqualTo(updatedPet.getBirthDate());
        assertThat(result.getPrice()).isEqualTo(updatedPet.getPrice());
        assertThat(result.isAvailable()).isEqualTo(updatedPet.isAvailable());
    }

    @Test
    void update_whenIdNotExists_throwsPetNotFoundException() {
        // Arrange
        Pet updatedPet = new Pet("BuddyUpdated", "dog", "Golden Retriever", LocalDate.of(2020, 1, 1), 200.0);
        updatedPet.setAvailable(false);
        doReturn(Optional.empty()).when(petRepository).findById(99L);

        // Act & Assert
        assertThatThrownBy(() -> petService.update(99L, updatedPet)).isInstanceOf(PetNotFoundException.class);
    }

    @Test
    void delete_whenIdExists_deletesPet() {
        // Arrange
        doReturn(Optional.of(samplePet1)).when(petRepository).findById(1L);

        // Act
        petService.delete(1L);

        // Assert
        verify(petRepository).delete(samplePet1);
    }

    @Test
    void delete_whenIdNotExists_throwsPetNotFoundException() {
        // Arrange
        doReturn(Optional.empty()).when(petRepository).findById(99L);

        // Act & Assert
        assertThatThrownBy(() -> petService.delete(99L)).isInstanceOf(PetNotFoundException.class);
    }

    @Test
    void markUnavailable_whenIdExists_marksPetUnavailableAndReturnsIt() {
        // Arrange
        Pet availablePet = new Pet("Buddy", "dog", "Labrador", LocalDate.of(2020, 1, 1), 150.0);
        availablePet.setId(1L);
        availablePet.setAvailable(true);
        doReturn(Optional.of(availablePet)).when(petRepository).findById(1L);
        when(petRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // Act
        Pet result = petService.markUnavailable(1L);

        // Assert
        assertThat(result.isAvailable()).isFalse();
    }

    @Test
    void markUnavailable_whenIdNotExists_throwsPetNotFoundException() {
        // Arrange
        doReturn(Optional.empty()).when(petRepository).findById(99L);

        // Act & Assert
        assertThatThrownBy(() -> petService.markUnavailable(99L)).isInstanceOf(PetNotFoundException.class);
    }
}
