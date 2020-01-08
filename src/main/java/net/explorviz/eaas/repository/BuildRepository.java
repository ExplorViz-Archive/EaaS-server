package net.explorviz.eaas.repository;

import net.explorviz.eaas.model.Build;
import net.explorviz.eaas.model.Project;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface BuildRepository extends PagingAndSortingRepository<Build, Long> {
    Optional<Build> findByProjectAndId(Project project, Long id);
}
