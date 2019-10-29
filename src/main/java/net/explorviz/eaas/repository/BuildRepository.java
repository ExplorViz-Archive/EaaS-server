package net.explorviz.eaas.repository;

import net.explorviz.eaas.model.Build;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BuildRepository extends PagingAndSortingRepository<Build, Long> {
}
