package com.scholarship.management.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * A student's application for one scholarship.
 * Many applications can belong to one student and to one scholarship (ManyToOne).
 */
@Entity
public class ScholarshipApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User student;

    @ManyToOne(optional = false)
    private Scholarship scholarship;

    private String course;

    private Double percentage;

    /** Yearly family income in rupees */
    private Long annualIncome;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    private LocalDate appliedOn = LocalDate.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    public Scholarship getScholarship() { return scholarship; }
    public void setScholarship(Scholarship scholarship) { this.scholarship = scholarship; }
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
    public Double getPercentage() { return percentage; }
    public void setPercentage(Double percentage) { this.percentage = percentage; }
    public Long getAnnualIncome() { return annualIncome; }
    public void setAnnualIncome(Long annualIncome) { this.annualIncome = annualIncome; }
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
    public LocalDate getAppliedOn() { return appliedOn; }
    public void setAppliedOn(LocalDate appliedOn) { this.appliedOn = appliedOn; }
}
