package br.com.vidaplus.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.vidaplus.model.AllRole;
import br.com.vidaplus.model.User;
import br.com.vidaplus.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final String SECRET_KEY = "chave-secreta-simples-para-teste-123456"; // Deve ser a mesma usada no JwtAuthenticationFilter

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        // Valida os campos
        if (email == null || password == null) {
            throw new BadCredentialsException("Email e senha são obrigatórios");
        }

        // Busca o usuário pelo email
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new BadCredentialsException("Usuário não encontrado com o email: " + email));

        // Compara a senha (sem criptografia, conforme sua configuração)
        if (!password.equals(user.getPassword())) {
            throw new BadCredentialsException("Senha incorreta");
        }

        // Obtém os papéis do usuário e converte para uma lista de strings usando um loop for
        Set<AllRole> userRoles = user.getRoles();
        List<String> roles = new ArrayList<>();
        for (AllRole role : userRoles) {
            roles.add(role.getName().toString());
        }

        // Define o tempo de expiração do token (7 dias)
        long expirationTime = 7 * 24 * 60 * 60 * 1000L; // 7 dias em milissegundos
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);

        // Gera o token JWT usando a nova API de assinatura
        String token = Jwts.builder()
            .setSubject(user.getEmail())
            .setIssuedAt(new Date())
            .setExpiration(expirationDate)
            .claim("roles", roles)
            .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes())) // Nova API para assinatura
            .compact();

        // Retorna o token na resposta
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout bem-sucedido.");
        return ResponseEntity.ok(response);
    }
}