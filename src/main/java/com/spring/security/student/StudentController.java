package com.spring.security.student;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController {

    private static final List<Student> STUDENTS = new ArrayList<>(
            Arrays.asList(
                    new Student(1,"Mariam Jamal"),
                    new Student(2, "Ahsan Khan"),
                    new Student(3,"Danial")
            )
    );


    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable("id") Integer id) {
            return STUDENTS.stream()
                    .filter(student -> student.getStudentId().equals(id))
                    .findFirst()
                    .orElseThrow(()->new IllegalStateException("Student with id-"+id+" does not exist"));
    }
}
