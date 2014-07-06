package org.thinkbigthings.boot.repository;

import org.thinkbigthings.boot.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.thinkbigthings.boot.domain.Role.NAME;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> 
{

   public Role findByName(NAME name);

}
