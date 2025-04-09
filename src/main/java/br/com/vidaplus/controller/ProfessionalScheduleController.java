package br.com.vidaplus.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.vidaplus.dto.ProfessionalScheduleDto;
import br.com.vidaplus.model.Appointment;
import br.com.vidaplus.model.ProfessionalSchedule;
import br.com.vidaplus.service.ProfessionalScheduleService;

@RestController
@RequestMapping("/api/schedule")
public class ProfessionalScheduleController {

    private final ProfessionalScheduleService scheduleService;

    @Autowired
    public ProfessionalScheduleController(ProfessionalScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    // Verifica todos os slots (disponíveis e ocupados) (GET)
    @GetMapping("/all-slots/{professionalId}/{date}")
    public ResponseEntity<ProfessionalScheduleDto> getAllSlots(
            @PathVariable("professionalId") Long proId,
            @PathVariable("date") String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            ProfessionalScheduleDto response = scheduleService.getAllSlots(proId, parsedDate);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao buscar slots disponíveis e ocupados: " + e.getMessage());
        }
    }

    // Verifica os horários disponíveis
    @PostMapping("/available-slots")
    public ResponseEntity<List<Appointment>> getAvailableSlots(@RequestBody Map<String, String> request) {
        Long proId = Long.valueOf(request.get("professionalId"));
        LocalDate date = LocalDate.parse(request.get("date"));
        List<Appointment> freeAppointments = scheduleService.getAvailableSlots(proId, date);
        return ResponseEntity.ok(freeAppointments);
    }

    // Cria ou atualiza a agenda do profissional
    @PostMapping("/save")
    public ResponseEntity<ProfessionalScheduleDto> saveSchedule(@RequestBody ProfessionalScheduleDto dto) {
        ProfessionalSchedule schedule = scheduleService.saveSchedule(
            dto.getHealthProfessionalId(),
            dto.getDate()
        );

        ProfessionalScheduleDto response = new ProfessionalScheduleDto();
        response.setId(schedule.getId());
        response.setHealthProfessionalId(schedule.getHealthProfessional().getId());
        response.setDate(schedule.getDate());
        response.setAvailableSlots(scheduleService.getAvailableSlots(schedule.getHealthProfessional().getId(), schedule.getDate()));

        return ResponseEntity.ok(response);
    }
}