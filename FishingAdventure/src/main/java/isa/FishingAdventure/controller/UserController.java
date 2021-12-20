package isa.FishingAdventure.controller;

import isa.FishingAdventure.model.VacationHomeOwner;
import isa.FishingAdventure.security.util.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

	@Autowired
	private TokenUtils tokenUtils;
	
	@RequestMapping(value="get", method = RequestMethod.GET)
	public @ResponseBody UserInfoDto getItem(@RequestHeader("Authorization") String token){
		String email = tokenUtils.getEmailFromToken(token.split(" ")[1]);
	    User user = userService.findByEmail(email);

		return new UserInfoDto(user);
	}

	@RequestMapping(value="getRole", method = RequestMethod.GET)
	public @ResponseBody String getRole(@RequestHeader("Authorization") String token){
		return tokenUtils.getRoleFromToken(token.split(" ")[1]);
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
	public @ResponseBody ChangePasswordDto changePassword(@RequestHeader("Authorization") String token, @RequestBody ChangePasswordDto dto) {
		String email = tokenUtils.getEmailFromToken(token.split(" ")[1]);
		User user = userService.findByEmail(email);

		if(dto.getNewPassword().equals(dto.getPasswordAgain())) {
			user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
		}
		
		userService.save(user);
		return dto;
	}

}
