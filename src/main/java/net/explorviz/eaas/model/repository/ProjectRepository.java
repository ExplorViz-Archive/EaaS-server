package net.explorviz.eaas.model.repository;

import net.explorviz.eaas.model.entity.Project;
import net.explorviz.eaas.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;
import java.util.Optional;

public interface ProjectRepository extends PagingAndSortingRepository<Project, Long> {
    Optional<Project> findByName(String name);

    Collection<Project> findByHidden(boolean hidden);

    Page<Project> findByHidden(boolean hidden, Pageable pageable);

    Collection<Project> findByOwner(User owner);

    Page<Project> findByOwner(User owner, Pageable pageable);

    Collection<Project> findByHiddenOrOwner(boolean hidden, User owner);

    Page<Project> findByHiddenOrOwner(boolean hidden, User owner, Pageable pageable);
}
