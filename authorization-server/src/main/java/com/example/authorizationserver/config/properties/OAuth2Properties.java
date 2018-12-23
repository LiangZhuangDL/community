package com.example.authorizationserver.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author admin
 */
@Data
@ConfigurationProperties(prefix = "unicorn.security.oauth2")
public class OAuth2Properties {

	private String jwtSigningKey = "unicorn";

	private OAuth2ClientProperties[] clients = {};
}
