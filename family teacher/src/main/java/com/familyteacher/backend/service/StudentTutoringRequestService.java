package com.familyteacher.backend.service;

import com.familyteacher.backend.entity.StudentTutoringRequest;
import com.familyteacher.backend.entity.Student;
import com.familyteacher.backend.repository.StudentTutoringRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class StudentTutoringRequestService {
    @Autowired
    private StudentTutoringRequestRepository studentTutoringRequestRepository;
    
    public StudentTutoringRequest createTutoringRequest(StudentTutoringRequest request) {
        return studentTutoringRequestRepository.save(request);
    }
    
    public StudentTutoringRequest getTutoringRequestById(Long id) {
        return studentTutoringRequestRepository.findById(id).orElse(null);
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
    
    @Transactional
    public Map<String, Object> createTutoringRequestFromData(Student student, Map<String, Object> requestData) {
        StudentTutoringRequest tutoringRequest = new StudentTutoringRequest();
        tutoringRequest.setStudent(student);
        
        if (requestData.containsKey("title")) {
            tutoringRequest.setTitle((String) requestData.get("title"));
        }
        if (requestData.containsKey("description")) {
            tutoringRequest.setDescription((String) requestData.get("description"));
        }
        if (requestData.containsKey("subject")) {
            tutoringRequest.setSubject((String) requestData.get("subject"));
        }
        if (requestData.containsKey("budgetPerHour")) {
            Object budgetObj = requestData.get("budgetPerHour");
            if (budgetObj instanceof Number) {
                tutoringRequest.setBudgetPerHour(((Number) budgetObj).doubleValue());
            }
        }
        if (requestData.containsKey("location")) {
            tutoringRequest.setLocation((String) requestData.get("location"));
        }
        if (requestData.containsKey("preferredTime")) {
            tutoringRequest.setPreferredTime((String) requestData.get("preferredTime"));
        }
        
        StudentTutoringRequest savedRequest = studentTutoringRequestRepository.save(tutoringRequest);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Tutoring request created successfully");
        response.put("id", savedRequest.getId());
        return response;
    }
    
    @Transactional
    public Map<String, Object> updateTutoringRequestFromData(Long requestId, Map<String, Object> requestData) {
        StudentTutoringRequest tutoringRequest = studentTutoringRequestRepository.findById(requestId).orElse(null);
        if (tutoringRequest == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Tutoring request not found");
            return response;
        }
        
        if (requestData.containsKey("title")) {
            tutoringRequest.setTitle((String) requestData.get("title"));
        }
        if (requestData.containsKey("description")) {
            tutoringRequest.setDescription((String) requestData.get("description"));
        }
        if (requestData.containsKey("subject")) {
            tutoringRequest.setSubject((String) requestData.get("subject"));
        }
        if (requestData.containsKey("budgetPerHour")) {
            Object budgetObj = requestData.get("budgetPerHour");
            if (budgetObj instanceof Number) {
                tutoringRequest.setBudgetPerHour(((Number) budgetObj).doubleValue());
            }
        }
        if (requestData.containsKey("location")) {
            tutoringRequest.setLocation((String) requestData.get("location"));
        }
        if (requestData.containsKey("preferredTime")) {
            tutoringRequest.setPreferredTime((String) requestData.get("preferredTime"));
        }
        
        studentTutoringRequestRepository.save(tutoringRequest);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Tutoring request updated successfully");
        return response;
    }
    
    public Map<String, Object> deactivateTutoringRequestResponse(Long id) {
        StudentTutoringRequest request = studentTutoringRequestRepository.findById(id).orElse(null);
        if (request != null) {
            request.setActive(false);
            studentTutoringRequestRepository.save(request);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Tutoring request deactivated successfully");
        return response;
    }
}
