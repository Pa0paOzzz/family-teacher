package com.familyteacher.backend.service;

import com.familyteacher.backend.entity.Teacher;
import com.familyteacher.backend.entity.TeacherJobPost;
import com.familyteacher.backend.repository.TeacherJobPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TeacherJobPostService {
    @Autowired
    private TeacherJobPostRepository teacherJobPostRepository;

    public TeacherJobPost createJobPost(TeacherJobPost jobPost) {
        return teacherJobPostRepository.save(jobPost);
    }

    public TeacherJobPost getJobPostById(Long id) {
        return teacherJobPostRepository.findById(id).orElse(null);
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

    @Transactional
    public Map<String, Object> createJobPost(Teacher teacher, Map<String, Object> jobPostData) {
        TeacherJobPost jobPost = new TeacherJobPost();
        jobPost.setTeacher(teacher);

        if (jobPostData.containsKey("title")) {
            jobPost.setTitle((String) jobPostData.get("title"));
        }
        if (jobPostData.containsKey("description")) {
            jobPost.setDescription((String) jobPostData.get("description"));
        }
        if (jobPostData.containsKey("subject")) {
            jobPost.setSubject((String) jobPostData.get("subject"));
        }
        if (jobPostData.containsKey("pricePerHour")) {
            Object priceObj = jobPostData.get("pricePerHour");
            if (priceObj instanceof Number) {
                jobPost.setPricePerHour(((Number) priceObj).doubleValue());
            }
        }
        applyLocation(jobPost, jobPostData);
        if (jobPostData.containsKey("availability")) {
            jobPost.setAvailability((String) jobPostData.get("availability"));
        }

        TeacherJobPost savedJobPost = teacherJobPostRepository.save(jobPost);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Job post created successfully");
        response.put("id", savedJobPost.getId());
        return response;
    }

    @Transactional
    public Map<String, Object> updateJobPost(Long jobPostId, Map<String, Object> jobPostData) {
        TeacherJobPost jobPost = teacherJobPostRepository.findById(jobPostId).orElse(null);
        if (jobPost == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Job post not found");
            return response;
        }

        if (jobPostData.containsKey("title")) {
            jobPost.setTitle((String) jobPostData.get("title"));
        }
        if (jobPostData.containsKey("description")) {
            jobPost.setDescription((String) jobPostData.get("description"));
        }
        if (jobPostData.containsKey("subject")) {
            jobPost.setSubject((String) jobPostData.get("subject"));
        }
        if (jobPostData.containsKey("pricePerHour")) {
            Object priceObj = jobPostData.get("pricePerHour");
            if (priceObj instanceof Number) {
                jobPost.setPricePerHour(((Number) priceObj).doubleValue());
            }
        }
        applyLocation(jobPost, jobPostData);
        if (jobPostData.containsKey("availability")) {
            jobPost.setAvailability((String) jobPostData.get("availability"));
        }

        teacherJobPostRepository.save(jobPost);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Job post updated successfully");
        return response;
    }

    public Map<String, Object> deactivateJobPostResponse(Long id) {
        TeacherJobPost jobPost = teacherJobPostRepository.findById(id).orElse(null);
        if (jobPost != null) {
            jobPost.setActive(false);
            teacherJobPostRepository.save(jobPost);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Job post deactivated successfully");
        return response;
    }

    private void applyLocation(TeacherJobPost jobPost, Map<String, Object> jobPostData) {
        if (!hasLocationPayload(jobPostData)) {
            return;
        }

        String formatted = getString(jobPostData, "locationFormatted");
        String rawLocation = getString(jobPostData, "location");
        jobPost.setLocation(StringUtils.hasText(formatted) ? formatted : rawLocation);
        jobPost.setLocationProvince(getString(jobPostData, "locationProvince"));
        jobPost.setLocationCity(getString(jobPostData, "locationCity"));
        jobPost.setLocationDistrict(getString(jobPostData, "locationDistrict"));
        jobPost.setLocationFormatted(formatted);
        jobPost.setLocationLongitude(null);
        jobPost.setLocationLatitude(null);
    }

    private boolean hasLocationPayload(Map<String, Object> jobPostData) {
        return jobPostData.containsKey("location")
                || jobPostData.containsKey("locationProvince")
                || jobPostData.containsKey("locationCity")
                || jobPostData.containsKey("locationDistrict")
                || jobPostData.containsKey("locationFormatted");
    }

    private String getString(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value == null ? null : String.valueOf(value);
    }
}
