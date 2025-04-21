package br.com.vidaplus.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.vidaplus.model.AllRole;
import br.com.vidaplus.model.Appointment;
import br.com.vidaplus.model.EventStatus;
import br.com.vidaplus.model.AppointmentType;
import br.com.vidaplus.model.MedicalRecord;
import br.com.vidaplus.model.Profile;
import br.com.vidaplus.model.Surgery;
import br.com.vidaplus.model.User;
import br.com.vidaplus.repository.AppointmentRepository;
import br.com.vidaplus.repository.SurgeryRepository;
import br.com.vidaplus.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class AppointmentService {
    
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final MedicalRecordService medicalRecordService;
    private final UserService userService;
    private final SurgeryRepository surgeryRepository;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,
                             UserRepository userRepository,
                             MedicalRecordService medicalRecordService,
                             UserService userService,
                             SurgeryRepository surgeryRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.medicalRecordService = medicalRecordService;
        this.userService = userService;
        this.surgeryRepository = surgeryRepository;
    }
    
    // lista todas as consultas
    public List<Appointment> getAllAppointments() {
        try {
            System.out.println("Buscando todos os agendamentos...");
            List<Appointment> appointments = appointmentRepository.findAll();
            System.out.println("Total de agendamentos encontrados: " + appointments.size());
            return appointments;
        } catch (Exception e) {
            System.out.println("Erro ao buscar agendamentos: " + e.getMessage());
            throw new RuntimeException("Erro ao acessar o banco de dados: " + e.getMessage());
        }
    }
    
    // Busca consulta por ID
    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }
    
    // Busca consulta por paciente
    public List<Appointment> getAppointmentsByPatient(Long patientId){
        User patient = userRepository.findById(patientId).orElse(null);
        if (patient != null){
            return appointmentRepository.findByPatient(patient);
        }
        else{
            throw new RuntimeException("Paciente não encontrado: " + patientId);
        }
    }

    // Busca consulta por profissional
    public List<Appointment> getAppointmentsByHealthProfessional(Long healthProfessionalId) {
        User healthProfessional = userRepository.findById(healthProfessionalId).orElse(null);
        if (healthProfessional != null) {
            return appointmentRepository.findByHealthProfessional(healthProfessional);
        } else {
            throw new RuntimeException("Profissional da Saúde não encontrado: " + healthProfessionalId);
        }
    }

    // Busca a(s) consulta(s) do usuário atual
    public List<Appointment> getAppointmentsByCurrentUser() {
    try {
        // Obtém o usuário autenticado
        User currentUser = userService.getCurrentAuthenticatedUser();

        // Busca consultas do paciente e do profissional de saúde
        List<Appointment> appointmentsAsPatient = appointmentRepository.findByPatient(currentUser);
        List<Appointment> appointmentsAsProfessional = appointmentRepository.findByHealthProfessional(currentUser);

        // Set para evitar duplicatas
        Set<Appointment> allAppointments = new HashSet<>();
        allAppointments.addAll(appointmentsAsPatient);
        allAppointments.addAll(appointmentsAsProfessional);

        return new ArrayList<>(allAppointments);
    } catch (Exception e) {
        throw new RuntimeException("Erro ao buscar consultas: " + e.getMessage());
    }
}

    // Marca a consulta
    public Appointment scheduleAppointment(Long patientId, Long healthProfessionalId, 
                                        LocalDateTime dateTime, AppointmentType type, 
                                        String reason, String observations) {
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

        // Verifica conflitos com consultas existentes
        LocalDateTime startOfDay = dateTime.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);
        List<Appointment> bookedAppointments = appointmentRepository.findByHealthProfessionalAndDateTimeBetween(
            healthProfessional, startOfDay, endOfDay);
        for (Appointment booked : bookedAppointments) {
            if (booked.getDateTime().getHour() == dateTime.getHour()) {
                throw new RuntimeException("Horário indisponível, escolha outro horário");
            }
        }

        // Verifica conflitos com cirurgias existentes
        List<Surgery> bookedSurgeries = surgeryRepository.findByHealthProfessionalAndDateTimeBetween(
            healthProfessional, startOfDay, endOfDay);
        for (Surgery surgery : bookedSurgeries) {
            int surgeryHour = surgery.getDateTime().getHour();
            if (dateTime.getHour() == surgeryHour || dateTime.getHour() == surgeryHour + 1) {
                throw new RuntimeException("Horário indisponível, escolha outro horário");
            }
        }

        
        // Busca ou cria o Prontuário para o paciente
        MedicalRecord medicalRecord = medicalRecordService.findOrCreateMedicalRecord(patient);
        

        // Criação e salvamento da consulta
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setHealthProfessional(healthProfessional);
        appointment.setDateTime(dateTime);
        appointment.setType(type);
        appointment.setStatus(EventStatus.SCHEDULED);
        appointment.setReason(reason);
        appointment.setMedicalRecord(medicalRecord); // Linka o prontuário com a consulta
        
        return appointmentRepository.save(appointment);
    }

    // Atualiza o status da consulta
    public Appointment updateAppointmentStatus(Long appointmentId, EventStatus status) {
        try {
            // Valida o ID
            if (appointmentId == null) {
                throw new RuntimeException("ID da consulta não pode ser nulo");
            }
    
            // Valida o status
            if (status == null) {
                throw new RuntimeException("Status não pode ser nulo");
            }
    
            // Busca a consulta
            Optional<Appointment> appointmentOptional = appointmentRepository.findById(appointmentId);
            if (!appointmentOptional.isPresent()) {
                throw new RuntimeException("Consulta não encontrada: " + appointmentId);
            }
            Appointment appointment = appointmentOptional.get();
    
            // Atualiza o status
            appointment.setStatus(status);
    
            // Salva a consulta atualizada
            return appointmentRepository.save(appointment);
    
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao atualizar status da consulta com ID " + appointmentId + ": " + e.getMessage());
        }
    }

    // Atualiza a consulta
    public Appointment updateAppointment(Long id, Long patientId, Long healthProfessionalId, 
                                   LocalDateTime dateTime, AppointmentType type, 
                                   String reason, String observations) {
    try {
        // Busca a consulta pelo ID
        Optional<Appointment> appointmentOptional = appointmentRepository.findById(id);
        if (!appointmentOptional.isPresent()) {
            throw new RuntimeException("Consulta não encontrada: " + id);
        }
        Appointment appointment = appointmentOptional.get();

        // Busca o paciente
        Optional<User> patientOptional = userRepository.findById(patientId);
        if (!patientOptional.isPresent()) {
            throw new RuntimeException("Paciente não encontrado: " + patientId);
        }
        User patient = patientOptional.get();

        // Busca o profissional de saúde
        Optional<User> healthProfessionalOptional = userRepository.findById(healthProfessionalId);
        if (!healthProfessionalOptional.isPresent()) {
            throw new RuntimeException("Profissional da Saúde não encontrado: " + healthProfessionalId);
        }
        User healthProfessional = healthProfessionalOptional.get();

        // Verifica se o usuário é um profissional de saúde
        boolean isHealthProfessional = false; 
        for (AllRole role : healthProfessional.getRoles()) {
            
            Profile roleName = role.getName(); // Pegamos o nome do papel (como PATIENT, admin, etc.)
            if (roleName.equals(Profile.HEALTH_PROFESSIONAL)) {
                isHealthProfessional = true; 
                break; 
            }
        }

        if (isHealthProfessional == false) {
            throw new RuntimeException("Usuário não é Profissional da Saúde.");
        }

        // Verifica conflitos com consultas existentes
        LocalDateTime startOfDay = dateTime.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);
        List<Appointment> bookedAppointments = appointmentRepository.findByHealthProfessionalAndDateTimeBetween(
            healthProfessional, startOfDay, endOfDay);
        for (Appointment booked : bookedAppointments) {
            if (!booked.getId().equals(id) && booked.getDateTime().getHour() == dateTime.getHour()) {
                throw new RuntimeException("Horário indisponível, escolha outro horário");
            }
        }

        // Verifica conflitos com cirurgias existentes
        List<Surgery> bookedSurgeries = surgeryRepository.findByHealthProfessionalAndDateTimeBetween(
            healthProfessional, startOfDay, endOfDay);
        for (Surgery surgery : bookedSurgeries) {
            int surgeryHour = surgery.getDateTime().getHour();
            if (dateTime.getHour() == surgeryHour || dateTime.getHour() == surgeryHour + 1) {
                throw new RuntimeException("Horário indisponível, escolha outro horário");
            }
        }


        // Busca ou cria o Prontuário para o paciente
        MedicalRecord medicalRecord = medicalRecordService.findOrCreateMedicalRecord(patient);

        // Atualiza os campos da consulta
        appointment.setPatient(patient);
        appointment.setHealthProfessional(healthProfessional);
        appointment.setDateTime(dateTime);
        appointment.setType(type);
        appointment.setReason(reason);
        appointment.setMedicalRecord(medicalRecord); // Linka

        // Salva a consulta atualizada
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return updatedAppointment;

    } catch (RuntimeException e) {
        throw new RuntimeException("Erro ao atualizar consulta: " + e.getMessage());
    }
}

    // deleta a consulta
    public void deleteAppointment(Long id) {
        try {
            // Valida o ID
            if (id == null) {
                throw new RuntimeException("ID da consulta não pode ser nulo");
            }
    
            // Verifica se a consulta existe
            Optional<Appointment> appointmentOptional = appointmentRepository.findById(id);
            if (!appointmentOptional.isPresent()) {
                throw new RuntimeException("Consulta não encontrada: " + id);
            }
    
            // Deleta a consulta
            appointmentRepository.deleteById(id);
    
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao deletar consulta com ID " + id + ": " + e.getMessage());
        }
    }
    
}
