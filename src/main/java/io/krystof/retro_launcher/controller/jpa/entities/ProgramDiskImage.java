package io.krystof.retro_launcher.controller.jpa.entities;

import io.krystof.retro_launcher.controller.resolvers.StoragePathResolver;
import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "program_disk_image")
public class ProgramDiskImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn( name = "program_id", nullable = false)
    private Program program;

    @Column(name = "disk_number", nullable = false)
    private Integer diskNumber;

    @Column(name = "image_name", nullable = false)
    private String imageName;

    @Column(name = "file_hash", nullable = false)
    private String fileHash;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;


    @Transient
    public String getStoragePath() {
        return StoragePathResolver.resolveStoragePath(this);
    }

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public Integer getDiskNumber() {
        return diskNumber;
    }

    public void setDiskNumber(Integer diskNumber) {
        this.diskNumber = diskNumber;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String filePath) {
        this.imageName = filePath;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String toStringForRoughCompare() {
        return getDiskNumber()+":"+getFileHash()+":"+getImageName();
    }
}