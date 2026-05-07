package com.familyteacher.backend.service;

import com.familyteacher.backend.entity.Student;
import com.familyteacher.backend.entity.Teacher;
import com.familyteacher.backend.entity.User;
import com.familyteacher.backend.repository.UserRepository;
import com.familyteacher.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StudentService studentService;

    @Autowired
    private TeacherService teacherService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Map<String, Object> register(User user) {
        Map<String, Object> response = new HashMap<>();
        
        // 检查用户名是否已存在
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            response.put("success", false);
            response.put("error", "用户名已存在");
            return response;
        }
        
        // 检查邮箱是否已存在
        if (StringUtils.hasText(user.getEmail()) && userRepository.findByEmail(user.getEmail()).isPresent()) {
            response.put("success", false);
            response.put("error", "邮箱已被注册");
            return response;
        }
        
        // 加密密码并保存用户
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        
        response.put("success", true);
        response.put("message", "注册成功");
        response.put("user", savedUser);
        return response;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public String login(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (!Boolean.TRUE.equals(user.getDeleted())
                    && !Boolean.FALSE.equals(user.getEnabled())
                    && passwordEncoder.matches(password, user.getPassword())) {
                return jwtUtil.generateToken(username);
            }
        }
        return null;
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserFromToken(String token) {
        if (token == null) {
            return null;
        }
        String username = jwtUtil.getUsernameFromToken(token);
        User user = userRepository.findByUsername(username).orElse(null);
        return user == null || Boolean.TRUE.equals(user.getDeleted()) ? null : user;
    }

    public Map<String, Object> getUserProfile(User user) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("phone", user.getPhone());
        response.put("name", user.getName());
        response.put("role", user.getRole());

        if ("STUDENT".equals(user.getRole())) {
            Optional<Student> studentOptional = studentService.findByUser(user);
            if (studentOptional.isPresent()) {
                Student student = studentOptional.get();
                response.put("studentId", student.getId());
                response.put("school", student.getSchool());
                response.put("grade", student.getGrade());
                response.put("major", student.getMajor());
                putAddressFields(response, student);
            }
        } else if ("TEACHER".equals(user.getRole())) {
            Optional<Teacher> teacherOptional = teacherService.findByUser(user);
            if (teacherOptional.isPresent()) {
                Teacher teacher = teacherOptional.get();
                response.put("teacherId", teacher.getId());
                response.put("school", teacher.getSchool());
                response.put("major", teacher.getMajor());
                response.put("education", teacher.getEducation());
                response.put("teachingExperience", teacher.getTeachingExperience());
                response.put("subject", teacher.getSubject());
                response.put("bio", teacher.getBio());
                response.put("pricePerHour", teacher.getPricePerHour());
                response.put("rating", teacher.getRating());
                response.put("reviewCount", teacher.getReviewCount());
                putAddressFields(response, teacher);
            }
        }

        return response;
    }

    public Map<String, Object> getStudentProfile(User user) {
        Optional<Student> studentOptional = studentService.findByUser(user);
        if (studentOptional.isEmpty()) {
            return null;
        }

        Student student = studentOptional.get();
        Map<String, Object> response = new HashMap<>();
        response.put("id", student.getId());
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("phone", user.getPhone());
        response.put("name", user.getName());
        response.put("school", student.getSchool());
        response.put("grade", student.getGrade());
        response.put("major", student.getMajor());
        putAddressFields(response, student);
        addEditableFields(response);

        return response;
    }

    public Map<String, Object> getTeacherProfile(User user) {
        Optional<Teacher> teacherOptional = teacherService.findByUser(user);
        if (teacherOptional.isEmpty()) {
            return null;
        }

        Teacher teacher = teacherOptional.get();
        Map<String, Object> response = new HashMap<>();
        response.put("id", teacher.getId());
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("phone", user.getPhone());
        response.put("name", user.getName());
        response.put("school", teacher.getSchool());
        response.put("major", teacher.getMajor());
        response.put("education", teacher.getEducation());
        response.put("teachingExperience", teacher.getTeachingExperience());
        response.put("subject", teacher.getSubject());
        response.put("bio", teacher.getBio());
        response.put("pricePerHour", teacher.getPricePerHour());
        response.put("rating", teacher.getRating());
        response.put("reviewCount", teacher.getReviewCount());
        putAddressFields(response, teacher);
        addEditableFields(response);

        return response;
    }

    @Transactional
    public Map<String, Object> updateStudentProfile(User user, Map<String, Object> profileData) {
        if (profileData.containsKey("name")) {
            user.setName((String) profileData.get("name"));
        }
        if (profileData.containsKey("phone")) {
            user.setPhone((String) profileData.get("phone"));
        }
        if (profileData.containsKey("email")) {
            String newEmail = (String) profileData.get("email");
            if (StringUtils.hasText(newEmail) && !newEmail.equals(user.getEmail())) {
                if (userRepository.findByEmail(newEmail).isPresent()) {
                    Map<String, Object> error = new HashMap<>();
                    error.put("success", false);
                    error.put("error", "邮箱已被注册");
                    return error;
                }
                user.setEmail(newEmail);
            }
        }
        updateUser(user);

        Optional<Student> studentOptional = studentService.findByUser(user);
        Student student;
        if (studentOptional.isPresent()) {
            student = studentOptional.get();
        } else {
            student = new Student();
            student.setUser(user);
        }

        if (profileData.containsKey("school")) {
            student.setSchool((String) profileData.get("school"));
        }
        if (profileData.containsKey("grade")) {
            student.setGrade((String) profileData.get("grade"));
        }
        if (profileData.containsKey("major")) {
            student.setMajor((String) profileData.get("major"));
        }
        applyStudentAddress(student, profileData);

        studentService.updateStudent(student);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Profile updated successfully");
        return response;
    }

    @Transactional
    public Map<String, Object> updateTeacherProfile(User user, Map<String, Object> profileData) {
        if (profileData.containsKey("name")) {
            user.setName((String) profileData.get("name"));
        }
        if (profileData.containsKey("phone")) {
            user.setPhone((String) profileData.get("phone"));
        }
        if (profileData.containsKey("email")) {
            String newEmail = (String) profileData.get("email");
            if (StringUtils.hasText(newEmail) && !newEmail.equals(user.getEmail())) {
                if (userRepository.findByEmail(newEmail).isPresent()) {
                    Map<String, Object> error = new HashMap<>();
                    error.put("success", false);
                    error.put("error", "邮箱已被注册");
                    return error;
                }
                user.setEmail(newEmail);
            }
        }
        updateUser(user);

        Optional<Teacher> teacherOptional = teacherService.findByUser(user);
        Teacher teacher;
        if (teacherOptional.isPresent()) {
            teacher = teacherOptional.get();
        } else {
            teacher = new Teacher();
            teacher.setUser(user);
        }

        if (profileData.containsKey("school")) {
            teacher.setSchool((String) profileData.get("school"));
        }
        if (profileData.containsKey("major")) {
            teacher.setMajor((String) profileData.get("major"));
        }
        if (profileData.containsKey("education")) {
            teacher.setEducation((String) profileData.get("education"));
        }
        if (profileData.containsKey("teachingExperience")) {
            teacher.setTeachingExperience((String) profileData.get("teachingExperience"));
        }
        if (profileData.containsKey("subject")) {
            teacher.setSubject((String) profileData.get("subject"));
        }
        if (profileData.containsKey("bio")) {
            teacher.setBio((String) profileData.get("bio"));
        }
        if (profileData.containsKey("pricePerHour")) {
            Object priceObj = profileData.get("pricePerHour");
            if (priceObj instanceof Number) {
                teacher.setPricePerHour(((Number) priceObj).doubleValue());
            }
        }
        applyTeacherAddress(teacher, profileData);

        teacherService.updateTeacher(teacher);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Profile updated successfully");
        return response;
    }

    private void applyStudentAddress(Student student, Map<String, Object> profileData) {
        if (!hasAddressPayload(profileData)) {
            return;
        }

        String formatted = getString(profileData, "addressFormatted");
        String rawAddress = getString(profileData, "address");
        student.setAddress(StringUtils.hasText(formatted) ? formatted : rawAddress);
        student.setAddressProvince(getString(profileData, "addressProvince"));
        student.setAddressCity(getString(profileData, "addressCity"));
        student.setAddressDistrict(getString(profileData, "addressDistrict"));
        student.setAddressFormatted(formatted);
        student.setAddressLongitude(null);
        student.setAddressLatitude(null);
    }

    private void applyTeacherAddress(Teacher teacher, Map<String, Object> profileData) {
        if (!hasAddressPayload(profileData)) {
            return;
        }

        String formatted = getString(profileData, "addressFormatted");
        String rawAddress = getString(profileData, "address");
        teacher.setAddress(StringUtils.hasText(formatted) ? formatted : rawAddress);
        teacher.setAddressProvince(getString(profileData, "addressProvince"));
        teacher.setAddressCity(getString(profileData, "addressCity"));
        teacher.setAddressDistrict(getString(profileData, "addressDistrict"));
        teacher.setAddressFormatted(formatted);
        teacher.setAddressLongitude(null);
        teacher.setAddressLatitude(null);
    }

    private boolean hasAddressPayload(Map<String, Object> profileData) {
        return profileData.containsKey("address")
                || profileData.containsKey("addressProvince")
                || profileData.containsKey("addressCity")
                || profileData.containsKey("addressDistrict")
                || profileData.containsKey("addressFormatted");
    }

    private void putAddressFields(Map<String, Object> response, Student student) {
        response.put("address", student.getAddress());
        response.put("addressProvince", student.getAddressProvince());
        response.put("addressCity", student.getAddressCity());
        response.put("addressDistrict", student.getAddressDistrict());
        response.put("addressFormatted", student.getAddressFormatted());
    }

    private void putAddressFields(Map<String, Object> response, Teacher teacher) {
        response.put("address", teacher.getAddress());
        response.put("addressProvince", teacher.getAddressProvince());
        response.put("addressCity", teacher.getAddressCity());
        response.put("addressDistrict", teacher.getAddressDistrict());
        response.put("addressFormatted", teacher.getAddressFormatted());
    }

    private String getString(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value == null ? null : String.valueOf(value);
    }

    private void addEditableFields(Map<String, Object> response) {
        Map<String, Boolean> editableFields = new HashMap<>();
        editableFields.put("name", true);
        editableFields.put("email", true);
        editableFields.put("phone", true);
        editableFields.put("school", true);
        editableFields.put("grade", true);
        editableFields.put("major", true);
        editableFields.put("address", true);
        editableFields.put("addressProvince", true);
        editableFields.put("addressCity", true);
        editableFields.put("addressDistrict", true);
        editableFields.put("addressFormatted", true);
        editableFields.put("education", true);
        editableFields.put("teachingExperience", true);
        editableFields.put("subject", true);
        editableFields.put("bio", true);
        editableFields.put("pricePerHour", true);

        editableFields.put("username", false);

        response.put("editableFields", editableFields);
    }

    @Transactional
    public Map<String, Object> adminUpdateStudentProfile(Long studentId, Map<String, Object> profileData) {
        Optional<Student> studentOptional = studentService.findById(studentId);
        if (studentOptional.isEmpty()) {
            return errorMap("Student not found");
        }

        Student student = studentOptional.get();
        User user = student.getUser();

        if (profileData.containsKey("username")) {
            String newUsername = getString(profileData, "username");
            if (StringUtils.hasText(newUsername) && !newUsername.equals(user.getUsername())) {
                if (userRepository.findByUsername(newUsername).isPresent()) {
                    return errorMap("Username already exists");
                }
                user.setUsername(newUsername);
            }
        }

        if (profileData.containsKey("email")) {
            String newEmail = getString(profileData, "email");
            if (StringUtils.hasText(newEmail) && !newEmail.equals(user.getEmail())) {
                if (userRepository.findByEmail(newEmail).isPresent()) {
                    return errorMap("Email already exists");
                }
                user.setEmail(newEmail);
            }
        }

        if (profileData.containsKey("name")) {
            user.setName(getString(profileData, "name"));
        }
        if (profileData.containsKey("phone")) {
            user.setPhone(getString(profileData, "phone"));
        }
        userRepository.save(user);

        if (profileData.containsKey("school")) {
            student.setSchool(getString(profileData, "school"));
        }
        if (profileData.containsKey("grade")) {
            student.setGrade(getString(profileData, "grade"));
        }
        if (profileData.containsKey("major")) {
            student.setMajor(getString(profileData, "major"));
        }
        applyStudentAddress(student, profileData);
        studentService.updateStudent(student);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Student profile updated successfully");
        return response;
    }

    @Transactional
    public Map<String, Object> adminUpdateTeacherProfile(Long teacherId, Map<String, Object> profileData) {
        Optional<Teacher> teacherOptional = teacherService.findById(teacherId);
        if (teacherOptional.isEmpty()) {
            return errorMap("Teacher not found");
        }

        Teacher teacher = teacherOptional.get();
        User user = teacher.getUser();

        if (profileData.containsKey("username")) {
            String newUsername = getString(profileData, "username");
            if (StringUtils.hasText(newUsername) && !newUsername.equals(user.getUsername())) {
                if (userRepository.findByUsername(newUsername).isPresent()) {
                    return errorMap("Username already exists");
                }
                user.setUsername(newUsername);
            }
        }

        if (profileData.containsKey("email")) {
            String newEmail = getString(profileData, "email");
            if (StringUtils.hasText(newEmail) && !newEmail.equals(user.getEmail())) {
                if (userRepository.findByEmail(newEmail).isPresent()) {
                    return errorMap("Email already exists");
                }
                user.setEmail(newEmail);
            }
        }

        if (profileData.containsKey("name")) {
            user.setName(getString(profileData, "name"));
        }
        if (profileData.containsKey("phone")) {
            user.setPhone(getString(profileData, "phone"));
        }
        userRepository.save(user);

        if (profileData.containsKey("school")) {
            teacher.setSchool(getString(profileData, "school"));
        }
        if (profileData.containsKey("major")) {
            teacher.setMajor(getString(profileData, "major"));
        }
        if (profileData.containsKey("education")) {
            teacher.setEducation(getString(profileData, "education"));
        }
        if (profileData.containsKey("teachingExperience")) {
            teacher.setTeachingExperience(getString(profileData, "teachingExperience"));
        }
        if (profileData.containsKey("subject")) {
            teacher.setSubject(getString(profileData, "subject"));
        }
        if (profileData.containsKey("bio")) {
            teacher.setBio(getString(profileData, "bio"));
        }
        if (profileData.containsKey("pricePerHour")) {
            Object priceObj = profileData.get("pricePerHour");
            if (priceObj instanceof Number) {
                teacher.setPricePerHour(((Number) priceObj).doubleValue());
            }
        }
        applyTeacherAddress(teacher, profileData);
        teacherService.updateTeacher(teacher);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Teacher profile updated successfully");
        return response;
    }

    private Map<String, Object> errorMap(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", message);
        return response;
    }
}
