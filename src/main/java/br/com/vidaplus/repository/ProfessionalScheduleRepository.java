package br.com.vidaplus.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.vidaplus.model.ProfessionalSchedule;
import br.com.vidaplus.model.User;

@Repository
public interface ProfessionalScheduleRepository extends JpaRepository<ProfessionalSchedule, Long>{

    // Para encontrar a agenda de um profissional de saúde específico
    Optional<ProfessionalSchedule> findByHealthProfessionalAndDate(User healthProfessional, LocalDate date);

}
