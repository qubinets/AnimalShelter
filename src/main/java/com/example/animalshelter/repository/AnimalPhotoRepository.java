package com.example.animalshelter.repository;

import com.example.animalshelter.model.AnimalPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AnimalPhotoRepository extends JpaRepository<AnimalPhoto, Long> {
    Optional<AnimalPhoto> findByFileName(String fileName);
}