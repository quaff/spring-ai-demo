package com.demo.ai;

import io.modelcontextprotocol.client.transport.customizer.McpSyncHttpClientRequestCustomizer;
import io.modelcontextprotocol.common.McpTransportContext;
import org.springframework.ai.mcp.customizer.McpSyncClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.reactive.function.client.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@Configuration
public class OAuth2ClientConfiguration {

    private static final String CLIENT_CREDENTIALS_CLIENT_REGISTRATION_ID = "authserver-client-credentials";

    private static final Authentication ANONYMOUS_AUTHENTICATION = new AnonymousAuthenticationToken("anonymous",
            "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

    @Bean
    McpSyncClientCustomizer syncClientCustomizer() {
        return (name, syncSpec) -> syncSpec.transportContextProvider(new AuthenticationMcpTransportContextProvider());
    }

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
                RequestAttributes requestAttributes = (RequestAttributes) context.get(AuthenticationMcpTransportContextProvider.REQUEST_ATTRIBUTES_KEY);
                if (requestAttributes != null) {
                    accessToken = requestAttributes.getSessionId();
                }
                accessToken = "ak-34d4e32c85234139992fe713e8645b77"; // for test
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

    private static final AtomicBoolean contextInitialized = new AtomicBoolean();

    static class AuthenticationMcpTransportContextProvider implements Supplier<McpTransportContext> {

        public static final String AUTHENTICATION_KEY = Authentication.class.getName();

        public static final String REQUEST_ATTRIBUTES_KEY = RequestAttributes.class.getName();

        @Override
        public McpTransportContext get() {
            Map<String, Object> data = new HashMap<>();
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (securityContext != null && securityContext.getAuthentication() != null) {
                data.put(AUTHENTICATION_KEY, securityContext.getAuthentication());
            }
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                data.put(REQUEST_ATTRIBUTES_KEY, requestAttributes);
            }
            return McpTransportContext.create(data);
        }

    }
}
