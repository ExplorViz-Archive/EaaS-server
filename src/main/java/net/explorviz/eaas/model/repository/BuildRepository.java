package net.explorviz.eaas.model.repository;

import net.explorviz.eaas.model.entity.Build;
import net.explorviz.eaas.model.entity.Project;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface BuildRepository extends PagingAndSortingRepository<Build, Long> {
    Optional<Build> findByProjectAndId(Project project, Long id);
}
