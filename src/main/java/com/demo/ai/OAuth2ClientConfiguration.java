package com.demo.ai;

import io.modelcontextprotocol.client.transport.customizer.McpSyncHttpClientRequestCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.reactive.function.client.*;

import java.util.concurrent.atomic.AtomicBoolean;

@Configuration
public class OAuth2ClientConfiguration {

    private static final String CLIENT_CREDENTIALS_CLIENT_REGISTRATION_ID = "authserver-client-credentials";

    private static final Authentication ANONYMOUS_AUTHENTICATION = new AnonymousAuthenticationToken("anonymous",
            "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

    @Bean
    AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager(ClientRegistrationRepository clientRegistrationRepository,
                                                                                 OAuth2AuthorizedClientService authorizedClientService) {
        return new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository,
                authorizedClientService);
    }

    @Bean
    McpSyncHttpClientRequestCustomizer mcpSyncHttpClientRequestCustomizer(OAuth2AuthorizedClientManager authorizedClientManager) {
        return (builder, method, endpoint, body, context) -> {
            String accessToken;
            if (contextInitialized.get()) {
                accessToken = obtainAccessTokenFromContext();
            } else {
                OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId(CLIENT_CREDENTIALS_CLIENT_REGISTRATION_ID)
                        .principal(ANONYMOUS_AUTHENTICATION)
                        .build();
                accessToken = authorizedClientManager.authorize(authorizeRequest).getAccessToken().getTokenValue();
            }
            builder.header("Authorization", "Bearer " + accessToken);
        };
    }

    @EventListener
    void listen(ContextRefreshedEvent contextRefreshedEvent) {
        contextInitialized.compareAndSet(false, true);
    }

    private String obtainAccessTokenFromContext() {
        return "ak-34d4e32c85234139992fe713e8645b77"; //TODO get session id from HttpServletRequest
    }

    private static final AtomicBoolean contextInitialized = new AtomicBoolean();
}
