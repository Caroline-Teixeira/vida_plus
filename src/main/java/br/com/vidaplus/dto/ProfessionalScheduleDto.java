package br.com.vidaplus.dto;

import java.time.LocalDate;
import java.util.List;

import br.com.vidaplus.model.Appointment;
import br.com.vidaplus.model.Surgery;

public class ProfessionalScheduleDto {

    private Long id;
    private Long healthProfessionalId;
    private LocalDate date;
    
    private List<Appointment> availableSlots;
    private List<Appointment> bookedSlots;
    private List<Surgery> bookedSurgeries;

    // Construtores
    public ProfessionalScheduleDto() {
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHealthProfessionalId() {
        return healthProfessionalId;
    }

    public void setHealthProfessionalId(Long healthProfessionalId) {
        this.healthProfessionalId = healthProfessionalId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<Appointment> getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(List<Appointment> availableSlots) {
        this.availableSlots = availableSlots;
    }

    public List<Appointment> getBookedSlots() {
        return bookedSlots;
    }

    public void setBookedSlots(List<Appointment> bookedSlots) {
        this.bookedSlots = bookedSlots;
    }

    public List<Surgery> getBookedSurgeries() {
        return bookedSurgeries;
    }

    public void setBookedSurgeries(List<Surgery> bookedSurgeries) {
        this.bookedSurgeries = bookedSurgeries;
    }

}