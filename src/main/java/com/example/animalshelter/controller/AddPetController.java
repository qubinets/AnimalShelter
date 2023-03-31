package com.example.animalshelter.controller;

import com.example.animalshelter.model.Animal;
import com.example.animalshelter.repository.AnimalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class AddPetController {

    @Autowired
    private AnimalRepository animalRepository;


    @GetMapping("/addpet")
    public String showAddPetForm(Model model) {
        model.addAttribute("animal", new Animal());
        return "/fragments/addpet";
    }
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('USER')")
    @PostMapping(value = "/addpet", headers = "X-Requested-With=XMLHttpRequest")
    public ResponseEntity<String> addPetAjax(@ModelAttribute("animal") Animal animal, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("Ошибка при добавлении животного.");
        }
        Animal savedAnimal = animalRepository.save(animal);
        return ResponseEntity.ok(String.valueOf(savedAnimal.getId()));
    }
}
