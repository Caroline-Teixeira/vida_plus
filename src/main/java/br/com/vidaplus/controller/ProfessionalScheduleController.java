package br.com.vidaplus.controller;

import java.time.LocalDate;
import java.util.List;

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
import br.com.vidaplus.service.ProfessionalScheduleService;

@RestController
@RequestMapping("/api/schedule")
public class ProfessionalScheduleController {

    private final ProfessionalScheduleService scheduleService;

    @Autowired
    public ProfessionalScheduleController(ProfessionalScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    //GET Verifica todos os slots (disponíveis e ocupados) 
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

    //GET Verifica os slots ocupados do usuário atual
    @GetMapping("/current/{date}")
    public ResponseEntity<ProfessionalScheduleDto> getCurrentUserSchedule(@PathVariable("date") String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            ProfessionalScheduleDto response = scheduleService.getCurrentUserSchedule(parsedDate);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar a agenda do usuário atual: " + e.getMessage());
        }
    }
    

    // POST Verifica os horários disponíveis
    @PostMapping("/available-slots")
    public ResponseEntity<ProfessionalScheduleDto> getAvailableSlots(@RequestBody ProfessionalScheduleDto request) {
        try {
            Long proId = request.getHealthProfessionalId();
            LocalDate date = request.getDate();
            List<Appointment> freeAppointments = scheduleService.getAvailableSlots(proId, date);

            ProfessionalScheduleDto response = new ProfessionalScheduleDto();
            response.setHealthProfessionalId(proId);
            response.setDate(date);
            response.setAvailableSlots(freeAppointments);
            response.setBookedSlots(null); // Não buscamos os slots ocupados neste endpoint
            

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar slots disponíveis: " + e.getMessage());
        }
    }

    
}