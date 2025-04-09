package br.com.vidaplus.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import br.com.vidaplus.model.Appointment;

public class ProfessionalScheduleDto {

    private Long id;
    private Long healthProfessionalId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<Appointment> availableSlots;
    private List<Appointment> bookedSlots;

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

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
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
}