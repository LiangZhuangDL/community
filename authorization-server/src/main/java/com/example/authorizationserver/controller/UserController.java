package com.example.authorizationserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * @author admin
 */
@RestController
public class UserController {

	@GetMapping(name = "/user")
	public Principal user(Principal principal){
		return principal;
	}
}
