package br.com.vidaplus.model;


import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
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
    private Long user_id;
    
    private String name;
    
    private String cpf;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String contact;

    @Column(name = "profile")
    private int profileValue;

    @Transient  //Não mapeado pra o banco de dados
    private Profile profile;


    @Column(name = "permission_state")
    private int permissionStateValue;
    
    @Transient  //Não mapeado pra o banco de dados
    private PermissionState permissionState;
    
    //private AllRole roles;

    @Embedded
    private Login login; // Indica que Login é um objeto embutido



    // Método chamado pelo @Getter para profile
    public Profile getProfile() {
        if (profile == null && profileValue != 0) {
            switch (profileValue) {
                case 1 -> profile = Profile.PATIENT;
                case 2 -> profile = Profile.HEALTH_PROFESSIONAL;
                case 4 -> profile = Profile.ATTENDANT;
                case 8 -> profile = Profile.ADMIN;
                default -> throw new IllegalArgumentException("Unknown Profile value: " + profileValue);
            }
        }
        return profile;
    }

    // Método chamado pelo @Setter para profile
    public void setProfile(Profile profile) {
        this.profile = profile;
        if (profile != null) {
            this.profileValue = profile.getValue();
        } else {
            this.profileValue = 0;
        }
    }

    // Método chamado pelo @Getter para permission state
    public PermissionState getPermissionState(){
        if (permissionState == null && permissionStateValue !=0){
            switch (permissionStateValue){
                case 1 -> permissionState = PermissionState.ACTIVE;
                case 2 -> permissionState = PermissionState.INACTIVE;
                case 4 -> permissionState = PermissionState.READ;
                case 8 -> permissionState = PermissionState.WRITE;
                case 16 -> permissionState = PermissionState.MANAGEMENT;
                default -> throw new IllegalArgumentException("Unknown Permission State value: " + permissionStateValue);
            }
        }
        return permissionState;
    }

    // Método chamado pelo @Setter para permission state
    public void setPermissionState(PermissionState permissionState){
        this.permissionState = permissionState;
        if (permissionState != null){
            this.permissionStateValue = permissionState.getValue();
        } else {
            this.permissionStateValue = 0;
        }
    }
    


    // MÉTODOS	LOGIN e LOGOUT
    public boolean login(String email, String password) {
        return this.login.getEmail().equals(email) && this.login.getPasswordHash().equals(password);
    }

    public boolean logout() {
        System.out.println("User " + name + " logged out.");
        return true;
    }
    
    
}

