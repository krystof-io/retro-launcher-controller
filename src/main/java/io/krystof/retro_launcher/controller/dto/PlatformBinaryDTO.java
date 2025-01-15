package io.krystof.retro_launcher.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

public class PlatformBinaryDTO {
    private Long id;
    private String name;
    private String variant;
    private String description;
    @JsonProperty("isDefault")
    private boolean isDefault;
    private List<PlatformBinaryLaunchArgumentDTO> launchArguments;
    // Getters and setters

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }

    public List<PlatformBinaryLaunchArgumentDTO> getLaunchArguments() {
        return launchArguments;
    }

    public void setLaunchArguments(List<PlatformBinaryLaunchArgumentDTO> launchArguments) {
        this.launchArguments = launchArguments;
    }

    @Override
    public String toString() {
        return "PlatformBinaryDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", variant='" + variant + '\'' +
                ", description='" + description + '\'' +
                ", isDefault=" + isDefault +
                ", launchArguments=" + launchArguments +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlatformBinaryDTO that = (PlatformBinaryDTO) o;
        return isDefault == that.isDefault && Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(variant, that.variant) && Objects.equals(description, that.description) && Objects.equals(launchArguments, that.launchArguments);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(name);
        result = 31 * result + Objects.hashCode(variant);
        result = 31 * result + Objects.hashCode(description);
        result = 31 * result + Boolean.hashCode(isDefault);
        result = 31 * result + Objects.hashCode(launchArguments);
        return result;
    }
}