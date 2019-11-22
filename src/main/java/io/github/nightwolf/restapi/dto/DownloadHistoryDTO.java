package io.github.nightwolf.restapi.dto;

import io.github.nightwolf.restapi.entity.Download;

import java.util.Date;

/**
 * @author oshan
 */
public class DownloadHistoryDTO {
    private long id;
    private String name;
    private String url;
    private Date addedDate;
    private Date downloadedDate;
    private double file_size;

    public DownloadHistoryDTO() {
    }

    public DownloadHistoryDTO(long id, String name, String url, Date addedDate, Date downloadedDate, double file_size) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.addedDate = addedDate;
        this.downloadedDate = downloadedDate;
        this.file_size = file_size;
    }

    public DownloadHistoryDTO(Download download) {
        this.id = download.getId();
        this.name = download.getName();
        this.url = download.getUrl();
        this.addedDate = download.getAddedDate();
        this.downloadedDate = download.getDownloadedDate();
        this.file_size = download.getFileSize();
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

    public Date getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }

    public Date getDownloadedDate() {
        return downloadedDate;
    }

    public void setDownloadedDate(Date downloadedDate) {
        this.downloadedDate = downloadedDate;
    }

    public double getFile_size() {
        return file_size;
    }

    public void setFile_size(double file_size) {
        this.file_size = file_size;
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
