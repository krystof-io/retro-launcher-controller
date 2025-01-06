package io.krystof.retro_launcher.controller.jpa.repositories;

import io.krystof.retro_launcher.controller.jpa.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByName(String name);

    @Query("SELECT DISTINCT a FROM Author a LEFT JOIN FETCH a.programs WHERE a.id = :id")
    Optional<Author> findByIdWithPrograms(Long id);

    List<Author> findByNameContainingIgnoreCase(String namePart);
}
