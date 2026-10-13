package com.scholarship.management.service;

import com.scholarship.management.dto.LoginRequest;
import com.scholarship.management.dto.UserRequest;
import com.scholarship.management.exception.ApiException;
import com.scholarship.management.model.Role;
import com.scholarship.management.model.User;
import com.scholarship.management.repository.ApplicationRepository;
import com.scholarship.management.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/** Business logic for login, registration and student records. */
@Service
public class UserService {

    private final UserRepository users;
    private final ApplicationRepository applications;
    private final BCryptPasswordEncoder encoder;

    public UserService(UserRepository users, ApplicationRepository applications, BCryptPasswordEncoder encoder) {
        this.users = users;
        this.applications = applications;
        this.encoder = encoder;
    }

    /** Checks email and password. Returns the user so React knows the role. */
    public User login(LoginRequest request) {
        User user = users.findByEmailIgnoreCase(request.email().trim())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Wrong email or password"));
        if (!encoder.matches(request.password(), user.getPassword())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Wrong email or password");
        }
        return user;
    }

    /** Creates a student account (used by the Register page and by the admin). */
    public User createStudent(UserRequest request) {
        if (request.password() == null || request.password().length() < 6) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Password must be at least 6 characters");
        }
        if (users.existsByEmailIgnoreCase(request.email().trim())) {
            throw new ApiException(HttpStatus.CONFLICT, "An account with this email already exists");
        }
        User student = new User();
        student.setRole(Role.STUDENT);
        copyDetails(request, student);
        student.setPassword(encoder.encode(request.password()));
        return users.save(student);
    }

    public List<User> getAllStudents() {
        return users.findByRoleOrderByIdDesc(Role.STUDENT);
    }

    public User getStudent(Long id) {
        User user = users.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Student not found"));
        if (user.getRole() != Role.STUDENT) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Student not found");
        }
        return user;
    }

    /** Edits a student. The password is changed only if a new one is given. */
    public User updateStudent(Long id, UserRequest request) {
        User student = getStudent(id);
        String newEmail = request.email().trim();
        if (!student.getEmail().equalsIgnoreCase(newEmail) && users.existsByEmailIgnoreCase(newEmail)) {
            throw new ApiException(HttpStatus.CONFLICT, "An account with this email already exists");
        }
        copyDetails(request, student);
        if (request.password() != null && !request.password().isBlank()) {
            if (request.password().length() < 6) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Password must be at least 6 characters");
            }
            student.setPassword(encoder.encode(request.password()));
        }
        return users.save(student);
    }

    /** Deletes a student together with all of their applications. */
    @Transactional
    public void deleteStudent(Long id) {
        User student = getStudent(id);
        applications.deleteByStudentId(student.getId());
        users.delete(student);
    }

    private void copyDetails(UserRequest request, User user) {
        user.setName(request.name().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setGender(request.gender());
        user.setCategory(request.category());
        user.setPhone(request.phone());
        user.setCourse(request.course());
    }
}
