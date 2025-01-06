package io.krystof.retro_launcher.controller.dto;

import java.util.List;
import java.util.Objects;

public class PlatformDTO {
    private Long id;
    private String name;
    private String description;
    private List<PlatformBinaryDTO> binaries;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<PlatformBinaryDTO> getBinaries() { return binaries; }
    public void setBinaries(List<PlatformBinaryDTO> binaries) { this.binaries = binaries; }

    @Override
    public String toString() {
        return "PlatformDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", binaries=" + binaries +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlatformDTO that = (PlatformDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(binaries, that.binaries);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(name);
        result = 31 * result + Objects.hashCode(description);
        result = 31 * result + Objects.hashCode(binaries);
        return result;
    }
}
