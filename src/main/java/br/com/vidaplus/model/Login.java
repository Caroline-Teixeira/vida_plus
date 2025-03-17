package br.com.vidaplus.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor // Construtor sem argumentos (necessário para Hibernate)
@AllArgsConstructor // Construtor com todos os argumentos (necessário para o teste)
public class Login {
   
    @Column(name= "login_email", nullable=false)
    private String email;

    @Column(name="passaword", nullable=false)
    private String passwordHash;


}
