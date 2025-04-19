package br.com.vidaplus.service;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.vidaplus.dto.HospitalizationDto;
import br.com.vidaplus.model.AppointmentStatus;
import br.com.vidaplus.model.Hospitalization;
import br.com.vidaplus.model.Surgery;
import br.com.vidaplus.repository.HospitalizationRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class HospitalizationService {

    private final HospitalizationRepository hospitalizationRepository;

    // Leitos disponíveis
    private static final List<String> AVAILABLE_BEDS = Arrays.asList(
        "LEITO-001", "LEITO-002", "LEITO-003", "LEITO-004", "LEITO-005",
        "LEITO-006", "LEITO-007", "LEITO-008", "LEITO-009", "LEITO-010"
    );

    // Status considerados ativos para internações
    private static final List<AppointmentStatus> ACTIVE_STATUSES = Arrays.asList(
        AppointmentStatus.SCHEDULED,
        AppointmentStatus.CONFIRMED,
        AppointmentStatus.IN_PROGRESS
    );

    @Autowired
    public HospitalizationService(HospitalizationRepository hospitalizationRepository) {
        this.hospitalizationRepository = hospitalizationRepository;
    }

    // Criar internação automaticamente ao agendar cirurgia
    public Hospitalization createHospitalizationForSurgery(Surgery surgery) {
        String bed = surgery.getBed();
        boolean isValidBed = false;
        for (String availableBed : AVAILABLE_BEDS) {
            if (availableBed.equals(bed)) {
                isValidBed = true;
                break;
            }
        }
        if (!isValidBed) {
            throw new RuntimeException("Leito inválido: " + bed);
        }

        // Verificar se o leito está ocupado
        List<Hospitalization> activeHospitalizations = hospitalizationRepository.findByBedAndStatusIn(bed, ACTIVE_STATUSES);
        if (!activeHospitalizations.isEmpty()) {
            throw new RuntimeException("Leito ocupado: " + bed);
        }

        Hospitalization hospitalization = new Hospitalization();
        hospitalization.setSurgery(surgery);
        hospitalization.setBed(bed);
        hospitalization.setStatus(surgery.getStatus());

        return hospitalizationRepository.save(hospitalization);
    }

    // Atualizar status da internação
    public void updateHospitalizationStatus(Surgery surgery) {
        List<Hospitalization> hospitalizations = hospitalizationRepository.findAll();
        Hospitalization targetHospitalization = null;
        for (Hospitalization hospitalization : hospitalizations) {
            if (hospitalization.getSurgery().getId().equals(surgery.getId())) {
                targetHospitalization = hospitalization;
                break;
            }
        }
        if (targetHospitalization != null) {
            targetHospitalization.setStatus(surgery.getStatus());
            hospitalizationRepository.save(targetHospitalization);
        }
    }

    // Listar internações ativas
    public List<HospitalizationDto> getActiveHospitalizations() {
        List<Hospitalization> hospitalizations = hospitalizationRepository.findByStatusIn(ACTIVE_STATUSES);
        List<HospitalizationDto> dtos = new ArrayList<>();
        for (Hospitalization hospitalization : hospitalizations) {
            HospitalizationDto dto = new HospitalizationDto();
            dto.setId(hospitalization.getId());
            dto.setSurgeryId(hospitalization.getSurgery().getId());
            dto.setBed(hospitalization.getBed());
            dto.setStatus(hospitalization.getStatus());
            dtos.add(dto);
        }
        return dtos;
    }

    // Listar leitos disponíveis
    public List<String> getAvailableBeds() {
        List<String> availableBeds = new ArrayList<String>();
        for (String bed : AVAILABLE_BEDS) {
            availableBeds.add(bed);
        }
        List<Hospitalization> activeHospitalizations = hospitalizationRepository.findByStatusIn(ACTIVE_STATUSES);
        for (Hospitalization hospitalization : activeHospitalizations) {
            String bed = hospitalization.getBed();
            for (int i = 0; i < availableBeds.size(); i++) {
                if (availableBeds.get(i).equals(bed)) {
                    availableBeds.remove(i);
                    break;
                }
            }
        }
        return availableBeds;
    }

    // Para deletar internação associada a uma cirurgia
    public void deleteBySurgery(Surgery surgery) {
        Hospitalization hospitalization = hospitalizationRepository.findBySurgery(surgery);
        if (hospitalization != null) {
            hospitalizationRepository.delete(hospitalization);
        }
    }
}