package com.example.demo.utilities;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.services.MyUserDetailsService;

import io.jsonwebtoken.ExpiredJwtException;

/**
 * Intercept every incoming request and validate JWT to authorize the request access
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private MyUserDetailsService myUserDetailsService;

	private UserDetails userDetails;

	@Autowired
	private JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		/**
		 * JWT token aka (Bearer token and Authorization token)
		 * 
		 * JWT token consists of three fields 1. Header 2. Payload 3. Signature
		 */
		// get the Bearer token (jwt) from the request header
		final String authorizationHeader = request.getHeader("Authorization");

		String username = null;
		String jwtAccessToken = null;

		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			
			jwtAccessToken = authorizationHeader.substring(7);
			
			// get the username from the Bearer token (jwt)
			try {
				username = jwtUtil.extractUsername(jwtAccessToken);
			} catch (IllegalArgumentException e) {
				System.out.println("Unable to get JWT Token");
			} catch (ExpiredJwtException e) {
				System.out.println("JWT Token has expired");
			}
		} 
		else {
			logger.warn("JWT Token does not begin with Bearer String");
		}

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			
			userDetails = myUserDetailsService.loadUserByUsername(username);

			// If userDetails.username is same as jwtAccessToken.username
			if (jwtUtil.validateToken(jwtAccessToken, userDetails)) {

				// adding the authentication (user details) to the SecurityContextHolder
				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			}
		}
		filterChain.doFilter(request, response);
	}

}
