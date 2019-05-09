package com.bryanwheeler.imageclassifierserver.model;

import com.bryanwheeler.imageclassifierserver.View;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.awt.*;

@Entity
@Table(name="images")
public class ImageModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonView(View.ImageInfo.class)
    private Long id;

    @Column(name = "filename")
    @JsonView(View.ImageInfo.class)
    private String filename;

    @JsonView(View.ImageInfo.class)
    @Column(name = "mimetype")
    private String mimetype;

    @Lob
    @Column(name = "image")
    private byte[] image;

    @JsonView(View.ImageInfo.class)
    @Column(name = "description")
    private String description;

    public ImageModel() {};

    public ImageModel(String filename, String mimetype, byte[] image, String description) {
        this.filename = filename;
        this.mimetype = mimetype;
        this.image = image;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }
}
