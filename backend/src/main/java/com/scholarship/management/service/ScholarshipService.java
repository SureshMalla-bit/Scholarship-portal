package com.scholarship.management.service;

import com.scholarship.management.exception.ApiException;
import com.scholarship.management.model.Scholarship;
import com.scholarship.management.repository.ApplicationRepository;
import com.scholarship.management.repository.ScholarshipRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/** Business logic for scholarships (admin CRUD). */
@Service
public class ScholarshipService {

    private final ScholarshipRepository scholarships;
    private final ApplicationRepository applications;

    public ScholarshipService(ScholarshipRepository scholarships, ApplicationRepository applications) {
        this.scholarships = scholarships;
        this.applications = applications;
    }

    public List<Scholarship> getAll() {
        return scholarships.findAllByOrderByDeadlineAsc();
    }

    public Scholarship getById(Long id) {
        return scholarships.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Scholarship not found"));
    }

    public Scholarship create(Scholarship scholarship) {
        scholarship.setId(null);
        return scholarships.save(scholarship);
    }

    public Scholarship update(Long id, Scholarship details) {
        getById(id);
        details.setId(id);
        return scholarships.save(details);
    }

    /** Deletes the scholarship and every application made to it. */
    @Transactional
    public void delete(Long id) {
        getById(id);
        applications.deleteByScholarshipId(id);
        scholarships.deleteById(id);
    }
}
