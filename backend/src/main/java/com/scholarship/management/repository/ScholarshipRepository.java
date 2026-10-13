package com.scholarship.management.repository;

import com.scholarship.management.model.Scholarship;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScholarshipRepository extends JpaRepository<Scholarship, Long> {
    List<Scholarship> findAllByOrderByDeadlineAsc();
}
