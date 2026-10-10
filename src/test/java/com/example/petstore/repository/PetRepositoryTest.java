package com.example.petstore.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.example.petstore.model.Pet;
import java.util.List;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PetRepositoryTest {

	@Mock
	private PetRepository petRepository;

	private Pet petAvailableDog;
	private Pet petAvailableCat;
	private Pet petUnavailableDog;
	private Pet petAvailableDogLowerCase;

	@BeforeEach
	void setUp() {
		petAvailableDog = new Pet();
		petAvailableDog.setId(1L);
		petAvailableDog.setName("Fido");
		petAvailableDog.setSpecies("Dog");
		petAvailableDog.setAvailable(true);
		petAvailableDog.setPrice(90.0);

		petAvailableCat = new Pet();
		petAvailableCat.setId(2L);
		petAvailableCat.setName("Whiskers");
		petAvailableCat.setSpecies("Cat");
		petAvailableCat.setAvailable(true);
		petAvailableCat.setPrice(150.0);

		petUnavailableDog = new Pet();
		petUnavailableDog.setId(3L);
		petUnavailableDog.setName("Rex");
		petUnavailableDog.setSpecies("Dog");
		petUnavailableDog.setAvailable(false);
		petUnavailableDog.setPrice(50.0);

		petAvailableDogLowerCase = new Pet();
		petAvailableDogLowerCase.setId(4L);
		petAvailableDogLowerCase.setName("Buddy");
		petAvailableDogLowerCase.setSpecies("dog");
		petAvailableDogLowerCase.setAvailable(true);
		petAvailableDogLowerCase.setPrice(80.0);
	}

	@Test
	void findByAvailableTrue_whenPetsAvailable_shouldReturnListOfAvailablePets() {
		// Arrange
		List<Pet> availablePets = new ArrayList<>();
		availablePets.add(petAvailableDog);
		availablePets.add(petAvailableCat);
		when(petRepository.findByAvailableTrue()).thenReturn(availablePets);

		// Act
		List<Pet> result = petRepository.findByAvailableTrue();

		// Assert
		assertThat(result).isNotEmpty();
		assertThat(result).allMatch(Pet::isAvailable);
	}

	@Test
	void findByAvailableTrue_whenNoPetsAvailable_shouldReturnEmptyList() {
		// Arrange
		when(petRepository.findByAvailableTrue()).thenReturn(new ArrayList<>());

		// Act
		List<Pet> result = petRepository.findByAvailableTrue();

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void findBySpeciesIgnoreCase_whenSpeciesExists_shouldReturnListOfPetsIgnoringCase() {
		// Arrange
		List<Pet> dogs = new ArrayList<>();
		dogs.add(petAvailableDog);
		dogs.add(petAvailableDogLowerCase);
		when(petRepository.findBySpeciesIgnoreCase("dog")).thenReturn(dogs);

		// Act
		List<Pet> result = petRepository.findBySpeciesIgnoreCase("dog");

		// Assert
		assertThat(result).isNotEmpty();
		assertThat(result).allMatch(p -> p.getSpecies().equalsIgnoreCase("dog"));
	}

	@Test
	void findBySpeciesIgnoreCase_whenSpeciesNotExists_shouldReturnEmptyList() {
		// Arrange
		when(petRepository.findBySpeciesIgnoreCase("hamster")).thenReturn(new ArrayList<>());

		// Act
		List<Pet> result = petRepository.findBySpeciesIgnoreCase("hamster");

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void findAvailableByMaxPrice_whenPetsAvailableWithinPrice_shouldReturnListOfPets() {
		// Arrange
		List<Pet> petsWithinPrice = new ArrayList<>();
		petsWithinPrice.add(petAvailableDog);
		petsWithinPrice.add(petAvailableDogLowerCase);
		when(petRepository.findAvailableByMaxPrice(90.0)).thenReturn(petsWithinPrice);

		// Act
		List<Pet> result = petRepository.findAvailableByMaxPrice(90.0);

		// Assert
		assertThat(result).isNotEmpty();
		assertThat(result).allMatch(p -> p.isAvailable() && p.getPrice() <= 90.0);
	}

	@Test
	void findAvailableByMaxPrice_whenNoPetsWithinPrice_shouldReturnEmptyList() {
		// Arrange
		when(petRepository.findAvailableByMaxPrice(40.0)).thenReturn(new ArrayList<>());

		// Act
		List<Pet> result = petRepository.findAvailableByMaxPrice(40.0);

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void existsByNameAndSpecies_whenPetExists_shouldReturnTrue() {
		// Arrange
		when(petRepository.existsByNameAndSpecies("Fido", "Dog")).thenReturn(true);

		// Act
		boolean exists = petRepository.existsByNameAndSpecies("Fido", "Dog");

		// Assert
		assertThat(exists).isTrue();
	}

	@Test
	void existsByNameAndSpecies_whenPetNotExists_shouldReturnFalse() {
		// Arrange
		when(petRepository.existsByNameAndSpecies("Ghost", "Cat")).thenReturn(false);

		// Act
		boolean exists = petRepository.existsByNameAndSpecies("Ghost", "Cat");

		// Assert
		assertThat(exists).isFalse();
	}
}
