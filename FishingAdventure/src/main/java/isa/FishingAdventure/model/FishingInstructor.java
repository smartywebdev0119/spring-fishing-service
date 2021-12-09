package isa.FishingAdventure.model;

import javax.persistence.*;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
public class FishingInstructor extends User {

    @Column(name = "biography", nullable = false)
    private String biography;

    public FishingInstructor(Integer userId, String email, String password, String name, String surname, String phoneNumber,
                             UserType userType, double points, UserCategory category, boolean activated, Address address, Timestamp lastPasswordResetDate) {
        super(userId, email, password, name, surname, phoneNumber, userType, points, category, activated, address, lastPasswordResetDate);
    }

    public FishingInstructor() {
    }

    public String getBiography() {
        return this.biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }
}