package com.demo.ai;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;

@Configuration
public class OAuth2ClientConfiguration {

    @Bean
    AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager(ClientRegistrationRepository clientRegistrationRepository,
                                                                                 OAuth2AuthorizedClientService authorizedClientService) {
        return new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository,
                authorizedClientService);
    }

    @Bean
    WebClient.Builder webClientBuilder(OAuth2AuthorizedClientManager authorizedClientManager) {
        return WebClient.builder().filter(new OAuth2ClientExchangeFilterFunction(authorizedClientManager));
    }

    static class OAuth2ClientExchangeFilterFunction implements ExchangeFilterFunction {

        private final OAuth2AuthorizedClientManager authorizedClientManager;

        private static final String CLIENT_CREDENTIALS_CLIENT_REGISTRATION_ID = "authserver-client-credentials";

        private static final Authentication ANONYMOUS = new AnonymousAuthenticationToken("client-credentials-client", "client-credentials-client",
                AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

        public OAuth2ClientExchangeFilterFunction(OAuth2AuthorizedClientManager authorizedClientManager) {
            this.authorizedClientManager = authorizedClientManager;
        }

        @Override
        public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
            String accessToken = getClientCredentialsAccessToken();
            ClientRequest requestWithToken = ClientRequest.from(request)
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .build();
            return next.exchange(requestWithToken);
        }

        private String getClientCredentialsAccessToken() {
            OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId(CLIENT_CREDENTIALS_CLIENT_REGISTRATION_ID)
                    .principal(ANONYMOUS)
                    .build();
            return authorizedClientManager.authorize(authorizeRequest).getAccessToken().getTokenValue();
        }

    }
}
