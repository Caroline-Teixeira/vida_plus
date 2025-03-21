package br.com.vidaplus.model;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {

    // ATRIBUTOS
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @Column(name="Name")
    private String name;
    
    @Column(name="CPF")
    private String cpf;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name="Phone")
    private String contact;

    @Column (name="E-mail")
    private String email;

    @Column (name="password")
    private String password;

    @ManyToMany
    @JoinTable(
        name = "user_roles",
        joinColumns= @JoinColumn(name= "user_id"),
        inverseJoinColumns = @JoinColumn(name="role_id")
    )
    private Set<AllRole> roles = new HashSet<>();
    
    
}

