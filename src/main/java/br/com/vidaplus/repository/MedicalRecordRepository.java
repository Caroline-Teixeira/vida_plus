package br.com.vidaplus.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.vidaplus.model.MedicalRecord;
import br.com.vidaplus.model.User;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    // Para encontrar o prontuário médico de um paciente específico
    Optional<MedicalRecord> findByPatient(User patient);
}