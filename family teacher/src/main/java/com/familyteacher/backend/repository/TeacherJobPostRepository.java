package com.familyteacher.backend.repository;

import com.familyteacher.backend.entity.TeacherJobPost;
import com.familyteacher.backend.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TeacherJobPostRepository extends JpaRepository<TeacherJobPost, Long> {
    List<TeacherJobPost> findByTeacher(Teacher teacher);
    List<TeacherJobPost> findByTeacherAndActiveTrue(Teacher teacher);
    List<TeacherJobPost> findByActiveTrue();
    List<TeacherJobPost> findBySubjectContainingIgnoreCaseAndActiveTrue(String subject);
}
