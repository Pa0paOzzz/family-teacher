package com.familyteacher.backend.service;

import com.familyteacher.backend.entity.Student;
import com.familyteacher.backend.entity.StudentTutoringRequest;
import com.familyteacher.backend.repository.StudentTutoringRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        applyLocation(tutoringRequest, requestData);
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
        applyLocation(tutoringRequest, requestData);
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

    @Transactional
    public Map<String, Object> setTutoringRequestActive(Long id, boolean active) {
        StudentTutoringRequest request = studentTutoringRequestRepository.findById(id).orElse(null);
        Map<String, Object> response = new HashMap<>();
        if (request == null) {
            response.put("success", false);
            response.put("error", "Tutoring request not found");
            return response;
        }

        request.setActive(active);
        studentTutoringRequestRepository.save(request);

        response.put("success", true);
        response.put("message", active ? "Tutoring request activated successfully" : "Tutoring request deactivated successfully");
        return response;
    }

    @Transactional
    public Map<String, Object> adminUpdateTutoringRequest(Long id, Map<String, Object> requestData) {
        Map<String, Object> response = updateTutoringRequestFromData(id, requestData);
        if (!response.containsKey("success")) {
            response.put("success", false);
        }
        return response;
    }

    @Transactional
    public Map<String, Object> adminDeleteTutoringRequest(Long id) {
        StudentTutoringRequest request = studentTutoringRequestRepository.findById(id).orElse(null);
        Map<String, Object> response = new HashMap<>();
        if (request == null) {
            response.put("success", false);
            response.put("error", "Tutoring request not found");
            return response;
        }

        studentTutoringRequestRepository.delete(request);
        response.put("success", true);
        response.put("message", "Tutoring request deleted successfully");
        return response;
    }

    private void applyLocation(StudentTutoringRequest tutoringRequest, Map<String, Object> requestData) {
        if (!hasLocationPayload(requestData)) {
            return;
        }

        String formatted = getString(requestData, "locationFormatted");
        String rawLocation = getString(requestData, "location");
        tutoringRequest.setLocation(StringUtils.hasText(formatted) ? formatted : rawLocation);
        tutoringRequest.setLocationProvince(getString(requestData, "locationProvince"));
        tutoringRequest.setLocationCity(getString(requestData, "locationCity"));
        tutoringRequest.setLocationDistrict(getString(requestData, "locationDistrict"));
        tutoringRequest.setLocationFormatted(formatted);
        tutoringRequest.setLocationLongitude(null);
        tutoringRequest.setLocationLatitude(null);
    }

    private boolean hasLocationPayload(Map<String, Object> requestData) {
        return requestData.containsKey("location")
                || requestData.containsKey("locationProvince")
                || requestData.containsKey("locationCity")
                || requestData.containsKey("locationDistrict")
                || requestData.containsKey("locationFormatted");
    }

    private String getString(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value == null ? null : String.valueOf(value);
    }
}
