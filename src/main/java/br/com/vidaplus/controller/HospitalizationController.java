package br.com.vidaplus.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.vidaplus.dto.HospitalizationDto;
import br.com.vidaplus.service.HospitalizationService;

@RestController
@RequestMapping("api/hospitalizations")
public class HospitalizationController {

    private final HospitalizationService hospitalizationService;

    @Autowired
    public HospitalizationController(HospitalizationService hospitalizationService) {
        this.hospitalizationService = hospitalizationService;
    }

    // GET Listar internações ativas
    @GetMapping("/active")
    public ResponseEntity<List<HospitalizationDto>> getActiveHospitalizations() {
        try {
            List<HospitalizationDto> hospitalizations = hospitalizationService.getActiveHospitalizations();
            return ResponseEntity.ok(hospitalizations);
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao buscar internações ativas: " + e.getMessage());
        }
    }

    // GET Listar leitos disponíveis
    @GetMapping("/available-beds")
    public ResponseEntity<List<String>> getAvailableBeds() {
        try {
            List<String> availableBeds = hospitalizationService.getAvailableBeds();
            return ResponseEntity.ok(availableBeds);
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao buscar leitos disponíveis: " + e.getMessage());
        }
    }
}