package com.familyteacher.backend.repository;

import com.familyteacher.backend.entity.AppointmentRequest;
import com.familyteacher.backend.entity.Student;
import com.familyteacher.backend.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AppointmentRequestRepository extends JpaRepository<AppointmentRequest, Long> {
    List<AppointmentRequest> findByStudent(Student student);
    List<AppointmentRequest> findByTeacher(Teacher teacher);
    List<AppointmentRequest> findByStatus(String status);
    List<AppointmentRequest> findByStudentAndStatus(Student student, String status);
    List<AppointmentRequest> findByTeacherAndStatus(Teacher teacher, String status);
}