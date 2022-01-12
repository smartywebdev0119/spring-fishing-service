package isa.FishingAdventure.service;

import isa.FishingAdventure.model.*;
import isa.FishingAdventure.repository.ServiceProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ServiceProfileService {

    @Autowired
    private ServiceProfileRepository profileRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private FishingAdventureService adventureService;

    @Autowired
    private VacationHomeService homeService;

    @Autowired
    private BoatService boatService;

    public void delete(int id) {
        Optional<ServiceProfile> profile = profileRepository.findById(id);
        profile.get().setDeleted(true);
        profileRepository.save(profile.get());
    }

    public ServiceProfile getById(int id) {
        return profileRepository.getById(id);
    }

    public ServiceProfile getByAppointmentsIsContaining(Appointment appointment) {
        return profileRepository.getByAppointmentsIsContaining(appointment);
    }

    public ServiceProfile getByNameForAdvertiser(String name, String email) {
        ServiceProfile service;
        String role = userService.findByEmail(email).getUserType().getName();
        if (role.equals("ROLE_FISHING_INSTRUCTOR"))
            service = getByNameForInstructor(name, email);
        else if (role.equals("ROLE_VACATION_HOME_OWNER"))
            service = getByNameForHomeOwner(name, email);
        else
            service = getByNameForBoatOwner(name, email);
        return service;
    }

    private ServiceProfile getByNameForInstructor(String name, String email) {
        ServiceProfile service = null;
        for (FishingAdventure adventure : adventureService.findAll()) {
            if (adventure.getName().equals(name)
                    && adventure.getFishingInstructor().getEmail().equals(email)) {
                service = adventure;
            }
        }
        return service;
    }

    private ServiceProfile getByNameForHomeOwner(String name, String email) {
        ServiceProfile service = null;
        for (VacationHome home : homeService.findAllNonDeleted()) {
            if (home.getName().equals(name)
                    && home.getVacationHomeOwner().getEmail().equals(email)) {
                service = home;
            }
        }
        return service;
    }

    private ServiceProfile getByNameForBoatOwner(String name, String email) {
        ServiceProfile service = null;
        for (Boat boat : boatService.findAllNonDeleted()) {
            if (boat.getName().equals(name)
                    && boat.getBoatOwner().getEmail().equals(email)) {
                service = boat;
            }
        }
        return service;
    }

    public void save(ServiceProfile profile) {
        profileRepository.save(profile);
    }

    public List<ServiceProfile> findAll() {
        return profileRepository.findAll();
    }

    public Boolean isServiceAvailableForDateRange(Integer id, Date start, Date end) {
        Optional<VacationHome> home = homeService.findByIdIfExists(id);
        Optional<Boat> boat = boatService.findByIdIfExists(id);
        Optional<FishingAdventure> adventure = adventureService.findByIdIfExists(id);

        Boolean isAvailable = true;
        if (home.isPresent()) {
            isAvailable = homeService.isCottageAvailableForDateRange(id, start, end);
        } else if (boat.isPresent()) {
            isAvailable = boatService.isBoatAvailableForDateRange(id, start, end);
        } else {
            isAvailable = adventureService.isInstructorAvailable(adventure.get().getFishingInstructor(), start, end);
        }
        return isAvailable;
    }

    public void deleteOffer(Integer offerId, Integer serviceProfileId) {
        ServiceProfile profile = profileRepository.getById(serviceProfileId);

        for (Appointment appointment : profile.getAppointments()) {
            if (appointment.getAppointmentId().equals(offerId)) {
                Set<Appointment> appointments = profile.getAppointments();
                appointments.remove(appointment);
                profile.setAppointments(appointments);
                break;
            }
        }
    }
}
