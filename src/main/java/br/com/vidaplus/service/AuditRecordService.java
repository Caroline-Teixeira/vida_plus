package br.com.vidaplus.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.vidaplus.model.AuditRecord;
import br.com.vidaplus.model.User;
import br.com.vidaplus.repository.AuditRecordRepository;

@Service
@Transactional
public class AuditRecordService {

    private final AuditRecordRepository auditRecordRepository;
    private final UserService userService;

    @Autowired
    public AuditRecordService(AuditRecordRepository auditRecordRepository, UserService userService) {
        this.auditRecordRepository = auditRecordRepository;
        this.userService = userService;
    }

    // Registra uma ação de auditoria
    public void logAction(String username, String action) {
        try {
            AuditRecord auditRecord = new AuditRecord();
            auditRecord.setUsername(username);
            auditRecord.setAction(action);
            auditRecord.setTimestamp(LocalDateTime.now());
            auditRecordRepository.save(auditRecord);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao registrar ação de auditoria: " + e.getMessage());
        }
    }

    // Para registrar ação usando o usuário autenticado
    public void logAction(String action) {
        try {
            // Obtém o usuário autenticado
            User currentUser = userService.getCurrentAuthenticatedUser();
            // Registra a ação usando o email do usuário
            
            logAction(currentUser.getEmail(), action);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao registrar ação de auditoria para o usuário atual: " + e.getMessage());
        }
    }

    // Busca todos os registros de auditoria
    public List<AuditRecord> getAllAuditRecords() {
        try {
            List<AuditRecord> records = auditRecordRepository.findAll();
            return records;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar registros de auditoria: " + e.getMessage());
        }
    }

    // Busca registros de auditoria com filtros (username e/ou action)
    public List<AuditRecord> getAuditRecordsByFilter(String username, String action) {
        try {
            // Busca todos os registros
            List<AuditRecord> allRecords = auditRecordRepository.findAll();
            List<AuditRecord> filteredRecords = new ArrayList<>();

            // Aplica filtros
            for (AuditRecord record : allRecords) {
                boolean matchesUsername = true;
                boolean matchesAction = true;

                // Verifica se o filtro de username foi fornecido e se corresponde
                if (username != null && !username.trim().isEmpty()) {
                    if (!record.getUsername().equals(username)) {
                        matchesUsername = false;
                    }
                }

                // Verifica se o filtro de action foi fornecido e se corresponde
                if (action != null && !action.trim().isEmpty()) {
                    if (!record.getAction().equals(action)) {
                        matchesAction = false;
                    }
                }

                // Adiciona o registro à lista filtrada se ele corresponder aos filtros
                if (matchesUsername && matchesAction) {
                    filteredRecords.add(record);
                }
            }

            return filteredRecords;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar registros de auditoria com filtros: " + e.getMessage());
        }
    }
}