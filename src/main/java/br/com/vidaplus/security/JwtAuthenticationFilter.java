package br.com.vidaplus.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.vidaplus.model.AllRole;
import br.com.vidaplus.model.User;
import br.com.vidaplus.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.security.WeakKeyException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final String SECRET_KEY = "chave-secreta-simples-para-teste-123456";

    public JwtAuthenticationFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Verifica se o cabeçalho Authorization está presente e começa com "Bearer "
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("Nenhum token JWT encontrado no cabeçalho Authorization");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7); // Remove o prefixo "Bearer "
        System.out.println("Token JWT recebido: " + token);

        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes())) // Chave secreta para assinar o token
                .build()  // Cria o parser
                .parseClaimsJws(token)  // Faz o parse do token
                .getBody();  // Extrai os claims do token

            String email = claims.getSubject();
            if (email == null) {
                System.out.println("Email não encontrado no token");
                filterChain.doFilter(request, response);
                return;
            }
            System.out.println("Email extraído do token: " + email);

            User user = userRepository.findByEmail(email) // Busca o usuário pelo email
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + email));
            System.out.println("Usuário encontrado: " + user.getEmail());

            // Verifica se o usuário está ativo
            List<GrantedAuthority> authorities = new ArrayList<>();
            System.out.println("Papéis associados ao usuário: " + user.getRoles());
            
            // Adiciona os papéis do usuário às autoridades
            for (AllRole role : user.getRoles()) {
                String roleName = role.getName().toString();
                System.out.println("Adicionando autoridade: " + roleName);
                authorities.add(new SimpleGrantedAuthority(roleName));
            }

            System.out.println("Autoridades configuradas para o usuário " + email + ": " + authorities);

            // Cria a autenticação
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException | SignatureException | WeakKeyException | ServletException | IOException | IllegalArgumentException e) {
            System.out.println("Erro ao validar token JWT: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido ou expirado");
        }
    }
}