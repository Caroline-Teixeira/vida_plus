package br.com.vidaplus.controller;

import java.time.LocalDateTime;
import java.util.Map;
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

import br.com.vidaplus.dto.MedicalRecordDto;
import br.com.vidaplus.model.Appointment;
import br.com.vidaplus.model.MedicalRecord;
import br.com.vidaplus.model.User;
import br.com.vidaplus.repository.AppointmentRepository;
import br.com.vidaplus.repository.UserRepository;
import br.com.vidaplus.service.MedicalRecordService;

@RestController
@RequestMapping("api/medical-records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    @Autowired
    public MedicalRecordController(MedicalRecordService medicalRecordService,
                                   UserRepository userRepository,
                                   AppointmentRepository appointmentRepository) {
        this.medicalRecordService = medicalRecordService;
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
    }

    // GET prontuário por paciente
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<MedicalRecord> getMedicalRecordByPatient(@PathVariable Long patientId) {
        try {
            Optional<User> patientOptional = userRepository.findById(patientId);
            if (!patientOptional.isPresent()) {
                throw new RuntimeException("Paciente não encontrado: " + patientId);
            }
            User patient = patientOptional.get();
            
            MedicalRecord medicalRecord = medicalRecordService.findOrCreateMedicalRecord(patient);
            return ResponseEntity.ok(medicalRecord);
        } 
        catch (RuntimeException e) {
            throw new RuntimeException("Erro ao buscar prontuário do paciente com id " + patientId + ": " + e.getMessage());
        }
    }

    // POST - adicionar observações ao prontuário
    @PostMapping("/{patientId}/add-observations")
    public ResponseEntity<String> addObservations(
            @PathVariable Long patientId,
            @RequestBody MedicalRecordDto medicalRecordDto) {
        
        try {
            // Validações
            if (medicalRecordDto.getAppointmentIds() == null || medicalRecordDto.getAppointmentIds().isEmpty()) {
                throw new RuntimeException("Nenhum ID de consulta fornecido.");
            }

            // Busca o paciente
            Optional<User> patientOptional = userRepository.findById(patientId);
            if (!patientOptional.isPresent()) {
                throw new RuntimeException("Paciente não encontrado: " + patientId);
            }
            User patient = patientOptional.get();

            // Busca o prontuário ou cria um novo
            MedicalRecord medicalRecord = medicalRecordService.findOrCreateMedicalRecord(patient);

            // Processa cada consulta
            for (Long appointmentId : medicalRecordDto.getAppointmentIds()) {
                Optional<Appointment> appointmentOptional = appointmentRepository.findById(appointmentId);
                if (!appointmentOptional.isPresent()) {
                    throw new RuntimeException("Consulta não encontrada: " + appointmentId);
                }
                Appointment appointment = appointmentOptional.get();

                // Adiciona observações para cada consulta
                medicalRecordService.addObservations(
                    medicalRecord, 
                    appointment, 
                    medicalRecordDto.getObservations()
                );
            }

            return ResponseEntity.ok("Observações adicionadas com sucesso ao prontuário do paciente: " + patientId);
        } 
        catch (RuntimeException e) {
            throw new RuntimeException("Erro ao adicionar observações ao prontuário do paciente com id " + patientId + ": " + e.getMessage());
        }
    }

    // Método PUT para atualizar observações
    @PutMapping("/{patientId}/update-observations")
    public ResponseEntity<String> updateObservations(
            @PathVariable Long patientId,
            @RequestBody MedicalRecordDto medicalRecordDto) {
        
        try {
            // Validações
            if (medicalRecordDto.getAppointmentIds() == null || medicalRecordDto.getAppointmentIds().isEmpty()) {
                throw new RuntimeException("Nenhum ID de consulta fornecido.");
            }

            // Busca o paciente
            Optional<User> patientOptional = userRepository.findById(patientId);
            if (!patientOptional.isPresent()) {
                throw new RuntimeException("Paciente não encontrado: " + patientId);
            }
            User patient = patientOptional.get();

            // Busca o prontuário
            MedicalRecord medicalRecord = medicalRecordService.findOrCreateMedicalRecord(patient);
            // Método no service para atualizar observações
        medicalRecordService.updateObservations(
            medicalRecord, 
            medicalRecordDto.getAppointmentIds(), 
            medicalRecordDto.getObservations()
        );

        return ResponseEntity.ok("Observações atualizadas com sucesso para o paciente: " + patientId);
    } 
    catch (RuntimeException e) {
        throw new RuntimeException("Erro ao atualizar observações do prontuário do paciente com id " + patientId + ": " + e.getMessage());
    }
    }

    // DELETE para remover entradas de observações por data
    @DeleteMapping("/{patientId}/remove-observations")
    public ResponseEntity<String> removeObservationEntries(
            @PathVariable Long patientId,
            @RequestBody Map<String, LocalDateTime> dateRange) {
        
        try {
            // Busca o paciente
            Optional<User> patientOptional = userRepository.findById(patientId);
            if (!patientOptional.isPresent()) {
                throw new RuntimeException("Paciente não encontrado: " + patientId);
            }
            User patient = patientOptional.get();
    
            // Remove entradas de observações no intervalo de datas
            int removedEntries = medicalRecordService.removeObservationEntries(
                patient, 
                dateRange.get("startDate"), 
                dateRange.get("endDate")
            );
    
            return ResponseEntity.ok("Removidas " + removedEntries + " entradas de observações no intervalo de datas.");
        } 
        catch (RuntimeException e) {
            throw new RuntimeException("Erro ao remover entradas de observações para o paciente com id " + patientId + ": " + e.getMessage());
        }
    }
    

    // DELETE para excluir prontuário completo
    @DeleteMapping("/{patientId}")
    public ResponseEntity<String> deleteMedicalRecord(@PathVariable Long patientId) {
        
        try {
            // Busca o paciente
            Optional<User> patientOptional = userRepository.findById(patientId);
            if (!patientOptional.isPresent()) {
                throw new RuntimeException("Paciente não encontrado: " + patientId);
            }
            User patient = patientOptional.get();

            // Exclui o prontuário
            medicalRecordService.deleteMedicalRecord(patient);

            return ResponseEntity.ok("Prontuário excluído com sucesso para o paciente: " + patientId);
        } 
        catch (RuntimeException e) {
            throw new RuntimeException("Erro ao excluir prontuário do paciente com id " + patientId + ": " + e.getMessage());
        }
    }
}

