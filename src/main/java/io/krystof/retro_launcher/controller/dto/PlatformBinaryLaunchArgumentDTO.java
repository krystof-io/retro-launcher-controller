package io.krystof.retro_launcher.controller.dto;

import io.krystof.retro_launcher.controller.jpa.entities.PlatformBinary;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.Objects;

public class PlatformBinaryLaunchArgumentDTO {

    private Long id;
    private Integer argumentOrder;
    private String argumentTemplate;
    private boolean isRequired = true;
    private boolean fileArgument = false;
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlatformBinaryLaunchArgumentDTO that = (PlatformBinaryLaunchArgumentDTO) o;
        return isRequired == that.isRequired && fileArgument == that.fileArgument && Objects.equals(id, that.id) && Objects.equals(argumentOrder, that.argumentOrder) && Objects.equals(argumentTemplate, that.argumentTemplate) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(argumentOrder);
        result = 31 * result + Objects.hashCode(argumentTemplate);
        result = 31 * result + Boolean.hashCode(isRequired);
        result = 31 * result + Boolean.hashCode(fileArgument);
        result = 31 * result + Objects.hashCode(description);
        return result;
    }

    @Override
    public String toString() {
        return "PlatformBinaryLaunchArgumentDTO{" +
                "id=" + id +
                ", argumentOrder=" + argumentOrder +
                ", argumentTemplate='" + argumentTemplate + '\'' +
                ", isRequired=" + isRequired +
                ", fileArgument=" + fileArgument +
                ", description='" + description + '\'' +
                '}';
    }
}