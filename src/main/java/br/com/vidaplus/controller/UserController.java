package br.com.vidaplus.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/users") // define o caminho
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    // GET por Lista - todos os usuários
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // GET por id
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id).orElse(null);
    }

    //Post para registrar usuário
    @PostMapping
    public User registerUser(@RequestBody UserDto userDto) {
    // Verifica se o email e cpf existe
    if (userService.existsByEmail(userDto.getEmail())) {
        throw new RuntimeException("E-mail existente: " + userDto.getEmail());
    }
    
    if (userService.existsByCpf(userDto.getCpf())) {
        throw new RuntimeException("CPF existente: " + userDto.getCpf());
    }

    // Converte UserDto para User e registra (exemplo implícito)
    User user = new User(); // Você precisará preencher isso com os dados do userDto
    user.setName(userDto.getName());
    user.setEmail(userDto.getEmail());
    user.setCpf(userDto.getCpf());
    user.setDateOfBirth(userDto.getDateOfBirth());
    user.setGender(userDto.getGender());
    user.setContact(userDto.getContact());
    user.setEmail(userDto.getEmail());
    user.setPassword(userDto.getPassword());


    return userService.registerUser(user, userDto.getRoles());
    }

    // Put pra atualizar um usuário
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        User user = userService.getUserById(id).orElse(null);
        
        if (user == null) {
            throw new RuntimeException("Usuário não encontrado: " + id);
        }
        System.out.println("Senha recebida: " + userDto.getPassword()); // Debug
        
        user.setName(userDto.getName());
        user.setDateOfBirth(userDto.getDateOfBirth());
        user.setGender(userDto.getGender());
        user.setContact(userDto.getContact());
        if (!user.getEmail().equals(userDto.getEmail()) && 
            userService.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("E-mail existente " + userDto.getEmail());
        }
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        
        
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




