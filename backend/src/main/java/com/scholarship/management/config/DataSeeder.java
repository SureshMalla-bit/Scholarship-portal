package com.scholarship.management.config;

import com.scholarship.management.model.Role;
import com.scholarship.management.model.Scholarship;
import com.scholarship.management.model.User;
import com.scholarship.management.repository.ScholarshipRepository;
import com.scholarship.management.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;

/**
 * Runs once when the server starts.
 * Creates the admin account and some sample Indian scholarships if the tables are empty.
 * The amounts follow the real schemes but the deadlines are sample dates - check them before presenting.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository users;
    private final ScholarshipRepository scholarships;
    private final BCryptPasswordEncoder encoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    public DataSeeder(UserRepository users, ScholarshipRepository scholarships, BCryptPasswordEncoder encoder) {
        this.users = users;
        this.scholarships = scholarships;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (!users.existsByRole(Role.ADMIN)) {
            if ("admin123".equals(adminPassword)) {
                log.warn("Admin account created with the default password admin123. Set ADMIN_PASSWORD before deploying.");
            }
            User admin = new User();
            admin.setName("Administrator");
            admin.setEmail(adminEmail);
            admin.setPassword(encoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            users.save(admin);
        }

        if (scholarships.count() == 0) {
            scholarships.saveAll(List.of(
                new Scholarship("Central Sector Scholarship for College and University Students",
                    "Ministry of Education (NSP)", "UG", "Rs 12,000 per year (UG), Rs 20,000 per year (PG)",
                    LocalDate.of(2026, 11, 30), "All", "All",
                    "For students above the 80th percentile in Class 12 with low family income."),
                new Scholarship("AICTE Pragati Scholarship for Girls", "AICTE", "UG", "Rs 50,000 per year",
                    LocalDate.of(2026, 12, 31), "Female", "All",
                    "For girl students admitted to an AICTE-approved technical degree or diploma course."),
                new Scholarship("AICTE Saksham Scholarship", "AICTE", "UG", "Rs 50,000 per year",
                    LocalDate.of(2026, 12, 31), "All", "All",
                    "For specially-abled students in AICTE-approved technical courses."),
                new Scholarship("Post-Matric Scholarship for SC Students", "Government of India and States", "Any",
                    "Course fees plus maintenance allowance", LocalDate.of(2026, 12, 15), "All", "SC",
                    "For SC students studying after Class 10, subject to a family income limit."),
                new Scholarship("Telangana ePASS Post-Matric Scholarship", "Government of Telangana", "Any",
                    "Fee reimbursement plus maintenance allowance", LocalDate.of(2026, 12, 15), "All",
                    "SC,ST,BC,EBC,Minority",
                    "For eligible students of Telangana from SC, ST, BC, EBC and minority communities."),
                new Scholarship("Prime Minister's Scholarship Scheme", "Ministry of Defence", "UG",
                    "Rs 2,500 per month (boys), Rs 3,000 per month (girls)", LocalDate.of(2026, 11, 30), "All", "All",
                    "For children of ex-servicemen in professional degree courses."),
                new Scholarship("INSPIRE Scholarship (SHE)", "Department of Science and Technology", "UG",
                    "Rs 80,000 per year", LocalDate.of(2026, 11, 30), "All", "All",
                    "For top-ranked students studying basic and natural sciences."),
                new Scholarship("Kotak Kanya Scholarship", "Kotak Education Foundation", "UG", "Rs 1.5 lakh per year",
                    LocalDate.of(2027, 1, 31), "Female", "All",
                    "For girl students in professional degree courses from low-income families."),
                new Scholarship("Reliance Foundation Undergraduate Scholarship", "Reliance Foundation", "UG",
                    "Up to Rs 2 lakh for the full degree", LocalDate.of(2026, 12, 31), "All", "All",
                    "For first-year undergraduate students with low family income.")
            ));
        }
    }
}
