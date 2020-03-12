package net.explorviz.eaas.model.repository;

import net.explorviz.eaas.model.entity.Build;
import net.explorviz.eaas.model.entity.Project;
import net.explorviz.eaas.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.lang.Nullable;

import java.util.Optional;

public interface BuildRepository extends PagingAndSortingRepository<Build, Long> {
    Optional<Build> findByProjectAndId(Project project, Long id);

    boolean existsByProjectAndNameIgnoreCase(Project project, String name);

    Page<Build> findByProjectOrderByCreatedDateDesc(Project project, Pageable pageable);

    Optional<Build> findByProjectAndDockerImageIgnoreCase(Project project, String dockerImage);

    boolean existsByDockerImageIgnoreCase(String dockerImage);

    @Query("SELECT b FROM Build b " +
        "LEFT JOIN b.project p " +
        "WHERE p.hidden = ?1 " +
        "OR (?2 IS NOT NULL AND p.owner = ?2) " +
        "ORDER BY b.createdDate DESC")
    Page<Build> findMostRecentBuilds(boolean hidden, @Nullable User owner, Pageable pageable);
}
