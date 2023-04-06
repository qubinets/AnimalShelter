package com.example.animalshelter.controller;


import com.example.animalshelter.service.AnimalPhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.sql.SQLException;


@Controller
@RequestMapping("/animals/{animalId}/photos")
public class AnimalPhotoController {

    @Autowired
    private AnimalPhotoService animalPhotoService;

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('USER')")
    @PostMapping
    public ResponseEntity<String> uploadPhotos(@RequestParam("photos") MultipartFile[] files, @PathVariable Long animalId) {
        try {
            if (animalPhotoService.addPhotos(files, animalId)) {
                return ResponseEntity.ok("Photos uploaded successfully!");
            } else {
                return ResponseEntity.badRequest().body("Max 5 photos are allowed per animal.");
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while uploading photos.");
        }
    }
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('USER')")
    @PostMapping("/{photoId}/delete")
    public String deletePhoto(@PathVariable Long photoId, @PathVariable Long animalId,
                              RedirectAttributes redirectAttributes) {
        animalPhotoService.deletePhoto(photoId);
        redirectAttributes.addFlashAttribute("message", "Photo deleted successfully.");
        return "redirect:/animals/" + animalId + "/photos";
    }


}