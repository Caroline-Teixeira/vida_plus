package br.com.vidaplus.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;

    @Column(name = "Name")
    @JsonProperty("name")
    private String name;

    @Column(name = "CPF")
    @JsonProperty("cpf")
    private String cpf;

    @JsonProperty("dateOfBirth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @JsonProperty("gender")
    private Gender gender;

    @Column(name = "Phone")
    @JsonProperty("contact")
    private String contact;

    @Column(name = "E-mail")
    @JsonProperty("email")
    private String email;

    @Column(name = "password")
    @JsonProperty(value = "password", access = JsonProperty.Access.WRITE_ONLY) // para não exibir a senha na API
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @JsonProperty("roles")
    private Set<AllRole> roles = new HashSet<>();
}