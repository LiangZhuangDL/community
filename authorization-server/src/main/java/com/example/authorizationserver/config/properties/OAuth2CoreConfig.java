package com.example.authorizationserver.config.properties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author admin
 */
@Configuration
@EnableConfigurationProperties(OAuth2Properties.class)
public class OAuth2CoreConfig {
}
