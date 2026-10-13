package com.scholarship.management.controller;

import com.scholarship.management.dto.ApplicationRequest;
import com.scholarship.management.model.ApplicationStatus;
import com.scholarship.management.model.ScholarshipApplication;
import com.scholarship.management.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** Applications: students apply, edit and withdraw; the admin views all and approves or rejects. */
@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    /** Admin: all applications */
    @GetMapping
    public List<ScholarshipApplication> getAll() {
        return applicationService.getAll();
    }

    /** Student: GET /api/applications/student/5 */
    @GetMapping("/student/{studentId}")
    public List<ScholarshipApplication> getForStudent(@PathVariable Long studentId) {
        return applicationService.getForStudent(studentId);
    }

    /** Student: apply */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ScholarshipApplication apply(@Valid @RequestBody ApplicationRequest request) {
        return applicationService.apply(request);
    }

    /** Student: edit a pending application */
    @PutMapping("/{id}")
    public ScholarshipApplication update(@PathVariable Long id, @Valid @RequestBody ApplicationRequest request) {
        return applicationService.update(id, request);
    }

    /** Student: withdraw a pending application - DELETE /api/applications/7?studentId=5 */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void withdraw(@PathVariable Long id, @RequestParam Long studentId) {
        applicationService.withdraw(id, studentId);
    }

    /** Admin: PATCH /api/applications/7/status?value=APPROVED */
    @PatchMapping("/{id}/status")
    public ScholarshipApplication changeStatus(@PathVariable Long id, @RequestParam ApplicationStatus value) {
        return applicationService.changeStatus(id, value);
    }
}
