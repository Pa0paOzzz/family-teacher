package com.familyteacher.backend.repository;

import com.familyteacher.backend.entity.Teacher;
import com.familyteacher.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByUser(User user);
    Optional<Teacher> findById(Long id);
}