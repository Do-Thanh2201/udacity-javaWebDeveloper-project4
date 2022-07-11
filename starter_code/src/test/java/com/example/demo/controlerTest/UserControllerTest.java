package com.example.demo.controlerTest;

import com.example.demo.TestUtils;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.junit.jupiter.api.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private PasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "encoder", encoder);
    }

    @Test
    public void create_user_successfully() {
        when(encoder.encode("password")).thenReturn("hashedPassword");
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("password");
        request.setConfirmPassword("password");
        ResponseEntity<User> response = (ResponseEntity<User>) userController.createUser(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        Assertions.assertNotNull(user);
        Assertions.assertEquals(0, user.getId());
        Assertions.assertEquals("test", user.getUsername());
        Assertions.assertEquals("hashedPassword", user.getPassword());
    }

    @Test
    public void create_user_password_not_match_confirmPass() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("password_not_match_confirmPass");
        request.setPassword("password");
        request.setConfirmPassword("different_password");
        ResponseEntity<?> response = userController.createUser(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(400, response.getStatusCodeValue());
    }
    @Test
    public void create_user_password_invalid_length() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("passwordInvalidLength");
        request.setPassword("t123");
        request.setConfirmPassword("t123");
        ResponseEntity<?> response = userController.createUser(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(400, response.getStatusCodeValue());
    }
    @Test
    public void create_user_already_username() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("thanhdv20");
        request.setPassword("t1234567");
        request.setConfirmPassword("t1234567");
        ResponseEntity<?> response = userController.createUser(request);
        // Create Successfully
        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        // Create again with already username
        CreateUserRequest requestAgain = new CreateUserRequest();
        request.setUsername("thanhdv20");
        request.setPassword("t123456789");
        request.setConfirmPassword("t123456789");
        ResponseEntity<?> responseAgain = userController.createUser(requestAgain);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(400, responseAgain.getStatusCodeValue());
    }
    @Test
    public void test_get_invalid_username() {
        when(userRepository.findByUsername("invalid_username")).thenReturn(null);
        ResponseEntity<User> user = userController.findByUserName("invalid_username");
        Assertions.assertEquals(404, user.getStatusCodeValue());
    }


    @Test
    public void test_get_valid_username() {
        when(userRepository.findByUsername("valid_username")).thenReturn(CartControllerTest.getTestUser(1L, "test"));
        ResponseEntity<User> user = userController.findByUserName("valid_username");
        Assertions.assertEquals(200, user.getStatusCodeValue());
    }

    @Test
    public void test_get_invalid_user_id() {
        when(userRepository.findById(9l)).thenReturn(Optional.ofNullable(null));
        ResponseEntity<User> user = userController.findById(9l);
        Assertions.assertEquals(404, user.getStatusCodeValue());
    }

    @Test
    public void test_get_valid_user_id() {
        when(userRepository.findById(1l)).thenReturn(Optional.of(CartControllerTest.getTestUser(1L, "test")));
        ResponseEntity<User> user = userController.findById(1l);
        Assertions.assertEquals(200, user.getStatusCodeValue());
    }
}
