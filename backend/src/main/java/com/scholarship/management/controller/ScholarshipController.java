package com.scholarship.management.controller;

import com.scholarship.management.model.Scholarship;
import com.scholarship.management.security.CurrentUser;
import com.scholarship.management.service.ScholarshipService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** Scholarships: students read them; the admin creates, updates and deletes them. */
@RestController
@RequestMapping("/api/scholarships")
public class ScholarshipController {

    private final ScholarshipService scholarshipService;

    public ScholarshipController(ScholarshipService scholarshipService) {
        this.scholarshipService = scholarshipService;
    }

    @GetMapping
    public List<Scholarship> getAll() {
        return scholarshipService.getAll();
    }

    @GetMapping("/{id}")
    public Scholarship getOne(@PathVariable Long id) {
        return scholarshipService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Scholarship create(@Valid @RequestBody Scholarship scholarship, @RequestAttribute(CurrentUser.ATTRIBUTE) CurrentUser me) {
        me.requireAdmin();
        return scholarshipService.create(scholarship);
    }

    @PutMapping("/{id}")
    public Scholarship update(@PathVariable Long id, @Valid @RequestBody Scholarship scholarship,
                              @RequestAttribute(CurrentUser.ATTRIBUTE) CurrentUser me) {
        me.requireAdmin();
        return scholarshipService.update(id, scholarship);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, @RequestAttribute(CurrentUser.ATTRIBUTE) CurrentUser me) {
        me.requireAdmin();
        scholarshipService.delete(id);
    }
}
