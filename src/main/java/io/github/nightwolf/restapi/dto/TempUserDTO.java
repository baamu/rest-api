package io.github.nightwolf.restapi.dto;

import java.util.Date;

/**
 * @author oshan
 */

public class TempUserDTO {
    private String email;
    private String name;
    private Date dob;
    private String username;
    private String password;
    private String nic;

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

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    @Override
    public String toString() {
        return "TempUserDTO{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", dob=" + dob +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", nic='" + nic + '\'' +
                '}';
    }
}
