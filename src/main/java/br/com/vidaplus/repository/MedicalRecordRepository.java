package br.com.vidaplus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.vidaplus.model.MedicalRecord;

@Repository
public interface MedicalRecordRepository extends JpaRepository <MedicalRecord,Long>{
    

}
