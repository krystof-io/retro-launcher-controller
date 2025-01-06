package io.krystof.retro_launcher.controller.jpa.entities;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.Set;

@Entity
@Table(name = "platform_binary")
public class PlatformBinary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "platform_id", nullable = false)
    private Platform platform;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String variant;

    private String description;

    @Column(name = "is_default")
    private boolean isDefault;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "platformBinary")
    @OrderBy("argumentOrder ASC")
    private Set<PlatformBinaryLaunchArgument> launchArguments;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
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

    public Set<PlatformBinaryLaunchArgument> getLaunchArguments() {
        return launchArguments;
    }

    public void setLaunchArguments(Set<PlatformBinaryLaunchArgument> launchArguments) {
        this.launchArguments = launchArguments;
    }
}