package br.com.vidaplus.dto;

public class MedicalRecordDto {
    

    private Long id;
    private String observations;

    // Construtores
    public MedicalRecordDto() {
        
    }

    public MedicalRecordDto(Long id, String observations) {
        this.id = id;
        this.observations = observations;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }
}


