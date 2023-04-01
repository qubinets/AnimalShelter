package com.example.animalshelter.controller;

import com.example.animalshelter.config.AnimalPhotoDTO;
import com.example.animalshelter.config.ImageUtils;
import com.example.animalshelter.model.Animal;
import com.example.animalshelter.model.AnimalPhoto;
import com.example.animalshelter.repository.AnimalPhotoRepository;
import com.example.animalshelter.repository.AnimalRepository;
import com.example.animalshelter.service.AnimalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
public class AnimalController {

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private AnimalPhotoRepository animalPhotoRepository;

    @Autowired
    private AnimalService animalService;

    @Autowired
    private DataSource dataSource;

    @Value("${file.upload-dir}")
    private String uploadDir;


    @GetMapping("/home")
    public String home(Model model) {

        return "home";
    }
    @GetMapping("/animals")
    public String animals(Model model) {

        return "animals";
    }
    @GetMapping("/help")
    public String help(Model model) {

        return "help";
    }
    @GetMapping("/contact")
    public String contact(Model model) {

        return "contact";
    }
    @GetMapping("/faq")
    public String faq(Model model) {

        return "faq";
    }

    @GetMapping("/dogs")
    public String showDogs(Model model) {
        Sort sortDescending = Sort.by("id").descending();
        model.addAttribute("animals", animalRepository.findByType("dog", sortDescending));
        return "/fragments/dogs";
    }

    @GetMapping("/cats")
    public String showCats(Model model) {
        Sort sortDescending = Sort.by("id").descending();
        model.addAttribute("animals", animalRepository.findByType("cat", sortDescending));
        return "/fragments/cats";
    }



    @GetMapping("/animals/cats")
    public String showCatsPublic(Model model,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "asc") String sortOrder) {

        int pageSize = 9; // 5 животных на странице

        Sort.Direction sortDirection = sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sortDirection, "arrivalDate"));

        Page<Animal> animalPage = animalRepository.findByType("cat", pageable);
        List<Animal> animals = animalPage.getContent();
        Map<Long, List<AnimalPhotoDTO>> animalPhotos = new HashMap<>();

        for (Animal animal : animals) {
            List<AnimalPhoto> animalPhotoList = animal.getPhotos();
            if (!animalPhotoList.isEmpty()) {
                List<AnimalPhotoDTO> animalPhotoDTOList = animalPhotoList.stream()
                        .map(this::convertAnimalPhotoToDTO)
                        .collect(Collectors.toList());
                animalPhotos.put(animal.getId(), animalPhotoDTOList);
            }
        }

        model.addAttribute("animals", animals);
        model.addAttribute("animalphotos", animalPhotos);
        model.addAttribute("imageUtils", new ImageUtils());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", animalPage.getTotalPages());
        model.addAttribute("photo", new AnimalPhotoDTO()); // добавлено
        model.addAttribute("sortOrder", sortOrder); // добавлено

        return "/catsPublic";
    }

    @GetMapping("/animals/dogs")
    public String showDogsPublic(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "asc") String sortOrder) {

        int pageSize = 9; // 5 животных на странице

        Sort.Direction sortDirection = sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sortDirection, "arrivalDate"));

        Page<Animal> animalPage = animalRepository.findByType("dog", pageable);
        List<Animal> animals = animalPage.getContent();
        Map<Long, List<AnimalPhotoDTO>> animalPhotos = new HashMap<>();

        for (Animal animal : animals) {
            List<AnimalPhoto> animalPhotoList = animal.getPhotos();
            if (!animalPhotoList.isEmpty()) {
                List<AnimalPhotoDTO> animalPhotoDTOList = animalPhotoList.stream()
                        .map(this::convertAnimalPhotoToDTO)
                        .collect(Collectors.toList());
                animalPhotos.put(animal.getId(), animalPhotoDTOList);
            }
        }

        model.addAttribute("animals", animals);
        model.addAttribute("animalphotos", animalPhotos);
        model.addAttribute("imageUtils", new ImageUtils());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", animalPage.getTotalPages());
        model.addAttribute("photo", new AnimalPhotoDTO()); // добавлено
        model.addAttribute("sortOrder", sortOrder); // добавлено

        return "/dogsPublic";
    }
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('USER')")
    @PostMapping(value = "/editpet/{id}", headers = "X-Requested-With=XMLHttpRequest")
    public @ResponseBody String updatePetAjax(@PathVariable("id") Long id, @ModelAttribute Animal animal, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "Ошибка при обновлении животного.";
        }
        Animal existingAnimal = animalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid animal id: " + id));
        existingAnimal.setName(animal.getName());
        existingAnimal.setType(animal.getType());
        existingAnimal.setGender(animal.getGender());
        existingAnimal.setAge(animal.getAge());
        existingAnimal.setArrivalDate(animal.getArrivalDate());
        existingAnimal.setOrigin(animal.getOrigin());
        existingAnimal.setVaccinated(animal.isVaccinated());
        existingAnimal.setNeutered(animal.isNeutered());
        animalRepository.save(existingAnimal);
        return "Животное успешно обновлено!";
    }

    @GetMapping("/getpetphotos/{id}")
    public String getPetPhotos(@PathVariable("id") Long id, Model model) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid animal id: " + id));
        List<AnimalPhoto> animalPhotoList = animal.getPhotos();

        // Преобразовать список AnimalPhoto в список AnimalPhotoDTO
        List<AnimalPhotoDTO> animalPhotoDTOList = animalPhotoList.stream()
                .map(this::convertAnimalPhotoToDTO)
                .collect(Collectors.toList());
        model.addAttribute("animal", animal);
        model.addAttribute("animalphotos", animalPhotoDTOList);
        model.addAttribute("imageUtils", new ImageUtils());
        return "/fragments/petphotos";
    }

    @GetMapping("/editpet/{id}")
    public String showEditPetForm(@PathVariable("id") Long id, Model model) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid animal id: " + id));
        List<AnimalPhoto> animalPhotoList = animal.getPhotos();

        // Преобразовать список AnimalPhoto в список AnimalPhotoDTO
        List<AnimalPhotoDTO> animalPhotoDTOList = animalPhotoList.stream()
                .map(this::convertAnimalPhotoToDTO)
                .collect(Collectors.toList());

        model.addAttribute("animal", animal);
        model.addAttribute("animalphotos", animalPhotoDTOList);
        return "/fragments/editpet";
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
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('USER')")
    @PostMapping(value = "/deletepet/{id}", headers = "X-Requested-With=XMLHttpRequest")
    public @ResponseBody String deletePetAjax(@PathVariable("id") Long id) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid animal id: " + id));

        // Получаем список фотографий животного
        List<AnimalPhoto> photos = animal.getPhotos();

        // Удаляем фото из директории
        for (AnimalPhoto photo : photos) {
            File photoFile = new File(uploadDir + "/" + photo.getFileName());
            if (photoFile.exists()) {
                photoFile.delete();
            }
        }

        // Удаляем животное из базы данных
        animalRepository.deleteById(id);

        return "Животное успешно удалено!";
    }
}