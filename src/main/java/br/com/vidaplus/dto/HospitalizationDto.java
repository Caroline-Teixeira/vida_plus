package br.com.vidaplus.dto;

import br.com.vidaplus.model.EventStatus;

public class HospitalizationDto {

    // Atributos
    private Long id;
    private Long surgeryId;
    private String bed;
    private EventStatus status;

    // Construtores
    public HospitalizationDto() {
    }

    public HospitalizationDto(Long id, Long surgeryId, String bed, EventStatus status) {
        this.id = id;
        this.surgeryId = surgeryId;
        this.bed = bed;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSurgeryId() {
        return surgeryId;
    }

    public void setSurgeryId(Long surgeryId) {
        this.surgeryId = surgeryId;
    }

    public String getBed() {
        return bed;
    }

    public void setBed(String bed) {
        this.bed = bed;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }
}