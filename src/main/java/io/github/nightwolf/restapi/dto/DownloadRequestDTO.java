package io.github.nightwolf.restapi.dto;

/**
 * @author oshan
 */
public class DownloadRequestDTO {
    private String url;
    private String userId;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "DownloadRequestDTO{" +
                "url='" + url + '\'' +
                '}';
    }
}
