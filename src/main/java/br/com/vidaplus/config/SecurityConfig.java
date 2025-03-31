package br.com.vidaplus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
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
                .requestMatchers("/auth/**").permitAll()  // Permite acesso público - pagina login (sem autenticação)
                .requestMatchers("/auth/logout").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/users").hasAnyAuthority("ADMIN", "ATTENDANT")
                .requestMatchers(HttpMethod.POST, "/api/users").hasAnyAuthority("ADMIN", "ATTENDANT")
                .requestMatchers(HttpMethod.PUT, "/api/users/**").hasAnyAuthority("ADMIN", "ATTENDANT")
                .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasAnyAuthority("ADMIN", "ATTENDANT")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); //nome longo arrumar
            // Garante que o token JWT será validado antes de qualquer outra verificação de autenticação

        return http.build();
    }

    // Define o codificador de senhas que será usado para criptografar as senhas dos usuários
    // O BCrypt é um algoritmo seguro e amplamente usado para hashing de senhas
    //@Bean
    //public PasswordEncoder passwordEncoder() {
       // return new BCryptPasswordEncoder();
    //}

    
}