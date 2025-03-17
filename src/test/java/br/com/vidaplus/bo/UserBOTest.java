package br.com.vidaplus.bo;


import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.vidaplus.model.Gender;
import br.com.vidaplus.model.Login;
import br.com.vidaplus.model.PermissionState;
import br.com.vidaplus.model.Profile;
import br.com.vidaplus.model.User;



@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserBOTest {

    @Autowired
    private UserBO userBO;
    

    @Test
    @Order(1)
    public void testSave() {
        User user = new User();
        user.setName("Teste");
        user.setCpf("123.456.789-00");
        user.setContact("123456789");
        user.setDateOfBirth(LocalDate.of(2000, 1, 1));
        user.setGender(Gender.MALE);
        user.setProfile(Profile.ADMIN);
        user.setPermissionState(PermissionState.ACTIVE);
        user.setLogin(new Login("teste@email.com", "123456"));

        userBO.save(user);

        assertNotNull(user.getUser_id(), "User ID should be generated after save");
}

    @Test
    @Order(2)
    public void testFindById() {
        User user = userBO.findById(1L);
        assertNotNull(user, "User should be found by ID");
        assertNotNull(user.getLogin(), "Login should not be null");
        assertEquals("teste@email.com", user.getLogin().getEmail(), "Email should match the saved value");
    }

    /*@Test
    @Order(3)
    public void testFindAll() {
        List<User> user = userBO.findAll();
        assertFalse(user.isEmpty(), "User list should not be empty");
        assertTrue(user.stream().anyMatch(u -> u.getUser_id().equals(((User) user).getUser_id())), "Test user should be in the list");
    }

    /*@Test
    @Order(4)
    public void testUpdate() {
        user.setName("Teste 2");
        userBO.update(user);

    }

   /*  @Test
    @Order(5)
    public void testDelete() {
        userBO.delete(user);

        User deletedUser = userBO.findById(testUser.getUser_id());
        assertNull(deletedUser, "User should be deleted");
    }

    @Test
    @Order(6)
    public void testLogin() {
        boolean loginSuccess = testUser.login("teste@email.com", "password");
        assertTrue(loginSuccess, "Login should succeed with correct credentials");
    }

    @Test
    @Order(7)
    public void testLogout() {
        boolean logoutSuccess = testUser.logout();
        assertTrue(logoutSuccess, "Logout should succeed");
    }
}*/
}