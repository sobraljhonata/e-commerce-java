package com.platform.iam.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração explícita na borda: login público; APIs administrativas com Bearer JWT emitido pelo BC
 * IAM (HS256, mesma chave que {@link com.platform.iam.adapters.out.security.JwtAccessTokenIssuer}).
 */
@Configuration
@EnableWebSecurity
public class IamSecurityConfiguration {

    @Bean
    SecurityFilterChain iamSecurityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers("/actuator/**")
                                        .permitAll()
                                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.POST, "/api/auth/login")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/auth/me")
                                        .authenticated()
                                        .requestMatchers("/api/admin/**")
                                        .hasRole("PLATFORM_ADMIN")
                                        .anyRequest()
                                        .permitAll())
                .oauth2ResourceServer(
                        oauth2 ->
                                oauth2.jwt(
                                        jwt ->
                                                jwt.decoder(jwtDecoder)
                                                        .jwtAuthenticationConverter(jwtAuthenticationConverter())));
        http.httpBasic(basic -> basic.disable());
        return http.build();
    }

    /**
     * Decodifica e valida assinatura HS256 com o mesmo segredo do emissor; conversão de claim {@code
     * roles} para {@code ROLE_*}.
     */
    @Bean
    JwtDecoder jwtDecoder(JwtSecurityProperties props) {
        SecretKey secretKey =
                new SecretKeySpec(props.getSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        var authorities = new JwtGrantedAuthoritiesConverter();
        authorities.setAuthorityPrefix("ROLE_");
        authorities.setAuthoritiesClaimName("roles");
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorities);
        return converter;
    }
}
