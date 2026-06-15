package com.example.petstore.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;
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
	void findByAvailableTrue_shouldReturnListOfAvailablePets_whenPetsExist() {
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
	void findByAvailableTrue_shouldReturnEmptyList_whenNoAvailablePetsExist() {
		// Arrange
		when(petRepository.findByAvailableTrue()).thenReturn(new ArrayList<>());

		// Act
		List<Pet> result = petRepository.findByAvailableTrue();

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void findBySpeciesIgnoreCase_shouldReturnPetsIgnoringCase_whenSpeciesExists() {
		// Arrange
		String species = "dog";
		List<Pet> petsBySpecies = new ArrayList<>();
		petsBySpecies.add(petAvailableDog);
		petsBySpecies.add(petAvailableDogLowerCase);
		when(petRepository.findBySpeciesIgnoreCase(species)).thenReturn(petsBySpecies);

		// Act
		List<Pet> result = petRepository.findBySpeciesIgnoreCase(species);

		// Assert
		assertThat(result).isNotEmpty();
		assertThat(result).allMatch(p -> p.getSpecies().equalsIgnoreCase(species));
	}

	@Test
	void findBySpeciesIgnoreCase_shouldReturnEmptyList_whenNoPetsMatchSpecies() {
		// Arrange
		String species = "hamster";
		when(petRepository.findBySpeciesIgnoreCase(species)).thenReturn(new ArrayList<>());

		// Act
		List<Pet> result = petRepository.findBySpeciesIgnoreCase(species);

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void findAvailableByMaxPrice_shouldReturnAvailablePetsUnderMaxPrice_whenPetsMatchCriteria() {
		// Arrange
		double maxPrice = 100.0;
		List<Pet> petsUnderMaxPrice = new ArrayList<>();
		petsUnderMaxPrice.add(petAvailableDog);
		petsUnderMaxPrice.add(petAvailableDogLowerCase);
		when(petRepository.findAvailableByMaxPrice(maxPrice)).thenReturn(petsUnderMaxPrice);

		// Act
		List<Pet> result = petRepository.findAvailableByMaxPrice(maxPrice);

		// Assert
		assertThat(result).isNotEmpty();
		assertThat(result).allMatch(p -> p.isAvailable() && p.getPrice() <= maxPrice);
	}

	@Test
	void findAvailableByMaxPrice_shouldReturnEmptyList_whenNoPetsMatchPriceCriteria() {
		// Arrange
		double maxPrice = 50.0;
		when(petRepository.findAvailableByMaxPrice(maxPrice)).thenReturn(new ArrayList<>());

		// Act
		List<Pet> result = petRepository.findAvailableByMaxPrice(maxPrice);

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void existsByNameAndSpecies_shouldReturnTrue_whenPetExistsWithNameAndSpecies() {
		// Arrange
		String name = "Fido";
		String species = "Dog";
		when(petRepository.existsByNameAndSpecies(name, species)).thenReturn(true);

		// Act
		boolean exists = petRepository.existsByNameAndSpecies(name, species);

		// Assert
		assertThat(exists).isTrue();
	}

	@Test
	void existsByNameAndSpecies_shouldReturnFalse_whenNoPetExistsWithNameAndSpecies() {
		// Arrange
		String name = "Ghost";
		String species = "Cat";
		when(petRepository.existsByNameAndSpecies(name, species)).thenReturn(false);

		// Act
		boolean exists = petRepository.existsByNameAndSpecies(name, species);

		// Assert
		assertThat(exists).isFalse();
	}
}
