package isa.FishingAdventure.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class AdvertiserEarnings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "reservation", nullable = false)
    private Reservation reservation;

    @Column(name = "advertiserEmail", nullable = false)
    private String advertiserEmail;

    @Column(name = "amountEarned", nullable = false)
    private Double amountEarned;

    @Column(name = "dateOfTransaction", nullable = false)
    private Date dateOfTransaction;

    public AdvertiserEarnings(Integer id, Reservation reservation, String advertiserEmail, Double amountEarned,
            Date dateOfTransaction) {
        this.id = id;
        this.reservation = reservation;
        this.advertiserEmail = advertiserEmail;
        this.amountEarned = amountEarned;
        this.dateOfTransaction = dateOfTransaction;
    }

    public AdvertiserEarnings() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Reservation getReservation() {
        return this.reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public String getAdvertiserEmail() {
        return this.advertiserEmail;
    }

    public void setAdvertiserEmail(String advertiserEmail) {
        this.advertiserEmail = advertiserEmail;
    }

    public Double getAmountEarned() {
        return this.amountEarned;
    }

    public void setAmountEarned(Double amountEarned) {
        this.amountEarned = amountEarned;
    }

    public Date getDateOfTransaction() {
        return this.dateOfTransaction;
    }

    public void setDateOfTransaction(Date dateOfTransaction) {
        this.dateOfTransaction = dateOfTransaction;
    }

}
