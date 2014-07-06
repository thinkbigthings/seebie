package org.thinkbigthings.boot.repository;


import org.thinkbigthings.boot.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> 
{
   public User findByUsername(String username);
}
