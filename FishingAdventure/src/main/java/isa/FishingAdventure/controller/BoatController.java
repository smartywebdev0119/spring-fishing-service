package isa.FishingAdventure.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import isa.FishingAdventure.service.BoatService;

@RestController
@RequestMapping(value = "boat")
public class BoatController {
	
	@Autowired
	private BoatService boatService;
}
