package com.example.authorizationserver.config.properties;

import lombok.Data;

/**
 * @author admin
 */
@Data
public class OAuth2ClientProperties {
	private String clientId;

	private String clientSecret;

	private Integer accessTokenValiditySeconds = 7200;
}
