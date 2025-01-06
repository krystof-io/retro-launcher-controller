package io.krystof.retro_launcher.controller.jpa.repositories;

import io.krystof.retro_launcher.controller.jpa.entities.PlatformBinaryLaunchArgument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BinaryArgumentTemplateRepository extends JpaRepository<PlatformBinaryLaunchArgument, Long> {
    List<PlatformBinaryLaunchArgument> findByPlatformBinaryIdOrderByArgumentOrder(Long platformBinaryId);
}