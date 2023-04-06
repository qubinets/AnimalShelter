package com.example.animalshelter.config;

import com.example.animalshelter.model.Animal;

import java.util.List;

public class RandomAnimalDTO {

    private String name;
    private String imageUrl;

    private long daysInShelter;

    private Long id;

    private List<String> imageUrls;

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getDaysInShelter() {
        return daysInShelter;
    }

    public void setDaysInShelter(long daysInShelter) {
        this.daysInShelter = daysInShelter;
    }
}