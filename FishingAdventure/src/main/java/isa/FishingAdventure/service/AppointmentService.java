package isa.FishingAdventure.service;

import isa.FishingAdventure.model.Appointment;
import isa.FishingAdventure.model.ServiceProfile;
import isa.FishingAdventure.model.VacationHome;
import isa.FishingAdventure.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private VacationHomeService vacationHomeService;
    @Autowired
    private ServiceProfileService serviceProfileService;

    public Appointment save(Appointment newAppointment) {
        return appointmentRepository.save(newAppointment);
    }

    public void addAppointmentToServiceProfile(Integer id, Appointment newAppointment) {
        ServiceProfile profile = serviceProfileService.getById(id);
        profile.getAppointments().add(newAppointment);
        serviceProfileService.save(profile);
    }
}
