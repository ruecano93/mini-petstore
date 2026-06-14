package com.example.petstore.repository;

import com.example.petstore.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for Pet entities.
 */
@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findByAvailableTrue();

    List<Pet> findBySpeciesIgnoreCase(String species);

    @Query("SELECT p FROM Pet p WHERE p.price <= :maxPrice AND p.available = true")
    List<Pet> findAvailableByMaxPrice(double maxPrice);

    boolean existsByNameAndSpecies(String name, String species);
}
