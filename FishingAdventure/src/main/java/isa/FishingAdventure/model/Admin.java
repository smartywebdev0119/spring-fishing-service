package isa.FishingAdventure.model;

public class Admin extends User {

    public Admin(int userId, String email, String password, String name, String surname, String phoneNumber,
            UserType userType, double points, UserCategory category, boolean activated, Address address) {
        super(userId, email, password, name, surname, phoneNumber, userType, points, category, activated, address);
        
    }

    

}