package net.explorviz.eaas.model.repository;

import net.explorviz.eaas.model.entity.Project;
import net.explorviz.eaas.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.lang.Nullable;

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

    @Query("SELECT DISTINCT p AS project, b AS build FROM Project p " +
               "LEFT JOIN p.builds b " +
               "WHERE p.hidden = false " +
               "OR (?1 IS NOT NULL AND p.owner = ?1) " +
               "ORDER BY b.createdDate DESC")
    Page<RecentlyUpdatedResult> findRecentlyUpdated(boolean hidden, @Nullable User owner,
                                                    Pageable pageable);
}
