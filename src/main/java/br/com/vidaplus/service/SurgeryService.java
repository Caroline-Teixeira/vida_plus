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
import br.com.vidaplus.model.Hospitalization;
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
public class SurgeryService {

    private final SurgeryRepository surgeryRepository;
    private final UserRepository userRepository;
    private final MedicalRecordService medicalRecordService;
    private final UserService userService;
    private final AppointmentRepository appointmentRepository;
    private final HospitalizationService hospitalizationService;

    @Autowired
    public SurgeryService(SurgeryRepository surgeryRepository,
                          UserRepository userRepository,
                          MedicalRecordService medicalRecordService,
                          UserService userService,
                          AppointmentRepository appointmentRepository,
                          HospitalizationService hospitalizationService) {
        this.surgeryRepository = surgeryRepository;
        this.userRepository = userRepository;
        this.medicalRecordService = medicalRecordService;
        this.userService = userService;
        this.appointmentRepository = appointmentRepository;
        this.hospitalizationService = hospitalizationService;
    }

    // Lista todas as cirurgias
    public List<Surgery> getAllSurgeries() {
        try {
            System.out.println("Buscando todas as cirurgias...");
            List<Surgery> surgeries = surgeryRepository.findAll();
            System.out.println("Total de cirurgias encontradas: " + surgeries.size());
            return surgeries;
        } catch (Exception e) {
            System.out.println("Erro ao buscar cirurgias: " + e.getMessage());
            throw new RuntimeException("Erro ao acessar o banco de dados: " + e.getMessage());
        }
    }

    // Busca uma cirurgia por ID
    public Optional<Surgery> getSurgeryById(Long id) {
        return surgeryRepository.findById(id);
    }

    // Busca cirurgias por paciente 
    public List<Surgery> getSurgeriesByPatient(Long patientId) {
        User patient = userRepository.findById(patientId).orElse(null);
        if (patient != null) {
            return surgeryRepository.findByPatient(patient);
        } else {
            throw new RuntimeException("Paciente não encontrado: " + patientId);
        }
    }

    // Busca cirurgias por profissional de saúde
    public List<Surgery> getSurgeriesByHealthProfessional(Long healthProfessionalId) {
        User healthProfessional = userRepository.findById(healthProfessionalId).orElse(null);
        if (healthProfessional != null) {
            return surgeryRepository.findByHealthProfessional(healthProfessional);
        } else {
            throw new RuntimeException("Profissional da Saúde não encontrado: " + healthProfessionalId);
        }
    }

    // Busca cirurgias do usuário atual
    public List<Surgery> getSurgeriesByCurrentUser() {
        try {
            User currentUser = userService.getCurrentAuthenticatedUser();
            List<Surgery> surgeriesAsPatient = surgeryRepository.findByPatient(currentUser);
            List<Surgery> surgeriesAsProfessional = surgeryRepository.findByHealthProfessional(currentUser);
            Set<Surgery> allSurgeries = new HashSet<>();
            allSurgeries.addAll(surgeriesAsPatient);
            allSurgeries.addAll(surgeriesAsProfessional);
            return new ArrayList<>(allSurgeries);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar cirurgias: " + e.getMessage());
        }
    }

    // Agendamento de cirurgias = mesma lógica de consultas
    public Surgery scheduleSurgery(Long patientId, Long healthProfessionalId, 
                                  LocalDateTime dateTime, String reason, 
                                  String bed, String observations) {
        User patient = userRepository.findById(patientId).orElse(null);
        if (patient == null) {
            throw new RuntimeException("Paciente não encontrado: " + patientId);
        }

        User healthProfessional = userRepository.findById(healthProfessionalId).orElse(null);
        if (healthProfessional == null) {
            throw new RuntimeException("Profissional da Saúde não encontrado: " + healthProfessionalId);
        }

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
            int bookedHour = booked.getDateTime().getHour();
            if (bookedHour == dateTime.getHour() || bookedHour == dateTime.getHour() + 1) {
                throw new RuntimeException("Horário indisponível, escolha outro horário");
            }
        }

        // Verifica conflitos com cirurgias existentes
        List<Surgery> bookedSurgeries = surgeryRepository.findByHealthProfessionalAndDateTimeBetween(
            healthProfessional, startOfDay, endOfDay);
        for (Surgery existingSurgery : bookedSurgeries) {
            if (existingSurgery.getStatus() != EventStatus.CANCELLED) {
                int existingHour = existingSurgery.getDateTime().getHour();
                int newHour = dateTime.getHour();
                // Verifica se há sobreposição nos intervalos de 2 horas
                if ((newHour >= existingHour && newHour < existingHour + 2) || 
                    (existingHour >= newHour && existingHour < newHour + 2)) {
                    throw new RuntimeException("Horário indisponível. Escolha outro horário");
                }
            }
        }

        MedicalRecord medicalRecord = medicalRecordService.findOrCreateMedicalRecord(patient);

        Surgery surgery = new Surgery();
        surgery.setPatient(patient);
        surgery.setHealthProfessional(healthProfessional);
        surgery.setDateTime(dateTime);
        surgery.setStatus(EventStatus.SCHEDULED);
        surgery.setReason(reason);
        surgery.setBed(bed);
        surgery.setMedicalRecord(medicalRecord);

