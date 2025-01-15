package io.krystof.retro_launcher.controller.dto;

import java.util.Objects;

public class ProgramLaunchArgumentDTO {
    private Long id;
    private Integer argumentOrder;
    private String argumentValue;
    private String argumentGroup;
    private String description;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getArgumentOrder() { return argumentOrder; }
    public void setArgumentOrder(Integer argumentOrder) { this.argumentOrder = argumentOrder; }

    public String getArgumentValue() { return argumentValue; }
    public void setArgumentValue(String argumentValue) { this.argumentValue = argumentValue; }

    public String getArgumentGroup() { return argumentGroup; }
    public void setArgumentGroup(String argumentGroup) { this.argumentGroup = argumentGroup; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "ProgramLaunchArgumentDTO{" +
                "id=" + id +
                ", argumentOrder=" + argumentOrder +
                ", argumentValue='" + argumentValue + '\'' +
                ", argumentGroup='" + argumentGroup + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProgramLaunchArgumentDTO that = (ProgramLaunchArgumentDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(argumentOrder, that.argumentOrder) && Objects.equals(argumentValue, that.argumentValue) && Objects.equals(argumentGroup, that.argumentGroup) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(argumentOrder);
        result = 31 * result + Objects.hashCode(argumentValue);
        result = 31 * result + Objects.hashCode(argumentGroup);
        result = 31 * result + Objects.hashCode(description);
        return result;
    }

    public String toStringForRoughCompare() {
        return "ProgramLaunchArgument{" +
                ", argumentOrder=" + argumentOrder +
                ", argumentValue='" + argumentValue + '\'' +
                ", argumentGroup='" + argumentGroup + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}