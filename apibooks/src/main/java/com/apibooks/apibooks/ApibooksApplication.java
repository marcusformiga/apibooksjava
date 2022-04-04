package com.apibooks.apibooks;

import java.util.Arrays;
import java.util.List;

import com.apibooks.apibooks.api.service.EmailService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class ApibooksApplication {

	@Autowired
	EmailService emailService;

	public static void main(String[] args) {
		SpringApplication.run(ApibooksApplication.class, args);
	}
	// sec, min, hour, day, month, year
	// @Scheduled(cron = "0 46 16 1/1 * ?")
	// public void testeAgendamento() {
	// 	System.out.println("Agendamento tarefa");
	// }
	@Bean
	public CommandLineRunner runner() {
		return args -> {
			List<String> emails = Arrays.asList("kiko.antares@gmail.com");
			emailService.sendEmail("Teste email", emails);
			System.out.println("Email enviando");
		};
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

}
