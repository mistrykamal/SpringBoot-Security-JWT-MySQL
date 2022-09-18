package com.example.demo.model;

public class AuthenticationResponse {
	
	//DONOT change serial version uid
	private static final long serialVersionUID = -8091879091924046844L;
	private final String jwt;
	
	public AuthenticationResponse(String jwt) {
		this.jwt = jwt;
	}
	
	public String getJwt() {
		return jwt;
	}

}
