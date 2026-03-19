package com.familyteacher.backend.service;

import com.familyteacher.backend.entity.Teacher;
import com.familyteacher.backend.entity.User;
import com.familyteacher.backend.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class TeacherService {
    @Autowired
    private TeacherRepository teacherRepository;
    
    public Teacher saveTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }
    
    public Optional<Teacher> findByUser(User user) {
        return teacherRepository.findByUser(user);
    }
    
    public Optional<Teacher> findById(Long id) {
        return teacherRepository.findById(id);
    }
    
    public Teacher updateTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }
}