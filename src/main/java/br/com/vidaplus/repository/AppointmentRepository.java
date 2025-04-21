package br.com.vidaplus.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.vidaplus.model.Appointment;
import br.com.vidaplus.model.User;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long>{
        
    List<Appointment> findByPatient(User patient);
    List<Appointment> findByHealthProfessional(User healthProfessional);
    List<Appointment> findByHealthProfessionalAndDateTimeBetween(
            User healthProfessional, LocalDateTime start, LocalDateTime end);
// Para encontrar os agendamentos de um paciente em um intervalo de datas
    List<Appointment> findByPatientAndDateTimeBetween(
            User patient, LocalDateTime start, LocalDateTime end);

}
