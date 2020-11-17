package com.hcngo.booking.campsite;

import com.hcngo.booking.campsite.model.Reservation;
import com.hcngo.booking.campsite.repository.ReservationRepository;
import com.hcngo.booking.campsite.util.Constants;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

import lombok.extern.java.Log;

@Log
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner setup(ReservationRepository reservationRepository) {
		return (args) -> {
			if (args.length == 0 || !args[0].equals("init")) {
				return;
			}
			log.info("Save a few reservation records: ");
			reservationRepository.save(new Reservation("email1Id", "2020-11-11", "2020-11-14", "email1@test.com", "email1"));
			reservationRepository.save(new Reservation("email2Id", "2020-11-16", "2020-11-17", "email2@test.com", "email2"));
			reservationRepository.save(new Reservation("email3Id", "2020-11-01", "2020-11-09", "email3@test.com", "email3"));
			reservationRepository.save(new Reservation("email4Id", "2020-11-20", "2020-11-25", "email4@test.com", "email4"));

			log.info("search:");
			List<Reservation> l = reservationRepository.findReservationByEndDateBetween(Constants.sdformat.parse("2020-11-12"), Constants.sdformat.parse("2020-11-20"));
			log.info(l.toString());
		};
	}
}
