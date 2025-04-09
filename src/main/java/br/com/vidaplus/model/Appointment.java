package br.com.vidaplus.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import br.com.vidaplus.serializer.MedicalRecordIdSerializer;
import br.com.vidaplus.serializer.UserIdSerializer;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    @JsonSerialize(using = UserIdSerializer.class)
    @JsonProperty("patientId")
    private User patient;

    @ManyToOne
    @JoinColumn(name = "health_professional_id")
    @JsonSerialize(using = UserIdSerializer.class)
    @JsonProperty("healthProfessionalId")
    private User healthProfessional;

    @Column(nullable = false)
    private LocalDateTime dateTime;
    
    @Enumerated(EnumType.STRING)
    private AppointmentType type;
    
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;
    
    private String reason;
    
    @ManyToOne
    @JoinColumn(name = "medical_record_id", nullable = false)
    @JsonBackReference
    @JsonSerialize(using = MedicalRecordIdSerializer.class)
    @JsonProperty("medicalRecordId")
    private MedicalRecord medicalRecord;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    @JsonIgnore
    private ProfessionalSchedule schedule;

}
