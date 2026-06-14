package com.example.petstore.service;

import com.example.petstore.exception.PetNotFoundException;
import com.example.petstore.model.Pet;
import com.example.petstore.repository.PetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic layer for pet management operations.
 */
@Service
@Transactional
public class PetService {

    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public List<Pet> findAll() {
        return petRepository.findAll();
    }

    public List<Pet> findAvailable() {
        return petRepository.findByAvailableTrue();
    }

    public List<Pet> findBySpecies(String species) {
        if (species == null || species.isBlank()) {
            throw new IllegalArgumentException("Species must not be blank");
        }
        return petRepository.findBySpeciesIgnoreCase(species);
    }

    public List<Pet> findAvailableUnderPrice(double maxPrice) {
        if (maxPrice <= 0) {
            throw new IllegalArgumentException("Max price must be positive");
        }
        return petRepository.findAvailableByMaxPrice(maxPrice);
    }

    public Pet findById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new PetNotFoundException(id));
    }

    public Pet create(Pet pet) {
        if (petRepository.existsByNameAndSpecies(pet.getName(), pet.getSpecies())) {
            throw new IllegalStateException(
                "A pet named '" + pet.getName() + "' of species '" + pet.getSpecies() + "' already exists");
        }
        return petRepository.save(pet);
    }

    public Pet update(Long id, Pet updated) {
        Pet existing = findById(id);
        existing.setName(updated.getName());
        existing.setSpecies(updated.getSpecies());
        existing.setBreed(updated.getBreed());
        existing.setBirthDate(updated.getBirthDate());
        existing.setPrice(updated.getPrice());
        existing.setAvailable(updated.isAvailable());
        return petRepository.save(existing);
    }

    public void delete(Long id) {
        Pet pet = findById(id);
        petRepository.delete(pet);
    }

    public Pet markUnavailable(Long id) {
        Pet pet = findById(id);
        pet.setAvailable(false);
        return petRepository.save(pet);
    }
}
