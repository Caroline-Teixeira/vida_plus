package br.com.vidaplus.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.vidaplus.model.AllRole;
import br.com.vidaplus.model.Appointment;
import br.com.vidaplus.model.AppointmentStatus;
import br.com.vidaplus.model.AppointmentType;
import br.com.vidaplus.model.Profile;
import br.com.vidaplus.model.User;
import br.com.vidaplus.repository.AppointmentRepository;
import br.com.vidaplus.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class AppointmentService {
    
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,
                             UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }
    
    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }
    
    public List<Appointment> getAppointmentsByPatient(Long patientId){
        User patient = userRepository.findById(patientId).orElse(null);
        if (patient != null){
            return appointmentRepository.findByPatient(patient);
        }
        else{
            throw new RuntimeException("Paciente não encontrado: " + patientId);
        }
    }

    public List<Appointment> getAppointmentsByHealthProfessional(Long healthProfessionalId) {
        User healthProfessional = userRepository.findById(healthProfessionalId).orElse(null);
        if (healthProfessional != null) {
            return appointmentRepository.findByHealthProfessional(healthProfessional);
        } else {
            throw new RuntimeException("Profissional da Saúde não encontrado: " + healthProfessionalId);
        }
    }

        
    public Appointment scheduleAppointment(Long patientId, Long healthProfessionalId, 
                                        LocalDateTime dateTime, AppointmentType type, 
                                        String reason) {
        // Busca o paciente
        User patient = userRepository.findById(patientId).orElse(null);
        if (patient == null) {
            throw new RuntimeException("Paciente não encontrado: " + patientId);
        }

        // Busca o profissional de saúde
        User healthProfessional = userRepository.findById(healthProfessionalId).orElse(null);
        if (healthProfessional == null) {
            throw new RuntimeException("Profissional da Saúde não encontrado: " + healthProfessionalId);
        }

        // Verifica se o usuário tem o papel de profissional de saúde
        boolean isHealthProfessional = false;
        for (AllRole role : healthProfessional.getRoles()) {
            if (role.getName() == Profile.HEALTH_PROFESSIONAL) {
                isHealthProfessional = true;
                break;
            }
        }

        if (!isHealthProfessional) {
            throw new RuntimeException("Usuário não é Profissional da Saúde.");
        }

        // Criação e salvamento do Appointment
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setHealthProfessional(healthProfessional);
        appointment.setDateTime(dateTime);
        appointment.setType(type);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setReason(reason);
        
        
        return appointmentRepository.save(appointment);
    }


    public Appointment updateAppointmentStatus(Long appointmentId, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
        if (appointment == null) {
            throw new RuntimeException("Consulta não encontrada: " + appointmentId);
        }

        appointment.setStatus(status);
        return appointmentRepository.save(appointment);
    }

    public Appointment updateAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

   
    public void cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
        if (appointment == null) {
            throw new RuntimeException("Consulta não encontrada: " + appointmentId);
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    public List<Appointment> getAvailableSlots(Long professionalId, String date) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAvailableSlots'");
    }

    public Appointment updateAppointment(Long id, Long patientId, Long healthProfessionalId, LocalDateTime dateTime,
            AppointmentType type, String reason) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateAppointment'");
    }

}
