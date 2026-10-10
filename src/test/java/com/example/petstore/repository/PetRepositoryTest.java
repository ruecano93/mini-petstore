package com.example.petstore.repository;

import static org.assertj.core.api.Assertions.assertThat;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.petstore.model.Pet;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class PetRepositoryTest {

    @Autowired
    private PetRepository petRepository;

    @BeforeEach
    void setUp() {
        petRepository.deleteAll();
    }

    @Test
    void findByAvailableTrue_whenPetsAvailable_returnsListOfAvailablePets() {
        // Arrange
        Pet pet1 = new Pet("Buddy", "Dog", "Beagle", LocalDate.of(2020, 1, 1), 150.0);
        pet1.setAvailable(true);
        Pet pet2 = new Pet("Mittens", "Cat", "Siamese", LocalDate.of(2019, 5, 15), 100.0);
        pet2.setAvailable(true);
        petRepository.save(pet1);
        petRepository.save(pet2);

        // Act
        List<Pet> availablePets = petRepository.findByAvailableTrue();

        // Assert
        assertThat(availablePets).isNotEmpty();
        assertThat(availablePets).allMatch(Pet::isAvailable);
    }

    @Test
    void findByAvailableTrue_whenNoPetsAvailable_returnsEmptyList() {
        // Arrange
        Pet pet1 = new Pet("Buddy", "Dog", "Beagle", LocalDate.of(2020, 1, 1), 150.0);
        pet1.setAvailable(false);
        petRepository.save(pet1);

        // Act
        List<Pet> availablePets = petRepository.findByAvailableTrue();

        // Assert
        assertThat(availablePets).isEmpty();
    }

    @Test
    void findBySpeciesIgnoreCase_whenSpeciesExists_returnsListOfPetsIgnoringCase() {
        // Arrange
        Pet pet1 = new Pet("Buddy", "Dog", "Beagle", LocalDate.of(2020, 1, 1), 150.0);
        pet1.setAvailable(true);
        Pet pet2 = new Pet("Charlie", "Dog", "Labrador", LocalDate.of(2018, 3, 10), 200.0);
        pet2.setAvailable(true);
        petRepository.save(pet1);
        petRepository.save(pet2);

        // Act
        List<Pet> dogs = petRepository.findBySpeciesIgnoreCase("dog");

        // Assert
        assertThat(dogs).isNotEmpty();
        assertThat(dogs).allMatch(p -> p.getSpecies().equalsIgnoreCase("dog"));
    }

    @Test
    void findBySpeciesIgnoreCase_whenSpeciesNotExists_returnsEmptyList() {
        // Arrange
        Pet pet1 = new Pet("Buddy", "Dog", "Beagle", LocalDate.of(2020, 1, 1), 150.0);
        petRepository.save(pet1);

        // Act
        List<Pet> cats = petRepository.findBySpeciesIgnoreCase("Cat");

        // Assert
        assertThat(cats).isEmpty();
    }

    @Test
    void findAvailableByMaxPrice_whenPetsAvailableUnderMaxPrice_returnsListOfPets() {
        // Arrange
        Pet pet1 = new Pet("Buddy", "Dog", "Beagle", LocalDate.of(2020, 1, 1), 100.0);
        pet1.setAvailable(true);
        Pet pet2 = new Pet("Mittens", "Cat", "Siamese", LocalDate.of(2019, 5, 15), 80.0);
        pet2.setAvailable(true);
        Pet pet3 = new Pet("Charlie", "Dog", "Labrador", LocalDate.of(2018, 3, 10), 150.0);
        pet3.setAvailable(true);
        petRepository.save(pet1);
        petRepository.save(pet2);
        petRepository.save(pet3);

        // Act
        List<Pet> petsUnderMaxPrice = petRepository.findAvailableByMaxPrice(100.0);

        // Assert
        assertThat(petsUnderMaxPrice).isNotEmpty();
        assertThat(petsUnderMaxPrice).allMatch(p -> p.isAvailable() && p.getPrice() <= 100.0);
    }

    @Test
    void findAvailableByMaxPrice_whenNoPetsUnderMaxPrice_returnsEmptyList() {
        // Arrange
        Pet pet1 = new Pet("Buddy", "Dog", "Beagle", LocalDate.of(2020, 1, 1), 150.0);
        pet1.setAvailable(true);
        petRepository.save(pet1);

        // Act
        List<Pet> petsUnderMaxPrice = petRepository.findAvailableByMaxPrice(50.0);

        // Assert
        assertThat(petsUnderMaxPrice).isEmpty();
    }

    @Test
    void existsByNameAndSpecies_whenPetExists_returnsTrue() {
        // Arrange
        Pet pet = new Pet("Buddy", "Dog", "Beagle", LocalDate.of(2020, 1, 1), 150.0);
        petRepository.save(pet);

        // Act
        boolean exists = petRepository.existsByNameAndSpecies("Buddy", "Dog");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void existsByNameAndSpecies_whenPetNotExists_returnsFalse() {
        // Arrange
        Pet pet = new Pet("Buddy", "Dog", "Beagle", LocalDate.of(2020, 1, 1), 150.0);
        petRepository.save(pet);

        // Act
        boolean exists = petRepository.existsByNameAndSpecies("Max", "Cat");

        // Assert
        assertThat(exists).isFalse();
    }
}
