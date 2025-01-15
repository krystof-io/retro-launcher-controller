package io.krystof.retro_launcher.controller.jpa.repositories;

import io.krystof.retro_launcher.controller.jpa.entities.Program;
import io.krystof.retro_launcher.model.CurationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Long> , JpaSpecificationExecutor<Program> , PagingAndSortingRepository<Program,Long> {
    List<Program> findByCurationStatus(CurationStatus status);

    @Query("SELECT p FROM Program p " +
            "JOIN p.authors a " +
            "WHERE a.id = :authorId")
    List<Program> findByAuthorId(@Param("authorId") Long authorId);

    @Query("SELECT p FROM Program p " +
            "LEFT JOIN FETCH p.platform " +
            "WHERE p.curationStatus = :status " +
            "ORDER BY p.createdAt DESC")
    List<Program> findByCurationStatusWithBasicDetails(String status);
}