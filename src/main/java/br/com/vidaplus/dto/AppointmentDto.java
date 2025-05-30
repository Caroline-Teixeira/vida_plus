package br.com.vidaplus.dto;

import java.time.LocalDateTime;

import br.com.vidaplus.model.EventStatus;
import br.com.vidaplus.model.AppointmentType;

public class AppointmentDto {

    // Atributos
    private Long id;
    private Long patientId;
    private Long healthProfessionalId;
    private LocalDateTime dateTime;
    private AppointmentType type;
    private EventStatus status;
    private String reason;
    private Long medicalRecordId;
    private String observations;
    private String patientName;
    private String healthProfessionalName;

    // Getters e Setters

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getPatientId() {
        return patientId;
    }
    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }
    public Long getHealthProfessionalId() {
        return healthProfessionalId;
    }
    public void setHealthProfessionalId(Long healthProfessionalId) {
        this.healthProfessionalId = healthProfessionalId;
    }
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
    public AppointmentType getType() {
        return type;
    }
    public void setType(AppointmentType type) {
        this.type = type;
    }
    public EventStatus getStatus() {
        return status;
    }
    public void setStatus(EventStatus status) {
        this.status = status;
    }
    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public String getObservations() {
        return observations;
    }
    public void setObservations(String observations) {
        this.observations = observations;
    }

    public Long getMedicalRecordId() {
        return medicalRecordId;
    }

    public void setMedicalRecordId(Long medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
    }

    public String getPatientName() {
        return patientName;
    }
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
    public String getHealthProfessionalName() {
        return healthProfessionalName;
    }
    public void setHealthProfessionalName(String healthProfessionalName) {
        this.healthProfessionalName = healthProfessionalName;
    }

}
