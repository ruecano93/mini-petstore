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
	}

	@Test
	void findByAvailableTrue_shouldReturnListOfAvailablePets() {
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
	void findByAvailableTrue_shouldReturnEmptyList_whenNoAvailablePets() {
		// Arrange
		when(petRepository.findByAvailableTrue()).thenReturn(new ArrayList<>());

		// Act
		List<Pet> result = petRepository.findByAvailableTrue();

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void findBySpeciesIgnoreCase_shouldReturnPetsIgnoringCase() {
		// Arrange
		List<Pet> dogs = new ArrayList<>();
		dogs.add(petAvailableDog);
		when(petRepository.findBySpeciesIgnoreCase("dog")).thenReturn(dogs);

		// Act
		List<Pet> result = petRepository.findBySpeciesIgnoreCase("dog");

		// Assert
		assertThat(result).isNotEmpty();
		assertThat(result).allMatch(p -> "Dog".equalsIgnoreCase(p.getSpecies()));
	}

	@Test
	void findBySpeciesIgnoreCase_shouldReturnEmptyList_whenNoMatchingSpecies() {
		// Arrange
		when(petRepository.findBySpeciesIgnoreCase("Cat")).thenReturn(new ArrayList<>());

		// Act
		List<Pet> result = petRepository.findBySpeciesIgnoreCase("Cat");

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void findAvailableByMaxPrice_shouldReturnAvailablePetsUnderMaxPrice() {
		// Arrange
		List<Pet> petsUnderMaxPrice = new ArrayList<>();
		petsUnderMaxPrice.add(petAvailableDog);
		when(petRepository.findAvailableByMaxPrice(100.0)).thenReturn(petsUnderMaxPrice);

		// Act
		List<Pet> result = petRepository.findAvailableByMaxPrice(100.0);

		// Assert
		assertThat(result).isNotEmpty();
		assertThat(result).allMatch(p -> p.isAvailable() && p.getPrice() <= 100.0);
	}

	@Test
	void findAvailableByMaxPrice_shouldReturnEmptyList_whenNoPetsUnderMaxPrice() {
		// Arrange
		when(petRepository.findAvailableByMaxPrice(50.0)).thenReturn(new ArrayList<>());

		// Act
		List<Pet> result = petRepository.findAvailableByMaxPrice(50.0);

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void existsByNameAndSpecies_shouldReturnTrue_whenPetExists() {
		// Arrange
		when(petRepository.existsByNameAndSpecies("Fido", "Dog")).thenReturn(true);

		// Act
		boolean exists = petRepository.existsByNameAndSpecies("Fido", "Dog");

		// Assert
		assertThat(exists).isTrue();
	}

	@Test
	void existsByNameAndSpecies_shouldReturnFalse_whenPetDoesNotExist() {
		// Arrange
		when(petRepository.existsByNameAndSpecies("Whiskers", "Cat")).thenReturn(false);

		// Act
		boolean exists = petRepository.existsByNameAndSpecies("Whiskers", "Cat");

		// Assert
		assertThat(exists).isFalse();
	}
}
