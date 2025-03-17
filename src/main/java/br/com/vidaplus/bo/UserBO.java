package br.com.vidaplus.bo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.vidaplus.dao.CRUD;
import br.com.vidaplus.dao.UserDAO;
import br.com.vidaplus.model.User;
import jakarta.transaction.Transactional;

@Transactional
@Service
public class UserBO implements CRUD<User, Long> {

    @Autowired
    private UserDAO userDAO;


    @Override
    public User findById(Long id) {
        return userDAO.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userDAO.findAll();
    }

    @Override
    public void save(User user) {
        userDAO.save(user);
    }

    @Override
    public void update(User user) {
        userDAO.update(user);
    }

    @Override
    public void delete(User user) {
        userDAO.delete(user);
    }


}
