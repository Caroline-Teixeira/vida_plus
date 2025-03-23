package br.com.vidaplus.dto;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import br.com.vidaplus.model.AllRole;
import br.com.vidaplus.model.Gender;


public class UserDto {
    private Long id;
    private String name;
    private String cpf;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String contact;
    private String email;
    private String password;
    private Set<AllRole> roles = new HashSet<>();

    
    // Construtores

    public UserDto() {

    }


    public UserDto(Long id, String name, String cpf, LocalDate dateOfBirth, Gender gender, String contact, String email,
            String password, Set<AllRole> roles) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.contact = contact;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }


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

    public Set<AllRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<AllRole> roles) {
        this.roles = roles;
    }
   

    
    
}
