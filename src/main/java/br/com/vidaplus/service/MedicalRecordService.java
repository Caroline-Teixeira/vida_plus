package br.com.vidaplus.service;

import java.time.LocalDateTime;
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
    public void addObservationsToMedicalRecord(MedicalRecord medicalRecord, Appointment appointment, String observations) {
        // Lança exceção se observations for null
        if (observations == null) {
            throw new IllegalArgumentException("Observações não podem ser nulas.");
        }

        // Ignora se observations for vazio
        if (observations.trim().isEmpty()) {
            return;
        }

        // Formata a observação com informações da consulta
        String formattedObservation = "Consulta em " + appointment.getDateTime() + " com " + 
                                    appointment.getHealthProfessional().getName() + ": " + observations;

        // Adiciona a nova observação às existentes
        String currentObservations = medicalRecord.getObservations();
        medicalRecord.setObservations(currentObservations + "\n" + formattedObservation);

        // Salva o MedicalRecord atualizado
        medicalRecordRepository.save(medicalRecord);
        }

}