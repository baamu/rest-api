package io.github.nightwolf.restapi.entity;

import javax.persistence.*;

/**
 * @author oshan
 */

@Entity
@Table(name = "download_type")
public class DownloadType {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "type")
    private String type;

    @Column(name = "ext")
    private String ext;

    @Column(name = "default_path")
    private String default_path;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getDefault_path() {
        return default_path;
    }

    public void setDefault_path(String default_path) {
        this.default_path = default_path;
    }
}
