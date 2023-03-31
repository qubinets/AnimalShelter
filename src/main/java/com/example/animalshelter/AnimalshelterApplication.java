package com.example.animalshelter;

import com.example.animalshelter.model.Animal;
import com.example.animalshelter.model.User;
import com.example.animalshelter.repository.AnimalRepository;
import com.example.animalshelter.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@SpringBootApplication
public class AnimalshelterApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnimalshelterApplication.class, args);
	}

	@Bean
	public CommandLineRunner createAdminUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			String adminUsername = "hello";
			String adminPassword = "hello"; // Замените на свой пароль

			String superAdminUsername = "hello";
			String superAdminPassword = "hello"; // Замените на свой пароль

			if (userRepository.findByUsername(adminUsername).isEmpty()) {
				User admin = new User();
				admin.setUsername(adminUsername);
				admin.setPassword(passwordEncoder.encode(adminPassword));
				admin.setRole("ADMIN");

				userRepository.save(admin);
			}
			if (userRepository.findByUsername(superAdminUsername).isEmpty()) {
				User supAdmin = new User();
				supAdmin.setUsername(superAdminUsername);
				supAdmin.setPassword(superAdminPassword);
				supAdmin.setRole("ADMIN");

				userRepository.save(supAdmin);
			}
		};

	}
	@Bean
	public CommandLineRunner createPet(AnimalRepository animalRepository) {
		return args -> {
			Random random = new Random();
			int count = 25;
			String[] origins = {"Johvi", "Kohtla-Jarve", "Narva", "Sillamae", "Luganuse"};

// Создание случайных собак
			for (int i = 0; i < count; i++) {
				Animal dog = new Animal();
				dog.setAge(String.valueOf(random.nextInt(15) + 1)); // Возраст от 1 до 15
				dog.setName("Dog" + i);

				LocalDate randomArrivalDate = LocalDate.now().minus(random.nextInt(30) + 1, ChronoUnit.DAYS);
				dog.setArrivalDate(randomArrivalDate.toString()); // Дата прибытия от 1 до 30 дней назад

				dog.setGender(random.nextBoolean() ? "male" : "female"); // Случайный пол
				dog.setType("dog");
				dog.setOrigin(origins[random.nextInt(origins.length)]); // Случайное место поступления
				animalRepository.save(dog);
			}

// Создание случайных кошек
			for (int i = 0; i < count; i++) {
				Animal cat = new Animal();
				cat.setAge(String.valueOf(random.nextInt(15) + 1)); // Возраст от 1 до 15
				cat.setName("Cat" + i);

				LocalDate randomArrivalDate = LocalDate.now().minus(random.nextInt(30) + 1, ChronoUnit.DAYS);
				cat.setArrivalDate(randomArrivalDate.toString()); // Дата прибытия от 1 до 30 дней назад

				cat.setGender(random.nextBoolean() ? "male" : "female"); // Случайный пол
				cat.setType("cat");
				cat.setOrigin(origins[random.nextInt(origins.length)]); // Случайное место поступления
				animalRepository.save(cat);
			}
		};
	}
}
