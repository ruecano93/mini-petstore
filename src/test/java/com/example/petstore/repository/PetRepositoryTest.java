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

    private Pet createPet(String name, String species, boolean available, double price) {
        Pet pet = new Pet(name, species, null, LocalDate.of(2020, 1, 1), price);
        pet.setAvailable(available);
        return pet;
    }

    @Test
    void findByAvailableTrue_whenPetsAvailable_returnsListOfAvailablePets() {
        // Arrange
        Pet availablePet1 = createPet("Buddy", "dog", true, 100);
        Pet availablePet2 = createPet("Mittens", "cat", true, 150);
        Pet unavailablePet = createPet("Shadow", "dog", false, 200);
        petRepository.saveAll(List.of(availablePet1, availablePet2, unavailablePet));

        // Act
        List<Pet> result = petRepository.findByAvailableTrue();

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(Pet::isAvailable);
        assertThat(result).extracting(Pet::getName).containsExactlyInAnyOrder("Buddy", "Mittens");
    }

    @Test
    void findByAvailableTrue_whenNoPetsAvailable_returnsEmptyList() {
        // Arrange
        Pet unavailablePet1 = createPet("Shadow", "dog", false, 200);
        Pet unavailablePet2 = createPet("Ghost", "cat", false, 150);
        petRepository.saveAll(List.of(unavailablePet1, unavailablePet2));

        // Act
        List<Pet> result = petRepository.findByAvailableTrue();

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findBySpeciesIgnoreCase_whenSpeciesExists_returnsListOfPetsIgnoringCase() {
        // Arrange
        Pet cat1 = createPet("Mittens", "cat", true, 100);
        Pet cat2 = createPet("Whiskers", "Cat", true, 120);
        Pet dog = createPet("Buddy", "dog", true, 150);
        petRepository.saveAll(List.of(cat1, cat2, dog));

        // Act
        List<Pet> result = petRepository.findBySpeciesIgnoreCase("CaT");

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(p -> p.getSpecies().equalsIgnoreCase("cat"));
        assertThat(result).extracting(Pet::getName).containsExactlyInAnyOrder("Mittens", "Whiskers");
    }

    @Test
    void findBySpeciesIgnoreCase_whenSpeciesNotExists_returnsEmptyList() {
        // Arrange
        Pet dog = createPet("Buddy", "dog", true, 150);
        Pet cat = createPet("Mittens", "cat", true, 100);
        petRepository.saveAll(List.of(dog, cat));

        // Act
        List<Pet> result = petRepository.findBySpeciesIgnoreCase("dragon");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findAvailableByMaxPrice_whenPetsAvailableUnderMaxPrice_returnsListOfPets() {
        // Arrange
        Pet pet1 = createPet("Buddy", "dog", true, 90);
        Pet pet2 = createPet("Mittens", "cat", true, 100);
        Pet pet3 = createPet("Shadow", "dog", true, 110);
        Pet pet4 = createPet("Ghost", "cat", false, 80);
        petRepository.saveAll(List.of(pet1, pet2, pet3, pet4));

        // Act
        List<Pet> result = petRepository.findAvailableByMaxPrice(100.0);

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(p -> p.isAvailable() && p.getPrice() <= 100.0);
        assertThat(result).extracting(Pet::getName).containsExactlyInAnyOrder("Buddy", "Mittens");
    }

    @Test
    void findAvailableByMaxPrice_whenNoPetsUnderMaxPrice_returnsEmptyList() {
        // Arrange
        Pet pet1 = createPet("Buddy", "dog", true, 150);
        Pet pet2 = createPet("Mittens", "cat", false, 50);
        petRepository.saveAll(List.of(pet1, pet2));

        // Act
        List<Pet> result = petRepository.findAvailableByMaxPrice(10.0);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void existsByNameAndSpecies_whenPetExists_returnsTrue() {
        // Arrange
        Pet pet = createPet("Buddy", "dog", true, 150);
        petRepository.save(pet);

        // Act
        boolean exists = petRepository.existsByNameAndSpecies("Buddy", "dog");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void existsByNameAndSpecies_whenPetNotExists_returnsFalse() {
        // Arrange
        Pet pet = createPet("Mittens", "cat", true, 100);
        petRepository.save(pet);

        // Act
        boolean exists = petRepository.existsByNameAndSpecies("Ghost", "dog");

        // Assert
        assertThat(exists).isFalse();
    }
}
