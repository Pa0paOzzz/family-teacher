package com.familyteacher.backend.service;

import com.familyteacher.backend.entity.StudentTutoringRequest;
import com.familyteacher.backend.entity.Student;
import com.familyteacher.backend.repository.StudentTutoringRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StudentTutoringRequestService {
    @Autowired
    private StudentTutoringRequestRepository studentTutoringRequestRepository;
    
    public StudentTutoringRequest createTutoringRequest(StudentTutoringRequest request) {
        return studentTutoringRequestRepository.save(request);
    }
    
    public List<StudentTutoringRequest> getRequestsByStudent(Student student) {
        return studentTutoringRequestRepository.findByStudent(student);
    }
    
    public List<StudentTutoringRequest> getAllActiveRequests() {
        return studentTutoringRequestRepository.findByActiveTrue();
    }
    
    public List<StudentTutoringRequest> searchRequestsBySubject(String subject) {
        return studentTutoringRequestRepository.findBySubjectContainingIgnoreCaseAndActiveTrue(subject);
    }
    
    public StudentTutoringRequest updateTutoringRequest(StudentTutoringRequest request) {
        return studentTutoringRequestRepository.save(request);
    }
    
    public void deactivateTutoringRequest(Long id) {
        StudentTutoringRequest request = studentTutoringRequestRepository.findById(id).orElse(null);
        if (request != null) {
            request.setActive(false);
            studentTutoringRequestRepository.save(request);
        }
    }
}