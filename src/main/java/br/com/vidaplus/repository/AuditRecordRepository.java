package br.com.vidaplus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.vidaplus.model.AuditRecord;

@Repository
public interface AuditRecordRepository extends JpaRepository<AuditRecord, Long> {

    List<AuditRecord> findByUsername(String username);

}
