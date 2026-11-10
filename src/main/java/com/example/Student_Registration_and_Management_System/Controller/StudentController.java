package com.example.Student_Registration_and_Management_System.Controller;

import com.example.Student_Registration_and_Management_System.Model.Student;
import com.example.Student_Registration_and_Management_System.Service.StudentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }


    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("student", new Student());
        return "register";
    }


    @PostMapping("/register")
    public String registerStudent(
            @Valid @ModelAttribute("student") Student student,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "register";
        }

        try {
            studentService.saveStudent(student);
            model.addAttribute("successMessage",
                    "Student registered successfully!");
            model.addAttribute("student", new Student());

        } catch (IllegalStateException e) {
            model.addAttribute("databaseError",
                    "Could not register student. Check whether the email is already registered.");
            model.addAttribute("student", student);
        }

        return "register";
    }


    @GetMapping("/all")
    public String showAllStudents(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        return "students";
    }


    @GetMapping("/{id}")
    public String showStudentById(@PathVariable int id, Model model) {

        return studentService.getStudentById(id)
                .map(student -> {
                    model.addAttribute("selectedStudent", student);
                    model.addAttribute("students", List.of(student));
                    return "students";
                })
                .orElseGet(() -> {
                    model.addAttribute("notFound",
                            "No student found with ID: " + id);
                    model.addAttribute("students", List.of());
                    return "students";
                });
    }
}