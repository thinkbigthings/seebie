package org.thinkbigthings.zdd.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.thinkbigthings.zdd.dto.User;
import org.thinkbigthings.zdd.server.controller.UserController;
import org.thinkbigthings.zdd.server.service.UserService;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserService userService = Mockito.mock(UserService.class);
    private String savedUsername = "saveduser";
    private User savedUser = new User(savedUsername, "", null, null, true);

    private UserController controller;

    @BeforeEach
    public void setup() {

        controller = new UserController(userService);
        when(userService.getUser(eq(savedUser.username()))).thenReturn(savedUser);
    }

    @Test
    public void getUser() {

        assertEquals(savedUser, controller.getUser(savedUsername));
    }
}
