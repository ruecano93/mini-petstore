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
    void findAll_whenCalled_shouldReturnListOfPets() {
        // Arrange
        List<Pet> expected = List.of(
                new Pet(),
                new Pet()
        );
        when(petRepository.findAll()).thenReturn(expected);

        // Act
        List<Pet> actual = petService.findAll();

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findAvailable_whenCalled_shouldReturnListOfAvailablePets() {
        // Arrange
        Pet pet1 = new Pet();
        pet1.setAvailable(true);
        Pet pet2 = new Pet();
        pet2.setAvailable(true);
        List<Pet> expected = List.of(pet1, pet2);
        when(petRepository.findByAvailableTrue()).thenReturn(expected);

        // Act
        List<Pet> actual = petService.findAvailable();

        // Assert
        assertThat(actual).allMatch(Pet::isAvailable);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findBySpecies_whenValidSpecies_shouldReturnPetsIgnoringCase() {
        // Arrange
        String species = "Dog";
        Pet pet1 = new Pet();
        pet1.setSpecies("dog");
        Pet pet2 = new Pet();
        pet2.setSpecies("DOG");
        List<Pet> expected = List.of(pet1, pet2);
        when(petRepository.findBySpeciesIgnoreCase(species)).thenReturn(expected);

        // Act
        List<Pet> actual = petService.findBySpecies(species);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findBySpecies_whenSpeciesIsBlank_shouldThrowIllegalArgumentException() {
        // Arrange
        String species = "  ";

        // Act / Assert
        assertThatThrownBy(() -> petService.findBySpecies(species))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Species must not be blank");
    }

    @Test
    void findAvailableUnderPrice_whenPositivePrice_shouldReturnPetsUnderPrice() {
        // Arrange
        double maxPrice = 100.0;
        Pet pet1 = new Pet();
        pet1.setPrice(50.0);
        Pet pet2 = new Pet();
        pet2.setPrice(99.99);
        List<Pet> expected = List.of(pet1, pet2);
        when(petRepository.findAvailableByMaxPrice(maxPrice)).thenReturn(expected);

        // Act
        List<Pet> actual = petService.findAvailableUnderPrice(maxPrice);

        // Assert
        assertThat(actual).isEqualTo(expected);
        assertThat(actual).allMatch(p -> p.getPrice() <= maxPrice);
    }

    @Test
    void findAvailableUnderPrice_whenNonPositivePrice_shouldThrowIllegalArgumentException() {
        // Arrange
        double maxPrice = 0;

        // Act / Assert
        assertThatThrownBy(() -> petService.findAvailableUnderPrice(maxPrice))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Max price must be positive");
    }

    @Test
    void findById_whenIdExists_shouldReturnPet() {
        // Arrange
        Long id = 1L;
        Pet expected = new Pet();
        when(petRepository.findById(id)).thenReturn(Optional.of(expected));

        // Act
        Pet actual = petService.findById(id);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findById_whenIdNotFound_shouldThrowPetNotFoundException() {
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
        Pet petToCreate = new Pet();
        petToCreate.setName("Buddy");
        petToCreate.setSpecies("Dog");
        Pet savedPet = new Pet();
        savedPet.setName("Buddy");
        savedPet.setSpecies("Dog");
        when(petRepository.existsByNameAndSpecies("Buddy", "Dog")).thenReturn(false);
        when(petRepository.save(petToCreate)).thenReturn(savedPet);

        // Act
        Pet actual = petService.create(petToCreate);

        // Assert
        assertThat(actual).isEqualTo(savedPet);
    }

    @Test
    void create_whenPetNameAndSpeciesExists_shouldThrowIllegalStateException() {
        // Arrange
        Pet petToCreate = new Pet();
        petToCreate.setName("Buddy");
        petToCreate.setSpecies("Dog");
        when(petRepository.existsByNameAndSpecies("Buddy", "Dog")).thenReturn(true);

        // Act / Assert
        assertThatThrownBy(() -> petService.create(petToCreate))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Buddy")
                .hasMessageContaining("Dog");
    }

    @Test
    void update_whenIdExists_shouldUpdateAndReturnPet() {
        // Arrange
        Long id = 1L;
        Pet existing = new Pet();
        existing.setName("OldName");
        existing.setSpecies("Cat");
        existing.setBreed("Siamese");
        existing.setBirthDate(LocalDate.of(2020, 1, 1));
        existing.setPrice(100.0);
        existing.setAvailable(true);

        Pet updated = new Pet();
        updated.setName("NewName");
        updated.setSpecies("Dog");
        updated.setBreed("Bulldog");
        updated.setBirthDate(LocalDate.of(2021, 2, 2));
        updated.setPrice(200.0);
        updated.setAvailable(false);

        when(petRepository.findById(id)).thenReturn(Optional.of(existing));
        when(petRepository.save(existing)).thenReturn(existing);

        // Act
        Pet actual = petService.update(id, updated);

        // Assert
        assertThat(actual.getName()).isEqualTo("NewName");
        assertThat(actual.getSpecies()).isEqualTo("Dog");
        assertThat(actual.getBreed()).isEqualTo("Bulldog");
        assertThat(actual.getBirthDate()).isEqualTo(LocalDate.of(2021, 2, 2));
        assertThat(actual.getPrice()).isEqualTo(200.0);
        assertThat(actual.isAvailable()).isFalse();
    }

    @Test
    void update_whenIdNotFound_shouldThrowPetNotFoundException() {
        // Arrange
        Long id = 1L;
        Pet updated = new Pet();
        when(petRepository.findById(id)).thenReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> petService.update(id, updated))
                .isInstanceOf(PetNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void delete_whenIdExists_shouldDeletePet() {
        // Arrange
        Long id = 1L;
        Pet pet = new Pet();
        when(petRepository.findById(id)).thenReturn(Optional.of(pet));

        // Act
        petService.delete(id);

        // Assert
        verify(petRepository).delete(pet);
    }

    @Test
    void delete_whenIdNotFound_shouldThrowPetNotFoundException() {
        // Arrange
        Long id = 1L;
        when(petRepository.findById(id)).thenReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> petService.delete(id))
                .isInstanceOf(PetNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void markUnavailable_whenIdExists_shouldSetAvailableFalseAndSave() {
        // Arrange
        Long id = 1L;
        Pet pet = new Pet();
        pet.setAvailable(true);
        when(petRepository.findById(id)).thenReturn(Optional.of(pet));
        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Pet actual = petService.markUnavailable(id);

        // Assert
        assertThat(actual.isAvailable()).isFalse();
    }

    @Test
    void markUnavailable_whenIdNotFound_shouldThrowPetNotFoundException() {
        // Arrange
        Long id = 1L;
        when(petRepository.findById(id)).thenReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> petService.markUnavailable(id))
                .isInstanceOf(PetNotFoundException.class)
                .hasMessageContaining(id.toString());
    }
}
