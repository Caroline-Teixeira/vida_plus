package br.com.vidaplus.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import br.com.vidaplus.model.AllRole;
import br.com.vidaplus.model.PermissionState;
import br.com.vidaplus.model.Profile;
import br.com.vidaplus.repository.AllRoleRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AllRoleRepository allRoleRepository;

    @Override
    public void run(String... args) throws Exception {
        // Só insere se a tabela estiver vazia
        if (allRoleRepository.count() == 0) {
            Arrays.asList(Profile.values()).forEach(profile -> {
                AllRole role = new AllRole();
                role.setName(profile);
                role.setPermissionValue(PermissionState.ACTIVE); // Valor padrão
                allRoleRepository.save(role);
            });
            System.out.println("Roles inicializados com sucesso!");
        }
    }
}