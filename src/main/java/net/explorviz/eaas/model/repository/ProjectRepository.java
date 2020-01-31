package net.explorviz.eaas.model.repository;

import net.explorviz.eaas.model.entity.Project;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;
import java.util.Optional;

public interface ProjectRepository extends PagingAndSortingRepository<Project, Long> {
    Optional<Project> findByName(String name);

    Collection<Project> findByHidden(boolean hidden);
}
