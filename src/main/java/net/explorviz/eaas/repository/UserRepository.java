package net.explorviz.eaas.repository;

import net.explorviz.eaas.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {
}
