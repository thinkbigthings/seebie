package org.thinkbigthings.boot.service;

import java.util.List;
import org.thinkbigthings.boot.domain.User;

public interface UserServiceInterface {

    User getUserById(Long id);
    User registerNewUser(User newUser);
    User updateUser(User userData);
    List<User> getUsers();
    
}
