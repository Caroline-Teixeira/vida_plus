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

        // Formata a observação com informações da consulta
        String formattedObservation = "Consulta em " + appointment.getDateTime() + " com " + 
                                    appointment.getHealthProfessional().getName() + ": " + observations;

        // Adiciona a nova observação às existentes
        String currentObservations = medicalRecord.getObservations();
        medicalRecord.setObservations((currentObservations + "\n" + formattedObservation).replace("\n", " "));


        // Salva o MedicalRecord atualizado
        medicalRecordRepository.save(medicalRecord);
        }


     // Método para atualizar observações de um prontuário
     public void updateObservations(MedicalRecord medicalRecord, List<Long> appointmentIds, String newObservations) {
            // Verifica se as observações são válidas
            if (newObservations == null || newObservations.trim().isEmpty()) {
                throw new IllegalArgumentException("Observações não podem ser vazias.");
            }

            // Adiciona novas observações ao prontuário
            String currentObservations = medicalRecord.getObservations();
            String updatedObservations;

            if (currentObservations.isEmpty()) {
                updatedObservations = newObservations;
            } 
            else {
                updatedObservations = (currentObservations.trim() + "\n" + newObservations.trim()).replace("\n---\n", "\n");
            }

            medicalRecord.setObservations(updatedObservations);
            medicalRecordRepository.save(medicalRecord);
            }

            public int removeObservationEntries(User patient, LocalDateTime startDate, LocalDateTime endDate) {
                // Busca o prontuário do paciente
                Optional<MedicalRecord> medicalRecordOptional = medicalRecordRepository.findByPatient(patient);
                if (!medicalRecordOptional.isPresent()) {
                    throw new RuntimeException("Prontuário não encontrado para o paciente.");
                }
            
                MedicalRecord medicalRecord = medicalRecordOptional.get();
                LocalDateTime recordDate = medicalRecord.getRecordDate();
            
                // Verifica se o recordDate está dentro do intervalo especificado
                if (recordDate.isBefore(startDate) || recordDate.isAfter(endDate)) {
                    return 0; // Nenhuma entrada removida, pois o prontuário está fora do intervalo
                }
            
                // Divide as observações usando o separador correto " -- "
                String[] observationEntries = medicalRecord.getObservations().split(" -- ");
                List<String> keptEntries = new ArrayList<>();
                int removedEntries = 0;
            
                // Itera sobre as entradas de observação
                for (String entry : observationEntries) {
                    // Remove apenas a entrada "Nenhum problema encontrado."
                    if (entry.trim().equals("Nenhum problema encontrado.")) {
                        removedEntries++;
                    } else {
                        keptEntries.add(entry.trim());
                    }
                }
            
                // Atualiza as observações do prontuário com o separador correto
                medicalRecord.setObservations(String.join(" -- ", keptEntries));
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
        } 
        else {
            throw new RuntimeException("Prontuário não encontrado para o paciente.");
        }
        }

        /*private LocalDateTime extractDataEntries(String entry) {
            // Procura onde começa "Consulta em " e soma 12 pra pular isso
            int startIndex = entry.indexOf("Consulta em ") + 12;
            
            // Se não achar "Consulta em ", dá erro
            if (startIndex == -1 + 12) { // -1 + 12 = 11 significa que não achou
                throw new RuntimeException("Formato de data inválido");
            }
            
            // Pega o texto depois de "Consulta em "
            String remainingText = entry.substring(startIndex);
            
            // Procura onde termina a data (antes de " com")
            int endIndex = remainingText.indexOf(" com");
            if (endIndex == -1) { // Se não achar " com", usa o fim do texto
                endIndex = remainingText.length();
            }
            
            // Pega só a data e tira espaços extras
            String dateString = remainingText.substring(0, endIndex).trim();
            
            // Tenta transformar o texto da data em LocalDateTime
            try {
                return LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (Exception e) {
                throw new RuntimeException("Não deu pra transformar a data");
            }
        }*/
}