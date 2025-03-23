package br.com.vidaplus.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.vidaplus.dto.UserDto;
import br.com.vidaplus.mapper.DtoMapper;
import br.com.vidaplus.model.AllRole;
import br.com.vidaplus.model.User;
import br.com.vidaplus.repository.AllRoleRepository;
import br.com.vidaplus.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AllRoleRepository allRoleRepository;
    private final DtoMapper dtoMapper;

    @Autowired
    public UserService (UserRepository userRepository, 
                        AllRoleRepository allRoleRepository,
                        DtoMapper dtoMapper) {
        this.userRepository = userRepository;
        this.allRoleRepository = allRoleRepository;
        this.dtoMapper = dtoMapper;
    }

    // ponte entre User e UserDto
    private UserDto convertToUserDto(User user) {
    return dtoMapper.toUserDto(user); // mapeamento
    }

    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = new ArrayList<>();
    
        for (User user : users) {
            UserDto userDto = convertToUserDto(user); // Usa o método
            userDtos.add(userDto);
        }
    
        return userDtos;
    }
    
    // Verifica se é nulo
    public Optional<UserDto> getUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            UserDto userDto = convertToUserDto(user); // Usa o método auxiliar
            return Optional.of(userDto);
        }
        return Optional.empty();
    }
    
    // Verifica se é nulo
    public Optional<UserDto> getUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            UserDto userDto = convertToUserDto(user); // Usa o método auxiliar
            return Optional.of(userDto);
        }
        return Optional.empty();
    }
    // Cadastra Usuário
    @Transactional
    public UserDto registerUser(User user, Set<AllRole> profiles){
        // Cria um conjunto para os papéis
        Set<AllRole> roles = new HashSet<>();

        // Para cada tipo de perfil, busca e adiciona ao conjunto
        for (AllRole profile : profiles) {
            AllRole role = allRoleRepository.findByName(profile).orElse(null); // orElse -> Optional do AllRoleRepository
            if (role == null) {
                throw new RuntimeException("Papel não encontrado: " + profile);
            }
            roles.add(role);
        }

        // Define os papéis no usuário
        user.setRoles(roles);

        // Salva e retorna o usuário
        User savedUser = userRepository.save(user);
        return convertToUserDto(savedUser);
    }

    // Atualiza usuário
    @Transactional
    public UserDto updateUser(User user, Set<AllRole> profiles) {
        Set<AllRole> roles = new HashSet<>();

        if (profiles != null) {
            for (AllRole profile : profiles) {
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

        User updatedUser = userRepository.save(user);
        return convertToUserDto(updatedUser);
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

    public User updateUser(UserDto user, Set<AllRole> roles) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateUser'");
    }



    
        

}