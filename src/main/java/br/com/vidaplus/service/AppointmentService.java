package br.com.vidaplus.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.vidaplus.model.AllRole;
import br.com.vidaplus.model.Appointment;
import br.com.vidaplus.model.AppointmentStatus;
import br.com.vidaplus.model.AppointmentType;
import br.com.vidaplus.model.MedicalRecord;
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
    private final MedicalRecordService medicalRecordService;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,
                             UserRepository userRepository,
                             MedicalRecordService medicalRecordService) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.medicalRecordService = medicalRecordService;
    }
    // lista todas as consultas
    public List<Appointment> getAllAppointments() {
        
            System.out.println("Buscando todos os agendamentos...");
            List<Appointment> appointments = new ArrayList<>();
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            
            if (auth == null) {
                System.out.println("Nenhuma autenticação encontrada. Consultas encontradas: " + appointments);
                return appointments;
            }

            System.out.println("Autoridades no contexto de segurança: " + auth.getAuthorities()); // log de autoridades
    
        boolean hasPermission = false;
        for (GrantedAuthority authority : auth.getAuthorities()) {
            System.out.println("Verificando autoridade: " + authority.getAuthority());
            if (authority.getAuthority().equals("ADMIN") || authority.getAuthority().equals("ATTENDANT")) {
                hasPermission = true;
                break;
            }
        }

        System.out.println("Usuário tem permissão (ADMIN ou ATTENDANT): " + hasPermission);

        if (hasPermission) {
            appointments = appointmentRepository.findAll();
            System.out.println("Resultado de findAll: " + appointments);
        }

        System.out.println("Consultas encontradas: " + appointments);
        return appointments;  
    }

    // Busca consulta por ID
    public Optional<Appointment> getAppointmentById(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Appointment> appointment = Optional.empty();
        
        if (auth == null) {
            System.out.println("Nenhuma autenticação encontrada. Consulta encontrada: " + appointment);
            return appointment;
        }

        boolean hasPermission = false;
        for (GrantedAuthority authority : auth.getAuthorities()) {
            System.out.println("Verificando autoridade: " + authority.getAuthority());
            if (authority.getAuthority().equals("ADMIN") || 
                authority.getAuthority().equals("ATTENDANT") || 
                authority.getAuthority().equals("HEALTH_PROFISSIONAL")) {
                hasPermission = true;
                break;
            }
        }

        System.out.println("Usuário tem permissão (ADMIN, ATTENDANT ou HEALTH_PROFISSIONAL): " + hasPermission);

        if (hasPermission) {
            appointment = appointmentRepository.findById(id);
            System.out.println("Resultado de findById: " + appointment);
        }

        System.out.println("Consulta encontrada: " + appointment);
        return appointment;
    }
    
    // consulta por paciente
    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        // Busca o paciente
        User patient = userRepository.findById(patientId).orElse(null);
        if (patient == null) {
            throw new RuntimeException("Paciente não encontrado: " + patientId);
        }

        List<Appointment> appointments = new ArrayList<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Se não houver autenticação, retorna as consultas (endpoint é público)
        if (auth == null) {
            System.out.println("Nenhuma autenticação encontrada. Consultas encontradas: " + appointments);
            appointments = appointmentRepository.findByPatient(patient);
            System.out.println("Consultas encontradas: " + appointments);
            return appointments;
        }

        // Se houver autenticação, verifica permissões
        System.out.println("Autoridades no contexto de segurança: " + auth.getAuthorities());
        User authenticatedUser = (User) auth.getPrincipal();
        boolean hasPermission = false;

        // Verifica se o usuário autenticado é o próprio paciente
        if (authenticatedUser.getId().equals(patientId)) {
            hasPermission = true;
        } else {
            // Verifica se o usuário tem um papel que permite acesso
            for (GrantedAuthority authority : auth.getAuthorities()) {
                System.out.println("Verificando autoridade: " + authority.getAuthority());
                if (authority.getAuthority().equals("ADMIN") || 
                    authority.getAuthority().equals("ATTENDANT") || 
                    authority.getAuthority().equals("HEALTH_PROFISSIONAL")) {
                    hasPermission = true;
                    break;
                }
            }
        }

        System.out.println("Usuário tem permissão: " + hasPermission);

        if (hasPermission) {
            appointments = appointmentRepository.findByPatient(patient);
            System.out.println("Consultas encontradas: " + appointments);
        }

        return appointments;
    }

    // consulta por profissional
    public List<Appointment> getAppointmentsByHealthProfessional(Long healthProfessionalId) {
        List<Appointment> appointments = new ArrayList<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null) {
            System.out.println("Nenhuma autenticação encontrada. Consultas encontradas: " + appointments);
            return appointments;
        }

        boolean hasPermission = false;
        for (GrantedAuthority authority : auth.getAuthorities()) {
            System.out.println("Verificando autoridade: " + authority.getAuthority());
            if (authority.getAuthority().equals("ADMIN") || 
                authority.getAuthority().equals("ATTENDANT") || 
                authority.getAuthority().equals("HEALTH_PROFISSIONAL")) {
                hasPermission = true;
                break;
            }
        }

        System.out.println("Usuário tem permissão (ADMIN, ATTENDANT ou HEALTH_PROFISSIONAL): " + hasPermission);

        if (hasPermission) {
            User healthProfessional = userRepository.findById(healthProfessionalId).orElse(null);
            if (healthProfessional == null) {
                throw new RuntimeException("Profissional da Saúde não encontrado: " + healthProfessionalId);
            }
            appointments = appointmentRepository.findByHealthProfessional(healthProfessional);
            System.out.println("Consultas encontradas: " + appointments);
        }

        return appointments;
    }

    // Marcar consulta
    public Appointment scheduleAppointment(Long patientId, Long healthProfessionalId, 
                                        LocalDateTime dateTime, AppointmentType type, 
                                        String reason, String observations) {
        // autenticação
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Appointment appointment = null;
        
        if (auth == null) {
            System.out.println("Nenhuma autenticação encontrada. Consulta criada: " + appointment);
            return appointment;
        }

        boolean hasPermission = false;
        for (GrantedAuthority authority : auth.getAuthorities()) {
            System.out.println("Verificando autoridade: " + authority.getAuthority());
            if (authority.getAuthority().equals("ADMIN") || 
                authority.getAuthority().equals("ATTENDANT")) {
                hasPermission = true;
                break;
            }
        }

        System.out.println("Usuário tem permissão (ADMIN ou ATTENDANT): " + hasPermission);
        
        // Busca o paciente
        if (hasPermission) {
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
            
            // Busca ou cria o Prontuário para o paciente
            MedicalRecord medicalRecord = medicalRecordService.findOrCreateMedicalRecord(patient);
            

            // Criação e salvamento da consulta
            appointment = new Appointment();
            appointment.setPatient(patient);
            appointment.setHealthProfessional(healthProfessional);
            appointment.setDateTime(dateTime);
            appointment.setType(type);
            appointment.setStatus(AppointmentStatus.SCHEDULED);
            appointment.setReason(reason);
            appointment.setMedicalRecord(medicalRecord); // Linka o prontuário com a consulta
            
            appointment = appointmentRepository.save(appointment);
            System.out.println("Consulta criada: " + appointment);
            
    }
    return appointment;
}

    //Atualizar status da consulta
    public Appointment updateAppointmentStatus(Long appointmentId, AppointmentStatus status) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Appointment appointment = null;
        
        if (auth == null) {
            System.out.println("Nenhuma autenticação encontrada. Consulta atualizada: " + appointment);
            return appointment;
        }

        boolean hasPermission = false;
        for (GrantedAuthority authority : auth.getAuthorities()) {
            System.out.println("Verificando autoridade: " + authority.getAuthority());
            if (authority.getAuthority().equals("ADMIN") || 
                authority.getAuthority().equals("ATTENDANT")) {
                hasPermission = true;
                break;
            }
        }

        System.out.println("Usuário tem permissão (ADMIN ou ATTENDANT): " + hasPermission);

        if (hasPermission) {
            if (appointmentId == null) {
                throw new RuntimeException("ID da consulta não pode ser nulo");
            }
        
            if (status == null) {
                throw new RuntimeException("Status não pode ser nulo");
            }
        
            Optional<Appointment> appointmentOptional = appointmentRepository.findById(appointmentId);
            if (!appointmentOptional.isPresent()) {
                throw new RuntimeException("Consulta não encontrada: " + appointmentId);
            }
            appointment = appointmentOptional.get();
        
            appointment.setStatus(status);
        
            appointment = appointmentRepository.save(appointment);
            System.out.println("Consulta atualizada: " + appointment);
        }

        return appointment;
    }
    
    // atualiza consulta
    public Appointment updateAppointment(Long id, Long patientId, Long healthProfessionalId, 
                                   LocalDateTime dateTime, AppointmentType type, 
                                   String reason, String observations) {
        
        // autenticação
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Appointment appointment = null;
        
        if (auth == null) {
            System.out.println("Nenhuma autenticação encontrada. Consulta atualizada: " + appointment);
            return appointment;
        }

        boolean hasPermission = false;
        for (GrantedAuthority authority : auth.getAuthorities()) {
            System.out.println("Verificando autoridade: " + authority.getAuthority());
            if (authority.getAuthority().equals("ADMIN") || 
                authority.getAuthority().equals("ATTENDANT")) {
                hasPermission = true;
                break;
            }
        }

        System.out.println("Usuário tem permissão (ADMIN ou ATTENDANT): " + hasPermission);

        
        // Busca a consulta pelo ID
        if (hasPermission) {
            Optional<Appointment> appointmentOptional = appointmentRepository.findById(id);
            if (!appointmentOptional.isPresent()) {
                throw new RuntimeException("Consulta não encontrada: " + id);
            }
            appointment = appointmentOptional.get();

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

            // Se não for um profissional de saúde, mostramos um erro
            if (isHealthProfessional == false) {
                throw new RuntimeException("Usuário não é Profissional da Saúde.");
            }

            // Busca ou cria o MedicalRecord para o paciente usando o MedicalRecordService
            MedicalRecord medicalRecord = medicalRecordService.findOrCreateMedicalRecord(patient);

            // Atualiza os campos da consulta
            appointment.setPatient(patient);
            appointment.setHealthProfessional(healthProfessional);
            appointment.setDateTime(dateTime);
            appointment.setType(type);
            appointment.setReason(reason);
            appointment.setMedicalRecord(medicalRecord); // Linka

            // Salva a consulta atualizada no banco
            appointment = appointmentRepository.save(appointment);
            System.out.println("Consulta atualizada: " + appointment);
        }
        return appointment;
}

    // deleta a consulta
    public void deleteAppointment(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean deleted = false;
        
        if (auth == null) {
            System.out.println("Nenhuma autenticação encontrada. Consulta deletada: " + deleted);
            return;
        }

        boolean hasPermission = false;
        for (GrantedAuthority authority : auth.getAuthorities()) {
            System.out.println("Verificando autoridade: " + authority.getAuthority());
            if (authority.getAuthority().equals("ADMIN") || 
                authority.getAuthority().equals("ATTENDANT")) {
                hasPermission = true;
                break;
            }
        }

        System.out.println("Usuário tem permissão (ADMIN ou ATTENDANT): " + hasPermission);

        if (hasPermission) {
            if (id == null) {
                throw new RuntimeException("ID da consulta não pode ser nulo");
            }
        
            Optional<Appointment> appointmentOptional = appointmentRepository.findById(id);
            if (!appointmentOptional.isPresent()) {
                throw new RuntimeException("Consulta não encontrada: " + id);
            }
        
            appointmentRepository.deleteById(id);
            deleted = true;
            System.out.println("Consulta deletada: " + deleted);
        }
    }
    

    public List<Appointment> getAvailableSlots(Long professionalId, String date) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAvailableSlots'");
    }

    

}
