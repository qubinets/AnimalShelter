package com.example.animalshelter.service;
import com.example.animalshelter.model.Animal;
import com.example.animalshelter.model.AnimalPhoto;
import com.example.animalshelter.repository.AnimalPhotoRepository;
import com.example.animalshelter.repository.AnimalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;
import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AnimalPhotoService {


        @Autowired
        private AnimalPhotoRepository animalPhotoRepository;

        @Autowired
        private AnimalRepository animalRepository;

        @Value("${file.upload-dir}")
        private String uploadDir;

    @Autowired
    private ResourceLoader resourceLoader;


    public AnimalPhoto findById(Long id) {
        return animalPhotoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid photo id: " + id));
    }

    public List<AnimalPhoto> findAllByAnimalId(Long animalId) {
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid animal id: " + animalId));
        return animal.getPhotos();
    }


    public boolean addPhotos(MultipartFile[] files, Long animalId) throws IOException, SQLException {
        if (files == null || files.length == 0) {

        }
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid animal id: " + animalId));

        // Check if the upload directory exists and create it if it doesn't
        File uploadDirectory = new File(uploadDir);
        if (!uploadDirectory.exists()) {
            if (!uploadDirectory.mkdirs()) {
                throw new IOException("Failed to create upload directory");
            }
        }

        int photosToAdd = files.length;
        if (animal.getPhotos().size() + photosToAdd <= 5) {
            for (MultipartFile file : files) {
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                String fileType = file.getContentType();
                long fileSize = file.getSize();
                byte[] fileData = file.getBytes();


                // Check available free space
                long freeSpaceKb = new File(uploadDir).getUsableSpace() / 1024; // Convert bytes to KB
                if (freeSpaceKb < fileSize / 1024) {
                    throw new IOException("Not enough free space on the disk");
                }

                // Resize image to reduce file size, if necessary
                BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(fileData));
                double scaleFactor = 1.0;
                int targetWidth = originalImage.getWidth();
                int targetHeight = originalImage.getHeight();
                int maxSizeKb = 1000; // Maximum allowed size in KB
                if (fileSize > maxSizeKb * 1024) {
                    scaleFactor = Math.min(1.0, Math.sqrt(maxSizeKb * 1024.0 / fileSize));
                    targetWidth = (int) Math.round(originalImage.getWidth() * scaleFactor);
                    targetHeight = (int) Math.round(originalImage.getHeight() * scaleFactor);
                }
                BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, originalImage.getType());
                Graphics2D graphics2D = resizedImage.createGraphics();
                graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
                graphics2D.dispose();

                String newFileName = UUID.randomUUID().toString() + "_" + fileName;
                File targetFile = new File(uploadDir + "/" + newFileName);
                ImageIO.write(resizedImage, fileType.split("/")[1], targetFile);

                AnimalPhoto photo = new AnimalPhoto();
                photo.setFileName(newFileName);
                photo.setFileType(fileType);
                photo.setFileSize(fileSize);
                // Replace the following line with using InputStream and Blob
                try (InputStream inputStream = file.getInputStream()) {
                    Blob blob = new SerialBlob(inputStream.readAllBytes());
                    photo.setData(blob);
                }
                photo.setAnimal(animal);
                animalPhotoRepository.save(photo);
            }
            return true;
        } else {
            return false;
        }
    }
    public AnimalPhoto findByFileName(String fileName) {
        return animalPhotoRepository.findByFileName(fileName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid file name: " + fileName));
    }
    public void deletePhoto(Long id) {
        AnimalPhoto photo = findById(id);
        String fileName = photo.getFileName();
        File file = new File(uploadDir + "/" + fileName);
        if (file.exists()) {
            file.delete();
        }
        animalPhotoRepository.deleteById(id);
    }
}