package br.com.vidaplus.dto;

public class AuditRecordDto {

    private String username;
    private String action;   

    // Construtores
    public AuditRecordDto() {
    }

    public AuditRecordDto(String username, String action) {
        this.username = username;
        this.action = action;
    }

    
    // Getters e Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
