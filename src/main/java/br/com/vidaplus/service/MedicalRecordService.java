package br.com.vidaplus.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.vidaplus.model.Appointment;
import br.com.vidaplus.model.MedicalRecord;
import br.com.vidaplus.model.Surgery;
import br.com.vidaplus.model.User;
import br.com.vidaplus.repository.MedicalRecordRepository;

@Service
@Transactional
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final UserService userService;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Autowired
    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository, UserService userService) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.userService = userService;
    }

    // Busca prontuário por paciente
    public MedicalRecord findOrCreateMedicalRecord(User patient) {
        if (patient == null) {
            throw new RuntimeException("Paciente não encontrado.");
        }

        Optional<MedicalRecord> medicalRecordOptional = medicalRecordRepository.findByPatient(patient);
        if (medicalRecordOptional.isPresent()) {
            return medicalRecordOptional.get();
        }

        // Se não existir, cria um novo prontuário
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setPatient(patient);
        medicalRecord.setRecordDate(LocalDateTime.now());
        medicalRecord.setObservations(""); // Inicializa como vazio
        return medicalRecordRepository.save(medicalRecord);
    }

    // Busca prontuário do usuário logado
    public MedicalRecord findMedicalRecordByCurrentUser() {
        try {
            //o usuário autenticado
            User currentUser = userService.getCurrentAuthenticatedUser();
    
            Optional<MedicalRecord> medicalRecordOptional = medicalRecordRepository.findByPatient(currentUser);
            if (!medicalRecordOptional.isPresent()) {
                throw new RuntimeException("Prontuário não encontrado para o usuário atual.");
            }
            return medicalRecordOptional.get();
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao buscar prontuário do usuário atual: " + e.getMessage());
        }
    }

    // Adiciona observações da consulta
    public void addObservations(MedicalRecord medicalRecord, Appointment appointment, String observations) {
        
        if (observations == null) {
            throw new IllegalArgumentException("Observações não podem ser nulas.");
        }

        // Ignora se observations for vazio
        if (observations.trim().isEmpty()) {
            return;
        }

        // Formatação com appointmentId, data e texto
        String dateTime = appointment.getDateTime().format(DATE_TIME_FORMATTER);
        String formattedObservation = appointment.getId() + " | " + dateTime + " | " + observations;

        // Adiciona a nova observação às existentes
        String currentObservations = medicalRecord.getObservations();
        if (currentObservations == null || currentObservations.trim().isEmpty()) {
            medicalRecord.setObservations(formattedObservation);
        } else {
            medicalRecord.setObservations(currentObservations + " || " + formattedObservation);
        }

        // Salva
        medicalRecordRepository.save(medicalRecord);
    }

    // Adiciona observações da cirurgia = mesmo método de addObservations
    public void addSurgeryObservations(MedicalRecord medicalRecord, Surgery surgery, String observations) {
        if (observations == null) {
            throw new IllegalArgumentException("Observações não podem ser nulas.");
        }

        if (observations.trim().isEmpty()) {
            return;
        }

        String dateTime = surgery.getDateTime().format(DATE_TIME_FORMATTER);
        String formattedObservation = "Surgery-" + surgery.getId() + " | " + dateTime + " | " + observations;

        String currentObservations = medicalRecord.getObservations();
        if (currentObservations == null || currentObservations.trim().isEmpty()) {
            medicalRecord.setObservations(formattedObservation);
        } else {
            medicalRecord.setObservations(currentObservations + " || " + formattedObservation);
        }

        medicalRecordRepository.save(medicalRecord);
    }

    // Atualiza as observações de consulta de um prontuário
    @SuppressWarnings("UnnecessaryTemporaryOnConversionFromString")
    public void updateObservations(MedicalRecord medicalRecord, List<Long> appointmentIds, String newObservations, Appointment appointment) {
        // Verifica se as observações são válidas
        if (newObservations == null || newObservations.trim().isEmpty()) {
            throw new IllegalArgumentException("Observações não podem ser vazias.");
        }

        // Verifica se há appointmentIds válidos
        if (appointmentIds == null || appointmentIds.isEmpty()) {
            throw new IllegalArgumentException("Nenhum ID de consulta fornecido.");
        }

        // Data atual
        String dateTime = appointment.getDateTime().format(DATE_TIME_FORMATTER);

        // Obtém as observações existentes
        String currentObservations = medicalRecord.getObservations();
        List<String> updatedEntries = new ArrayList<>();

        if (currentObservations == null || currentObservations.trim().isEmpty()) {
            // Se não houver observações existentes, cria uma nova entrada para cada appointmentId
            for (Long appointmentId : appointmentIds) {
                String formattedObservation = appointmentId + " | " + dateTime + " | " + newObservations;
                updatedEntries.add(formattedObservation);
            }
        } else {
            // Divide as observações existentes em entradas
            String[] observationEntries = currentObservations.split(" \\|\\| ");
            boolean updated = false;

            for (String entry : observationEntries) {
                String[] parts = entry.split(" \\| ", 3);
                if (parts.length < 3) {
                    continue; // Ignora entradas mal formatadas
                }

                Long entryAppointmentId; // ID da consulta na entrada

                try {
                    entryAppointmentId = Long.parseLong(parts[0].trim()); 
                } catch (NumberFormatException e) {
                    updatedEntries.add(entry); // Mantém entradas mal formatadas
                    continue;
                }

                // Verifica se a entrada corresponde a um dos appointmentIds fornecidos
                if (appointmentIds.contains(entryAppointmentId)) {
                    // Atualiza a observação
                    String formattedObservation = entryAppointmentId + " | " + dateTime + " | " + newObservations;
                    updatedEntries.add(formattedObservation);
                    updated = true;
                } else {
                    // Mantém a observação existente
                    updatedEntries.add(entry);
                }
            }

            // Se não encontrou uma entrada, adiciona uma nova
            if (!updated) {
                for (Long appointmentId : appointmentIds) {
                    String formattedObservation = appointmentId + " | " + dateTime + " | " + newObservations;
                    updatedEntries.add(formattedObservation);
                }
            }
        }

        // Atualiza as observações do prontuário com as entradas atualizadas
        medicalRecord.setObservations(String.join(" || ", updatedEntries));
        medicalRecordRepository.save(medicalRecord);
    }


    // Atualiza observações de cirurgias = mesmo método de updateObservations
    @SuppressWarnings("UnnecessaryTemporaryOnConversionFromString")
    public void updateSurgeryObservations(MedicalRecord medicalRecord, List<Long> surgeryIds, String newObservations, Surgery surgery) {
        if (newObservations == null || newObservations.trim().isEmpty()) {
            throw new IllegalArgumentException("Observações não podem ser vazias.");
        }

        if (surgeryIds == null || surgeryIds.isEmpty()) {
            throw new IllegalArgumentException("Nenhum ID de cirurgia fornecido.");
        }

        String dateTime = surgery.getDateTime().format(DATE_TIME_FORMATTER);
        String currentObservations = medicalRecord.getObservations();
        List<String> updatedEntries = new ArrayList<>();

        if (currentObservations == null || currentObservations.trim().isEmpty()) {
            for (Long surgeryId : surgeryIds) {
                String formattedObservation = "Surgery-" + surgeryId + " | " + dateTime + " | " + newObservations;
                updatedEntries.add(formattedObservation);
            }
        } else {
            String[] observationEntries = currentObservations.split(" \\|\\| ");
            boolean updated = false;

            for (String entry : observationEntries) {
                String[] parts = entry.split(" \\| ", 3);
                if (parts.length < 3) {
                    continue;
                }

                String idPart = parts[0].trim();
                Long entryId;
                try {
                    entryId = Long.parseLong(idPart.replace("Surgery-", ""));
                } catch (NumberFormatException e) {
                    updatedEntries.add(entry);
                    continue;
                }

                if (surgeryIds.contains(entryId) && idPart.startsWith("Surgery-")) {
                    String formattedObservation = "Surgery-" + entryId + " | " + dateTime + " | " + newObservations;
                    updatedEntries.add(formattedObservation);
                    updated = true;
                } else {
                    updatedEntries.add(entry);
                }
            }

            if (!updated) {
                for (Long surgeryId : surgeryIds) {
                    String formattedObservation = "Surgery-" + surgeryId + " | " + dateTime + " | " + newObservations;
                    updatedEntries.add(formattedObservation);
                }
            }
        }

        medicalRecord.setObservations(String.join(" || ", updatedEntries));
        medicalRecordRepository.save(medicalRecord);
    }

    // Remove as observações por intervalo de datas
    public int removeObservationEntries(User patient, LocalDateTime startDate, LocalDateTime endDate) {
        // Busca o prontuário do paciente
        Optional<MedicalRecord> medicalRecordOptional = medicalRecordRepository.findByPatient(patient);
        if (!medicalRecordOptional.isPresent()) {
            throw new RuntimeException("Prontuário não encontrado para o paciente.");
        }

        MedicalRecord medicalRecord = medicalRecordOptional.get();
        String observations = medicalRecord.getObservations();

        if (observations == null || observations.trim().isEmpty()) {
            return 0;
        }

        // Divide as observações usando o separador " || "
        String[] observationEntries = observations.split(" \\|\\| ");
        List<String> keptEntries = new ArrayList<>();
        int removedEntries = 0;

        // Itera sobre as entradas de observação
        for (String entry : observationEntries) {
            // Extrai a data da entrada
            String[] parts = entry.split(" \\| ", 3);
            if (parts.length < 3) {
                continue; // Ignora entradas mal formatadas
            }

            String dateString = parts[1].trim(); // A data está na segunda posição agora
            try {
                LocalDateTime observationDate = LocalDateTime.parse(dateString, DATE_TIME_FORMATTER);
                // Verifica se a data está dentro do intervalo
                if (observationDate.isBefore(startDate) || observationDate.isAfter(endDate)) {
                    keptEntries.add(entry); // Mantém a entrada se estiver fora do intervalo
                } else {
                    removedEntries++; // Conta a entrada removida
                }
            } catch (Exception e) {
                // Se a data não puder ser parseada, mantém a entrada
                keptEntries.add(entry);
            }
        }

        // Atualiza as observações do prontuário com as entradas mantidas
        medicalRecord.setObservations(String.join(" || ", keptEntries));
        medicalRecordRepository.save(medicalRecord);

        return removedEntries;
    }

    // Remove observações de cirurgias = mesmo método de removeObservationEntries
    @SuppressWarnings("UnnecessaryTemporaryOnConversionFromString")
    public int removeSurgeryObservationEntries(User patient, List<Long> surgeryIds) {
        Optional<MedicalRecord> medicalRecordOptional = medicalRecordRepository.findByPatient(patient);
        if (!medicalRecordOptional.isPresent()) {
            throw new RuntimeException("Prontuário não encontrado para o paciente.");
        }

        MedicalRecord medicalRecord = medicalRecordOptional.get();
        String observations = medicalRecord.getObservations();

        if (observations == null || observations.trim().isEmpty()) {
            return 0;
        }

        String[] observationEntries = observations.split(" \\|\\| ");
        List<String> keptEntries = new ArrayList<>();
        int removedEntries = 0;

        for (String entry : observationEntries) {
            String[] parts = entry.split(" \\| ", 3);
            if (parts.length < 3) {
                continue;
            }

            String idPart = parts[0].trim();
            if (!idPart.startsWith("Surgery-")) {
                keptEntries.add(entry);
                continue;
            }

            Long entryId;
            try {
                entryId = Long.parseLong(idPart.replace("Surgery-", ""));
            } catch (NumberFormatException e) {
                keptEntries.add(entry);
                continue;
            }

            if (surgeryIds.contains(entryId)) {
                removedEntries++;
            } else {
                keptEntries.add(entry);
            }
        }

        medicalRecord.setObservations(String.join(" || ", keptEntries));
        medicalRecordRepository.save(medicalRecord);

        return removedEntries;
    }

    // Deleta prontuário
    public void deleteMedicalRecord(User patient) {
        // Busca o prontuário do paciente
        Optional<MedicalRecord> medicalRecordOptional = medicalRecordRepository.findByPatient(patient);

        // Remove o prontuário se existir
        if (medicalRecordOptional.isPresent()) {
            medicalRecordRepository.delete(medicalRecordOptional.get());
        } else {
            throw new RuntimeException("Prontuário não encontrado para o paciente.");
        }
    }
}