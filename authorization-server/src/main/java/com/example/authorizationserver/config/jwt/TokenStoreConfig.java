package com.example.authorizationserver.config.jwt;

import com.example.authorizationserver.config.properties.OAuth2Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * @author admin
 */
@Configuration
public class TokenStoreConfig {

	@Autowired
	private RedisConnectionFactory redisConnectionFactory;

	/**
	 * 用于存放token
	 * @return
	 */
	@Bean
	@ConditionalOnProperty(prefix = "unicorn.security.oauth2", name = "storeType", havingValue = "redis")
	public TokenStore redisTokenStore(){
		return new RedisTokenStore(redisConnectionFactory);
	}

	/**
	 * Java Web Token(JWT)配置信息
	 */
	@Configuration
	@ConditionalOnProperty(prefix = "unicorn.security.oauth2", name = "storeType", havingValue = "jwt", matchIfMissing = true)
	public static class JwtTokenConfig{

		@Autowired
		private OAuth2Properties oAuth2Properties;

		/**
		 * 用于生成JWT
		 * @return
		 */
		@Bean
		public JwtAccessTokenConverter jwtAccessTokenConverter(){
			JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
			accessTokenConverter.setSigningKey(oAuth2Properties.getJwtSigningKey());
			return accessTokenConverter;
		}

		/**
		 * 使用jwtTokenStore存储token
		 * @return
		 */
		@Bean
		public TokenStore jwtTokenStore(){
			return new JwtTokenStore(jwtAccessTokenConverter());
		}

		/**
		 * 用于拓展JWT
		 * @return
		 */
		@Bean
		@ConditionalOnMissingBean(name = "jwtTokenEnhancer")
		public TokenEnhancer jwtTokenEnhancer(){
			return new CustomJwtTokenEnhancerImpl();
		}
	}
}
