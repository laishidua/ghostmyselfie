package com.laishidua.mobilecloud.ghostmyselfie.controller;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.laishidua.mobilecloud.ghostmyselfie.client.UserSvcApi;
import com.laishidua.mobilecloud.ghostmyselfie.model.RoleRepository;
import com.laishidua.mobilecloud.ghostmyselfie.model.User;
import com.laishidua.mobilecloud.ghostmyselfie.model.UserCredentialsStatus;
import com.laishidua.mobilecloud.ghostmyselfie.model.UserRepository;

@Controller
public class UserController {
	
	@Autowired
	UserRepository userRepo;
	@Autowired
	RoleRepository roleRepo;
	
	@RequestMapping(value=UserSvcApi.USER_ADD_NEW_PATH, method=RequestMethod.POST)
	public @ResponseBody User addUser(@RequestBody User u){
		boolean add = true;
		User check = null;
		check = userRepo.findByUsernameIgnoreCase(u.getUsername().toLowerCase());
		if (check != null) {
			add = false;
		}
		check = userRepo.findByEmailIgnoreCase(u.getEmail().toLowerCase());
		if (check != null) {
			add = false;
		}
		check = userRepo.findByUsernameAndPassword(u.getUsername().toLowerCase(), u.getPassword());
		if (check != null) {
			add = false;
		}	
		if (add) {
		u.setUsername(u.getUsername().toLowerCase());
		u.setEmail(u.getEmail().toLowerCase());
		u.setRoles(roleRepo.findAll());
		Date now = Calendar.getInstance().getTime();
		u.setCreatedDate(now);
		u.setLastModifiedDate(now);
		u = userRepo.save(u);	
		return u;
		} else {
			return null;
		}
	}
	
	@RequestMapping(value=UserSvcApi.USER_CHECK_CREDENTIALS_PATH, method=RequestMethod.POST)
	public @ResponseBody UserCredentialsStatus checkCredentialsState(@RequestBody User user){
		String username = user.getUsername();
		String email = user.getEmail();
		System.out.println("\n\nUSER:"+ username);
		System.out.println("\n\nEMAIl:"+ email);	
		User u = null;
		UserCredentialsStatus.UserCredentialsState state = UserCredentialsStatus.UserCredentialsState.NONE_AVAILABLE;
		if(username!=null){
			u = userRepo.findByUsernameIgnoreCase(username);
			if(u==null){
				state = UserCredentialsStatus.UserCredentialsState.USERNAME_AVAILABLE;
			}
		}
		u = null;
		if(email!=null){
			u = userRepo.findByEmailIgnoreCase(email);
			if(u==null){
				if(state==UserCredentialsStatus.UserCredentialsState.USERNAME_AVAILABLE)
					state = UserCredentialsStatus.UserCredentialsState.BOTH_AVAILABLE;
				else
					state = UserCredentialsStatus.UserCredentialsState.EMAIL_AVAILABLE;
				u = null;
			}
		}
		return new UserCredentialsStatus(state);
	}
	
	@RequestMapping(value=UserSvcApi.USER_USERNAME_AND_PASSWORD_SEARCH_PATH, method=RequestMethod.POST)
	public @ResponseBody User login(@RequestParam(UserSvcApi.USERNAME_PARAMETER) String username, @RequestParam(UserSvcApi.PASSWORD_PARAMETER) String pass){
		System.out.println("username: "+username+" pass: "+pass);
		
		return userRepo.findByUsernameAndPassword(username, pass);
	}

	@RequestMapping(value=UserSvcApi.USER_COUNTALL_PATH, method=RequestMethod.GET)
	public @ResponseBody Long countAll() {
		return userRepo.count();
	}
}
