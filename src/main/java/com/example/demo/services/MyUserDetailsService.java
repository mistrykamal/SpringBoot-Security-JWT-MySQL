package com.example.demo.services;

import java.util.ArrayList;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * This class is only to load/get UserDetails from the database(if any) and 
 * provide the UserDetails to AuthenticationManager
 *
 */
@Service
public class MyUserDetailsService implements UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		if("kamaluser".equals(username)) {
			return new User("kamaluser","userpass", new ArrayList<>());
		}
		else {
			throw new UsernameNotFoundException(username+ " does not match with the system");
		}	
	}
}
