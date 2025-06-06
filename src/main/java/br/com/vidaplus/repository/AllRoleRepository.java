package br.com.vidaplus.repository;



import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.vidaplus.model.AllRole;
import br.com.vidaplus.model.Profile;

@Repository
public interface AllRoleRepository extends JpaRepository <AllRole, Long>{
    Optional<AllRole> findByName(Profile name);
}
