package br.com.vidaplus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.vidaplus.repository.UserRepository;
import br.com.vidaplus.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor

public class SecurityConfig {

    
    private final UserRepository userRepository;
    

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(userRepository);
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Define que a API é stateless (sem sessões)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.GET, "/api/users/current").hasAnyAuthority("ADMIN", "ATTENDANT", "HEALTH_PROFESSIONAL", "PATIENT") // Dados do usuário autenticado
                .requestMatchers(HttpMethod.GET, "/api/appointments/current").hasAnyAuthority("ADMIN", "ATTENDANT", "HEALTH_PROFESSIONAL", "PATIENT")
                .requestMatchers(HttpMethod.GET, "/api/medical-records/current").hasAnyAuthority("ADMIN", "ATTENDANT", "HEALTH_PROFESSIONAL", "PATIENT")
                .requestMatchers("/auth/**").permitAll()  // Permite acesso público - pagina login (sem autenticação)
                .requestMatchers("/auth/logout").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/users").hasAnyAuthority("ADMIN", "ATTENDANT")
                .requestMatchers(HttpMethod.GET, "/api/users/{id}").hasAnyAuthority("ADMIN", "ATTENDANT")
                .requestMatchers(HttpMethod.GET, "/api/appointments/{id}").hasAnyAuthority("ADMIN", "ATTENDANT", "HEALTH_PROFESSIONAL")
                .requestMatchers(HttpMethod.GET, "/api/appointments").hasAnyAuthority("ADMIN", "ATTENDANT")
                .requestMatchers(HttpMethod.GET, "/api/appointments/patient/{patientId}").hasAnyAuthority("ADMIN", "ATTENDANT", "HEALTH_PROFESSIONAL")
                .requestMatchers(HttpMethod.GET, "/api/appointments/healthProfessional/{healthProfessionalId}").hasAnyAuthority("ADMIN", "ATTENDANT", "HEALTH_PROFESSIONAL")
                .requestMatchers(HttpMethod.GET, "/api/medical-records/patient/{patientId}").hasAnyAuthority("ADMIN", "ATTENDANT", "HEALTH_PROFESSIONAL")
                .requestMatchers(HttpMethod.POST, "/api/users").hasAnyAuthority("ADMIN", "ATTENDANT")
                .requestMatchers(HttpMethod.POST, "/api/appointments").hasAnyAuthority("ADMIN", "ATTENDANT")
                .requestMatchers(HttpMethod.POST, "/api/medical-records/{patientId}/add-observations").hasAnyAuthority("ADMIN", "ATTENDANT", "HEALTH_PROFESSIONAL")
                .requestMatchers(HttpMethod.PUT, "/api/users/{id}").hasAnyAuthority("ADMIN", "ATTENDANT")
                .requestMatchers(HttpMethod.PUT, "/api/appointments/{id}").hasAnyAuthority("ADMIN", "ATTENDANT")
                .requestMatchers(HttpMethod.PUT, "/api/appointments/{id}/status").hasAnyAuthority("ADMIN", "ATTENDANT")
                .requestMatchers(HttpMethod.PUT, "/api/medical-records/{patientId}/update-observations").hasAnyAuthority("ADMIN", "ATTENDANT", "HEALTH_PROFESSIONAL")
                .requestMatchers(HttpMethod.DELETE, "/api/users/{id}").hasAnyAuthority("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/appointments/{id}").hasAnyAuthority("ADMIN", "ATTENDANT")
                .requestMatchers(HttpMethod.DELETE, "/api/medical-records/{patientId}/remove-observations").hasAnyAuthority("ADMIN", "ATTENDANT", "HEALTH_PROFESSIONAL")
                .requestMatchers(HttpMethod.DELETE, "/api/medical-records/{patientId}").hasAnyAuthority("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); 
            // Garante que o token JWT será validado antes de qualquer outra verificação de autenticação

        return http.build();
    }

    // Define o codificador de senhas que será usado para criptografar as senhas dos usuários - hash
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    
}