package com.example.authorizationserver.config.handler;

import com.google.common.collect.Maps;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author admin
 */
@Component("clientLoginInSuccessHandler")
public class ClientLoginInSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ClientDetailsService clientDetailsService;

	@Autowired
	private AuthorizationServerTokenServices defaultAuthorizationServerTokenServices;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
		String header = request.getHeader("Authorization");
		if (header == null || !header.startsWith("Basic ")) {
			throw new UnapprovedClientAuthenticationException("请求头中无客户端信息");
		}
		String[] tokens = this.extractAndDecodeHeader(header, request);
		assert tokens.length == 2;
		String clientId = tokens[0];
		String secret = tokens[1];

		ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
		if (clientDetails == null) {
			throw new UnapprovedClientAuthenticationException("客户端ID对应的配置信息不存在：" + clientId);
		} else if (!passwordEncoder.matches(secret, clientDetails.getClientSecret())) {
			throw new UnapprovedClientAuthenticationException("客户端密码不匹配：" + clientId);
		}

		TokenRequest tokenRequest = new TokenRequest(Maps.newHashMap(), clientId, clientDetails.getScope(), "custom");
		OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);
		OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);
		OAuth2AccessToken token = defaultAuthorizationServerTokenServices.createAccessToken(oAuth2Authentication);

		response.setContentType("application/json;charset=UTF-8");
		ObjectMapper objectMapper = new ObjectMapper();
		response.getWriter().write(objectMapper.writeValueAsString(token));
	}

	private String[] extractAndDecodeHeader(String header, HttpServletRequest request) throws IOException {
		byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);

		byte[] decoded;
		try {
			decoded = Base64.decode(base64Token);
		} catch (IllegalArgumentException var7) {
			throw new BadCredentialsException("Failed to decode basic authentication token");
		}

		String token = new String(decoded, "UTF-8");
		int tag = token.indexOf(":");
		if (tag == -1) {
			throw new BadCredentialsException("Invalid basic authentication token");
		} else {
			return new String[]{token.substring(0, tag), token.substring(tag + 1)};
		}
	}
}
