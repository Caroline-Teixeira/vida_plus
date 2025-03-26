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
import br.com.vidaplus.model.User;
import br.com.vidaplus.repository.MedicalRecordRepository;

@Service
@Transactional
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Autowired
    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
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

        // Se não existir, cria um novo MedicalRecord
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setPatient(patient);
        medicalRecord.setRecordDate(LocalDateTime.now());
        medicalRecord.setObservations(""); // Inicializa como vazio
        return medicalRecordRepository.save(medicalRecord);
    }

    // Adiciona observações da consulta
    public void addObservations(MedicalRecord medicalRecord, Appointment appointment, String observations) {
        // Lança exceção se observations for null
        if (observations == null) {
            throw new IllegalArgumentException("Observações não podem ser nulas.");
        }

        // Ignora se observations for vazio
        if (observations.trim().isEmpty()) {
            return;
        }

        // Formata a observação com data e texto
        String dateTime = appointment.getDateTime().format(DATE_TIME_FORMATTER);
        String formattedObservation = dateTime + " | " + observations;

        // Adiciona a nova observação às existentes
        String currentObservations = medicalRecord.getObservations();
        if (currentObservations == null || currentObservations.trim().isEmpty()) {
            medicalRecord.setObservations(formattedObservation);
        } else {
            medicalRecord.setObservations(currentObservations + " || " + formattedObservation);
        }

        // Salva o MedicalRecord atualizado
        medicalRecordRepository.save(medicalRecord);
    }

    // Método para atualizar observações de um prontuário
    public void updateObservations(MedicalRecord medicalRecord, List<Long> appointmentIds, String newObservations) {
        // Verifica se as observações são válidas
        if (newObservations == null || newObservations.trim().isEmpty()) {
            throw new IllegalArgumentException("Observações não podem ser vazias.");
        }

        // Usa a data atual para a nova observação
        String dateTime = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        String formattedObservation = dateTime + " | " + newObservations;

        // Adiciona novas observações ao prontuário
        String currentObservations = medicalRecord.getObservations();
        if (currentObservations == null || currentObservations.trim().isEmpty()) {
            medicalRecord.setObservations(formattedObservation);
        } else {
            medicalRecord.setObservations(currentObservations + " || " + formattedObservation);
        }

        medicalRecordRepository.save(medicalRecord);
    }

    // Método para remover observações por intervalo de datas
    public int removeObservationEntries(User patient, LocalDateTime startDate, LocalDateTime endDate) {
        // Busca o prontuário do paciente
        Optional<MedicalRecord> medicalRecordOptional = medicalRecordRepository.findByPatient(patient);
        if (!medicalRecordOptional.isPresent()) {
            throw new RuntimeException("Prontuário não encontrado para o paciente.");
        }

        MedicalRecord medicalRecord = medicalRecordOptional.get();
        String observations = medicalRecord.getObservations();

        // Se não houver observações, retorna 0
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
            String[] parts = entry.split(" \\| ");
            if (parts.length < 2) {
                continue; // Ignora entradas mal formatadas
            }

            String dateString = parts[0].trim();
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

    // Método para deletar prontuário
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