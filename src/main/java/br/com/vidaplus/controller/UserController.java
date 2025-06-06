package br.com.vidaplus.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.vidaplus.dto.UserDto;
import br.com.vidaplus.model.User;
import br.com.vidaplus.service.UserService;

@RestController
@RequestMapping("/api/users") 
public class UserController {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder; 

    @Autowired
    public UserController(UserService userService, BCryptPasswordEncoder passwordEncoder){
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // GET lista todos os usuários
    @GetMapping
    public List<User> getAllUsers() {
        System.out.println("Método chamando método lista todos os usuários");
        List<User> users = userService.getAllUsers();
        System.out.println("Controller - Usuários retornados: " + users);
        return users;
  
    }

    // GET por id
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id).orElse(null);
    }

    // GET para obter dados usuário logado
    @GetMapping("/current")
    public User getCurrentUser() {
        return userService.getCurrentAuthenticatedUser();
    }

    //POST para cadastrar um usuário
    @PostMapping
    public User registerUser(@RequestBody UserDto userDto) {
    // Verifica se o email e cpf existe
    if (userService.existsByEmail(userDto.getEmail())) {
        throw new RuntimeException("E-mail existente: " + userDto.getEmail());
    }
    
    if (userService.existsByCpf(userDto.getCpf())) {
        throw new RuntimeException("CPF existente: " + userDto.getCpf());
    }

    // Valida se o campo roles foi fornecido
    if (userDto.getRoles() == null || userDto.getRoles().isEmpty()) {
        throw new RuntimeException("O campo 'roles' é obrigatório");
    }

    // Converte UserDto para User e registra
    User user = new User(); 
    user.setName(userDto.getName());
    user.setEmail(userDto.getEmail());
    user.setCpf(userDto.getCpf());
    user.setDateOfBirth(userDto.getDateOfBirth());
    user.setGender(userDto.getGender());
    user.setContact(userDto.getContact());
    user.setPassword(passwordEncoder.encode(userDto.getPassword()));


    return userService.registerUser(user, userDto.getRoles());
    }

    // PUT pra atualizar um usuário
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        User user = userService.getUserById(id).orElse(null);
        
        if (user == null) {
            throw new RuntimeException("Usuário não encontrado: " + id);
        }
        
        
        user.setName(userDto.getName());
        user.setDateOfBirth(userDto.getDateOfBirth());
        user.setGender(userDto.getGender());
        user.setContact(userDto.getContact());

        // Verifica CPF existente
        if (userDto.getCpf() != null && !userDto.getCpf().isEmpty() && !user.getCpf().equals(userDto.getCpf())) {
            if (userService.existsByCpf(userDto.getCpf())) {
                throw new RuntimeException("CPF existente: " + userDto.getCpf());
            }
            user.setCpf(userDto.getCpf());
        }
        
        // Verifica email existente
        if (!user.getEmail().equals(userDto.getEmail()) && userService.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("E-mail existente: " + userDto.getEmail());
        }
        user.setEmail(userDto.getEmail());

        // Criptografa a senha apenas se ela foi fornecida
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        // Valida se o campo roles foi fornecido
        if (userDto.getRoles() == null || userDto.getRoles().isEmpty()) {
            throw new RuntimeException("O campo 'roles' é obrigatório");
        }

        // Atualiza o usuário e os papéis
        return userService.updateUser(user, userDto.getRoles());
    }

    //DELETE por id
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        User user = userService.getUserById(id).orElse(null);
    
        if (user == null) {
            throw new RuntimeException("Usuário não encontrado: " + id);
        }
    
        userService.deleteUser(id);
        return ResponseEntity.ok("Usuário deletado com sucesso");
    }

}




