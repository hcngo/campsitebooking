package com.hcngo.booking.campsite.repository;

import com.hcngo.booking.campsite.model.Reservation;

import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface ReservationRepository extends CrudRepository<Reservation, String> {
    Reservation findReservationById(String id);
    List<Reservation> findReservationByStartDateBetween(Date d1, Date d2);
    List<Reservation> findReservationByEndDateBetween(Date d1, Date d2);
    List<Reservation> findReservationByStartDateLessThanEqualAndEndDateGreaterThanEqual(Date d1, Date d2);
}
