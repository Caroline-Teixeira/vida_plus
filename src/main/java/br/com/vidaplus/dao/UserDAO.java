package br.com.vidaplus.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import br.com.vidaplus.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class UserDAO implements CRUD<User, Long> {

    @PersistenceContext
    private EntityManager em;


    @Override
    public User findById(Long id) {
        return em.find(User.class, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<User> findAll() {
        Query query = em.createQuery("SELECT u FROM User u");
        return (List<User>) query.getResultList();
    }

    @Override
    public void save(User user) {
        em.persist(user);
        em.flush(); // Garante que a gravação imediata no banco de dados
        em.clear(); // Limpa cache para evitar dados obsoletos
    }

    @Override
    public void update(User user) {
        em.merge(user);

    }

    @Override
    public void delete(User user) {
        em.remove(user);

    }

}
