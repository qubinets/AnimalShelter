package com.example.animalshelter.controller;

import com.example.animalshelter.model.AnimalPhoto;
import com.example.animalshelter.service.AnimalPhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.SQLException;

@Controller
@RequestMapping("/photos")
public class PhotoController {

    @Autowired
    private AnimalPhotoService animalPhotoService;

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> getPhoto(@PathVariable String fileName) {
        AnimalPhoto photo = animalPhotoService.findByFileName(fileName);
        if (photo != null) {
            byte[] photoData;
            try {
                photoData = photo.getData().getBytes(1, (int) photo.getData().length());
            } catch (SQLException e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            ByteArrayResource resource = new ByteArrayResource(photoData);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=\"" + photo.getFileName() + "\"")
                    .contentType(MediaType.parseMediaType(photo.getFileType()))
                    .body(resource);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}