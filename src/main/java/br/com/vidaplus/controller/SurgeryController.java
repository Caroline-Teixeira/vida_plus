package br.com.vidaplus.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.vidaplus.dto.SurgeryDto;
import br.com.vidaplus.model.EventStatus;
import br.com.vidaplus.model.Surgery;
import br.com.vidaplus.service.SurgeryService;

@RestController
@RequestMapping("api/surgeries")
public class SurgeryController {

    private final SurgeryService surgeryService;

    @Autowired
    public SurgeryController(SurgeryService surgeryService) {
        this.surgeryService = surgeryService;
    }

    // GET lista todas as cirurgias
    @GetMapping
    public ResponseEntity<List<Surgery>> getAllSurgeries() {
        try {
            List<Surgery> surgeries = surgeryService.getAllSurgeries();
            return ResponseEntity.ok(surgeries);
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao buscar cirurgias: " + e.getMessage());
        }
    }

    // GET cirurgia por id
    @GetMapping("/{id}")
    public Surgery getSurgeryById(@PathVariable Long id) {
        return surgeryService.getSurgeryById(id).orElse(null);
    }

    // GET cirurgia por paciente
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Surgery>> getSurgeriesByPatient(@PathVariable Long patientId) {
        try {
            List<Surgery> surgeries = surgeryService.getSurgeriesByPatient(patientId);
            return ResponseEntity.ok(surgeries);
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao buscar cirurgias do paciente com id " + patientId + ": " + e.getMessage());
        }
    }

    // GET cirurgia por profissional de saúde
    @GetMapping("/healthProfessional/{healthProfessionalId}")
    public ResponseEntity<List<Surgery>> getSurgeriesByHealthProfessional(@PathVariable Long healthProfessionalId) {
        try {
            List<Surgery> surgeries = surgeryService.getSurgeriesByHealthProfessional(healthProfessionalId);
            return ResponseEntity.ok(surgeries);
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao buscar cirurgias do profissional com id " + healthProfessionalId + ": " + e.getMessage());
        }
    }

    // GET lista cirurgias do usuário logado
    @GetMapping("/current")
    public List<Surgery> getCurrentUserSurgeries() {
        return surgeryService.getSurgeriesByCurrentUser();
    }

    // POST agenda uma nova cirurgia
    @PostMapping
    public ResponseEntity<Surgery> scheduleSurgery(@RequestBody SurgeryDto surgeryDto) {
        try {
            Surgery surgery = surgeryService.scheduleSurgery(
                surgeryDto.getPatientId(),
                surgeryDto.getHealthProfessionalId(),
                surgeryDto.getDateTime(),
                surgeryDto.getReason(),
                surgeryDto.getBed(),
                surgeryDto.getObservations()
            );
            return ResponseEntity.ok(surgery);
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao agendar cirurgia: " + e.getMessage());
        }
    }

    // PUT atualiza STATUS da cirurgia
    @PutMapping("/{id}/status")
    public ResponseEntity<Surgery> updateSurgeryStatus(
            @PathVariable Long id, @RequestBody EventStatus status) {
        try {
            Surgery surgery = surgeryService.updateSurgeryStatus(id, status);
            return ResponseEntity.ok(surgery);
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao atualizar status da cirurgia com id " + id + ": " + e.getMessage());
        }
    }

    // PUT atualiza a cirurgia
    @PutMapping("/{id}")
    public ResponseEntity<Surgery> updateSurgery(
            @PathVariable Long id, @RequestBody SurgeryDto surgeryDto) {
        try {
            Surgery surgery = surgeryService.updateSurgery(
                id,
                surgeryDto.getPatientId(),
                surgeryDto.getHealthProfessionalId(),
                surgeryDto.getDateTime(),
                surgeryDto.getReason(),
                surgeryDto.getBed(),
                surgeryDto.getObservations()
            );
            return ResponseEntity.ok(surgery);
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao atualizar cirurgia com id " + id + ": " + e.getMessage());
        }
    }

    // DELETE exclui a cirurgia
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSurgery(@PathVariable Long id) {
        try {
            Optional<Surgery> surgeryBefore = surgeryService.getSurgeryById(id);
            if (!surgeryBefore.isPresent()) {
                throw new RuntimeException("Cirurgia não encontrada: " + id);
            }
            surgeryService.deleteSurgery(id);
            Optional<Surgery> surgeryAfter = surgeryService.getSurgeryById(id);
            if (surgeryAfter.isPresent()) {
                throw new RuntimeException("Erro ao deletar cirurgia: acesso negado: " + id);
            }
            return ResponseEntity.ok("Cirurgia deletada com sucesso");
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao deletar cirurgia com id " + id + ": " + e.getMessage());
        }
    }
}