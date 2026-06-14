package com.example.petstore.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Represents a pet in the store.
 */
@Entity
@Table(name = "pets")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String species;

    private String breed;

    private LocalDate birthDate;

    @Column(nullable = false)
    private double price;

    private boolean available = true;

    public Pet() {}

    public Pet(String name, String species, String breed, LocalDate birthDate, double price) {
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.birthDate = birthDate;
        this.price = price;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }

    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
