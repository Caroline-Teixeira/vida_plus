package br.com.vidaplus.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.vidaplus.model.AllRole;
import br.com.vidaplus.model.User;
import br.com.vidaplus.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final String SECRET_KEY = "chave-secreta-simples-para-teste-123456"; 
    // 37 caracters deve ser a mesma no authcontroller

    public JwtAuthenticationFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override  // filtros pra pegar o e-mail
    @SuppressWarnings("UnnecessaryReturnStatement")
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException { 
                
                
        String authHeader = request.getHeader("Authorization");
        
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7); // Começa do 7 pra não pegar a palavra Bearer
        try {
            
            Claims claims = Jwts.parserBuilder() // Nova API - cria um parser para processar o token
                    .setSigningKey(SECRET_KEY.getBytes()) // Chave como bytes, define a chave secreta para verificar a assinatura do token
                    .build()  // finaliza a configuração do parser
                    .parseClaimsJws(jwt)  // faz o parsing do token e verifica se ele é válido (não expirado, assinatura correta,
                    .getBody();  // extrai as "claims" (dados) do token, como o email do usuário

            String email = claims.getSubject();
            // Se o email for nulo ou o usuário já estiver autenticado, passa a requisição adiante
            if (email == null || SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }

        // Busca o usuário no banco de dados usando o UserRepository
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("Usuário não encontrado com o email: " + email);
        }
        User user = userOptional.get();

        // Adiciona uma única linha de log aqui
        System.out.println("Filtro JWT - Token: " + jwt + ", Email: " + email + ", User: " + user + ", Roles: " + user.getRoles());

        // Converter os papéis (AllRole) em uma lista de autoridades
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (AllRole role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getName().toString()));
        }

        // Criar o token de autenticação e definir no contexto do Spring Security
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        } 
        catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException | SignatureException | ServletException | IOException | IllegalArgumentException | UsernameNotFoundException e) { // Captura todas as exceções em um único bloco
            // Usa um switch para tratar as exceções com base no tipo
            String errorMessage;
            errorMessage = switch (e.getClass().getSimpleName()) {
                    case "ExpiredJwtException" -> "Token expirado";
                    case "MalformedJwtException", "UnsupportedJwtException", "SignatureException", "IllegalArgumentException" -> "Token inválido";
                    case "UsernameNotFoundException" -> "Usuário não encontrado";
                    default -> "Erro desconhecido ao validar o token";
                }; // Switch baseado no nome da classe da exceção
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, errorMessage);
            return; // Impede que a requisição continue após um erro 
        }
    }
}