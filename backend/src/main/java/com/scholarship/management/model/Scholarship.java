package com.scholarship.management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Arrays;

/** One scholarship that students can apply for. Stored in the "scholarship" table. */
@Entity
public class Scholarship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Scholarship name is required")
    private String name;

    @NotBlank(message = "Provider is required")
    private String provider;

    /** Diploma, UG, PG, PhD or Any */
    @NotBlank(message = "Level is required")
    private String level;

    @NotBlank(message = "Amount is required")
    private String amount;

    @NotNull(message = "Deadline is required")
    private LocalDate deadline;

    /** Who can apply by gender: All, Female or Male */
    private String gender = "All";

    /** Who can apply by category: "All" or a list such as "SC,ST,BC" */
    private String categories = "All";

    @Column(length = 1000)
    private String description;

    public Scholarship() {}

    public Scholarship(String name, String provider, String level, String amount, LocalDate deadline,
                       String gender, String categories, String description) {
        this.name = name;
        this.provider = provider;
        this.level = level;
        this.amount = amount;
        this.deadline = deadline;
        this.gender = gender;
        this.categories = categories;
        this.description = description;
    }

    /** True if a student of this gender may apply. */
    public boolean isOpenToGender(String studentGender) {
        return getGender().equalsIgnoreCase("All") || getGender().equalsIgnoreCase(studentGender);
    }

    /** True if a student of this category may apply. */
    public boolean isOpenToCategory(String studentCategory) {
        if (getCategories().equalsIgnoreCase("All")) {
            return true;
        }
        return Arrays.stream(getCategories().split(","))
                .map(String::trim)
                .anyMatch(c -> c.equalsIgnoreCase(studentCategory));
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public String getGender() { return gender == null || gender.isBlank() ? "All" : gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getCategories() { return categories == null || categories.isBlank() ? "All" : categories; }
    public void setCategories(String categories) { this.categories = categories; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
