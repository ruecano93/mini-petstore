package com.example.petstore.mapper;

import com.example.petstore.model.Pet;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Maps Pet entities to DTO representations (plain Map for simplicity).
 */
@Component
public class PetMapper {

    public Map<String, Object> toDto(Pet pet) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id",        pet.getId());
        dto.put("name",      pet.getName());
        dto.put("species",   pet.getSpecies());
        dto.put("breed",     pet.getBreed());
        dto.put("birthDate", pet.getBirthDate() != null ? pet.getBirthDate().toString() : null);
        dto.put("price",     pet.getPrice());
        dto.put("available", pet.isAvailable());
        return dto;
    }

    public Pet fromDto(Map<String, Object> dto) {
        Pet pet = new Pet();
        pet.setName((String) dto.get("name"));
        pet.setSpecies((String) dto.get("species"));
        pet.setBreed((String) dto.get("breed"));
        pet.setPrice(((Number) dto.getOrDefault("price", 0.0)).doubleValue());
        pet.setAvailable((Boolean) dto.getOrDefault("available", true));
        return pet;
    }
}
