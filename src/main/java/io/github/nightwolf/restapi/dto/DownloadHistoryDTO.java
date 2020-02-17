package io.github.nightwolf.restapi.dto;

import io.github.nightwolf.restapi.entity.Download;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author oshan
 */
public class DownloadHistoryDTO {
    private long id;
    private String name;
    private String url;
    private String addedDate;
    private String downloadedDate;
    private double file_size;
    private String user;

    public DownloadHistoryDTO() {
    }

    public DownloadHistoryDTO(long id, String name, String url, String addedDate, String downloadedDate, double file_size) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.addedDate = addedDate;
        this.downloadedDate = downloadedDate;
        this.file_size = file_size;
    }

    public DownloadHistoryDTO(Download download) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        this.id = download.getId();
        this.name = download.getName();
        this.url = download.getUrl();
        this.addedDate = sdf.format(download.getAddedDate());
        this.downloadedDate = sdf.format(download.getDownloadedDate());
        this.file_size = download.getFileSize();
        this.user = download.getUser().getEmail();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public String getDownloadedDate() {
        return downloadedDate;
    }

    public void setDownloadedDate(String downloadedDate) {
        this.downloadedDate = downloadedDate;
    }

    public double getFile_size() {
        return file_size;
    }

    public void setFile_size(double file_size) {
        this.file_size = file_size;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "DownloadHistoryDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", addedDate=" + addedDate +
                ", downloadedDate=" + downloadedDate +
                ", file_size=" + file_size +
                '}';
    }
}
