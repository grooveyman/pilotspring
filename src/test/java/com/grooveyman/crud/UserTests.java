package com.grooveyman.crud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.grooveyman.crud.model.User;
import com.grooveyman.crud.repository.UserRepository;

@DataJpaTest
public class UserTests {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testUserCreation() {
        User user = User.builder().name("Stephen Anim").email("steph@mail.com").password("password1122").build();
        User savedUser = userRepository.save(user);
        assertNotNull(savedUser.getId());
        assertEquals("Stephen Anim", savedUser.getName());
    }

    @Test
    public void testUserRetrieval() {
        User user = User.builder().name("Stephen Anim").email("steph@mail.com").password("password1122").build();
        userRepository.save(user);
        var foundUser = userRepository.findById(Long.valueOf("32")).orElse(null);
        assertNotNull(foundUser);
        assertEquals("Stephen Anim", foundUser.getName());
    }

    @Test
    public void testUserUpdate() {
        // Implement test logic
    }

    @Test
    public void testUserDeletion() {
        // Implement test logic
    }
}
