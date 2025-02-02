package io.krystof.retro_launcher.controller.jpa.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.OffsetDateTime;

@Entity
@Table(name = "program_vote")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "program_id", nullable = false)
    private Long programId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "platform_id", nullable = false)
    private String platformId;

    @Column(name = "music_score", nullable = false)
    private int musicScore;

    @Column(name = "graphics_score", nullable = false)
    private int graphicsScore;

    @Column(name = "vibes_score", nullable = false)
    private int vibesScore;

    @Column(name = "comment")
    private String comment;

    @Column(name = "created_at")
    @CreatedDate
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
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
}