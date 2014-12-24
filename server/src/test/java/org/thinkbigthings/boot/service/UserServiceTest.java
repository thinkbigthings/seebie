package org.thinkbigthings.boot.service;

import javax.persistence.EntityNotFoundException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.AdditionalAnswers.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thinkbigthings.boot.domain.Role;
import org.thinkbigthings.boot.domain.User;
import org.thinkbigthings.boot.dto.UserRegistration;
import org.thinkbigthings.boot.repository.RoleRepository;
import org.thinkbigthings.boot.repository.UserRepository;

public class UserServiceTest {

    private UserService service;

    private final UserRepository userRepo = mock(UserRepository.class);
    private final RoleRepository roleRepo = mock(RoleRepository.class);
    private final PasswordEncoder encoder  = mock(PasswordEncoder.class);

    private final UserRegistration requestUser = new UserRegistration();
    private final User persistedUser = new User();
    private final Role persistedUserRole = new Role(Role.NAME.USER);

    @Before
    public void setup() throws Exception {
        service = new UserService(userRepo, roleRepo, encoder);
        
        persistedUser.withId(1L).withUsername("my@username.com").withRoles(persistedUserRole);
        requestUser.setUserName("my2username.com");

        when(userRepo.findOne(persistedUser.getId())).thenReturn(persistedUser);
        when(userRepo.save(any(User.class))).then(returnsFirstArg());
        when(userRepo.saveAndFlush(any(User.class))).then(returnsFirstArg());
        when(roleRepo.findByName(Role.NAME.USER)).thenReturn(persistedUserRole);
    }

    @Test
    public void registerNewUserShouldSetData() throws Exception {
        requestUser.setUserName("name");
        User newUser = service.registerNewUser(requestUser);
        assertTrue(newUser.getRoles().contains(persistedUserRole));
        assertEquals(requestUser.getUserName(), newUser.getUsername());
    }

//    @Test
//    public void updateShouldPersistProperties() throws Exception {
//        requestUser.setUserName("my2@username.com");
//        User persisted = service.updateUser(requestUser);
//        assertEquals(requestUser.getUserName(), persisted.getUsername());
//    }
        
    @Test(expected = EntityNotFoundException.class)
    public void shouldFailOnInvalidId() throws Exception {
        service.getUserById(persistedUser.getId() + 1);
    }

    @Test
    public void shouldGetUser() throws Exception {
        User user = service.getUserById(persistedUser.getId());
        assertEquals(user.getId(), persistedUser.getId());
        assertEquals(user.getUsername(), persistedUser.getUsername());
    }
}
