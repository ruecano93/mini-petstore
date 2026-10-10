package com.example.petstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.petstore.exception.PetNotFoundException;
import com.example.petstore.model.Pet;
import com.example.petstore.repository.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        samplePet1 = new Pet("Buddy", "dog", "Beagle", LocalDate.of(2020, 1, 1), 150.0);
        samplePet1.setId(1L);
        samplePet1.setAvailable(true);

        samplePet2 = new Pet("Mittens", "cat", "Siamese", LocalDate.of(2019, 6, 15), 100.0);
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
        assertThat(result).isNotNull().hasSize(2).containsExactlyInAnyOrder(samplePet1, samplePet2);
    }

    @Test
    void findAvailable_whenCalled_returnsListOfAvailablePets() {
        // Arrange
        List<Pet> availablePets = List.of(samplePet1);
        doReturn(availablePets).when(petRepository).findByAvailableTrue();

        // Act
        List<Pet> result = petService.findAvailable();

        // Assert
        assertThat(result).isNotNull().hasSize(1).containsExactly(samplePet1);
    }

    @Test
    void findBySpecies_whenSpeciesIsValid_returnsListOfPets() {
        // Arrange
        List<Pet> dogs = List.of(samplePet1);
        doReturn(dogs).when(petRepository).findBySpeciesIgnoreCase("dog");

        // Act
        List<Pet> result = petService.findBySpecies("dog");

        // Assert
        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getSpecies()).isEqualToIgnoringCase("dog");
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
        List<Pet> petsUnderPrice = List.of(samplePet2);
        doReturn(petsUnderPrice).when(petRepository).findAvailableByMaxPrice(100.0);

        // Act
        List<Pet> result = petService.findAvailableUnderPrice(100.0);

        // Assert
        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getPrice()).isLessThanOrEqualTo(100.0);
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
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findById_whenIdNotExists_throwsPetNotFoundException() {
        // Arrange
        doReturn(Optional.empty()).when(petRepository).findById(1L);

        // Act & Assert
        assertThatThrownBy(() -> petService.findById(1L)).isInstanceOf(PetNotFoundException.class);
    }

    @Test
    void create_whenPetNameAndSpeciesNotExists_savesAndReturnsPet() {
        // Arrange
        Pet newPet = new Pet("Charlie", "dog", "Labrador", LocalDate.of(2021, 5, 10), 200.0);
        when(petRepository.existsByNameAndSpecies(newPet.getName(), newPet.getSpecies())).thenReturn(false);
        when(petRepository.save(any(Pet.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Pet result = petService.create(newPet);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(newPet.getName());
        assertThat(result.getSpecies()).isEqualTo(newPet.getSpecies());
    }

    @Test
    void create_whenPetNameAndSpeciesExists_throwsIllegalStateException() {
        // Arrange
        Pet newPet = new Pet("Buddy", "dog", "Beagle", LocalDate.of(2020, 1, 1), 150.0);
        when(petRepository.existsByNameAndSpecies(newPet.getName(), newPet.getSpecies())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> petService.create(newPet)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void update_whenIdExists_updatesAndReturnsPet() {
        // Arrange
        Pet updatedPet = new Pet("Max", "dog", "Golden Retriever", LocalDate.of(2018, 3, 20), 300.0);
        updatedPet.setAvailable(false);
        updatedPet.setId(1L);

        Pet existingPet = new Pet("OldName", "dog", "Beagle", LocalDate.of(2015, 1, 1), 100.0);
        existingPet.setId(1L);
        existingPet.setAvailable(true);

        doReturn(Optional.of(existingPet)).when(petRepository).findById(1L);
        when(petRepository.save(any(Pet.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Pet result = petService.update(1L, updatedPet);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
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
        Pet updatedPet = new Pet("Max", "dog", "Golden Retriever", LocalDate.of(2018, 3, 20), 300.0);
        doReturn(Optional.empty()).when(petRepository).findById(1L);

        // Act & Assert
        assertThatThrownBy(() -> petService.update(1L, updatedPet)).isInstanceOf(PetNotFoundException.class);
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
        doReturn(Optional.empty()).when(petRepository).findById(1L);

        // Act & Assert
        assertThatThrownBy(() -> petService.delete(1L)).isInstanceOf(PetNotFoundException.class);
    }

    @Test
    void markUnavailable_whenIdExists_marksPetUnavailableAndReturnsIt() {
        // Arrange
        Pet availablePet = new Pet("Buddy", "dog", "Beagle", LocalDate.of(2020, 1, 1), 150.0);
        availablePet.setId(1L);
        availablePet.setAvailable(true);

        doReturn(Optional.of(availablePet)).when(petRepository).findById(1L);
        when(petRepository.save(any(Pet.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Pet result = petService.markUnavailable(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isAvailable()).isFalse();
    }

    @Test
    void markUnavailable_whenIdNotExists_throwsPetNotFoundException() {
        // Arrange
        doReturn(Optional.empty()).when(petRepository).findById(1L);

        // Act & Assert
        assertThatThrownBy(() -> petService.markUnavailable(1L)).isInstanceOf(PetNotFoundException.class);
    }
}
