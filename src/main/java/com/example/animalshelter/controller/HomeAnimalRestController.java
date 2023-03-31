package com.example.animalshelter.controller;

import com.example.animalshelter.config.AnimalPhotoDTO;
import com.example.animalshelter.config.RandomAnimalDTO;
import com.example.animalshelter.model.Animal;
import com.example.animalshelter.model.AnimalPhoto;
import com.example.animalshelter.repository.AnimalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
// Add logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
public class HomeAnimalRestController {

    @Autowired
    private AnimalRepository    animalRepository;

    private static final Logger logger = LoggerFactory.getLogger(HomeAnimalRestController.class);

    @GetMapping("/randomAnimal")
    public ResponseEntity<RandomAnimalDTO> getRandomAnimal() {
        Animal randomAnimal = animalRepository.findRandomAnimal();
        if (randomAnimal != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate arrivalDate = LocalDate.parse(randomAnimal.getArrivalDate(), formatter);
            long daysInShelter = ChronoUnit.DAYS.between(arrivalDate, LocalDate.now());

            RandomAnimalDTO randomAnimalDTO = new RandomAnimalDTO();
            randomAnimalDTO.setId(randomAnimal.getId());
            randomAnimalDTO.setName(randomAnimal.getName());
            randomAnimalDTO.setDaysInShelter(daysInShelter);

            List<String> imageUrls = new ArrayList<>();
            List<AnimalPhoto> animalPhotos = randomAnimal.getPhotos();
            logger.info("Animal photos count: {}", animalPhotos.size()); // Add this line to log the photo count

            if (!animalPhotos.isEmpty()) {
                for (AnimalPhoto animalPhoto : animalPhotos) {
                    AnimalPhotoDTO animalPhotoDTO = convertAnimalPhotoToDTO(animalPhoto);
                    imageUrls.add(animalPhotoDTO.getImageUrl());
                    }
                }
            randomAnimalDTO.setImageUrls(imageUrls);
            return ResponseEntity.ok(randomAnimalDTO);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    public String getImageUrl(String fileName) {
        String relativePath = "/images/" + fileName; // Замените на ваш путь к файлам изображений
        return relativePath;
    }
    private AnimalPhotoDTO convertAnimalPhotoToDTO(AnimalPhoto animalPhoto) {
        AnimalPhotoDTO dto = new AnimalPhotoDTO();
        dto.setId(animalPhoto.getId());
        dto.setImageUrl(getImageUrl(animalPhoto.getFileName()));
        return dto;
    }
}
