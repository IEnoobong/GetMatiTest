package com.example.getmatitest;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import lombok.RequiredArgsConstructor;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

@Configuration
@SuppressWarnings("deprecation")
@RequiredArgsConstructor
public class GetMatiOAuth2Configuration {

    private ClientHttpRequestFactory clientHttpRequestFactory;

    @Bean
    @Qualifier("getMati")
    public OAuth2RestTemplate getMatiRestTemplate(
            @Qualifier("getMati") OAuth2ProtectedResourceDetails oAuth2ProtectedResourceDetails,
            AccessTokenProvider accessTokenProvider, RestTemplateBuilder builder) {

        OAuth2ClientContext defaultOAuth2ClientContext = new DefaultOAuth2ClientContext(
                new DefaultAccessTokenRequest());
        OAuth2RestTemplate template = new OAuth2RestTemplate(oAuth2ProtectedResourceDetails,
                defaultOAuth2ClientContext);
        template.setAccessTokenProvider(accessTokenProvider);

        template.setRequestFactory(getClientHttpRequestFactory());
        template.getInterceptors().add(new HttpHeaderInterceptor(HttpHeaders.CONTENT_TYPE,
                MediaType.APPLICATION_JSON_VALUE));
        template.getInterceptors().add(new HttpHeaderInterceptor(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE));
        template.getInterceptors().add(new HttpHeaderInterceptor(HttpHeaders.USER_AGENT, "Bundle-Identity-Verifier/Java V1.0"));

        return builder.configure(template);
    }

    @Bean
    @Qualifier("getMati")
    public OAuth2ProtectedResourceDetails getMatiOauth2ProtectedResourceDetails() {
        ClientCredentialsResourceDetails resource = new ClientCredentialsResourceDetails();
        resource.setAccessTokenUri("https://api.getmati.com/oauth");
        resource.setClientId("REPLACE_WITH_YOUR_CLIENT_DETAIL");
        resource.setClientSecret("REPLACE_WITH_YOUR_CLIENT_DETAIL");
        return resource;
    }

    @Bean
    public AccessTokenProvider clientAccessTokenProvider() {
        ClientCredentialsAccessTokenProvider accessTokenProvider = new ClientCredentialsAccessTokenProvider();
        accessTokenProvider.setRequestFactory(getClientHttpRequestFactory());
        return accessTokenProvider;
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        if (clientHttpRequestFactory == null) {
            try {
                SSLContext sslContext = SSLContexts.custom().build();

                SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext,
                        new String[]{"TLSv1.3", "TLSv1.2"}, null, SSLConnectionSocketFactory.getDefaultHostnameVerifier());

                CloseableHttpClient httpClient = HttpClients.custom()
                        .setSSLSocketFactory(sslConnectionSocketFactory)
                        .build();

                clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
            } catch (NoSuchAlgorithmException | KeyManagementException ex) {
                throw new SecurityException(ex);
            }
        }
        return clientHttpRequestFactory;
    }

}
