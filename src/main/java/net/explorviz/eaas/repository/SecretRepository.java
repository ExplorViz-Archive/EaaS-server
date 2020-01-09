package net.explorviz.eaas.repository;

import net.explorviz.eaas.model.Project;
import net.explorviz.eaas.model.Secret;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface SecretRepository extends PagingAndSortingRepository<Secret, Long> {
    Optional<Secret> findByProjectAndSecret(Project project, String secret);
}
