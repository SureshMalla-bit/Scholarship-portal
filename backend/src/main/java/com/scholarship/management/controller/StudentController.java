package com.scholarship.management.controller;

import com.scholarship.management.dto.UserRequest;
import com.scholarship.management.model.User;
import com.scholarship.management.security.CurrentUser;
import com.scholarship.management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** Student records: full CRUD for the admin; GET and PUT on their own id for a student's profile. */
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final UserService userService;

    public StudentController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAll(@RequestAttribute(CurrentUser.ATTRIBUTE) CurrentUser me) {
        me.requireAdmin();
        return userService.getAllStudents();
    }

    @GetMapping("/{id}")
    public User getOne(@PathVariable Long id, @RequestAttribute(CurrentUser.ATTRIBUTE) CurrentUser me) {
        me.requireSelfOrAdmin(id);
        return userService.getStudent(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody UserRequest request, @RequestAttribute(CurrentUser.ATTRIBUTE) CurrentUser me) {
        me.requireAdmin();
        return userService.createStudent(request);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @Valid @RequestBody UserRequest request,
                       @RequestAttribute(CurrentUser.ATTRIBUTE) CurrentUser me) {
        me.requireSelfOrAdmin(id);
        return userService.updateStudent(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, @RequestAttribute(CurrentUser.ATTRIBUTE) CurrentUser me) {
        me.requireAdmin();
        userService.deleteStudent(id);
    }
}
