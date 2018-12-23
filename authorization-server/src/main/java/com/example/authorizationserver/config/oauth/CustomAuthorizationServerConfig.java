package com.example.authorizationserver.config.oauth;

import com.google.common.collect.Lists;

import com.example.authorizationserver.config.CustomUserDetailsServiceImpl;
import com.example.authorizationserver.config.jwt.CustomJwtTokenEnhancerImpl;
import com.example.authorizationserver.config.properties.OAuth2ClientProperties;
import com.example.authorizationserver.config.properties.OAuth2Properties;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.List;

/**
 * @author admin
 */
@Configuration
@EnableAuthorizationServer
public class CustomAuthorizationServerConfig  extends AuthorizationServerConfigurerAdapter {

	@Autowired
	private OAuth2Properties oAuth2Properties;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private CustomUserDetailsServiceImpl userDetailsService;

	@Autowired
	private TokenStore tokenStore;

	@Autowired(required = false)
	private JwtAccessTokenConverter jwtAccessTokenConverter;

	@Autowired(required = false)
	private CustomJwtTokenEnhancerImpl tokenEnhancer;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.tokenStore(tokenStore)
				.authenticationManager(authenticationManager)
				.userDetailsService(userDetailsService);
		if(jwtAccessTokenConverter != null && tokenEnhancer != null){
			TokenEnhancerChain chain = new TokenEnhancerChain();
			List<TokenEnhancer> tokenEnhancers = Lists.newArrayList();
			tokenEnhancers.add(jwtAccessTokenConverter);
			tokenEnhancers.add(tokenEnhancer);
			chain.setTokenEnhancers(tokenEnhancers);

			endpoints.tokenEnhancer(chain)
					.accessTokenConverter(jwtAccessTokenConverter);
		}
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		InMemoryClientDetailsServiceBuilder builder = clients.inMemory();
		if(ArrayUtils.isNotEmpty(oAuth2Properties.getClients())){
			for (OAuth2ClientProperties properties: oAuth2Properties.getClients()){
				builder.withClient(properties.getClientId())
						.secret(properties.getClientSecret())
						.accessTokenValiditySeconds(properties.getAccessTokenValiditySeconds())
						.refreshTokenValiditySeconds(60 * 60 * 24 * 15)
						.authorizedGrantTypes("refresh_token", "password", "authorization_code")
						.redirectUris("http://localhost:3601/user")
						.scopes("all");
			}
		}
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.tokenKeyAccess("permitAll()")
				.checkTokenAccess("isAuthenticated()");
	}
}
