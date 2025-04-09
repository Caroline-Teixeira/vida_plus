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

import br.com.vidaplus.dto.AppointmentDto;
import br.com.vidaplus.model.Appointment;
import br.com.vidaplus.model.AppointmentStatus;
import br.com.vidaplus.service.AppointmentService;

@RestController
@RequestMapping("api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Autowired
    public AppointmentController (AppointmentService appointmentService){
        this.appointmentService = appointmentService;

    }

// GET lista de consultas
    @GetMapping
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        try {
            List<Appointment> appointments = appointmentService.getAllAppointments();
            return ResponseEntity.ok(appointments);
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao buscar consultas: " + e.getMessage());
        }
    }

// GET lista de uma consulta
    @GetMapping("/{id}")
    public Appointment getAppointmentById(@PathVariable Long id) {
        return appointmentService.getAppointmentById(id).orElse(null);
    }

// GET consulta por paciente
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByPatient(@PathVariable Long patientId) {
        try {
            List<Appointment> appointments = appointmentService.getAppointmentsByPatient(patientId);
            return ResponseEntity.ok(appointments);
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao buscar consultas do paciente com id " + patientId + ": " + e.getMessage());
        }
    }
    
// GET consulta por profissional
    @GetMapping("/healthProfessional/{healthProfessionalId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByHealthProfessional(@PathVariable Long healthProfessionalId) {
        try {
            List<Appointment> appointments = appointmentService.getAppointmentsByHealthProfessional(healthProfessionalId);
            return ResponseEntity.ok(appointments);
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao buscar consultas do profissional com id " + healthProfessionalId + ": " + e.getMessage());
        }
    }

// GET consulta do usuário atual
    @GetMapping("/current")
    public List<Appointment> getCurrentUserAppointments() {
        return appointmentService.getAppointmentsByCurrentUser();
    }

    // POST agendar consulta
    @PostMapping
    public ResponseEntity<Appointment> scheduleAppointment(@RequestBody AppointmentDto appointmentDto) {
        try {
            Appointment appointment = appointmentService.scheduleAppointment(
                appointmentDto.getPatientId(),
                appointmentDto.getHealthProfessionalId(),
                appointmentDto.getDateTime(),
                appointmentDto.getType(),
                appointmentDto.getReason(),
                appointmentDto.getObservations()
            );
            //if (appointment != null) {
                return ResponseEntity.ok(appointment);
            //} else {
                //throw new RuntimeException("Erro ao agendar consulta: acesso negado ou dados inválidos");
            //}
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao agendar consulta: " + e.getMessage());
        }
    }
    
    // PUT atualizar Status da consulta
    @PutMapping("/{id}/status")
    public ResponseEntity<Appointment> updateAppointmentStatus(
            @PathVariable Long id, @RequestBody AppointmentStatus status) {
        try {
            Appointment appointment = appointmentService.updateAppointmentStatus(id, status);
            return ResponseEntity.ok(appointment);
           
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao atualizar status da consulta com id " + id + ": " + e.getMessage());
        }
    }
    
    // PUT atualizar consulta
    @PutMapping("/{id}")
    public ResponseEntity<Appointment> updateAppointment(
            @PathVariable Long id, @RequestBody AppointmentDto appointmentDto) {
        try {
            Appointment appointment = appointmentService.updateAppointment(
                id,
                appointmentDto.getPatientId(),
                appointmentDto.getHealthProfessionalId(),
                appointmentDto.getDateTime(),
                appointmentDto.getType(),
                appointmentDto.getReason(),
                appointmentDto.getObservations()
            );
            
            return ResponseEntity.ok(appointment);
            
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao atualizar consulta com id " + id + ": " + e.getMessage());
        }
    }
    
    // DELETE pra cancelar consultas
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAppointment(@PathVariable Long id) {
        try {
            // Verifica se a consulta existe antes de tentar deletar
            Optional<Appointment> appointmentBefore = appointmentService.getAppointmentById(id);
            if (!appointmentBefore.isPresent()) {
                throw new RuntimeException("Consulta não encontrada: " + id);
            }

            // Tenta deletar
            appointmentService.deleteAppointment(id);

            // Verifica se a consulta ainda existe
            Optional<Appointment> appointmentAfter = appointmentService.getAppointmentById(id);
            if (appointmentAfter.isPresent()) {
                throw new RuntimeException("Erro ao deletar consulta: acesso negado: " + id);
            }

            return ResponseEntity.ok("Consulta deletada com sucesso");
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao deletar consulta com id " + id + ": " + e.getMessage());
        }
    }
    
   
}
