package com.familyteacher.backend.repository;

import com.familyteacher.backend.entity.StudentTutoringRequest;
import com.familyteacher.backend.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudentTutoringRequestRepository extends JpaRepository<StudentTutoringRequest, Long> {
    List<StudentTutoringRequest> findByStudent(Student student);
    List<StudentTutoringRequest> findByActiveTrue();
    List<StudentTutoringRequest> findBySubjectContainingIgnoreCaseAndActiveTrue(String subject);
}