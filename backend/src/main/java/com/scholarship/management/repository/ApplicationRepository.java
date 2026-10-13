package com.scholarship.management.repository;

import com.scholarship.management.model.ScholarshipApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApplicationRepository extends JpaRepository<ScholarshipApplication, Long> {
    List<ScholarshipApplication> findAllByOrderByIdDesc();
    List<ScholarshipApplication> findByStudentIdOrderByIdDesc(Long studentId);
    boolean existsByStudentIdAndScholarshipId(Long studentId, Long scholarshipId);
    void deleteByStudentId(Long studentId);
    void deleteByScholarshipId(Long scholarshipId);
}
