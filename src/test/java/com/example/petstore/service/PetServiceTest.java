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

    private Pet createPet(Long id, String name, String species, String breed, LocalDate birthDate, double price, boolean available) {
        Pet pet = new Pet();
        pet.setId(id);
        pet.setName(name);
        pet.setSpecies(species);
        pet.setBreed(breed);
        pet.setBirthDate(birthDate);
        pet.setPrice(price);
        pet.setAvailable(available);
        return pet;
    }

    @Test
    void findAll_whenCalled_shouldReturnListOfPets() {
        // Arrange
        Pet pet1 = createPet(1L, "Max", "Dog", "Beagle", LocalDate.of(2020, 1, 1), 300.0, true);
        Pet pet2 = createPet(2L, "Whiskers", "Cat", "Siamese", LocalDate.of(2019, 5, 10), 150.0, false);
        List<Pet> expectedPets = List.of(pet1, pet2);
        when(petRepository.findAll()).thenReturn(expectedPets);

        // Act
        List<Pet> actualPets = petService.findAll();

        // Assert
        assertThat(actualPets).isEqualTo(expectedPets);
        verify(petRepository).findAll();
    }

    @Test
    void findAvailable_whenCalled_shouldReturnListOfAvailablePets() {
        // Arrange
        Pet pet1 = createPet(1L, "Max", "Dog", "Beagle", LocalDate.of(2020, 1, 1), 300.0, true);
        Pet pet2 = createPet(2L, "Buddy", "Dog", "Labrador", LocalDate.of(2021, 3, 15), 400.0, true);
        List<Pet> availablePets = List.of(pet1, pet2);
        when(petRepository.findByAvailableTrue()).thenReturn(availablePets);

        // Act
        List<Pet> actualPets = petService.findAvailable();

        // Assert
        assertThat(actualPets).allMatch(Pet::isAvailable);
        assertThat(actualPets).containsExactlyElementsOf(availablePets);
        verify(petRepository).findByAvailableTrue();
    }

    @Test
    void findBySpecies_whenSpeciesIsValid_shouldReturnPetsOfThatSpecies() {
        // Arrange
        String species = "dog";
        Pet pet1 = createPet(1L, "Max", "Dog", "Beagle", LocalDate.of(2020, 1, 1), 300.0, true);
        Pet pet2 = createPet(2L, "Buddy", "Dog", "Labrador", LocalDate.of(2021, 3, 15), 400.0, false);
        List<Pet> dogs = List.of(pet1, pet2);
        when(petRepository.findBySpeciesIgnoreCase(species)).thenReturn(dogs);

        // Act
        List<Pet> actualPets = petService.findBySpecies(species);

        // Assert
        assertThat(actualPets).allMatch(p -> p.getSpecies().equalsIgnoreCase(species));
        assertThat(actualPets).containsExactlyElementsOf(dogs);
        verify(petRepository).findBySpeciesIgnoreCase(species);
    }

    @Test
    void findBySpecies_whenSpeciesIsBlank_shouldThrowIllegalArgumentException() {
        // Arrange
        String blankSpecies = "  ";

        // Act & Assert
        assertThatThrownBy(() -> petService.findBySpecies(blankSpecies))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Species must not be blank");
        verifyNoInteractions(petRepository);
    }

    @Test
    void findAvailableUnderPrice_whenMaxPricePositive_shouldReturnAvailablePetsUnderPrice() {
        // Arrange
        double maxPrice = 350.0;
        Pet pet1 = createPet(1L, "Max", "Dog", "Beagle", LocalDate.of(2020, 1, 1), 300.0, true);
        Pet pet2 = createPet(2L, "Whiskers", "Cat", "Siamese", LocalDate.of(2019, 5, 10), 150.0, true);
        List<Pet> petsUnderPrice = List.of(pet1, pet2);
        when(petRepository.findAvailableByMaxPrice(maxPrice)).thenReturn(petsUnderPrice);

        // Act
        List<Pet> actualPets = petService.findAvailableUnderPrice(maxPrice);

        // Assert
        assertThat(actualPets).allMatch(p -> p.isAvailable() && p.getPrice() <= maxPrice);
        assertThat(actualPets).containsExactlyElementsOf(petsUnderPrice);
        verify(petRepository).findAvailableByMaxPrice(maxPrice);
    }

    @Test
    void findAvailableUnderPrice_whenMaxPriceNotPositive_shouldThrowIllegalArgumentException() {
        // Arrange
        double zeroPrice = 0.0;
        double negativePrice = -10.0;

        // Act & Assert
        assertThatThrownBy(() -> petService.findAvailableUnderPrice(zeroPrice))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Max price must be positive");

        assertThatThrownBy(() -> petService.findAvailableUnderPrice(negativePrice))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Max price must be positive");

        verifyNoInteractions(petRepository);
    }

    @Test
    void findById_whenIdExists_shouldReturnPet() {
        // Arrange
        Long id = 1L;
        Pet pet = createPet(id, "Max", "Dog", "Beagle", LocalDate.of(2020, 1, 1), 300.0, true);
        when(petRepository.findById(id)).thenReturn(Optional.of(pet));

        // Act
        Pet actualPet = petService.findById(id);

        // Assert
        assertThat(actualPet).isEqualTo(pet);
        verify(petRepository).findById(id);
    }

    @Test
    void findById_whenIdNotExists_shouldThrowPetNotFoundException() {
        // Arrange
        Long id = 1L;
        when(petRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> petService.findById(id))
                .isInstanceOf(PetNotFoundException.class)
                .hasMessageContaining(id.toString());
        verify(petRepository).findById(id);
    }

    @Test
    void create_whenPetNameSpeciesNotExists_shouldSaveAndReturnPet() {
        // Arrange
        Pet pet = createPet(null, "Max", "Dog", "Beagle", LocalDate.of(2020, 1, 1), 300.0, true);
        when(petRepository.existsByNameAndSpecies(pet.getName(), pet.getSpecies())).thenReturn(false);
        when(petRepository.save(pet)).thenReturn(pet);

        // Act
        Pet createdPet = petService.create(pet);

        // Assert
        assertThat(createdPet).isEqualTo(pet);
        verify(petRepository).existsByNameAndSpecies(pet.getName(), pet.getSpecies());
        verify(petRepository).save(pet);
    }

    @Test
    void create_whenPetNameSpeciesExists_shouldThrowIllegalStateException() {
        // Arrange
        Pet pet = createPet(null, "Max", "Dog", "Beagle", LocalDate.of(2020, 1, 1), 300.0, true);
        when(petRepository.existsByNameAndSpecies(pet.getName(), pet.getSpecies())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> petService.create(pet))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("A pet named 'Max' of species 'Dog' already exists");
        verify(petRepository).existsByNameAndSpecies(pet.getName(), pet.getSpecies());
        verify(petRepository, never()).save(any());
    }

    @Test
    void update_whenIdExists_shouldUpdateAndReturnPet() {
        // Arrange
        Long id = 1L;
        Pet existingPet = createPet(id, "Max", "Dog", "Beagle", LocalDate.of(2020, 1, 1), 300.0, true);
        Pet updatedPet = createPet(null, "Maximus", "Dog", "Beagle", LocalDate.of(2020, 1, 1), 350.0, false);
        when(petRepository.findById(id)).thenReturn(Optional.of(existingPet));
        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> invocation.getArgument(0));

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
        verify(petRepository).findById(id);
        verify(petRepository).save(existingPet);
    }

    @Test
    void update_whenIdNotExists_shouldThrowPetNotFoundException() {
        // Arrange
        Long id = 1L;
        Pet updatedPet = createPet(null, "Maximus", "Dog", "Beagle", LocalDate.of(2020, 1, 1), 350.0, false);
        when(petRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> petService.update(id, updatedPet))
                .isInstanceOf(PetNotFoundException.class)
                .hasMessageContaining(id.toString());
        verify(petRepository).findById(id);
        verify(petRepository, never()).save(any());
    }

    @Test
    void delete_whenIdExists_shouldDeletePet() {
        // Arrange
        Long id = 1L;
        Pet pet = createPet(id, "Max", "Dog", "Beagle", LocalDate.of(2020, 1, 1), 300.0, true);
        when(petRepository.findById(id)).thenReturn(Optional.of(pet));
        doNothing().when(petRepository).delete(pet);

        // Act
        petService.delete(id);

        // Assert
        verify(petRepository).findById(id);
        verify(petRepository).delete(pet);
    }

    @Test
    void delete_whenIdNotExists_shouldThrowPetNotFoundException() {
        // Arrange
        Long id = 1L;
        when(petRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> petService.delete(id))
                .isInstanceOf(PetNotFoundException.class)
                .hasMessageContaining(id.toString());
        verify(petRepository).findById(id);
        verify(petRepository, never()).delete(any());
    }

    @Test
    void markUnavailable_whenIdExists_shouldSetAvailableFalseAndSave() {
        // Arrange
        Long id = 1L;
        Pet pet = createPet(id, "Max", "Dog", "Beagle", LocalDate.of(2020, 1, 1), 300.0, true);
        when(petRepository.findById(id)).thenReturn(Optional.of(pet));
        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Pet result = petService.markUnavailable(id);

        // Assert
        assertThat(result.isAvailable()).isFalse();
        verify(petRepository).findById(id);
        verify(petRepository).save(pet);
    }

    @Test
    void markUnavailable_whenIdNotExists_shouldThrowPetNotFoundException() {
        // Arrange
        Long id = 1L;
        when(petRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> petService.markUnavailable(id))
                .isInstanceOf(PetNotFoundException.class)
                .hasMessageContaining(id.toString());
        verify(petRepository).findById(id);
        verify(petRepository, never()).save(any());
    }
}
