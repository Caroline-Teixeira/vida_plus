package br.com.vidaplus.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.vidaplus.dto.ProfessionalScheduleDto;
import br.com.vidaplus.model.Appointment;
import br.com.vidaplus.model.ProfessionalSchedule;
import br.com.vidaplus.model.User;
import br.com.vidaplus.repository.AppointmentRepository;
import br.com.vidaplus.repository.ProfessionalScheduleRepository;
import br.com.vidaplus.repository.UserRepository;

@Service
public class ProfessionalScheduleService {

    private final ProfessionalScheduleRepository scheduleRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProfessionalScheduleService(ProfessionalScheduleRepository scheduleRepository,
                                       AppointmentRepository appointmentRepository,
                                       UserRepository userRepository) {
        this.scheduleRepository = scheduleRepository;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
    }

    // Verifica os horários disponíveis (8 h até 18 h)
    // proId: ID do profissional
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

        // Define o início e fim do dia: 8h e 18 h
        LocalDateTime startDay = date.atTime(8, 0);
        LocalDateTime endDay = date.atTime(18, 0);

        // Busca as consultas do profissional para a data
        List<Appointment> bookedAppointments = appointmentRepository.findByHealthProfessionalAndDateTimeBetween(healthPro, startDay, endDay);

        // Cria slots disponíveis, sendo que último slot começa às 17h)
        List<Appointment> freeAppointments = new ArrayList<>();
        for (int hour = 8; hour < 18; hour++) {
            LocalDateTime slotTime = date.atTime(hour, 0);
            boolean isAvailable = true;

            // Verifica se o horário está ocupado (booked)
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
                freeAppointments.add(freeSlot);
            }
        }

        return freeAppointments;
    }

    // Verifica todos os slots (disponíveis e ocupados) do profissional para uma data (08:00 a 18:00)
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

        // Define o início e fim do dia (08:00 a 18:00 fixo)
        LocalDateTime startDay = date.atTime(8, 0);
        LocalDateTime endDay = date.atTime(18, 0);

        // Busca as consultas do profissional para a data (slots ocupados)
        List<Appointment> bookedAppointments = appointmentRepository.findByHealthProfessionalAndDateTimeBetween(healthPro, startDay, endDay);

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
                freeAppointments.add(freeSlot);
            }
        }

        // Cria o DTO de resposta
        ProfessionalScheduleDto response = new ProfessionalScheduleDto();
        response.setHealthProfessionalId(proId);
        response.setDate(date);
        response.setAvailableSlots(freeAppointments);
        response.setBookedSlots(bookedAppointments);

        return response;
    }

    // Salva a agenda do profissional (apenas para registro)
    public ProfessionalSchedule saveSchedule(Long proId, LocalDate date) {
        // Valida os parâmetros
        if (proId == null || date == null) {
            throw new RuntimeException("Parâmetros não podem ser nulos");
        }

        // Busca o profissional
        User healthPro = userRepository.findById(proId).orElse(null);
        if (healthPro == null) {
            throw new RuntimeException("Profissional não encontrado");
        }

        // Verifica se já existe uma agenda para o profissional na data
        Optional<ProfessionalSchedule> scheduleOpt = scheduleRepository.findByHealthProfessionalAndDate(healthPro, date);
        ProfessionalSchedule schedule;
        if (scheduleOpt.isPresent()) {
            schedule = scheduleOpt.get();
        } else {
            schedule = new ProfessionalSchedule();
            schedule.setHealthProfessional(healthPro);
            schedule.setDate(date);
        }

        // Define os horários padrão (apenas para registro)
        schedule.setStartTime(LocalTime.of(8, 0));
        schedule.setEndTime(LocalTime.of(18, 0));

        return scheduleRepository.save(schedule);
    }


}