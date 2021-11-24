package isa.FishingAdventure.model;

import javax.persistence.Entity;

@Entity
public class Admin extends User {

    public Admin(Integer userId, String email, String password, String name, String surname, String phoneNumber,
                 UserType userType, double points, UserCategory category, boolean activated, Address address) {
        super(userId, email, password, name, surname, phoneNumber, userType, points, category, activated, address);

    }


    public Admin() {
    }
}