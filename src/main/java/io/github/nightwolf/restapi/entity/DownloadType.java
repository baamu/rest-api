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

    @Column(name = "fileType")
    private String fileType;

    @Column(name = "defaultPath")
    private String defaultPath;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getDefaultPath() {
        return defaultPath;
    }

    public void setDefaultPath(String defaultPath) {
        this.defaultPath = defaultPath;
    }
}
