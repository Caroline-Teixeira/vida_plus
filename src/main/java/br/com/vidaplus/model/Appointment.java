package br.com.vidaplus.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "Appointments")
@Data
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private User patient;

    @ManyToOne
    @JoinColumn(name = "health_professional_id")
    private User healthProfessional;

    @Column(nullable = false)
    private LocalDateTime dateTime;
    
    @Enumerated(EnumType.STRING)
    private AppointmentType type;
    
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;
    
    private String reason;
    
    private String observations;

}
