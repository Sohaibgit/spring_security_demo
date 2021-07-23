package com.spring.security.student;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("management/api/v1/students")
public class StudentManagementController {
    private static final Logger LOGGER = LoggerFactory.getLogger(StudentManagementController.class);

    private static final List<Student> STUDENTS = new ArrayList<>(
            Arrays.asList(
                    new Student(1, "Mariam Jamal"),
                    new Student(2, "Ahsan Khan"),
                    new Student(3, "Danial")
            )
    );

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMINTRAINEE')")
    public List<Student> getStudents() {
        return STUDENTS;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('student:write')")
    public void registerStudent(@RequestBody Student student) {
        LOGGER.info("registerStudent::student: " + student);
    }

    @DeleteMapping(path = "{studentId}")
    @PreAuthorize("hasAuthority('student:write')")
    public void deleteStudent(@PathVariable("studentId") Integer studentId) {
        LOGGER.info("deleteStudent::studentId: " + studentId);
    }

    @PutMapping(path = "{studentId}")
    @PreAuthorize("hasAuthority('student:write')")
    public void updateStudent(@PathVariable("studentId") Integer studentId, @RequestBody Student student) {
        LOGGER.info("updateStudent::studentId: " + studentId + ", student: " + student);
    }
}
