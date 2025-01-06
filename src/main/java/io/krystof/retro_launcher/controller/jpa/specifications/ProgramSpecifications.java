package io.krystof.retro_launcher.controller.jpa.specifications;

import io.krystof.retro_launcher.controller.jpa.entities.Program;

import io.krystof.retro_launcher.model.ContentRating;
import io.krystof.retro_launcher.model.CurationStatus;
import io.krystof.retro_launcher.model.ProgramType;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class ProgramSpecifications {

    public static Specification<Program> titleContains(String title) {
        return (root, query, cb) -> {
            if (title == null) {
                return null;
            }
            return cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
        };
    }

    public static Specification<Program> withType(ProgramType type) {
        return (root, query, cb) -> {
            if (type == null) {
                return null;
            }
            return cb.equal(root.get("type"), type);
        };
    }

    public static Specification<Program> withPlatform(Long platformId) {
        return (root, query, cb) -> {
            if (platformId == null) {
                return null;
            }
            return cb.equal(root.get("platform").get("id"), platformId);
        };
    }

    public static Specification<Program> byAuthorId(Long authorId) {
        return (root, query, cb) -> {
            if (authorId == null) {
                return null;
            }
            return cb.equal(root.join("authors").get("id"), authorId);
        };
    }

    public static Specification<Program> withContentRating(ContentRating rating) {
        return (root, query, cb) -> {
            if (rating == null) {
                return null;
            }
            return cb.equal(root.get("contentRating"), rating);
        };
    }

    public static Specification<Program> withCurationStatus(CurationStatus status) {
        return (root, query, cb) -> {
            if (status == null) {
                return null;
            }
            return cb.equal(root.get("curationStatus"), status);
        };
    }

    public static Specification<Program> withEagerLoading() {
        return (root, query, cb) -> {
            if (query.getResultType().equals(Long.class)) {
                return null; // Don't join for count queries
            }
            root.fetch("platform", JoinType.LEFT);
            root.fetch("platformBinary", JoinType.LEFT);
            return null;
        };
    }

    public static Specification<Program> withAuthors() {
        return (root, query, cb) -> {
            if (query.getResultType().equals(Long.class)) {
                return null; // Don't join for count queries
            }
            root.fetch("authors", JoinType.LEFT);
            return null;
        };
    }

    public static Specification<Program> withFullDetails() {
        return (root, query, cb) -> {
            if (query.getResultType().equals(Long.class)) {
                return null; // Don't join for count queries
            }
            root.fetch("platform", JoinType.LEFT);
            root.fetch("platformBinary", JoinType.LEFT);
            root.fetch("authors", JoinType.LEFT);
            root.fetch("diskImages", JoinType.LEFT);
            root.fetch("launchArguments", JoinType.LEFT);
            return null;
        };
    }

    public static Specification<Program> releaseYearAfter(Integer year) {
        return (root, query, cb) -> {
            if (year == null) {
                return null;
            }
            return cb.greaterThanOrEqualTo(root.get("releaseYear"), year);
        };
    }

    public static Specification<Program> releaseYearBefore(Integer year) {
        return (root, query, cb) -> {
            if (year == null) {
                return null;
            }
            return cb.lessThanOrEqualTo(root.get("releaseYear"), year);
        };
    }
}