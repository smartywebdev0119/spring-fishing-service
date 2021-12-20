package isa.FishingAdventure.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import isa.FishingAdventure.dto.ChangePasswordDto;
import isa.FishingAdventure.dto.UserDto;
import isa.FishingAdventure.dto.UserInfoDto;
import isa.FishingAdventure.model.User;
import isa.FishingAdventure.service.EmailService;
import isa.FishingAdventure.service.UserService;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
public class UserController {

	private Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private EmailService emailService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(value="get", method = RequestMethod.GET)
	public @ResponseBody UserInfoDto getItem(@RequestParam("email") String email){

	    User user = userService.findByEmail(email);
	    UserInfoDto userInfoDto = new UserInfoDto(user);
	    
	    return userInfoDto;
	}
	
	
	@RequestMapping(value="update", method = RequestMethod.PUT)
	public @ResponseBody UserInfoDto update(@RequestBody UserInfoDto dto) {
		
		System.out.println(dto.getEmail());
		
		
		User user = userService.findByEmail(dto.getEmail());

		user.setName(dto.getName()); 
		user.setSurname(dto.getSurname());
		user.setBiography(dto.getBiography());
		user.setPhoneNumber(dto.getPhoneNumber());
		user.getAddress().setStreet(dto.getStreet());
		user.getAddress().setCity(dto.getCity());
		user.getAddress().setCountry(dto.getCountry());
		
		userService.save(user);

		return dto;
	}
	
	@RequestMapping(value="changePassword", method = RequestMethod.PUT)
	public @ResponseBody ChangePasswordDto changePassword(@RequestBody ChangePasswordDto dto) {		
		User user = userService.findByEmail(dto.getEmail());

		if(dto.getNewPassword().equals(dto.getPasswordAgain())) {
			user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
		}
		
		userService.save(user);
		return dto;
	}

}
