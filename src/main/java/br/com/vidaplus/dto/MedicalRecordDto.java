package br.com.vidaplus.dto;

import java.time.LocalDateTime;
import java.util.List;

import br.com.vidaplus.model.User;

public class MedicalRecordDto {

    // Atributos
    private Long id;
    private User patient;
    private List<Long> appointmentIds;
    private List<Long> surgeryIds;
    private String observations;
    private LocalDateTime recordDate;

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getPatient() {
        return patient;
    }

    public void setPatient(User patient) {
        this.patient = patient;
    }

    public List<Long> getAppointmentIds() {
        return appointmentIds;
    }

    public void setAppointmentIds(List<Long> appointmentIds) {
        this.appointmentIds = appointmentIds;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public LocalDateTime getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDateTime recordDate) {
        this.recordDate = recordDate;
    }

    public List<Long> getSurgeryIds() {
        return surgeryIds;
    }

    public void setSurgeryIds(List<Long> surgeryIds) {
        this.surgeryIds = surgeryIds;

    }
}
