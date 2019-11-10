package io.github.nightwolf.restapi.dto;

/**
 * @author oshan
 */
public class BasicReplyDTO {
    private String status;

    public BasicReplyDTO() {
    }

    public BasicReplyDTO(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
