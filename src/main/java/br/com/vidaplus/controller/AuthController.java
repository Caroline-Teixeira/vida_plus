package br.com.vidaplus.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.vidaplus.model.AllRole;
import br.com.vidaplus.model.User;
import br.com.vidaplus.repository.UserRepository;
import io.jsonwebtoken.Jwts;

@RestController
@RequestMapping("/auth") // Define o prefixo para as rotas dessa classe
public class AuthController {

    private final UserRepository userRepository; // Repositório para buscar usuários no banco de dados
    private final PasswordEncoder passwordEncoder; // Encoder para verificar senhas
    private final String SECRET_KEY = "chave-secreta-simples-para-teste-123456"; // Chave secreta para assinar tokens JWT

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        if (email == null || password == null) {
            throw new BadCredentialsException("Email e senha são obrigatórios");
        }

        User user = userRepository.findByEmail(email).orElseThrow(() -> 
            new BadCredentialsException("Usuário não encontrado com o email: " + email));

        // Substitua a validação BCrypt por comparação direta (apenas para testes)
    if (!password.equals(user.getPassword())) {
        throw new BadCredentialsException("Senha incorreta");
    }

        // Adicione os logs aqui para depurar o email e o usuário encontrado
        System.out.println("Email: " + email);
        System.out.println("Usuário encontrado: " + user);

        // Obtém os papéis do usuário e converte para lista de strings
        Set<AllRole> userRoles = user.getRoles();
        List<String> roles = new ArrayList<>();

        for (AllRole role : userRoles) {
            roles.add(role.getName().toString());
        }

        // Define tempo de expiração do token (7 dias)
        long expirationTime = 7 * 24 * 60 * 60 * 1000L;
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);

         // Gera token JWT usando o mesmo método do JwtAuthenticationFilter
         String jwt = Jwts.builder()
         .setSubject(user.getEmail())
         .setIssuedAt(new Date())
         .setExpiration(expirationDate)
         .claim("roles", roles)
         .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
         .compact();

        // Adicione o log aqui para verificar o token gerado
        System.out.println("Token gerado: " + jwt);
        Map<String, String> response = new HashMap<>();
        response.put("token", jwt);
        return ResponseEntity.ok(response);
    }
}
