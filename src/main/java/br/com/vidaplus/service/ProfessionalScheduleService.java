package br.com.vidaplus.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.vidaplus.dto.ProfessionalScheduleDto;
import br.com.vidaplus.model.Appointment;
import br.com.vidaplus.model.AppointmentType;
import br.com.vidaplus.model.ProfessionalSchedule;
import br.com.vidaplus.model.User;
import br.com.vidaplus.repository.AppointmentRepository;
import br.com.vidaplus.repository.ProfessionalScheduleRepository;
import br.com.vidaplus.repository.UserRepository;

@Service
public class ProfessionalScheduleService {

    private final ProfessionalScheduleRepository scheduleRepository; 
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserService userService;

    @Autowired
    public ProfessionalScheduleService(ProfessionalScheduleRepository scheduleRepository,
                                       UserRepository userRepository,
                                       AppointmentRepository appointmentRepository,
                                       UserService userService) {
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.userService = userService;
    }

    // Verifica os horários disponíveis e agendados
    // proId: ID do profissional
    @Transactional
    public List<Appointment> getAvailableSlots(Long proId, LocalDate date) {
        // Valida os parâmetros
        if (proId == null || date == null) {
            throw new RuntimeException("ID do profissional e data não podem ser nulos");
        }

        // Busca o profissional
        User healthPro = userRepository.findById(proId).orElse(null);
        if (healthPro == null) {
            throw new RuntimeException("Profissional não encontrado");
        }

        // Busca as consultas agendadas diretamente do AppointmentRepository
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        List<Appointment> bookedAppointments = appointmentRepository.findByHealthProfessionalAndDateTimeBetween(
            healthPro, startOfDay, endOfDay);
        System.out.println("Consultas encontradas para o profissional " + proId + " na data " + date + ". Total de consultas: " + bookedAppointments.size());

        // Cria slots disponíveis (08:00 a 17:00)
        List<Appointment> freeAppointments = new ArrayList<>();
        for (int hour = 8; hour < 18; hour++) {
            LocalDateTime slotTime = date.atTime(hour, 0);
            boolean isAvailable = true;

            // Verifica se o horário está ocupado
            for (Appointment booked : bookedAppointments) {
                if (booked.getDateTime().getHour() == hour) {
                    isAvailable = false;
                    break;
                }
            }

            // Se o slot estiver disponível, adiciona à lista
            if (isAvailable) {
                Appointment freeSlot = new Appointment();
                freeSlot.setHealthProfessional(healthPro);
                freeSlot.setDateTime(slotTime);
                freeSlot.setType(AppointmentType.IN_PERSON);
                
                freeAppointments.add(freeSlot);
            }
        }

        return freeAppointments;
    }

    // Verifica todos os slots (disponíveis e ocupados)
    @Transactional(readOnly = true)
    public ProfessionalScheduleDto getAllSlots(Long proId, LocalDate date) {
        // Valida os parâmetros
        if (proId == null || date == null) {
            throw new RuntimeException("ID do profissional e data não podem ser nulos");
        }

        // Busca o profissional
        User healthPro = userRepository.findById(proId).orElse(null);
        if (healthPro == null) {
            throw new RuntimeException("Profissional não encontrado");
        }

        // Busca as consultas agendadas diretamente do AppointmentRepository
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        List<Appointment> bookedAppointments = appointmentRepository.findByHealthProfessionalAndDateTimeBetween(
            healthPro, startOfDay, endOfDay);
        System.out.println("Consultas encontradas para o profissional " + proId + " na data " + date + ". Total de consultas: " + bookedAppointments.size());

        // Cria slots disponíveis (08:00 a 17:00, já que o último slot começa às 17:00)
        List<Appointment> freeAppointments = new ArrayList<>();
        for (int hour = 8; hour < 18; hour++) {
            LocalDateTime slotTime = date.atTime(hour, 0);
            boolean isAvailable = true;

            // Verifica se o horário está ocupado
            for (Appointment booked : bookedAppointments) {
                if (booked.getDateTime().getHour() == hour) {
                    isAvailable = false;
                    break;
                }
            }

            // Se o slot estiver disponível, adiciona à lista de disponíveis
            if (isAvailable) {
                Appointment freeSlot = new Appointment();
                freeSlot.setHealthProfessional(healthPro);
                freeSlot.setDateTime(slotTime);
                freeSlot.setType(AppointmentType.IN_PERSON);
                
                freeAppointments.add(freeSlot);
            }
        }

        // Cria o DTO de resposta
        ProfessionalScheduleDto response = new ProfessionalScheduleDto();
        response.setHealthProfessionalId(proId);
        response.setDate(date);
        response.setAvailableSlots(freeAppointments);
        response.setBookedSlots(bookedAppointments);

        // Busca a agenda apenas para preencher o ID, se existir
        Optional<ProfessionalSchedule> scheduleOpt = scheduleRepository.findByHealthProfessionalAndDate(healthPro, date);
        if (scheduleOpt.isPresent()) {
            response.setId(scheduleOpt.get().getId());
        }

        return response;
    }

    // Verifica a agenda do usuário TUAL
    @Transactional(readOnly = true)
    public ProfessionalScheduleDto getCurrentUserSchedule(LocalDate date) {
        try {
            // Obtém o usuário autenticado
            User currentUser = userService.getCurrentAuthenticatedUser();

            // Usa o ID do usuário autenticado para buscar a agenda
            Long proId = currentUser.getId();
            return getAllSlots(proId, date);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar a agenda do usuário atual: " + e.getMessage());
        }
    }

    // Busca as consultas agendadas do usuário logado
    @Transactional(readOnly = true)
    public List<Appointment> getMyBookedAppointments() {
        try {
            // Obtém o usuário autenticado
            User currentUser = userService.getCurrentAuthenticatedUser();

            // Busca as consultas onde o usuário autenticado é o profissional de saúde
            List<Appointment> bookedAppointments = appointmentRepository.findByHealthProfessional(currentUser);

            return bookedAppointments;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar as consultas agendadas do usuário atual: " + e.getMessage());
        }
    }
    
}