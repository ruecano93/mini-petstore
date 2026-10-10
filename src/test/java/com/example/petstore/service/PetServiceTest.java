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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private PetService petService;

    private Pet pet1;
    private Pet pet2;

    @BeforeEach
    void setUp() {
        pet1 = new Pet("Fido", "dog", "bulldog", LocalDate.of(2020, 1, 1), 150.0);
        pet1.setId(1L);
        pet1.setAvailable(true);

        pet2 = new Pet("Whiskers", "cat", "siamese", LocalDate.of(2019, 5, 5), 100.0);
        pet2.setId(2L);
        pet2.setAvailable(true);
    }

    @Test
    void findAll_whenCalled_returnsListOfPets() {
        // Arrange
        List<Pet> pets = List.of(pet1, pet2);
        doReturn(pets).when(petRepository).findAll();

        // Act
        List<Pet> result = petService.findAll();

        // Assert
        assertThat(result).isEqualTo(pets);
    }

    @Test
    void findAvailable_whenCalled_returnsListOfAvailablePets() {
        // Arrange
        List<Pet> availablePets = List.of(pet1);
        doReturn(availablePets).when(petRepository).findByAvailableTrue();

        // Act
        List<Pet> result = petService.findAvailable();

        // Assert
        assertThat(result).isEqualTo(availablePets);
    }

    @Test
    void findBySpecies_whenSpeciesIsValid_returnsPetsOfThatSpecies() {
        // Arrange
        List<Pet> dogs = List.of(pet1);
        doReturn(dogs).when(petRepository).findBySpeciesIgnoreCase("dog");

        // Act
        List<Pet> result = petService.findBySpecies("dog");

        // Assert
        assertThat(result).isEqualTo(dogs);
    }

    @Test
    void findBySpecies_whenSpeciesIsBlank_throwsIllegalArgumentException() {
        // Act / Assert
        assertThatThrownBy(() -> petService.findBySpecies(" "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findAvailableUnderPrice_whenMaxPriceIsPositive_returnsPetsUnderPrice() {
        // Arrange
        List<Pet> petsUnderPrice = List.of(pet1);
        doReturn(petsUnderPrice).when(petRepository).findAvailableByMaxPrice(100.0);

        // Act
        List<Pet> result = petService.findAvailableUnderPrice(100.0);

        // Assert
        assertThat(result).isEqualTo(petsUnderPrice);
    }

    @Test
    void findAvailableUnderPrice_whenMaxPriceIsZeroOrNegative_throwsIllegalArgumentException() {
        // Act / Assert
        assertThatThrownBy(() -> petService.findAvailableUnderPrice(0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> petService.findAvailableUnderPrice(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findById_whenIdExists_returnsPet() {
        // Arrange
        doReturn(Optional.of(pet1)).when(petRepository).findById(1L);

        // Act
        Pet result = petService.findById(1L);

        // Assert
        assertThat(result).isEqualTo(pet1);
    }

    @Test
    void findById_whenIdDoesNotExist_throwsPetNotFoundException() {
        // Arrange
        doReturn(Optional.empty()).when(petRepository).findById(99L);

        // Act / Assert
        assertThatThrownBy(() -> petService.findById(99L))
                .isInstanceOf(PetNotFoundException.class);
    }

    @Test
    void create_whenPetDoesNotExist_savesAndReturnsPet() {
        // Arrange
        Pet newPet = new Pet("Buddy", "dog", "beagle", LocalDate.of(2021, 6, 15), 200.0);
        newPet.setAvailable(true);
        when(petRepository.existsByNameAndSpecies(newPet.getName(), newPet.getSpecies())).thenReturn(false);
        when(petRepository.save(any(Pet.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Pet result = petService.create(newPet);

        // Assert
        assertThat(result).isEqualTo(newPet);
    }

    @Test
    void create_whenPetExists_throwsIllegalStateException() {
        // Arrange
        Pet duplicatePet = new Pet("Fido", "dog", "bulldog", LocalDate.of(2020, 1, 1), 150.0);
        when(petRepository.existsByNameAndSpecies(duplicatePet.getName(), duplicatePet.getSpecies())).thenReturn(true);

        // Act / Assert
        assertThatThrownBy(() -> petService.create(duplicatePet))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void update_whenIdExists_updatesAndReturnsPet() {
        // Arrange
        Pet updatedPet = new Pet("FidoUpdated", "dog", "bulldog", LocalDate.of(2020, 1, 1), 180.0);
        updatedPet.setAvailable(false);
        doReturn(Optional.of(pet1)).when(petRepository).findById(1L);
        when(petRepository.save(any(Pet.class))).thenAnswer(i -> i.getArgument(0));

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
    void update_whenIdDoesNotExist_throwsPetNotFoundException() {
        // Arrange
        Pet updatedPet = new Pet("NonExistent", "dog", "bulldog", LocalDate.of(2020, 1, 1), 180.0);
        doReturn(Optional.empty()).when(petRepository).findById(99L);

        // Act / Assert
        assertThatThrownBy(() -> petService.update(99L, updatedPet))
                .isInstanceOf(PetNotFoundException.class);
    }

    @Test
    void delete_whenIdExists_deletesPet() {
        // Arrange
        doReturn(Optional.of(pet1)).when(petRepository).findById(1L);

        // Act
        petService.delete(1L);

        // Assert
        verify(petRepository, times(1)).delete(pet1);
    }

    @Test
    void delete_whenIdDoesNotExist_throwsPetNotFoundException() {
        // Arrange
        doReturn(Optional.empty()).when(petRepository).findById(99L);

        // Act / Assert
        assertThatThrownBy(() -> petService.delete(99L))
                .isInstanceOf(PetNotFoundException.class);
    }

    @Test
    void markUnavailable_whenIdExists_marksPetUnavailableAndReturnsIt() {
        // Arrange
        pet1.setAvailable(true);
        doReturn(Optional.of(pet1)).when(petRepository).findById(1L);
        when(petRepository.save(any(Pet.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Pet result = petService.markUnavailable(1L);

        // Assert
        assertThat(result.isAvailable()).isFalse();
    }

    @Test
    void markUnavailable_whenIdDoesNotExist_throwsPetNotFoundException() {
        // Arrange
        doReturn(Optional.empty()).when(petRepository).findById(99L);

        // Act / Assert
        assertThatThrownBy(() -> petService.markUnavailable(99L))
                .isInstanceOf(PetNotFoundException.class);
    }
}
