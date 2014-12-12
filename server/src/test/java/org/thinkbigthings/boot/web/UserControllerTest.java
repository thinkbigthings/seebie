package org.thinkbigthings.boot.web;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.thinkbigthings.boot.domain.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.springframework.hateoas.Link;
import org.thinkbigthings.boot.assembler.Resource;
import org.thinkbigthings.boot.assembler.UserPageResourceAssembler;
import org.thinkbigthings.boot.service.UserServiceInterface;

public class UserControllerTest {

    private UserController resource;
    private final UserServiceInterface userService = mock(UserServiceInterface.class);
    private final UserPageResourceAssembler resourceAssembler = mock(UserPageResourceAssembler.class);
    private final BindingResult goodBinding = mock(BindingResult.class);
    private final BindingResult failedBinding = mock(BindingResult.class);
    private final User currentUser = new User();
    private final User persistedUser = new User();
    private final User userRequestBody = new User();
    private final Long ID = 1L;

    @Before
    public void setup() throws Exception {

        currentUser.withId(ID).withUsername("my@username.com");
        userRequestBody.withId(ID).withUsername("myupdate@username.com");
        persistedUser.withId(ID);

        resource = new UserController(userService, resourceAssembler);

        when(goodBinding.hasErrors()).thenReturn(false);
        when(failedBinding.hasErrors()).thenReturn(true);
        when(userService.getUserById(ID)).thenReturn(persistedUser);
        when(userService.updateUser(userRequestBody)).thenReturn(persistedUser);
        when(resourceAssembler.toResource(persistedUser)).thenReturn(new Resource(persistedUser, new Link[]{}));
    }

    
    @Test
    public void validUserShouldUpdateSelf() throws Exception {
        Resource<User> user = resource.update(ID, userRequestBody, goodBinding);
        assertEquals(persistedUser, user.getContent());
    }
    
    @Test(expected = InvalidRequestBodyException.class)
    public void urlIdShouldMatchRequestBodyId() throws Exception {
        userRequestBody.setId(ID + 1);
        resource.update(ID, userRequestBody, goodBinding);
    }

    @Test(expected = InvalidRequestBodyException.class)
    public void invalidUserDataShouldNotUpdate() throws Exception {
        resource.update(ID, userRequestBody, failedBinding);
    }

    @Test
    public void shouldGetUser() throws Exception {
        Resource<User> user = resource.getUser(ID);
        assertNotNull(user.getContent());
    }


}
