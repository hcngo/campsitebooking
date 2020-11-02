package com.hcngo.booking.campsite;

import com.hcngo.booking.campsite.model.Reservation;
import com.hcngo.booking.campsite.repository.ReservationRepository;
import com.hcngo.booking.campsite.service.ReservationService;
import com.hcngo.booking.campsite.util.Constants;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

import lombok.extern.java.Log;

@Log
@SpringBootApplication
public class CampsiteApplication {

	public static void main(String[] args) {
		SpringApplication.run(CampsiteApplication.class, args);
	}

	@Bean
	public CommandLineRunner setup(ReservationRepository reservationRepository) {
		return (args) -> {
			log.info("Save a few reservation records: ");
			reservationRepository.save(new Reservation("2020-03-11", "2020-03-14", "email1@test.com", "email1"));
			reservationRepository.save(new Reservation("2020-03-16", "2020-03-17", "email2@test.com", "email2"));
			reservationRepository.save(new Reservation("2020-03-01", "2020-03-09", "email3@test.com", "email3"));
			reservationRepository.save(new Reservation("2020-03-20", "2020-03-25", "email4@test.com", "email4"));

			log.info("search:");
			List<Reservation> l = reservationRepository.findReservationByEndDateBetween(Constants.sdformat.parse("2020-03-12"), Constants.sdformat.parse("2020-03-20"));
			log.info(l.toString());
		};
	}
}
