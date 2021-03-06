package io.github.nightwolf.restapi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author oshan
 */

@Entity
@Table(name = "users")
public class User implements Serializable {

    @Id
    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "dob")
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date dob;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "nic")
    private String nic;

    @JoinColumn(name = "role_id")
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = Role.class)
    private Role role;

    public User() {
    }

    public User(TempUser tempUser) {
        this.email = tempUser.getEmail();
        this.dob = tempUser.getDob();
        this.username = tempUser.getUsername();
        this.name = tempUser.getName();
        this.password = tempUser.getPassword();
        this.nic = tempUser.getNic();

        Role role = new Role("USER");
        role.setId(1);

        this.role = role;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
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

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }
}
