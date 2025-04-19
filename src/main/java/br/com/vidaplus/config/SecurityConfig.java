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
                .requestMatchers(HttpMethod.GET, "/api/audit-records/all").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/users/current").hasAnyAuthority("ADMIN", "ATTENDANT", "HEALTH_PROFESSIONAL", "PATIENT") // Dados do usuário autenticado
                .requestMatchers(HttpMethod.GET, "/api/appointments/current").hasAnyAuthority("ADMIN", "ATTENDANT", "HEALTH_PROFESSIONAL", "PATIENT")
                .requestMatchers(HttpMethod.GET, "/api/surgeries/current").hasAnyAuthority("ADMIN", "ATTENDANT", "HEALTH_PROFESSIONAL", "PATIENT")
                .requestMatchers(HttpMethod.GET, "/api/medical-records/current").hasAnyAuthority("ADMIN", "ATTENDANT", "HEALTH_PROFESSIONAL", "PATIENT")
                .requestMatchers(HttpMethod.GET, "/api/schedule").hasAnyAuthority("ADMIN", "ATTENDANT", "HEALTH_PROFESSIONAL")
                .requestMatchers(HttpMethod.GET, "/api/schedule/all-slots/{professionalId}/{date}").hasAnyAuthority("ADMIN", "ATTENDANT")
                .requestMatchers(HttpMethod.GET, "/api/schedule/current/{date}").hasAnyAuthority("ADMIN", "ATTENDANT", "HEALTH_PROFESSIONAL")
                .requestMatchers(HttpMethod.GET, "api/hospitalizations/active").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.GET, "api/hospitalizations/available-beds").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/users").hasAnyAuthority("ADMIN", "ATTENDANT")
                .requestMatchers(HttpMethod.GET, "/api/users/{id}").hasAnyAuthority("ADMIN", "ATTENDANT")
                .requestMatchers(HttpMethod.GET, "/api/surgeries/{id}").hasAnyAuthority("ADMIN", "ATTENDANT", "HEALTH_PROFESSIONAL")
                .requestMatchers(HttpMethod.GET, "/api/appointments/{id}").hasAnyAuthority("ADMIN", "ATTENDANT", "HEALTH_PROFESSIONAL")
                .requestMatchers(HttpMethod.GET, "/api/appointments").hasAnyAuthority("ADMIN", "ATTENDANT")
                .requestMatchers(HttpMethod.GET, "/api/surgeries").hasAnyAuthority("ADMIN", "ATTENDANT")
                .requestMatchers(HttpMethod.GET, "/api/appointments/patient/{patientId}").hasAnyAuthority("ADMIN", "ATTENDANT", "HEALTH_PROFESSIONAL")
                .requestMatchers(HttpMethod.GET, "/api/appointments/healthProfessional/{healthProfessionalId}").hasAnyAuthority("ADMIN", "ATTENDANT", "HEALTH_PROFESSIONAL")
                .requestMatchers(HttpMethod.GET, "/api/surgeries/patient/{patientId}").hasAnyAuthority("ADMIN", "ATTENDANT", "HEALTH_PROFESSIONAL")
                .requestMatchers(HttpMethod.GET, "/api/surgeries/healthProfessional/{healthProfessionalId}").hasAnyAuthority("ADMIN", "ATTENDANT", "HEALTH_PROFESSIONAL")
                .requestMatchers(HttpMethod.GET, "/api/medical-records/patient/{patientId}").hasAnyAuthority("ADMIN", "ATTENDANT", "HEALTH_PROFESSIONAL")
                .requestMatchers(HttpMethod.POST, "/api/audit-records/filter").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/schedule/available-slots").hasAnyAuthority("ADMIN", "ATTENDANT", "HEALTH_PROFESSIONAL")
                .requestMatchers(HttpMethod.POST, "/api/users").hasAnyAuthority("ADMIN", "ATTENDANT")
                .requestMatchers(HttpMethod.POST, "/api/appointments").hasAnyAuthority("ADMIN", "ATTENDANT")
                .requestMatchers(HttpMethod.POST, "/api/surgeries").hasAnyAuthority("ADMIN", "ATTENDANT")
                .requestMatchers(HttpMethod.POST, "/api/medical-records/{patientId}/add-observations").hasAnyAuthority("ADMIN", "ATTENDANT", "HEALTH_PROFESSIONAL")
                .requestMatchers(HttpMethod.POST, "/api/medical-records/{patientId}/add-surgery-observations").hasAnyAuthority("ADMIN", "ATTENDANT", "HEALTH_PROFESSIONAL")
                .requestMatchers(HttpMethod.PUT, "/api/users/{id}").hasAnyAuthority("ADMIN", "ATTENDANT")
                .requestMatchers(HttpMethod.PUT, "/api/appointments/{id}").hasAnyAuthority("ADMIN", "ATTENDANT")
                .requestMatchers(HttpMethod.PUT, "/api/appointments/{id}/status").hasAnyAuthority("ADMIN", "ATTENDANT")
                .requestMatchers(HttpMethod.PUT, "/api/surgeries/{id}").hasAnyAuthority("ADMIN", "ATTENDANT")
                .requestMatchers(HttpMethod.PUT, "/api/surgeries/{id}/status").hasAnyAuthority("ADMIN", "ATTENDANT")
                .requestMatchers(HttpMethod.PUT, "/api/medical-records/{patientId}/update-observations").hasAnyAuthority("ADMIN", "ATTENDANT", "HEALTH_PROFESSIONAL")
                .requestMatchers(HttpMethod.PUT, "/api/medical-records/{patientId}/update-surgery-observations").hasAnyAuthority("ADMIN", "ATTENDANT", "HEALTH_PROFESSIONAL")
                .requestMatchers(HttpMethod.DELETE, "/api/users/{id}").hasAnyAuthority("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/appointments/{id}").hasAnyAuthority("ADMIN", "ATTENDANT")
                .requestMatchers(HttpMethod.DELETE, "/api/surgeries/{id}").hasAnyAuthority("ADMIN", "ATTENDANT")
                .requestMatchers(HttpMethod.DELETE, "/api/medical-records/{patientId}/remove-observations").hasAnyAuthority("ADMIN", "ATTENDANT", "HEALTH_PROFESSIONAL")
                .requestMatchers(HttpMethod.DELETE, "/api/medical-records/{patientId}/remove-surgery-observations").hasAnyAuthority("ADMIN", "ATTENDANT", "HEALTH_PROFESSIONAL")
                .requestMatchers(HttpMethod.DELETE, "/api/medical-records/{patientId}").hasAnyAuthority("ADMIN")
                .requestMatchers("/css/**", "/js/**").permitAll()
                .requestMatchers("/auth/login.html", "/auth/login").permitAll()
                .requestMatchers("/users/**").permitAll()
                .requestMatchers("/auth/logout").authenticated()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); 
            // Garante que o token JWT será validado antes de qualquer outra verificação de autenticação

        return http.build();
    }

    // Define o codificador de senhas - hash
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    
}