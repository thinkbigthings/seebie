package org.thinkbigthings.boot.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.thinkbigthings.boot.domain.User;

public interface UserServiceInterface {

    User getUserById(Long id);
    User registerNewUser(User newUser);
    User updateUser(User userData);
    Page<User> getUsers(Pageable pageable);
    
}
