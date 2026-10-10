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
    void findAll_whenCalled_returnsAllPets() {
        // Arrange
        Pet pet1 = new Pet();
        pet1.setId(1L);
        Pet pet2 = new Pet();
        pet2.setId(2L);
        List<Pet> pets = List.of(pet1, pet2);
        when(petRepository.findAll()).thenReturn(pets);

        // Act
        List<Pet> result = petService.findAll();

        // Assert
        assertThat(result).isEqualTo(pets);
    }

    @Test
    void findAvailable_whenCalled_returnsAvailablePets() {
        // Arrange
        Pet pet1 = new Pet();
        pet1.setId(1L);
        pet1.setAvailable(true);
        Pet pet2 = new Pet();
        pet2.setId(2L);
        pet2.setAvailable(true);
        List<Pet> availablePets = List.of(pet1, pet2);
        when(petRepository.findByAvailableTrue()).thenReturn(availablePets);

        // Act
        List<Pet> result = petService.findAvailable();

        // Assert
        assertThat(result).isEqualTo(availablePets);
    }

    @Test
    void findBySpecies_whenValidSpecies_returnsPetsIgnoringCase() {
        // Arrange
        String species = "Dog";
        Pet pet1 = new Pet();
        pet1.setSpecies("dog");
        Pet pet2 = new Pet();
        pet2.setSpecies("DOG");
        List<Pet> pets = List.of(pet1, pet2);
        when(petRepository.findBySpeciesIgnoreCase(species)).thenReturn(pets);

        // Act
        List<Pet> result = petService.findBySpecies(species);

        // Assert
        assertThat(result).isEqualTo(pets);
    }

    @Test
    void findBySpecies_whenSpeciesBlank_throwsIllegalArgumentException() {
        // Arrange
        String species = "  ";

        // Act / Assert
        assertThatThrownBy(() -> petService.findBySpecies(species))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Species must not be blank");
    }

    @Test
    void findAvailableUnderPrice_whenPositivePrice_returnsPetsUnderPrice() {
        // Arrange
        double maxPrice = 100.0;
        Pet pet1 = new Pet();
        pet1.setPrice(50.0);
        Pet pet2 = new Pet();
        pet2.setPrice(80.0);
        List<Pet> pets = List.of(pet1, pet2);
        when(petRepository.findAvailableByMaxPrice(maxPrice)).thenReturn(pets);

        // Act
        List<Pet> result = petService.findAvailableUnderPrice(maxPrice);

        // Assert
        assertThat(result).isEqualTo(pets);
    }

    @Test
    void findAvailableUnderPrice_whenNonPositivePrice_throwsIllegalArgumentException() {
        // Arrange
        double maxPrice = 0;

        // Act / Assert
        assertThatThrownBy(() -> petService.findAvailableUnderPrice(maxPrice))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Max price must be positive");
    }

    @Test
    void findById_whenIdExists_returnsPet() {
        // Arrange
        Long id = 1L;
        Pet pet = new Pet();
        pet.setId(id);
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

        // Act / Assert
        assertThatThrownBy(() -> petService.findById(id))
                .isInstanceOf(PetNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void create_whenPetNameSpeciesNotExists_savesAndReturnsPet() {
        // Arrange
        Pet pet = new Pet();
        pet.setName("Buddy");
        pet.setSpecies("Dog");
        Pet savedPet = new Pet();
        savedPet.setName("Buddy");
        savedPet.setSpecies("Dog");
        when(petRepository.existsByNameAndSpecies(pet.getName(), pet.getSpecies())).thenReturn(false);
        when(petRepository.save(pet)).thenReturn(savedPet);

        // Act
        Pet result = petService.create(pet);

        // Assert
        assertThat(result).isEqualTo(savedPet);
    }

    @Test
    void create_whenPetNameSpeciesExists_throwsIllegalStateException() {
        // Arrange
        Pet pet = new Pet();
        pet.setName("Buddy");
        pet.setSpecies("Dog");
        when(petRepository.existsByNameAndSpecies(pet.getName(), pet.getSpecies())).thenReturn(true);

        // Act / Assert
        assertThatThrownBy(() -> petService.create(pet))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void update_whenIdExists_updatesAndReturnsPet() {
        // Arrange
        Long id = 1L;
        Pet existingPet = new Pet();
        existingPet.setId(id);
        existingPet.setName("OldName");
        existingPet.setSpecies("Cat");
        existingPet.setBreed("Breed1");
        existingPet.setBirthDate(LocalDate.of(2020, 1, 1));
        existingPet.setPrice(100.0);
        existingPet.setAvailable(true);

        Pet updatedPet = new Pet();
        updatedPet.setName("NewName");
        updatedPet.setSpecies("Dog");
        updatedPet.setBreed("Breed2");
        updatedPet.setBirthDate(LocalDate.of(2021, 2, 2));
        updatedPet.setPrice(200.0);
        updatedPet.setAvailable(false);

        when(petRepository.findById(id)).thenReturn(Optional.of(existingPet));
        when(petRepository.save(existingPet)).thenReturn(existingPet);

        // Act
        Pet result = petService.update(id, updatedPet);

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
        Long id = 1L;
        Pet updatedPet = new Pet();
        when(petRepository.findById(id)).thenReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> petService.update(id, updatedPet))
                .isInstanceOf(PetNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void delete_whenIdExists_deletesPet() {
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
    void delete_whenIdNotExists_throwsPetNotFoundException() {
        // Arrange
        Long id = 1L;
        when(petRepository.findById(id)).thenReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> petService.delete(id))
                .isInstanceOf(PetNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void markUnavailable_whenIdExists_marksPetUnavailableAndSaves() {
        // Arrange
        Long id = 1L;
        Pet pet = new Pet();
        pet.setId(id);
        pet.setAvailable(true);
        when(petRepository.findById(id)).thenReturn(Optional.of(pet));
        when(petRepository.save(pet)).thenReturn(pet);

        // Act
        Pet result = petService.markUnavailable(id);

        // Assert
        assertThat(result.isAvailable()).isFalse();
        assertThat(result).isEqualTo(pet);
    }

    @Test
    void markUnavailable_whenIdNotExists_throwsPetNotFoundException() {
        // Arrange
        Long id = 1L;
        when(petRepository.findById(id)).thenReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> petService.markUnavailable(id))
                .isInstanceOf(PetNotFoundException.class)
                .hasMessageContaining(id.toString());
    }
}
