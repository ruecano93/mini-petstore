package com.example.petstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
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

    private Pet samplePet;

    @BeforeEach
    void setUp() {
        samplePet = new Pet("Buddy", "Dog", "Beagle", LocalDate.of(2020, 1, 1), 150.0);
        samplePet.setAvailable(true);
    }

    @Test
    void findBySpecies_whenSpeciesBlank_throwsIllegalArgumentException() {
        // Arrange - no mocks needed

        // Act & Assert
        assertThatThrownBy(() -> petService.findBySpecies(" "))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> petService.findBySpecies(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findAvailableUnderPrice_whenMaxPricePositive_returnsListOfPets() {
        // Arrange
        List<Pet> pets = List.of(samplePet);
        doReturn(pets).when(petRepository).findAvailableByMaxPrice(100.0);

        // Act
        List<Pet> result = petService.findAvailableUnderPrice(100.0);

        // Assert
        assertThat(result).isNotNull().isNotEmpty().containsExactlyElementsOf(pets);
    }

    @Test
    void create_whenPetNameSpeciesNotExists_savesAndReturnsPet() {
        // Arrange
        when(petRepository.existsByNameAndSpecies(samplePet.getName(), samplePet.getSpecies())).thenReturn(false);
        when(petRepository.save(any(Pet.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Pet created = petService.create(samplePet);

        // Assert
        assertThat(created).isNotNull();
        assertThat(created.getName()).isEqualTo(samplePet.getName());
        assertThat(created.getSpecies()).isEqualTo(samplePet.getSpecies());
    }

    @Test
    void create_whenPetNameSpeciesExists_throwsIllegalStateException() {
        // Arrange
        when(petRepository.existsByNameAndSpecies(samplePet.getName(), samplePet.getSpecies())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> petService.create(samplePet))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void delete_whenIdNotExists_throwsPetNotFoundException() {
        // Arrange
        Long nonExistentId = 99L;
        when(petRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> petService.delete(nonExistentId))
                .isInstanceOf(PetNotFoundException.class);
    }

    @Test
    void markUnavailable_whenIdExists_marksPetUnavailableAndReturns() {
        // Arrange
        Long petId = 1L;
        Pet availablePet = new Pet("Kitty", "Cat", "Siamese", LocalDate.of(2019, 5, 5), 200.0);
        availablePet.setAvailable(true);
        when(petRepository.findById(petId)).thenReturn(Optional.of(availablePet));
        when(petRepository.save(any(Pet.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Pet result = petService.markUnavailable(petId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isAvailable()).isFalse();
    }
}
