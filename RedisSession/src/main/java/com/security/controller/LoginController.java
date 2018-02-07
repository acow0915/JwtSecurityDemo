package com.security.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.security.TokenHandler;

@RestController
@RequestMapping("/auth")
public class LoginController {
	
	private Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenHandler tokenHandler;

    @Autowired
    private UserDetailsService customUserDetailsService;
    
    @RequestMapping("/login")
    public ResponseEntity<?> login(@RequestParam("username") String username){
    	
    	logger.info("username : {}", username);
    	
    	// Perform the security
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                		username, username
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Reload password post-security so we can generate token
        final UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        final String token = tokenHandler.build(userDetails.getUsername());

        // Return the token
        return ResponseEntity.ok(token);
    }
    
    @RequestMapping("/check")
    public String doCheck(String token){
		return "OK";
    }
    
    @RequestMapping("/hi")
    public String sayHi(){
    	return "HI";
    }
}
