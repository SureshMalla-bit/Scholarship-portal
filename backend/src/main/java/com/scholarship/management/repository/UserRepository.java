package com.scholarship.management.repository;

import com.scholarship.management.model.Role;
import com.scholarship.management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/** Spring Data writes the SQL for these methods from their names. */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByRole(Role role);
    List<User> findByRoleOrderByIdDesc(Role role);
}
