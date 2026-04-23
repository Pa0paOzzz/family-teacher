package com.familyteacher.backend.service;

import com.familyteacher.backend.entity.Student;
import com.familyteacher.backend.entity.User;
import com.familyteacher.backend.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;
    
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }
    
    public Optional<Student> findByUser(User user) {
        return studentRepository.findByUser(user);
    }

    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }

    public Student updateStudent(Student student) {
        return studentRepository.save(student);
    }
}