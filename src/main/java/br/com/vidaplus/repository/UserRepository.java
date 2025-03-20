package br.com.vidaplus.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.vidaplus.model.User;

@Repository
public interface UserRepository extends JpaRepository <User, Long>{
    Optional<User> findByEmail(String email); // Optional verifica se existe o valor ou chave no banco de dados
    Optional<User> findByCpf(String cpf);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);

}
