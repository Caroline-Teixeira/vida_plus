package br.com.vidaplus.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.vidaplus.dto.AuditRecordDto;
import br.com.vidaplus.model.AuditRecord;
import br.com.vidaplus.service.AuditRecordService;

@RestController
@RequestMapping("/api/audit-records")
public class AuditRecordController {

    private final AuditRecordService auditRecordService;

    @Autowired
    public AuditRecordController(AuditRecordService auditRecordService) {
        this.auditRecordService = auditRecordService;
    }

    // GET para buscar todos os registros de auditoria
    @GetMapping("/all")
    public ResponseEntity<List<AuditRecord>> getAllAuditRecords() {
        try {
            List<AuditRecord> records = auditRecordService.getAllAuditRecords();
            return ResponseEntity.ok(records);
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao buscar todos os registros de auditoria: " + e.getMessage());
        }
    }

    // POST para buscar registros de auditoria com filtros (usuário e ação)
    @PostMapping("/filter")
    public ResponseEntity<List<AuditRecord>> getAuditRecordsByFilter(@RequestBody AuditRecordDto auditRecordDto) {
        try {
            List<AuditRecord> records = auditRecordService.getAuditRecordsByFilter(
                auditRecordDto.getUsername(),
                auditRecordDto.getAction()
            );
            return ResponseEntity.ok(records);
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao buscar registros de auditoria com filtros: " + e.getMessage());
        }
    }
}