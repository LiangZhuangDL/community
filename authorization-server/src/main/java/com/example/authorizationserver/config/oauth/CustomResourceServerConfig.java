package com.example.authorizationserver.config.oauth;

import com.example.authorizationserver.config.handler.ClientLoginInSuccessHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

/**
 * @author admin
 */
@Configuration
@EnableResourceServer
public class CustomResourceServerConfig extends ResourceServerConfigurerAdapter {

	@Autowired
	private ClientLoginInSuccessHandler clientLoginInSuccessHandler;

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.formLogin()
				.successHandler(clientLoginInSuccessHandler)
				.and()
				.authorizeRequests().anyRequest().authenticated().and()
				.csrf().disable();
	}

}
