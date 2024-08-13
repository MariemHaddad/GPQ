package com.example.gpq.Configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/api/authentication/register", "/api/authentication/authenticate","/activites/getActivities").permitAll()
                .requestMatchers("/api/authentication/change-account-status","/api/authentication/pending-users","/activites/modifier/{id}","/activites/supprimer/{id}","/activites/ajouter").hasRole("ADMIN")
                .requestMatchers("/api/projet/ajouter").hasAnyRole("CHEFDEPROJET", "DIRECTEUR")
                .requestMatchers("/api/phases/ajouterPhases","/api/phases/updatePhase/{id}","").hasAnyRole("CHEFDEPROJET")
                .requestMatchers("/api/checklists/initialize","/api/phases/projet/{projetId}","/api/checklists/byPhase/{phaseId}").hasAnyRole("CHEFDEPROJET","RQUALITE")
                .requestMatchers("/api/checklists/**").hasAnyRole("RQUALITE")

                .anyRequest().authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}