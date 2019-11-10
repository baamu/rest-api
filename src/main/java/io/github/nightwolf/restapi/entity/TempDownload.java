package io.github.nightwolf.restapi.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * @author oshan
 */

@Entity
@Table(name = "temp_download")
public class TempDownload {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "added_date")
    private Date added_date;

    @Column(name = "name")
    private String name;

    @Column(name = "size")
    private double size;

    @JoinColumn(name = "added_by")
    @OneToOne
    private User user;

    @JoinColumn(name = "type")
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
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
