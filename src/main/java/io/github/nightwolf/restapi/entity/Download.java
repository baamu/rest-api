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

    @Column(name = "added_date")
    private Date added_date;

    @Column(name = "downloaded_date")
    private Date downloaded_date;

    @Column(name = "name")
    private String name;

    @Column(name = "file_size")
    private double file_size;

    @Column(name = "used_times")        //incremented when a user copies from repository
    private long used_times;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getFile_size() {
        return file_size;
    }

    public void setFile_size(double file_size) {
        this.file_size = file_size;
    }

    public long getUsed_times() {
        return used_times;
    }

    public void setUsed_times(long used_times) {
        this.used_times = used_times;
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
