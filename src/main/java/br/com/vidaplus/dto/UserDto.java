package br.com.vidaplus.dto;

import java.time.LocalDate;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.vidaplus.model.Gender;
import br.com.vidaplus.model.Profile;

public class UserDto {
    private Long id;
    private String name;
    private String cpf;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String contact;
    private String email;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // para não exibir a senha na API
    private String password;
    private Set<Profile> roles;


    // Getters e Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCpf() {
        return cpf;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    public Gender getGender() {
        return gender;
    }
    public void setGender(Gender gender) {
        this.gender = gender;
    }
    public String getContact() {
        return contact;
    }
    public void setContact(String contact) {
        this.contact = contact;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Set<Profile> getRoles() {
        return roles;
    }
    public void setRoles(Set<Profile> roles) {
        this.roles = roles;
    }

    
    
}
