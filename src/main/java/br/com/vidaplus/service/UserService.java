package br.com.vidaplus.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.vidaplus.model.AllRole;
import br.com.vidaplus.model.Profile;
import br.com.vidaplus.model.User;
import br.com.vidaplus.repository.AllRoleRepository;
import br.com.vidaplus.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AllRoleRepository allRoleRepository;

    @Autowired
    public UserService (UserRepository userRepository, AllRoleRepository allRoleRepository) {
        this.userRepository = userRepository;
        this.allRoleRepository = allRoleRepository;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    
        if (auth == null) {
            System.out.println("Nenhuma autenticação encontrada. Usuários encontrados: " + users);
            return users;
        }
    
        System.out.println("Autoridades no contexto de segurança: " + auth.getAuthorities()); // Adicione este log
    
        boolean isAdmin = false;
        for (GrantedAuthority authority : auth.getAuthorities()) {
            System.out.println("Verificando autoridade: " + authority.getAuthority()); // Adicione este log
            if (authority.getAuthority().equals("ADMIN")) {
                isAdmin = true;
                break;
            }
        }
    
        if (isAdmin) {
            users = userRepository.findAll();
        }
    
        System.out.println("Usuários encontrados: " + users);
        return users;
    }
    
    // Verifica se é nulo
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    // Verifica se é nulo
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Cadastra Usuário
    @Transactional
    public User registerUser(User user, Set<Profile> profiles){
        // Cria um conjunto para os papéis
        Set<AllRole> roles = new HashSet<>();

        // Para cada tipo de perfil, busca e adiciona ao conjunto
        for (Profile profile : profiles) {
            AllRole role = allRoleRepository.findByName(profile).orElse(null); // orElse -> Optional do AllRoleRepository
            if (role == null) {
                throw new RuntimeException("Papel não encontrado: " + profile);
            }
            roles.add(role);
        }

        // Define os papéis no usuário
        user.setRoles(roles);

        // Salva e retorna o usuário
        return userRepository.save(user);
    }

    // Atualiza usuário
    @Transactional
    public User updateUser(User user, Set<Profile> profiles) {
        Set<AllRole> roles = new HashSet<>();

        if (profiles != null) {
            for (Profile profile : profiles) {
                Optional<AllRole> roleOptional = allRoleRepository.findByName(profile);
                
                try {
                    AllRole role = roleOptional.get();
                    roles.add(role);
                } catch (NoSuchElementException e) {
                    throw new RuntimeException("Papel não encontrado: " + profile);
                }
            }
            user.setRoles(roles);
        }

        return userRepository.save(user);
    }
    
    // Deleta usuário
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public boolean existsByCpf(String cpf) {
        return userRepository.existsByCpf(cpf);
    }
        

}