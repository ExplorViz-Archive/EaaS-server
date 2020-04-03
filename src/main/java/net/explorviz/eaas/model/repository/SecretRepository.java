package net.explorviz.eaas.model.repository;

import net.explorviz.eaas.model.entity.Project;
import net.explorviz.eaas.model.entity.Secret;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface SecretRepository extends PagingAndSortingRepository<Secret, Long> {
    Optional<Secret> findByProjectAndSecret(Project project, String secret);

    Page<Secret> findByProject(Project project, Pageable unpaged);

    boolean existsByProjectAndNameIgnoreCase(Project project, String name);
}
