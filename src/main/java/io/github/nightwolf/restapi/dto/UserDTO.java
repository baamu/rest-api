package io.github.nightwolf.restapi.dto;

import io.github.nightwolf.restapi.entity.User;

import java.text.SimpleDateFormat;

/**
 * @author oshan
 */
public class UserDTO {
    private String email;
    private String username;
    private String name;
    private String nic;
    private String dob;

    public UserDTO(User user) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.name = user.getName();
        this.nic = user.getNic();

        this.dob = sdf.format(user.getDob());
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }
}
