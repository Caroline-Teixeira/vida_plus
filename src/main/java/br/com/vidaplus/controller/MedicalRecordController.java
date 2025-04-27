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
import br.com.vidaplus.model.Surgery;
import br.com.vidaplus.model.User;
import br.com.vidaplus.repository.AppointmentRepository;
import br.com.vidaplus.repository.SurgeryRepository;
import br.com.vidaplus.repository.UserRepository;
import br.com.vidaplus.service.MedicalRecordService;


@RestController
@RequestMapping("api/medical-records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final SurgeryRepository surgeryRepository;

    @Autowired
    public MedicalRecordController(MedicalRecordService medicalRecordService,
                                   UserRepository userRepository,
                                   AppointmentRepository appointmentRepository,
                                   SurgeryRepository surgeryRepository) {
        this.medicalRecordService = medicalRecordService;
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.surgeryRepository = surgeryRepository;
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

    // GET prontuário do usuário atual
    @GetMapping("/current")
    public ResponseEntity<MedicalRecord> getCurrentUserMedicalRecord() {
        try {
            MedicalRecord medicalRecord = medicalRecordService.findMedicalRecordByCurrentUser();
            return ResponseEntity.ok(medicalRecord);
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao buscar prontuário do usuário atual: " + e.getMessage());
        }
    }

    // POST adiciona observações de consultas ao prontuário
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

    // POST adiciona observações de cirurgias
    @PostMapping("/{patientId}/add-surgery-observations")
    public ResponseEntity<String> addSurgeryObservations(
            @PathVariable Long patientId,
            @RequestBody MedicalRecordDto medicalRecordDto) {
        
        try {
            if (medicalRecordDto.getSurgeryIds() == null || medicalRecordDto.getSurgeryIds().isEmpty()) {
                throw new RuntimeException("Nenhum ID de cirurgia fornecido.");
            }

            Optional<User> patientOptional = userRepository.findById(patientId);
            if (!patientOptional.isPresent()) {
                throw new RuntimeException("Paciente não encontrado: " + patientId);
            }
            User patient = patientOptional.get();

            MedicalRecord medicalRecord = medicalRecordService.findOrCreateMedicalRecord(patient);

            for (Long surgeryId : medicalRecordDto.getSurgeryIds()) {
                Optional<Surgery> surgeryOptional = surgeryRepository.findById(surgeryId);
                if (!surgeryOptional.isPresent()) {
                    throw new RuntimeException("Cirurgia não encontrada: " + surgeryId);
                }
                Surgery surgery = surgeryOptional.get();

                medicalRecordService.addSurgeryObservations(
                    medicalRecord, 
                    surgery, 
                    medicalRecordDto.getObservations()
                );
            }

            return ResponseEntity.ok("Observações de cirurgia adicionadas com sucesso ao prontuário do paciente: " + patientId);
        } 
        catch (RuntimeException e) {
            throw new RuntimeException("Erro ao adicionar observações de cirurgia ao prontuário do paciente com id " + patientId + ": " + e.getMessage());
        }
    }

    // PUT para atualizar observações das consultas
    @PutMapping("/{patientId}/update-observations")
    public ResponseEntity<String> updateObservations(
            @PathVariable Long patientId,
            @RequestBody MedicalRecordDto medicalRecordDto) {
        
        try {
            if (medicalRecordDto.getAppointmentIds() == null || medicalRecordDto.getAppointmentIds().isEmpty()) {
                throw new RuntimeException("Nenhum ID de consulta fornecido.");
            }

            Optional<User> patientOptional = userRepository.findById(patientId);
            if (!patientOptional.isPresent()) {
                throw new RuntimeException("Paciente não encontrado: " + patientId);
            }
            User patient = patientOptional.get();

            MedicalRecord medicalRecord = medicalRecordService.findOrCreateMedicalRecord(patient);

            // Busca a primeira consulta para obter a data
            Long appointmentId = medicalRecordDto.getAppointmentIds().get(0);
            Optional<Appointment> appointmentOptional = appointmentRepository.findById(appointmentId);
            if (!appointmentOptional.isPresent()) {
                throw new RuntimeException("Consulta não encontrada: " + appointmentId);
            }
            Appointment appointment = appointmentOptional.get();

            medicalRecordService.updateObservations(
                medicalRecord, 
                medicalRecordDto.getAppointmentIds(), 
                medicalRecordDto.getObservations(),
                appointment
            );

            return ResponseEntity.ok("Observações atualizadas com sucesso para o paciente: " + patientId);
        } 
        catch (RuntimeException e) {
            throw new RuntimeException("Erro ao atualizar observações do prontuário do paciente com id " + patientId + ": " + e.getMessage());
        }
    }

    // PUT para atualizar observações de cirurgias
    @PutMapping("/{patientId}/update-surgery-observations")
    public ResponseEntity<String> updateSurgeryObservations(
            @PathVariable Long patientId,
            @RequestBody MedicalRecordDto medicalRecordDto) {
        
        try {
            if (medicalRecordDto.getSurgeryIds() == null || medicalRecordDto.getSurgeryIds().isEmpty()) {
                throw new RuntimeException("Nenhum ID de cirurgia fornecido.");
            }

            Optional<User> patientOptional = userRepository.findById(patientId);
            if (!patientOptional.isPresent()) {
                throw new RuntimeException("Paciente não encontrado: " + patientId);
            }
            User patient = patientOptional.get();

            MedicalRecord medicalRecord = medicalRecordService.findOrCreateMedicalRecord(patient);

            Long surgeryId = medicalRecordDto.getSurgeryIds().get(0);
            Optional<Surgery> surgeryOptional = surgeryRepository.findById(surgeryId);
            if (!surgeryOptional.isPresent()) {
                throw new RuntimeException("Cirurgia não encontrada: " + surgeryId);
            }
            Surgery surgery = surgeryOptional.get();

            medicalRecordService.updateSurgeryObservations(
                medicalRecord, 
                medicalRecordDto.getSurgeryIds(), 
                medicalRecordDto.getObservations(),
                surgery
            );

            return ResponseEntity.ok("Observações de cirurgia atualizadas com sucesso para o paciente: " + patientId);
        } 
        catch (RuntimeException e) {
            throw new RuntimeException("Erro ao atualizar observações de cirurgia do prontuário do paciente com id " + patientId + ": " + e.getMessage());
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

    // DELETE para remover observações de cirurgias
    @DeleteMapping("/{patientId}/remove-surgery-observations")
public ResponseEntity<String> removeSurgeryObservationEntries(
        @PathVariable Long patientId,
        @RequestBody Map<String, LocalDateTime> dateRange) {
    try {
        Optional<User> patientOptional = userRepository.findById(patientId);
        if (!patientOptional.isPresent()) {
            throw new RuntimeException("Paciente não encontrado: " + patientId);
        }
        User patient = patientOptional.get();

        int removedEntries = medicalRecordService.removeSurgeryObservationEntries(
            patient, 
            dateRange.get("startDate"), 
            dateRange.get("endDate")
        );

        return ResponseEntity.ok("Removidas " + removedEntries + " entradas de observações de cirurgias.");
    } 
    catch (RuntimeException e) {
        throw new RuntimeException("Erro ao remover entradas de observações de cirurgias para o paciente com id " + patientId + ": " + e.getMessage());
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