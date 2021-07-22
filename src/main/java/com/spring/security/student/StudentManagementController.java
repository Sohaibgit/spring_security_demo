package com.spring.security.student;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public List<Student> getStudents() {
        return STUDENTS;
    }

    @PostMapping
    public void registerStudent(@RequestBody Student student) {
        LOGGER.info("registerStudent::student: " + student);
    }

    @DeleteMapping(path = "{studentId}")
    public void deleteStudent(@PathVariable("studentId") Integer studentId) {
        LOGGER.info("deleteStudent::studentId: " + studentId);
    }

    @PutMapping(path = "{studentId}")
    public void updateStudent(@PathVariable("studentId") Integer studentId, @RequestBody Student student) {
        LOGGER.info("updateStudent::studentId: " + studentId + ", student: " + student);
    }
}
