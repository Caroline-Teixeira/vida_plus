package br.com.vidaplus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.vidaplus.model.AppointmentStatus;
import br.com.vidaplus.model.Hospitalization;
import br.com.vidaplus.model.Surgery;


@Repository
public interface HospitalizationRepository extends JpaRepository<Hospitalization, Long> {

    List<Hospitalization> findByStatusIn(List<AppointmentStatus> statuses);

    List<Hospitalization> findByBedAndStatusIn(String bed, List<AppointmentStatus> statuses);

    Hospitalization findBySurgery(Surgery surgery);
}