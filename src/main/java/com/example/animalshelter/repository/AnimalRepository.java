package com.example.animalshelter.repository;

import com.example.animalshelter.model.Animal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {

    List<Animal> findByType(String type);
    Page<Animal> findByType(String type, Pageable pageable);
    List<Animal> findByType(@Param("type") String type, Sort sort);
    @Query(value = "SELECT * FROM animal ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Animal findRandomAnimal();
}