        Surgery savedSurgery = surgeryRepository.save(surgery);

        // internação pra controle
        hospitalizationService.createHospitalizationForSurgery(savedSurgery);

        return savedSurgery;
    }

    // Atualiza o status da cirurgia
    public Surgery updateSurgeryStatus(Long surgeryId, EventStatus status) {
        try {
            if (surgeryId == null) {
                throw new RuntimeException("ID da cirurgia não pode ser nulo");
            }
            if (status == null) {
                throw new RuntimeException("Status não pode ser nulo");
            }
            Optional<Surgery> surgeryOptional = surgeryRepository.findById(surgeryId);
            if (!surgeryOptional.isPresent()) {
                throw new RuntimeException("Cirurgia não encontrada: " + surgeryId);
            }
            Surgery surgery = surgeryOptional.get();
            surgery.setStatus(status);
            Surgery updatedSurgery = surgeryRepository.save(surgery);
    
            // Atualizar o status da internação associada
            hospitalizationService.updateHospitalizationStatus(updatedSurgery);
    
            return updatedSurgery;
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao atualizar status da cirurgia com ID " + surgeryId + ": " + e.getMessage());
        }
    }

    // Atualiza a cirurgia
   public Surgery updateSurgery(Long id, Long patientId, Long healthProfessionalId, 
                             LocalDateTime dateTime, String reason, 
                             String bed, String observations) {
    try {
        Optional<Surgery> surgeryOptional = surgeryRepository.findById(id);
        if (!surgeryOptional.isPresent()) {
            throw new RuntimeException("Cirurgia não encontrada: " + id);
        }
        Surgery surgery = surgeryOptional.get();

        Optional<User> patientOptional = userRepository.findById(patientId);
        if (!patientOptional.isPresent()) {
            throw new RuntimeException("Paciente não encontrado: " + patientId);
        }
        User patient = patientOptional.get();

        Optional<User> healthProfessionalOptional = userRepository.findById(healthProfessionalId);
        if (!healthProfessionalOptional.isPresent()) {
            throw new RuntimeException("Profissional da Saúde não encontrado: " + healthProfessionalId);
        }
        User healthProfessional = healthProfessionalOptional.get();

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
            int bookedHour = booked.getDateTime().getHour();
            if (bookedHour == dateTime.getHour() || bookedHour == dateTime.getHour() + 1) {
                throw new RuntimeException("Horário indisponível, escolha outro horário");
            }
        }

        // Verifica conflitos com cirurgias existentes
        List<Surgery> bookedSurgeries = surgeryRepository.findByHealthProfessionalAndDateTimeBetween(
            healthProfessional, startOfDay, endOfDay);
        for (Surgery existingSurgery : bookedSurgeries) {
            if (existingSurgery.getId().equals(id)) {
                continue; // Ignora a própria cirurgia sendo atualizada
            }
            if (existingSurgery.getStatus() != EventStatus.CANCELLED) {
                int existingHour = existingSurgery.getDateTime().getHour();
                int newHour = dateTime.getHour();
                // Verifica se há sobreposição nos intervalos de 2 horas
                if ((newHour >= existingHour && newHour < existingHour + 2) || 
                    (existingHour >= newHour && existingHour < newHour + 2)) {
                    throw new RuntimeException("Horário indisponível. Escolha outro horário");
                }
            }
        }

        MedicalRecord medicalRecord = medicalRecordService.findOrCreateMedicalRecord(patient);

        // Atualiza os dados da cirurgia
        surgery.setPatient(patient);
        surgery.setHealthProfessional(healthProfessional);
        surgery.setDateTime(dateTime);
        surgery.setReason(reason);
        surgery.setBed(bed);
        surgery.setMedicalRecord(medicalRecord);

        Surgery savedSurgery = surgeryRepository.save(surgery);

        // Atualiza a internação existente 
        Hospitalization hospitalization = hospitalizationService.findBySurgery(savedSurgery);
        if (hospitalization != null) {
            // Atualiza os dados da internação 
            hospitalizationService.updateHospitalization(hospitalization, bed, savedSurgery.getStatus());
        } else {
            // Caso não exista uma internação, cria uma nova
            hospitalizationService.createHospitalizationForSurgery(savedSurgery);
        }

        return savedSurgery;

    } catch (RuntimeException e) {
        throw new RuntimeException("Erro ao atualizar cirurgia: " + e.getMessage());
    }
}
    // Deleta a cirugia
    public void deleteSurgery(Long id) {
        try {
            if (id == null) {
                throw new RuntimeException("ID da cirurgia não pode ser nulo");
            }
            Optional<Surgery> surgeryOptional = surgeryRepository.findById(id);
            if (!surgeryOptional.isPresent()) {
                throw new RuntimeException("Cirurgia não encontrada: " + id);
            }
            Surgery surgery = surgeryOptional.get();
    
            // Deletar a internação associada à cirurgia
            hospitalizationService.deleteBySurgery(surgery);
    
            // Deleta
            surgeryRepository.deleteById(id);
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao deletar cirurgia com ID " + id + ": " + e.getMessage());
        }
    }
}