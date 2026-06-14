package com.example.petstore.controller;

import com.example.petstore.mapper.PetMapper;
import com.example.petstore.model.Pet;
import com.example.petstore.service.PetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller exposing CRUD endpoints for pets.
 */
@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;
    private final PetMapper petMapper;

    public PetController(PetService petService, PetMapper petMapper) {
        this.petService = petService;
        this.petMapper = petMapper;
    }

    @GetMapping
    public List<Map<String, Object>> listAll() {
        return petService.findAll().stream().map(petMapper::toDto).toList();
    }

    @GetMapping("/available")
    public List<Map<String, Object>> listAvailable() {
        return petService.findAvailable().stream().map(petMapper::toDto).toList();
    }

    @GetMapping("/species/{species}")
    public List<Map<String, Object>> bySpecies(@PathVariable String species) {
        return petService.findBySpecies(species).stream().map(petMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(petMapper.toDto(petService.findById(id)));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> create(@RequestBody Pet pet) {
        return petMapper.toDto(petService.create(pet));
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody Pet pet) {
        return petMapper.toDto(petService.update(id, pet));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        petService.delete(id);
    }

    @PatchMapping("/{id}/unavailable")
    public Map<String, Object> markUnavailable(@PathVariable Long id) {
        return petMapper.toDto(petService.markUnavailable(id));
    }
}
