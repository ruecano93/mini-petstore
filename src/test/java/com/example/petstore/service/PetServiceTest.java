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

    private Pet createPet(Long id, String name, String species, String breed, LocalDate birthDate, double price, boolean available) {
        Pet pet = new Pet(name, species, breed, birthDate, price);
        pet.setId(id);
        pet.setAvailable(available);
        return pet;
    }

    @Test
    void findAll_whenCalled_returnsListOfPets() {
        // Arrange
        Pet pet1 = createPet(1L, "Buddy", "Dog", "Beagle", LocalDate.of(2020, 1, 1), 300.0, true);
        Pet pet2 = createPet(2L, "Mittens", "Cat", "Siamese", LocalDate.of(2019, 5, 10), 150.0, true);
        List<Pet> pets = List.of(pet1, pet2);
        doReturn(pets).when(petRepository).findAll();

        // Act
        List<Pet> result = petService.findAll();

        // Assert
        assertThat(result).containsExactlyElementsOf(pets);
    }

    @Test
    void findAvailable_whenCalled_returnsListOfAvailablePets() {
        // Arrange
        Pet pet1 = createPet(1L, "Buddy", "Dog", "Beagle", LocalDate.of(2020, 1, 1), 300.0, true);
        Pet pet2 = createPet(2L, "Mittens", "Cat", "Siamese", LocalDate.of(2019, 5, 10), 150.0, true);
        List<Pet> availablePets = List.of(pet1, pet2);
        doReturn(availablePets).when(petRepository).findByAvailableTrue();

        // Act
        List<Pet> result = petService.findAvailable();

        // Assert
        assertThat(result).containsExactlyElementsOf(availablePets);
    }

    @Test
    void findBySpecies_whenSpeciesValid_returnsListOfPets() {
        // Arrange
        String species = "Dog";
        Pet pet1 = createPet(1L, "Buddy", species, "Beagle", LocalDate.of(2020, 1, 1), 300.0, true);
        Pet pet2 = createPet(2L, "Max", species, "Labrador", LocalDate.of(2018, 3, 15), 400.0, true);
        List<Pet> petsBySpecies = List.of(pet1, pet2);
        doReturn(petsBySpecies).when(petRepository).findBySpeciesIgnoreCase(species);

        // Act
        List<Pet> result = petService.findBySpecies(species);

        // Assert
        assertThat(result).containsExactlyElementsOf(petsBySpecies);
    }

    @Test
    void findBySpecies_whenSpeciesBlank_throwsIllegalArgumentException() {
        // Arrange
        String blankSpecies = "  ";

        // Act & Assert
        assertThatThrownBy(() -> petService.findBySpecies(blankSpecies))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findAvailableUnderPrice_whenMaxPricePositive_returnsListOfPets() {
        // Arrange
        double maxPrice = 200.0;
        Pet pet1 = createPet(1L, "Mittens", "Cat", "Siamese", LocalDate.of(2019, 5, 10), 150.0, true);
        List<Pet> petsUnderPrice = List.of(pet1);
        doReturn(petsUnderPrice).when(petRepository).findAvailableByMaxPrice(maxPrice);

        // Act
        List<Pet> result = petService.findAvailableUnderPrice(maxPrice);

        // Assert
        assertThat(result).containsExactlyElementsOf(petsUnderPrice);
    }

    @Test
    void findAvailableUnderPrice_whenMaxPriceNotPositive_throwsIllegalArgumentException() {
        // Arrange
        double zeroPrice = 0.0;

        // Act & Assert
        assertThatThrownBy(() -> petService.findAvailableUnderPrice(zeroPrice))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findById_whenIdExists_returnsPet() {
        // Arrange
        Long id = 1L;
        Pet pet = createPet(id, "Buddy", "Dog", "Beagle", LocalDate.of(2020, 1, 1), 300.0, true);
        when(petRepository.findById(id)).thenReturn(Optional.of(pet));

        // Act
        Pet result = petService.findById(id);

        // Assert
        assertThat(result).isEqualTo(pet);
    }

    @Test
    void findById_whenIdNotExists_throwsPetNotFoundException() {
        // Arrange
        Long id = 1L;
        when(petRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> petService.findById(id))
                .isInstanceOf(PetNotFoundException.class);
    }

    @Test
    void create_whenPetNameSpeciesNotExists_savesAndReturnsPet() {
        // Arrange
        Pet petToCreate = createPet(null, "Buddy", "Dog", "Beagle", LocalDate.of(2020, 1, 1), 300.0, true);
        when(petRepository.existsByNameAndSpecies(petToCreate.getName(), petToCreate.getSpecies())).thenReturn(false);
        when(petRepository.save(any(Pet.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Pet result = petService.create(petToCreate);

        // Assert
        assertThat(result).isEqualTo(petToCreate);
    }

    @Test
    void create_whenPetNameSpeciesExists_throwsIllegalStateException() {
        // Arrange
        Pet petToCreate = createPet(null, "Buddy", "Dog", "Beagle", LocalDate.of(2020, 1, 1), 300.0, true);
        when(petRepository.existsByNameAndSpecies(petToCreate.getName(), petToCreate.getSpecies())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> petService.create(petToCreate))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void update_whenIdExists_updatesAndReturnsPet() {
        // Arrange
        Long id = 1L;
        Pet existingPet = createPet(id, "Buddy", "Dog", "Beagle", LocalDate.of(2020, 1, 1), 300.0, true);
        Pet updatedPet = createPet(null, "Max", "Dog", "Labrador", LocalDate.of(2019, 3, 15), 400.0, false);
        when(petRepository.findById(id)).thenReturn(Optional.of(existingPet));
        when(petRepository.save(any(Pet.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Pet result = petService.update(id, updatedPet);

        // Assert
        assertThat(result.getId()).isEqualTo(id);
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
        Long id = 1L;
        Pet updatedPet = createPet(null, "Max", "Dog", "Labrador", LocalDate.of(2019, 3, 15), 400.0, false);
        when(petRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> petService.update(id, updatedPet))
                .isInstanceOf(PetNotFoundException.class);
    }

    @Test
    void delete_whenIdExists_deletesPet() {
        // Arrange
        Long id = 1L;
        Pet existingPet = createPet(id, "Buddy", "Dog", "Beagle", LocalDate.of(2020, 1, 1), 300.0, true);
        when(petRepository.findById(id)).thenReturn(Optional.of(existingPet));

        // Act
        petService.delete(id);

        // Assert
        verify(petRepository).delete(existingPet);
    }

    @Test
    void delete_whenIdNotExists_throwsPetNotFoundException() {
        // Arrange
        Long id = 1L;
        when(petRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> petService.delete(id))
                .isInstanceOf(PetNotFoundException.class);
    }

    @Test
    void markUnavailable_whenIdExists_marksPetUnavailableAndReturns() {
        // Arrange
        Long id = 1L;
        Pet existingPet = createPet(id, "Buddy", "Dog", "Beagle", LocalDate.of(2020, 1, 1), 300.0, true);
        when(petRepository.findById(id)).thenReturn(Optional.of(existingPet));
        when(petRepository.save(any(Pet.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Pet result = petService.markUnavailable(id);

        // Assert
        assertThat(result.isAvailable()).isFalse();
    }

    @Test
    void markUnavailable_whenIdNotExists_throwsPetNotFoundException() {
        // Arrange
        Long id = 1L;
        when(petRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> petService.markUnavailable(id))
                .isInstanceOf(PetNotFoundException.class);
    }
}
