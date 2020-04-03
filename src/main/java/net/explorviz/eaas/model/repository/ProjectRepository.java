package net.explorviz.eaas.model.repository;

import net.explorviz.eaas.model.entity.Project;
import net.explorviz.eaas.model.entity.User;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;

public interface ProjectRepository extends PagingAndSortingRepository<Project, Long> {
    Collection<Project> findByHidden(boolean hidden);

    Collection<Project> findByOwner(User owner);

    boolean existsByNameIgnoreCase(String name);
}
