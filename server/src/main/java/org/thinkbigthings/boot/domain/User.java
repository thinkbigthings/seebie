package org.thinkbigthings.boot.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Cascade;
import org.hibernate.validator.constraints.Email;
import org.springframework.hateoas.Identifiable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

// TODO 5 add Address and Image objects to User
// check behavior: if I modify the schema, does the database get updated 
// because I specified “update”? or do I need to specify “none” or “verify?”

/**
 * 
 * There are certain things you can and can't do when mixing JPA and Hibernate annotations
 * http://www.mkyong.com/hibernate/cascade-jpa-hibernate-annotation-common-mistake/
 */
@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = { "username" }) })
public class User implements UserDetails, Identifiable<Long>  {

   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue(strategy=GenerationType.AUTO)
   private Long id = 0L;

   // TODO 5 separate username from email address? if requires email format should rename for clarity
   @Basic
   @NotNull
   @Size(min = 3, message = "must be at least three characters")
   @Email(message = "Name must be in email format")
   private String username = "";

   @Basic
   @NotNull
   @Size(min = 3, message = "must be at least three characters")
   private String displayName = "";

   @Basic
   @NotNull
   private String password = ""; // should be a salted hash

   @Basic
   private boolean enabled = false;

   @ManyToMany(fetch = FetchType.LAZY)
   @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
   private Set<Role> roles = new HashSet<>();

   @OneToMany(fetch = FetchType.LAZY, mappedBy="user", orphanRemoval=true)
   @Cascade(value = { org.hibernate.annotations.CascadeType.ALL })
   private Set<Sleep> sleepSessions = new HashSet<>();
   
   @Temporal(value = TemporalType.TIMESTAMP)
   private Date registration = new Date();

   public User()
   {
   }

   public void setUsername(String newName)
   {
      username = newName;
   }

   @Override
   public Long getId()
   {
      return id;
   }

   public void setId(Long id)
   {
      this.id = id;
   }

    /////////////////////////////////////////////////////////////
   // UserDetails methods
   
   @Override
   @JsonIgnore
   public Collection<? extends GrantedAuthority> getAuthorities() {
      return roles;
   }

   @Override
   public boolean isAccountNonExpired() {
      return true;
   }

   @Override
   public boolean isAccountNonLocked() {
      return true;
   }

   @Override
   public boolean isCredentialsNonExpired() {
      return true;
   }

   @Override
   public boolean isEnabled() {
      return enabled;
   }
   
   @Override
   public String getPassword() {
      return password;
   }

   @Override
   public String getUsername() {
      return username;
   }

   //////////////////////////////////////
   
   public Set<Role> getRoles()
   {
      return roles;
   }

   public void setRoles(Set<Role> roles)
   {
      this.roles = roles;
   }

   public Set<Sleep> getSleepSessions()
   {
      return sleepSessions;
   }

   public void setSleepSessions(Set<Sleep> sessions)
   {
      this.sleepSessions = sessions;
   }
   
   public String getDisplayName()
   {
      return displayName;
   }

   public void setDisplayName(String displayName)
   {
      this.displayName = displayName;
   }

   public Date getRegistration()
   {
      return registration;
   }

   public void setRegistration(Date registration)
   {
      this.registration = registration;
   }

   public void setPassword(String encryptedPassword)
   {
      this.password = encryptedPassword;
   }

   public void setEnabled(boolean enabled)
   {
      this.enabled = enabled;
   }

    public User withId(Long id) {
        setId(id);
        return this;
    }

    public User withUsername(String name) {
        setUsername(name);
        return this;
    }

    public User withDisplayName(String name) {
        setDisplayName(name);
        return this;
    }

    public User withRoles(Role... inroles) {
        roles.addAll(Arrays.asList(inroles));
        return this;
    }
    
}
