package com.familyteacher.backend.service;

import com.familyteacher.backend.entity.Favorite;
import com.familyteacher.backend.entity.User;
import com.familyteacher.backend.entity.TeacherJobPost;
import com.familyteacher.backend.entity.StudentTutoringRequest;
import com.familyteacher.backend.repository.FavoriteRepository;
import com.familyteacher.backend.repository.TeacherJobPostRepository;
import com.familyteacher.backend.repository.StudentTutoringRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class FavoriteService {
    @Autowired
    private FavoriteRepository favoriteRepository;
    
    @Autowired
    private TeacherJobPostRepository teacherJobPostRepository;
    
    @Autowired
    private StudentTutoringRequestRepository studentTutoringRequestRepository;
    
    @Transactional
    public Map<String, Object> addFavorite(User user, String resourceType, Long resourceId) {
        Optional<Favorite> existingFavorite = favoriteRepository
            .findByUserIdAndResourceIdAndResourceType(user.getId(), resourceId, resourceType);
        
        if (existingFavorite.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Already in favorites");
            return response;
        }
        
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setResourceType(resourceType);
        favorite.setResourceId(resourceId);
        favoriteRepository.save(favorite);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Added to favorites");
        response.put("id", favorite.getId());
        return response;
    }
    
    @Transactional
    public Map<String, Object> removeFavorite(User user, String resourceType, Long resourceId) {
        favoriteRepository.deleteByUserIdAndResourceIdAndResourceType(user.getId(), resourceId, resourceType);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Removed from favorites");
        return response;
    }
    
    public List<Map<String, Object>> getFavorites(User user, String resourceType) {
        List<Favorite> favorites;
        if (resourceType != null && !resourceType.isEmpty()) {
            favorites = favoriteRepository.findByUserIdAndResourceType(user.getId(), resourceType);
        } else {
            favorites = favoriteRepository.findByUserId(user.getId());
        }
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Favorite favorite : favorites) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", favorite.getId());
            item.put("resourceType", favorite.getResourceType());
            item.put("resourceId", favorite.getResourceId());
            item.put("createdAt", favorite.getCreatedAt());
            
            if ("TEACHER_JOB_POST".equals(favorite.getResourceType())) {
                Optional<TeacherJobPost> jobPost = teacherJobPostRepository.findById(favorite.getResourceId());
                if (jobPost.isPresent()) {
                    TeacherJobPost post = jobPost.get();
                    Map<String, Object> resource = new HashMap<>();
                    resource.put("id", post.getId());
                    resource.put("title", post.getTitle());
                    resource.put("description", post.getDescription());
                    resource.put("subject", post.getSubject());
                    resource.put("pricePerHour", post.getPricePerHour());
                    resource.put("location", post.getLocation());
                    resource.put("availability", post.getAvailability());
                    resource.put("active", post.getActive());
                    if (post.getTeacher() != null) {
                        Map<String, Object> teacher = new HashMap<>();
                        teacher.put("id", post.getTeacher().getId());
                        if (post.getTeacher().getUser() != null) {
                            teacher.put("name", post.getTeacher().getUser().getName());
                        } else {
                            teacher.put("name", "未知");
                        }
                        teacher.put("school", post.getTeacher().getSchool());
                        teacher.put("major", post.getTeacher().getMajor());
                        resource.put("teacher", teacher);
                    }
                    item.put("resource", resource);
                }
            } else if ("STUDENT_TUTORING_REQUEST".equals(favorite.getResourceType())) {
                Optional<StudentTutoringRequest> request = studentTutoringRequestRepository.findById(favorite.getResourceId());
                if (request.isPresent()) {
                    StudentTutoringRequest req = request.get();
                    Map<String, Object> resource = new HashMap<>();
                    resource.put("id", req.getId());
                    resource.put("title", req.getTitle());
                    resource.put("description", req.getDescription());
                    resource.put("subject", req.getSubject());
                    resource.put("budgetPerHour", req.getBudgetPerHour());
                    resource.put("location", req.getLocation());
                    resource.put("preferredTime", req.getPreferredTime());
                    resource.put("active", req.getActive());
                    if (req.getStudent() != null) {
                        Map<String, Object> student = new HashMap<>();
                        student.put("id", req.getStudent().getId());
                        if (req.getStudent().getUser() != null) {
                            student.put("name", req.getStudent().getUser().getName());
                        } else {
                            student.put("name", "未知");
                        }
                        student.put("school", req.getStudent().getSchool());
                        resource.put("student", student);
                    }
                    item.put("resource", resource);
                }
            }
            
            result.add(item);
        }
        
        return result;
    }
    
    public boolean isFavorite(User user, String resourceType, Long resourceId) {
        return favoriteRepository.findByUserIdAndResourceIdAndResourceType(user.getId(), resourceId, resourceType).isPresent();
    }
}
