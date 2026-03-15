package com.familyteacher.backend.service;

import com.familyteacher.backend.entity.AppointmentRequest;
import com.familyteacher.backend.entity.Student;
import com.familyteacher.backend.entity.Teacher;
import com.familyteacher.backend.repository.AppointmentRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentRequestRepository appointmentRequestRepository;
    
    public AppointmentRequest createAppointmentRequest(AppointmentRequest request) {
        return appointmentRequestRepository.save(request);
    }
    
    public List<AppointmentRequest> getAppointmentsByStudent(Student student) {
        return appointmentRequestRepository.findByStudent(student);
    }
    
    public List<AppointmentRequest> getAppointmentsByTeacher(Teacher teacher) {
        return appointmentRequestRepository.findByTeacher(teacher);
    }
    
    public List<AppointmentRequest> getAppointmentsByStatus(String status) {
        return appointmentRequestRepository.findByStatus(status);
    }
    
    public AppointmentRequest updateAppointmentStatus(Long id, String status) {
        AppointmentRequest appointment = appointmentRequestRepository.findById(id).orElse(null);
        if (appointment != null) {
            appointment.setStatus(status);
            return appointmentRequestRepository.save(appointment);
        }
        return null;
    }
    
    public AppointmentRequest getAppointmentById(Long id) {
        return appointmentRequestRepository.findById(id).orElse(null);
    }
}