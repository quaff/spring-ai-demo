package com.demo.ai;

import io.modelcontextprotocol.client.transport.customizer.McpSyncHttpClientRequestCustomizer;
import io.modelcontextprotocol.common.McpTransportContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mcp.customizer.McpSyncClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.reactive.function.client.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Configuration
public class OAuth2ClientConfiguration {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Bean
    AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager(ClientRegistrationRepository clientRegistrationRepository,
                                                                                 OAuth2AuthorizedClientService authorizedClientService) {
        return new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository,
                authorizedClientService);
    }

    @Bean
    McpSyncClientCustomizer mcpSyncClientCustomizer() {
        return (name, syncSpec) -> syncSpec.transportContextProvider(new AuthenticationMcpTransportContextProvider());
    }

    @Bean
    McpSyncHttpClientRequestCustomizer mcpSyncHttpClientRequestCustomizer(OAuth2AuthorizedClientManager authorizedClientManager,
                                                                          ClientRegistrationRepository clientRegistrationRepository) {
        return (builder, method, endpoint, body, context) -> {
            String accessToken;
            if (contextInitialized) {
                RequestAttributes requestAttributes = (RequestAttributes) context.get(AuthenticationMcpTransportContextProvider.REQUEST_ATTRIBUTES_KEY);
                if (requestAttributes != null) {
                    accessToken = requestAttributes.getSessionId();
                }
                accessToken = "ak-34d4e32c85234139992fe713e8645b77"; // for test
            } else {
                OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId(findUniqueClientRegistration(clientRegistrationRepository))
                        .principal("mcp-client")
                        .build();
                accessToken = authorizedClientManager.authorize(authorizeRequest).getAccessToken().getTokenValue();
            }
            logger.info("Using access token {} for {} to {} with\n{}", accessToken, method, endpoint, body);
            builder.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        };
    }

    private static String findUniqueClientRegistration(ClientRegistrationRepository clientRegistrationRepository) {
        String registrationId;
        if (!(clientRegistrationRepository instanceof InMemoryClientRegistrationRepository repo)) {
            throw new IllegalStateException("Expected an InMemoryClientRegistrationRepository");
        }
        var iterator = repo.iterator();
        var firstRegistration = iterator.next();
        if (iterator.hasNext()) {
            throw new IllegalStateException("Expected a single Client Registration");
        }
        registrationId = firstRegistration.getRegistrationId();
        return registrationId;
    }

    @EventListener
    void listen(ContextRefreshedEvent contextRefreshedEvent) {
        contextInitialized = true;
    }

    private boolean contextInitialized;

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
