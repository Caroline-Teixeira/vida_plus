package br.com.vidaplus.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.vidaplus.model.Surgery;
import br.com.vidaplus.model.User;

@Repository
public interface SurgeryRepository extends JpaRepository<Surgery, Long> {
        
    List<Surgery> findByPatient(User patient);
    List<Surgery> findByHealthProfessional(User healthProfessional);
    List<Surgery> findByHealthProfessionalAndDateTimeBetween(
            User healthProfessional, LocalDateTime start, LocalDateTime end);
            
        // Para encontrar os agendamentos de um paciente em um intervalo de datas
    List<Surgery> findByPatientAndDateTimeBetween(
            User patient, LocalDateTime start, LocalDateTime end);
}