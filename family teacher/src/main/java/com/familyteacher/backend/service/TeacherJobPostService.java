package com.familyteacher.backend.service;

import com.familyteacher.backend.entity.TeacherJobPost;
import com.familyteacher.backend.entity.Teacher;
import com.familyteacher.backend.repository.TeacherJobPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TeacherJobPostService {
    @Autowired
    private TeacherJobPostRepository teacherJobPostRepository;
    
    public TeacherJobPost createJobPost(TeacherJobPost jobPost) {
        return teacherJobPostRepository.save(jobPost);
    }
    
    public List<TeacherJobPost> getJobPostsByTeacher(Teacher teacher) {
        return teacherJobPostRepository.findByTeacher(teacher);
    }
    
    public List<TeacherJobPost> getAllActiveJobPosts() {
        return teacherJobPostRepository.findByActiveTrue();
    }
    
    public List<TeacherJobPost> searchJobPostsBySubject(String subject) {
        return teacherJobPostRepository.findBySubjectContainingIgnoreCaseAndActiveTrue(subject);
    }
    
    public TeacherJobPost updateJobPost(TeacherJobPost jobPost) {
        return teacherJobPostRepository.save(jobPost);
    }
    
    public void deactivateJobPost(Long id) {
        TeacherJobPost jobPost = teacherJobPostRepository.findById(id).orElse(null);
        if (jobPost != null) {
            jobPost.setActive(false);
            teacherJobPostRepository.save(jobPost);
        }
    }
}