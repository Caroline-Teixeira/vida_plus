package br.com.vidaplus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.vidaplus.model.EventStatus;
import br.com.vidaplus.model.Hospitalization;
import br.com.vidaplus.model.Surgery;


@Repository
public interface HospitalizationRepository extends JpaRepository<Hospitalization, Long> {

    List<Hospitalization> findByStatusIn(List<EventStatus> statuses);

    List<Hospitalization> findByBedAndStatusIn(String bed, List<EventStatus> statuses);

    Hospitalization findBySurgery(Surgery surgery);
}