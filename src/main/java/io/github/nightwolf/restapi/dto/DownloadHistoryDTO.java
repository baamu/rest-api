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
    private Date added_date;
    private Date downloaded_date;
    private double file_size;

    public DownloadHistoryDTO() {
    }

    public DownloadHistoryDTO(long id, String name, String url, Date added_date, Date downloaded_date, double file_size) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.added_date = added_date;
        this.downloaded_date = downloaded_date;
        this.file_size = file_size;
    }

    public DownloadHistoryDTO(Download download) {
        this.id = download.getId();
        this.name = download.getName();
        this.url = download.getUrl();
        this.added_date = download.getAdded_date();
        this.downloaded_date = download.getDownloaded_date();
        this.file_size = download.getFile_size();
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

    public Date getAdded_date() {
        return added_date;
    }

    public void setAdded_date(Date added_date) {
        this.added_date = added_date;
    }

    public Date getDownloaded_date() {
        return downloaded_date;
    }

    public void setDownloaded_date(Date downloaded_date) {
        this.downloaded_date = downloaded_date;
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
                ", added_date=" + added_date +
                ", downloaded_date=" + downloaded_date +
                ", file_size=" + file_size +
                '}';
    }
}
