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
                .requestMatchers("/api/authentication/register", "/api/user/reset-password","/api/user/all-users","/api/user/update/{id}","/api/authentication/authenticate","/api/user/forgot-password","/activites/getActivities","/api/analyseCausale/byChecklist/{checklistId}","/api/planAction/analyseCausale/{idAN}/planAction").permitAll()
                .requestMatchers("/api/authentication/change-account-status","/api/authentication/pending-users","/activites/modifier/{id}","/activites/supprimer/{id}","/activites/ajouter").hasRole("ADMIN")
                .requestMatchers("/api/projet/**").hasAnyRole("CHEFDEPROJET", "DIRECTEUR","RQUALITE")
                .requestMatchers("/api/phases/ajouterPhases","/api/phases/updatePhase/{id}","/projet/{projetId}/tauxNCInterne").hasAnyRole("CHEFDEPROJET")
                .requestMatchers("/api/checklists/initialize","/api/phases/projet/{projetId}","/api/checklists/byPhase/{phaseId}","/api/projet/**").hasAnyRole("CHEFDEPROJET","RQUALITE")
                .requestMatchers("/api/checklists/**","/api/analyseCausale/add","/api/analyseCausale/{id}/addPourquoi", "/api/analyseCausale/{id}/addCauseIshikawa","/api/planAction/**").hasRole("RQUALITE")

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