package com.example.demo.utilities;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.cglib.core.internal.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtUtil {

	private String kkey = "secret";
	private SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

	// retrieve username from jwt token
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}
	
	// retrieve expiration date from jwt token
	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	//for retrieving any information from token we will need the secret key
	private Claims extractAllClaims(String token) {
		Claims jws = Jwts.claims();
		try {
			jws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
		} catch (JwtException ex) {
			ex.printStackTrace();
		}
		return jws;
	}

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims, userDetails.getUsername());
	}

	/** while creating the token -
     *		1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
     *		2. Sign the JWT using the HS512 algorithm and secret key.
     *		3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
     *  	compaction of the JWT to a URL-safe string
     */
	private String createToken(Map<String, Object> claims, String username) {
		return Jwts.builder().setClaims(claims).setSubject(username).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() * 1000 * 60 * 60 * 10)).signWith(key).compact();
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.contentEquals(userDetails.getUsername()) && !isTokenExpired(token));
	}
}
