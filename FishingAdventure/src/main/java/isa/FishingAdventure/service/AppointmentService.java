package isa.FishingAdventure.service;

import isa.FishingAdventure.repository.AppointmentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppointmentService{
	
	@Autowired
	private AppointmentRepository appointmentRepository;
}
