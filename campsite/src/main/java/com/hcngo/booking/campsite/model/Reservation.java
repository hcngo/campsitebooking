package com.hcngo.booking.campsite.model;

import com.hcngo.booking.campsite.util.Constants;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Reservation {
    @EqualsAndHashCode.Include
    @Id
    private String id;

    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    private Date endDate;

    private String email;

    private String name;

    public Reservation() {}

    public Reservation(String startDate, String endDate, String email, String name) throws ParseException {
        this(UUID.randomUUID().toString(), startDate, endDate, email, name);
    }

    public Reservation(String id, String startDate, String endDate, String email, String name) throws ParseException {
        this.id = id;
        this.startDate = Constants.sdformat.parse(startDate);
        this.endDate = Constants.sdformat.parse(endDate);
        this.email = email;
        this.name = name;
    }
}
