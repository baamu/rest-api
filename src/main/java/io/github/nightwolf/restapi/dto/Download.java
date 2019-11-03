package io.github.nightwolf.restapi.dto;

import java.util.Objects;

/**
 * @author oshan
 */
public class Download {
    private String id;
    private String userId;
    private String url;
    private double downloadedSize;
    private double fileSize;
    private boolean completed = false;

    public Download() {
    }

    public Download(String id) {
        this.id = id;
    }

    public Download(String userId, String url) {
        this.userId = userId;
        this.url = url;
    }

    public Download(String id, String userId, String url) {
        this.id = id;
        this.userId = userId;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getDownloadedSize() {
        return downloadedSize;
    }

    public void setDownloadedSize(double downloadedSize) {
        this.downloadedSize = downloadedSize;
    }

    public double getFileSize() {
        return fileSize;
    }

    public void setFileSize(double fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Download download = (Download) o;
        return Objects.equals(id, download.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
