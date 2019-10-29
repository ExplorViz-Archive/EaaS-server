package net.explorviz.eaas.repository;

import net.explorviz.eaas.model.Secret;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SecretRepository extends PagingAndSortingRepository<Secret, Long> {
}
