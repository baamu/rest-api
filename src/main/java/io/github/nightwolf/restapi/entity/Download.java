package io.github.nightwolf.restapi.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * @author oshan
 */

@Entity
@Table(name = "download")
public class Download {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "url")
    private String url;

    @Column(name = "addedDate")
    private Date addedDate;

    @Column(name = "downloadedDate")
    private Date downloadedDate;

    @Column(name = "name")
    private String name;

    @Column(name = "fileSize")
    private double fileSize;

    @Column(name = "usedTimes")        //incremented when a user copies from repository
    private long usedTimes;

    @JoinColumn(name = "added_by")
    @OneToOne
    private User user;

    @JoinColumn(name = "file_type_id")
    @ManyToOne
    private DownloadType type;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getFileSize() {
        return fileSize;
    }

    public void setFileSize(double fileSize) {
        this.fileSize = fileSize;
    }

    public long getUsedTimes() {
        return usedTimes;
    }

    public void setUsedTimes(long usedTimes) {
        this.usedTimes = usedTimes;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public DownloadType getType() {
        return type;
    }

    public void setType(DownloadType type) {
        this.type = type;
    }
}
