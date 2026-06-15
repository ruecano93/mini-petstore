package com.example.petstore.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.petstore.model.Pet;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PetMapperTest {

	private PetMapper petMapper;

	@BeforeEach
	void setUp() {
		petMapper = new PetMapper();
	}

	@Test
	void toDto_validPet_returnsCorrectMap() {
		// Arrange
		Pet pet = new Pet();
		pet.setId(10L);
		pet.setName("Max");
		pet.setSpecies("Dog");
		pet.setBreed("Labrador");
		pet.setBirthDate(LocalDate.of(2020, 5, 20));
		pet.setPrice(200.0);
		pet.setAvailable(false);

		// Act
		Map<String, Object> dto = petMapper.toDto(pet);

		// Assert
		assertThat(dto).containsEntry("id", 10L);
		assertThat(dto).containsEntry("name", "Max");
		assertThat(dto).containsEntry("species", "Dog");
		assertThat(dto).containsEntry("breed", "Labrador");
		assertThat(dto).containsEntry("birthDate", "2020-05-20");
		assertThat(dto).containsEntry("price", 200.0);
		assertThat(dto).containsEntry("available", false);
	}

	@Test
	void toDto_petWithNullBirthDate_returnsMapWithNullBirthDate() {
		// Arrange
		Pet pet = new Pet();
		pet.setId(11L);
		pet.setName("Bella");
		pet.setSpecies("Cat");
		pet.setBreed("Siamese");
		pet.setBirthDate(null);
		pet.setPrice(150.0);
		pet.setAvailable(true);

		// Act
		Map<String, Object> dto = petMapper.toDto(pet);

		// Assert
		assertThat(dto).containsEntry("birthDate", null);
	}

	@Test
	void fromDto_validMap_returnsPetWithFieldsSet() {
		// Arrange
		Map<String, Object> dto = new LinkedHashMap<>();
		dto.put("name", "Charlie");
		dto.put("species", "Bird");
		dto.put("breed", "Parrot");
		dto.put("price", 75.5);
		dto.put("available", false);

		// Act
		Pet pet = petMapper.fromDto(dto);

		// Assert
		assertThat(pet.getName()).isEqualTo("Charlie");
		assertThat(pet.getSpecies()).isEqualTo("Bird");
		assertThat(pet.getBreed()).isEqualTo("Parrot");
		assertThat(pet.getPrice()).isEqualTo(75.5);
		assertThat(pet.isAvailable()).isFalse();
	}

	@Test
	void fromDto_mapMissingPriceAndAvailable_returnsPetWithDefaultValues() {
		// Arrange
		Map<String, Object> dto = new LinkedHashMap<>();
		dto.put("name", "Daisy");
		dto.put("species", "Rabbit");
		dto.put("breed", "Angora");

		// Act
		Pet pet = petMapper.fromDto(dto);

		// Assert
		assertThat(pet.getPrice()).isEqualTo(0.0);
		assertThat(pet.isAvailable()).isTrue();
	}

	@Test
	void fromDto_mapWithNullName_returnsPetWithNullName() {
		// Arrange
		Map<String, Object> dto = new LinkedHashMap<>();
		dto.put("name", null);
		dto.put("species", "Fish");
		dto.put("breed", "Goldfish");
		dto.put("price", 10.0);
		dto.put("available", true);

		// Act
		Pet pet = petMapper.fromDto(dto);

		// Assert
		assertThat(pet.getName()).isNull();
		assertThat(pet.getSpecies()).isEqualTo("Fish");
		assertThat(pet.getBreed()).isEqualTo("Goldfish");
		assertThat(pet.getPrice()).isEqualTo(10.0);
		assertThat(pet.isAvailable()).isTrue();
	}
}
