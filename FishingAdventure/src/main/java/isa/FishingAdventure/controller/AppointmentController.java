package isa.FishingAdventure.controller;

import isa.FishingAdventure.dto.AppointmentDto;
import isa.FishingAdventure.model.AdditionalService;
import isa.FishingAdventure.model.Appointment;
import isa.FishingAdventure.model.ServiceProfile;
import isa.FishingAdventure.service.AppointmentService;
import isa.FishingAdventure.service.ServiceProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.time.Duration;
import java.util.*;

@RestController
@RequestMapping(value = "appointment")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private ServiceProfileService serviceProfileService;

    @GetMapping(value = "/getOffersByAdvertiser")
    @PreAuthorize("hasRole('ROLE_VACATION_HOME_OWNER') || hasRole('ROLE_BOAT_OWNER') || hasRole('ROLE_FISHING_INSTRUCTOR')")
    @Transactional
    public ResponseEntity<List<AppointmentDto>> getOffersByAdvertiser(@RequestHeader("Authorization") String token) {
        return new ResponseEntity<>(createAppointmentDtos(appointmentService.getOffersByAdvertiser(token)),
                HttpStatus.OK);
    }

    @GetMapping(value = "/getOffersByServiceId/{id}")
    @Transactional
    public ResponseEntity<List<AppointmentDto>> getOffersByServiceId(@PathVariable String id) {
        return new ResponseEntity<>(
                createAppointmentDtos(appointmentService.getOffersByServiceId(Integer.parseInt(id))), HttpStatus.OK);
    }

    private List<AppointmentDto> createAppointmentDtos(List<Appointment> appointments) {
        List<AppointmentDto> appointmentDtos = new ArrayList<>();
        if (appointments != null) {
            for (Appointment appointment : appointments) {
                ServiceProfile profile = serviceProfileService.getByAppointmentsIsContaining(appointment);
                appointmentDtos.add(new AppointmentDto(appointment, profile));
            }
        }
        return appointmentDtos;
    }

    @PostMapping(value = "/create")
    @PreAuthorize("hasRole('ROLE_VACATION_HOME_OWNER') || hasRole('ROLE_BOAT_OWNER') || hasRole('ROLE_FISHING_INSTRUCTOR')")
    @Transactional
    public ResponseEntity<AppointmentDto> create(@RequestBody AppointmentDto dto)
            throws InterruptedException, MessagingException {
        ResponseEntity<AppointmentDto> retVal = new ResponseEntity<AppointmentDto>(dto, HttpStatus.BAD_REQUEST);
        Appointment savedAppointment = new Appointment(dto);
        Integer appointmentId = appointmentService.createAppointment(savedAppointment, dto.getDuration(),
                dto.getServiceProfileId());
        if (appointmentId != null) {
            dto.setOfferId(appointmentId);
            retVal = new ResponseEntity<>(dto, HttpStatus.OK);
        }
        return retVal;
    }

}
