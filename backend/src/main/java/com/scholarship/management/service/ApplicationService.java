package com.scholarship.management.service;

import com.scholarship.management.dto.ApplicationRequest;
import com.scholarship.management.exception.ApiException;
import com.scholarship.management.model.ApplicationStatus;
import com.scholarship.management.model.Scholarship;
import com.scholarship.management.model.ScholarshipApplication;
import com.scholarship.management.model.User;
import com.scholarship.management.repository.ApplicationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

/** Business logic for applications, including every eligibility rule. */
@Service
public class ApplicationService {

    private final ApplicationRepository applications;
    private final UserService userService;
    private final ScholarshipService scholarshipService;

    public ApplicationService(ApplicationRepository applications, UserService userService,
                              ScholarshipService scholarshipService) {
        this.applications = applications;
        this.userService = userService;
        this.scholarshipService = scholarshipService;
    }

    /** Admin: every application. */
    public List<ScholarshipApplication> getAll() {
        return applications.findAllByOrderByIdDesc();
    }

    /** Student: only their own applications. */
    public List<ScholarshipApplication> getForStudent(Long studentId) {
        return applications.findByStudentIdOrderByIdDesc(studentId);
    }

    /** Student applies. All rules are checked here before saving. */
    public ScholarshipApplication apply(ApplicationRequest request) {
        User student = userService.getStudent(request.studentId());
        Scholarship scholarship = scholarshipService.getById(request.scholarshipId());

        if (scholarship.getDeadline().isBefore(LocalDate.now())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "The deadline for this scholarship has passed");
        }
        if (!scholarship.isOpenToGender(student.getGender())) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "This scholarship is only for " + scholarship.getGender().toLowerCase() + " students");
        }
        if (!scholarship.isOpenToCategory(student.getCategory())) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "This scholarship is only for these categories: " + scholarship.getCategories());
        }
        if (applications.existsByStudentIdAndScholarshipId(student.getId(), scholarship.getId())) {
            throw new ApiException(HttpStatus.CONFLICT, "You have already applied for this scholarship");
        }

        ScholarshipApplication application = new ScholarshipApplication();
        application.setStudent(student);
        application.setScholarship(scholarship);
        copyDetails(request, application);
        application.setStatus(ApplicationStatus.PENDING);
        application.setAppliedOn(LocalDate.now());
        return applications.save(application);
    }

    /** Student edits their own application while it is still pending. */
    public ScholarshipApplication update(Long id, ApplicationRequest request) {
        ScholarshipApplication application = getOwnPending(id, request.studentId(), "edited");
        copyDetails(request, application);
        return applications.save(application);
    }

    /** Student withdraws (deletes) their own pending application. */
    public void withdraw(Long id, Long studentId) {
        applications.delete(getOwnPending(id, studentId, "withdrawn"));
    }

    /** Admin approves or rejects. */
    public ScholarshipApplication changeStatus(Long id, ApplicationStatus status) {
        ScholarshipApplication application = findById(id);
        application.setStatus(status);
        return applications.save(application);
    }

    private ScholarshipApplication findById(Long id) {
        return applications.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Application not found"));
    }

    private ScholarshipApplication getOwnPending(Long id, Long studentId, String action) {
        ScholarshipApplication application = findById(id);
        if (!application.getStudent().getId().equals(studentId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "This is not your application");
        }
        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Only pending applications can be " + action);
        }
        return application;
    }

    private void copyDetails(ApplicationRequest request, ScholarshipApplication application) {
        application.setCourse(request.course().trim());
        application.setPercentage(request.percentage());
        application.setAnnualIncome(request.annualIncome());
    }
}
