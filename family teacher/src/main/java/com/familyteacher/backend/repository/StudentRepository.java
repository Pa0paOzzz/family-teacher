package com.familyteacher.backend.repository;

import com.familyteacher.backend.entity.Student;
import com.familyteacher.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUser(User user);
}