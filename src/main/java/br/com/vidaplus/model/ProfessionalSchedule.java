package br.com.vidaplus.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProfessionalSchedule {
    // agenda do profissional da saúde

    // Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "health_professional_id")
    private User healthProfessional;

    private LocalDate date;
    
    @OneToMany(mappedBy = "schedule")
    private List<Appointment> appointments = new ArrayList<>();

    @OneToMany(mappedBy = "schedule")
    private List<Surgery> surgeries = new ArrayList<>();

    
}