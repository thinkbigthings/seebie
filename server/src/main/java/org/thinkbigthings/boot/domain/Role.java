package org.thinkbigthings.boot.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "roles", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }) })
@JsonIgnoreProperties(ignoreUnknown = true)
public class Role implements GrantedAuthority, Serializable {

   private static final long serialVersionUID = 1L;

    /**
     * Not using a role prefix right now, can cause issues for edge cases (?) but seems to be working fine for now.
     * http://stackoverflow.com/questions/20787590/spring-security-roles-should-always-be-prefixed-with-role
     */
   public enum NAME {
      USER, ADMIN
   }

   @Id
   @NotNull
   @GeneratedValue(strategy = GenerationType.AUTO)
   private long id = -1;

   @Enumerated(EnumType.STRING)
   @NotNull
   private NAME name = NAME.USER;

   // empty constructor is necessary for hibernate to create
   protected Role() {

   }

   public Role(NAME n) {
      name = n;
   }

   @Override
   public String getAuthority() {
      return name.toString();
   }

   public long getId() {
      return id;
   }

   public void setId(long id) {
      this.id = id;
   }

   public NAME getName() {
      return name;
   }

   public void setName(NAME name) {
      this.name = name;
   }
   
   @Override
   public boolean equals(Object other) {
      if( ! (other instanceof Role)) {
         return false;
      }
      return name.equals(((Role)other).getName());
   }
   
   @Override
   public int hashCode() {
      return name.hashCode();
   }

}
