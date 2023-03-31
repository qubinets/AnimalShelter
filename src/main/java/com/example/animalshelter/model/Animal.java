package com.example.animalshelter.model;

import com.sun.istack.NotNull;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @NotNull
    private String type; // "cat" или "dog"
    private String gender;
    private String age;
    private String arrivalDate;
    private String origin;
    private boolean vaccinated;
    private boolean neutered;
    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnimalPhoto> photos = new ArrayList<>();


    public Animal() {
    }

    public Animal(String name, String type, String gender, String age, String arrivalDate, String origin, boolean vaccinated, boolean neutered) {
        this.name = name;
        this.type = type;
        this.gender = gender;
        this.age = age;
        this.arrivalDate = arrivalDate;
        this.origin = origin;
        this.vaccinated = vaccinated;
        this.neutered = neutered;
    }

    public Animal(String name, String type, String gender, String age, String arrivalDate, String origin, boolean vaccinated, boolean neutered, List<AnimalPhoto> photos) {

        this.name = name;
        this.type = type;
        this.gender = gender;
        this.age = age;
        this.arrivalDate = arrivalDate;
        this.origin = origin;
        this.vaccinated = vaccinated;
        this.neutered = neutered;
        this.photos = photos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(String arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public boolean isVaccinated() {
        return vaccinated;
    }

    public void setVaccinated(boolean vaccinated) {
        this.vaccinated = vaccinated;
    }

    public boolean isNeutered() {
        return neutered;
    }

    public void setNeutered(boolean neutered) {
        this.neutered = neutered;
    }
    public List<AnimalPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<AnimalPhoto> photos) {
        this.photos = photos;
    }
}