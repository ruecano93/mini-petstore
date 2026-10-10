package com.example.petstore.repository;

import static org.assertj.core.api.Assertions.assertThat;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.petstore.model.Pet;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class PetRepositoryTest {

    @Autowired
    private PetRepository petRepository;

    @Test
    @DisplayName("findByAvailableTrue_whenPetsAvailable_returnsListOfAvailablePets")
    void findByAvailableTrue_whenPetsAvailable_returnsListOfAvailablePets() {
        // Arrange
        Pet pet1 = new Pet("Buddy", "dog", "Beagle", LocalDate.of(2020, 1, 1), 150.0);
        pet1.setAvailable(true);
        Pet pet2 = new Pet("Mittens", "cat", "Siamese", LocalDate.of(2019, 5, 10), 100.0);
        pet2.setAvailable(false);
        Pet pet3 = new Pet("Charlie", "dog", "Labrador", LocalDate.of(2021, 3, 15), 200.0);
        pet3.setAvailable(true);
        petRepository.save(pet1);
        petRepository.save(pet2);
        petRepository.save(pet3);

        // Act
        List<Pet> availablePets = petRepository.findByAvailableTrue();

        // Assert
        assertThat(availablePets).isNotEmpty();
        assertThat(availablePets).allMatch(Pet::isAvailable);
        assertThat(availablePets).extracting(Pet::getName).containsExactlyInAnyOrder("Buddy", "Charlie");
    }

    @Test
    @DisplayName("findByAvailableTrue_whenNoPetsAvailable_returnsEmptyList")
    void findByAvailableTrue_whenNoPetsAvailable_returnsEmptyList() {
        // Arrange
        Pet pet1 = new Pet("Buddy", "dog", "Beagle", LocalDate.of(2020, 1, 1), 150.0);
        pet1.setAvailable(false);
        Pet pet2 = new Pet("Mittens", "cat", "Siamese", LocalDate.of(2019, 5, 10), 100.0);
        pet2.setAvailable(false);
        petRepository.save(pet1);
        petRepository.save(pet2);

        // Act
        List<Pet> availablePets = petRepository.findByAvailableTrue();

        // Assert
        assertThat(availablePets).isEmpty();
    }

    @Test
    @DisplayName("findBySpeciesIgnoreCase_whenSpeciesExists_returnsListOfPetsIgnoringCase")
    void findBySpeciesIgnoreCase_whenSpeciesExists_returnsListOfPetsIgnoringCase() {
        // Arrange
        Pet pet1 = new Pet("Buddy", "Dog", "Beagle", LocalDate.of(2020, 1, 1), 150.0);
        Pet pet2 = new Pet("Mittens", "cat", "Siamese", LocalDate.of(2019, 5, 10), 100.0);
        Pet pet3 = new Pet("Charlie", "DOG", "Labrador", LocalDate.of(2021, 3, 15), 200.0);
        petRepository.save(pet1);
        petRepository.save(pet2);
        petRepository.save(pet3);

        // Act
        List<Pet> dogs = petRepository.findBySpeciesIgnoreCase("dog");

        // Assert
        assertThat(dogs).isNotEmpty();
        assertThat(dogs).extracting(Pet::getSpecies).allMatch(s -> s.equalsIgnoreCase("dog"));
        assertThat(dogs).extracting(Pet::getName).containsExactlyInAnyOrder("Buddy", "Charlie");
    }

    @Test
    @DisplayName("findBySpeciesIgnoreCase_whenSpeciesNotExists_returnsEmptyList")
    void findBySpeciesIgnoreCase_whenSpeciesNotExists_returnsEmptyList() {
        // Arrange
        Pet pet1 = new Pet("Buddy", "Dog", "Beagle", LocalDate.of(2020, 1, 1), 150.0);
        Pet pet2 = new Pet("Mittens", "cat", "Siamese", LocalDate.of(2019, 5, 10), 100.0);
        petRepository.save(pet1);
        petRepository.save(pet2);

        // Act
        List<Pet> unicorns = petRepository.findBySpeciesIgnoreCase("unicorn");

        // Assert
        assertThat(unicorns).isEmpty();
    }

    @Test
    @DisplayName("findAvailableByMaxPrice_whenPetsAvailableUnderMaxPrice_returnsListOfPets")
    void findAvailableByMaxPrice_whenPetsAvailableUnderMaxPrice_returnsListOfPets() {
        // Arrange
        Pet pet1 = new Pet("Buddy", "dog", "Beagle", LocalDate.of(2020, 1, 1), 90.0);
        pet1.setAvailable(true);
        Pet pet2 = new Pet("Mittens", "cat", "Siamese", LocalDate.of(2019, 5, 10), 120.0);
        pet2.setAvailable(true);
        Pet pet3 = new Pet("Charlie", "dog", "Labrador", LocalDate.of(2021, 3, 15), 100.0);
        pet3.setAvailable(true);
        Pet pet4 = new Pet("Max", "dog", "Bulldog", LocalDate.of(2018, 7, 20), 80.0);
        pet4.setAvailable(false);
        petRepository.save(pet1);
        petRepository.save(pet2);
        petRepository.save(pet3);
        petRepository.save(pet4);

        // Act
        List<Pet> affordableAvailablePets = petRepository.findAvailableByMaxPrice(100.0);

        // Assert
        assertThat(affordableAvailablePets).isNotEmpty();
        assertThat(affordableAvailablePets).allMatch(Pet::isAvailable);
        assertThat(affordableAvailablePets).allMatch(p -> p.getPrice() <= 100.0);
        assertThat(affordableAvailablePets).extracting(Pet::getName).containsExactlyInAnyOrder("Buddy", "Charlie");
    }

    @Test
    @DisplayName("findAvailableByMaxPrice_whenNoPetsUnderMaxPrice_returnsEmptyList")
    void findAvailableByMaxPrice_whenNoPetsUnderMaxPrice_returnsEmptyList() {
        // Arrange
        Pet pet1 = new Pet("Buddy", "dog", "Beagle", LocalDate.of(2020, 1, 1), 150.0);
        pet1.setAvailable(true);
        Pet pet2 = new Pet("Mittens", "cat", "Siamese", LocalDate.of(2019, 5, 10), 100.0);
        pet2.setAvailable(false);
        petRepository.save(pet1);
        petRepository.save(pet2);

        // Act
        List<Pet> affordableAvailablePets = petRepository.findAvailableByMaxPrice(50.0);

        // Assert
        assertThat(affordableAvailablePets).isEmpty();
    }

    @Test
    @DisplayName("existsByNameAndSpecies_whenPetExists_returnsTrue")
    void existsByNameAndSpecies_whenPetExists_returnsTrue() {
        // Arrange
        Pet pet = new Pet("Buddy", "dog", "Beagle", LocalDate.of(2020, 1, 1), 150.0);
        petRepository.save(pet);

        // Act
        boolean exists = petRepository.existsByNameAndSpecies("Buddy", "dog");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByNameAndSpecies_whenPetNotExists_returnsFalse")
    void existsByNameAndSpecies_whenPetNotExists_returnsFalse() {
        // Arrange
        Pet pet = new Pet("Mittens", "cat", "Siamese", LocalDate.of(2019, 5, 10), 100.0);
        petRepository.save(pet);

        // Act
        boolean exists = petRepository.existsByNameAndSpecies("Buddy", "dog");

        // Assert
        assertThat(exists).isFalse();
    }
}
