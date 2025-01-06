package io.krystof.retro_launcher.controller.jpa.entities;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "platform_binary_launch_argument")
public class PlatformBinaryLaunchArgument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "platform_binary_id", nullable = false)
    private PlatformBinary platformBinary;

    @Column(name = "argument_order", nullable = false)
    private Integer argumentOrder;

    @Column(name = "argument_template", nullable = false)
    private String argumentTemplate;

    @Column(name = "is_required")
    private boolean isRequired = true;

    @Column(name = "file_argument")
    private boolean fileArgument = false;

    private String description;

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

    public PlatformBinary getPlatformBinary() {
        return platformBinary;
    }

    public void setPlatformBinary(PlatformBinary platformBinary) {
        this.platformBinary = platformBinary;
    }

    public Integer getArgumentOrder() {
        return argumentOrder;
    }

    public void setArgumentOrder(Integer argumentOrder) {
        this.argumentOrder = argumentOrder;
    }

    public String getArgumentTemplate() {
        return argumentTemplate;
    }

    public void setArgumentTemplate(String argumentTemplate) {
        this.argumentTemplate = argumentTemplate;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }

    public boolean isFileArgument() {
        return fileArgument;
    }

    public void setFileArgument(boolean fileArgument) {
        this.fileArgument = fileArgument;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}