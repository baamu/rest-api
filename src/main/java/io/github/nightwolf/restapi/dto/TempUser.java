package io.github.nightwolf.restapi.dto;

/**
 * @author oshan
 */

public class TempUser {
    private String email;
    private String name;
    private String dob;
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

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    @Override
    public String toString() {
        return "TempUser{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", dob='" + dob + '\'' +
                ", nic='" + nic + '\'' +
                '}';
    }
}
