package io.github.nightwolf.restapi.dto;

/**
 * @author oshan
 */
public class BasicReply {
    private String status;

    public BasicReply() {
    }

    public BasicReply(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
