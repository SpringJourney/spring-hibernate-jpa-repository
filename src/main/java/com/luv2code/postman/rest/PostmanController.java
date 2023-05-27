package com.luv2code.postman.rest;

import com.github.javafaker.Faker;
import com.luv2code.postman.entity.Student;
import com.luv2code.postman.repository.PostmanRepository;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PostmanController {
    private final PostmanRepository postmanRepository;

    public PostmanController(final PostmanRepository postmanRepository) {
        this.postmanRepository = postmanRepository;
    }

    // define endpoint for "/students" - return list of students
    @GetMapping("/students")
    public Iterable<Student> getStudents() {
        return postmanRepository.findAll();
    }

    @PostMapping("/addstudent")
    @ResponseStatus(HttpStatus.CREATED)
    public Student addStudent(@RequestBody Student student) {
        if(ObjectUtils.isEmpty(student.getFirstName()) || ObjectUtils.isEmpty(student.getLastName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "First or last name is empty");
        }
        return this.postmanRepository.save(student);
    }

    // define endpoint for "/students/{studentId}" - return student at index
    @GetMapping("/students/find/{studentId}")
    public Student getStudent(@PathVariable("studentId") int studentId) {
        Optional<Student> student = postmanRepository.findById(studentId);
        if(student.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        }
        return student.get();
    }

    @PostMapping("/students/generate/{count}")
    @ResponseStatus(HttpStatus.CREATED)
    public Iterable<Student> generateStudents(@PathVariable("count") int count) {
        List<Student> fakerStudents = new ArrayList<>();
        Faker faker = new Faker();
        for (int i = 0; i < count; i++) {
            Student tempStudent = new Student(faker.name().firstName(),faker.name().lastName());
            fakerStudents.add(tempStudent);
        }
        return this.postmanRepository.saveAll(fakerStudents);
    }
    // student status change
    @PutMapping("/students/change/{studentId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Student statusChange(@PathVariable("studentId") int studentId, @RequestBody Student student) {
        Optional<Student> studentToUpdate = this.postmanRepository.findById(studentId);
        if(studentToUpdate.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        }
        Student updatedStudent = studentToUpdate.get();
        if(student.getFirstName() != null) {
            updatedStudent.setFirstName(student.getFirstName());
        }
        if(student.getLastName() != null) {
            updatedStudent.setLastName(student.getLastName());
        }
        if(student.getStatus() != null) {
            updatedStudent.setStatus(student.getStatus());
        }
        return this.postmanRepository.save(updatedStudent);
    }

    @DeleteMapping("/students/delete/{studentId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Student deleteStudent(@PathVariable("studentId") int studentId) {
        Optional<Student> studentToDelete = this.postmanRepository.findById(studentId);
        if(studentToDelete.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        }
        Student deletedStudent = studentToDelete.get();
        this.postmanRepository.delete(deletedStudent);
        return deletedStudent;
    }

    @GetMapping("/students/find")
    @ResponseStatus(HttpStatus.OK)
    public List<Student> findStudent(
            @RequestParam(name = "first_name", required = false) String firstName,
            @RequestParam(name = "last_name", required = false) String lastName,
            @RequestParam(name = "status", required = false) String status
    ) {
        if(firstName !=null && lastName !=null && status !=null) {
            List<Student> findStudents = this.postmanRepository.findByFirstNameAndLastNameAndStatus(firstName,lastName,status);
            if(findStudents.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Student were found");
            }
            return findStudents;
        } else if(firstName !=null && lastName !=null) {
            List<Student> findStudents = this.postmanRepository.findByFirstNameAndLastName(firstName,lastName);
            if(findStudents.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Student were found");
            }
            return findStudents;
        } else if(firstName != null) {
            List<Student> findStudents = this.postmanRepository.findByFirstName(firstName);
            if(findStudents.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Student were found");
            }
            return findStudents;
        } else if(lastName != null) {
            List<Student> findStudents = this.postmanRepository.findByLastName(lastName);
            if(findStudents.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Student were found");
            }
            return findStudents;
        } else if(status != null){
            List<Student> findStudents = this.postmanRepository.findByStatus(status);
            if(findStudents.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Student were found");
            }
            return findStudents;
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All fields are empty");
    }

    @DeleteMapping("students/deteteall")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public long deleteAll() {
        long rowDelete = this.postmanRepository.count();
        this.postmanRepository.deleteAll();
        return rowDelete;
    }
}
