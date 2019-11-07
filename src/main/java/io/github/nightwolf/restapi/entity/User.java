package io.github.nightwolf.restapi.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author oshan
 */

@Entity
@Table(name = "users")
public class User implements Serializable {

    //The properties are for testing only. Will be changed in future

    @Id
    @Column(name = "email")
    private String email;

    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;

//
//    @Column(name = "role")
    @JoinColumn(name = "role")
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = Role.class)
    private Role role;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
