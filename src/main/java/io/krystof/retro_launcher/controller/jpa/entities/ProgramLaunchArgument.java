package io.krystof.retro_launcher.controller.jpa.entities;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "program_launch_argument")
public class ProgramLaunchArgument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @Column(name = "argument_order", nullable = false)
    private Integer argumentOrder;

    @Column(name = "argument_value", nullable = false)
    private String argumentValue;

    @Column(name = "argument_group")
    private String argumentGroup;

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

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public Integer getArgumentOrder() {
        return argumentOrder;
    }

    public void setArgumentOrder(Integer argumentOrder) {
        this.argumentOrder = argumentOrder;
    }

    public String getArgumentValue() {
        return argumentValue;
    }

    public void setArgumentValue(String argumentValue) {
        this.argumentValue = argumentValue;
    }

    public String getArgumentGroup() {
        return argumentGroup;
    }

    public void setArgumentGroup(String argumentGroup) {
        this.argumentGroup = argumentGroup;
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


    public String toStringForRoughCompare() {
        return "ProgramLaunchArgument{" +
                ", argumentOrder=" + argumentOrder +
                ", argumentValue='" + argumentValue + '\'' +
                ", argumentGroup='" + argumentGroup + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}