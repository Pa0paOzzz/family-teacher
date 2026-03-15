package com.familyteacher.backend.util;

import com.familyteacher.backend.entity.*;
import com.familyteacher.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private TeacherJobPostRepository teacherJobPostRepository;

    @Autowired
    private StudentTutoringRequestRepository studentTutoringRequestRepository;

    @Autowired
    private AppointmentRequestRepository appointmentRequestRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 检查是否已有数据
        if (userRepository.count() > 0) {
            return; // 已有数据，跳过初始化
        }

        // 创建管理员用户
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword(passwordEncoder.encode("admin123"));
        adminUser.setEmail("admin@example.com");
        adminUser.setRole("ADMIN");
        adminUser.setName("管理员");
        adminUser.setPhone("13800138000");
        userRepository.save(adminUser);

        // 创建学生用户
        User studentUser1 = new User();
        studentUser1.setUsername("student1");
        studentUser1.setPassword(passwordEncoder.encode("student123"));
        studentUser1.setEmail("student1@example.com");
        studentUser1.setRole("STUDENT");
        studentUser1.setName("张三");
        studentUser1.setPhone("13800138001");
        userRepository.save(studentUser1);

        Student student1 = new Student();
        student1.setUser(studentUser1);
        student1.setSchool("北京大学");
        student1.setMajor("计算机科学与技术");
        student1.setGrade("大一");
        student1.setAddress("北京市海淀区");
        studentRepository.save(student1);

        // 创建家教老师用户
        User teacherUser1 = new User();
        teacherUser1.setUsername("teacher1");
        teacherUser1.setPassword(passwordEncoder.encode("teacher123"));
        teacherUser1.setEmail("teacher1@example.com");
        teacherUser1.setRole("TEACHER");
        teacherUser1.setName("李四");
        teacherUser1.setPhone("13900139001");
        userRepository.save(teacherUser1);

        Teacher teacher1 = new Teacher();
        teacher1.setUser(teacherUser1);
        teacher1.setSchool("清华大学");
        teacher1.setMajor("数学");
        teacher1.setEducation("本科");
        teacher1.setTeachingExperience("3年");
        teacher1.setSubject("数学");
        teacher1.setBio("擅长数学和英语教学，有3年家教经验");
        teacher1.setPricePerHour(150.0);
        teacher1.setAddress("北京市朝阳区");
        teacher1.setRating(5);
        teacher1.setReviewCount(10);
        teacherRepository.save(teacher1);

        // 创建家教求职信息
        TeacherJobPost jobPost1 = new TeacherJobPost();
        jobPost1.setTeacher(teacher1);
        jobPost1.setTitle("数学家教，擅长高中数学");
        jobPost1.setDescription("本人是清华大学数学专业大四学生，有3年家教经验，擅长高中数学教学，特别是三角函数和立体几何");
        jobPost1.setSubject("数学");
        jobPost1.setPricePerHour(150.0);
        jobPost1.setLocation("北京市海淀区");
        jobPost1.setAvailability("周末全天，周内晚上");
        jobPost1.setActive(true);
        teacherJobPostRepository.save(jobPost1);

        // 创建学生家教需求
        StudentTutoringRequest tutoringRequest1 = new StudentTutoringRequest();
        tutoringRequest1.setStudent(student1);
        tutoringRequest1.setTitle("寻找高中数学家教");
        tutoringRequest1.setDescription("需要一名有经验的数学家教，帮助我提高高中数学成绩，特别是三角函数部分");
        tutoringRequest1.setSubject("数学");
        tutoringRequest1.setBudgetPerHour(120.0);
        tutoringRequest1.setLocation("北京市海淀区");
        tutoringRequest1.setPreferredTime("周末上午");
        tutoringRequest1.setActive(true);
        studentTutoringRequestRepository.save(tutoringRequest1);

        // 创建预约请求
        AppointmentRequest appointmentRequest1 = new AppointmentRequest();
        appointmentRequest1.setStudent(student1);
        appointmentRequest1.setTeacher(teacher1);
        appointmentRequest1.setStatus("PENDING");
        appointmentRequestRepository.save(appointmentRequest1);

        // 创建订单
        Order order1 = new Order();
        order1.setAppointment(appointmentRequest1);
        order1.setTotalAmount(150.0);
        order1.setPaymentStatus("PAID");
        orderRepository.save(order1);

        // 创建评价
        Evaluation evaluation1 = new Evaluation();
        evaluation1.setAppointment(appointmentRequest1);
        evaluation1.setEvaluator(student1.getUser());
        evaluation1.setEvaluated(teacher1.getUser());
        evaluation1.setTeachingQuality(5);
        evaluation1.setAttitude(5);
        evaluation1.setSatisfaction(5);
        evaluation1.setComment("老师教学非常认真，讲解清晰，对我的数学成绩提高帮助很大");
        evaluationRepository.save(evaluation1);

        // 创建更多样例数据
        // 学生2
        User studentUser2 = new User();
        studentUser2.setUsername("student2");
        studentUser2.setPassword(passwordEncoder.encode("student123"));
        studentUser2.setEmail("student2@example.com");
        studentUser2.setRole("STUDENT");
        studentUser2.setName("王五");
        studentUser2.setPhone("13700137001");
        userRepository.save(studentUser2);

        Student student2 = new Student();
        student2.setUser(studentUser2);
        student2.setSchool("复旦大学");
        student2.setMajor("英语");
        student2.setGrade("高二");
        student2.setAddress("上海市杨浦区");
        studentRepository.save(student2);

        // 家教老师2
        User teacherUser2 = new User();
        teacherUser2.setUsername("teacher2");
        teacherUser2.setPassword(passwordEncoder.encode("teacher123"));
        teacherUser2.setEmail("teacher2@example.com");
        teacherUser2.setRole("TEACHER");
        teacherUser2.setName("赵六");
        teacherUser2.setPhone("13600136001");
        userRepository.save(teacherUser2);

        Teacher teacher2 = new Teacher();
        teacher2.setUser(teacherUser2);
        teacher2.setSchool("上海交通大学");
        teacher2.setMajor("英语");
        teacher2.setEducation("研究生");
        teacher2.setTeachingExperience("4年");
        teacher2.setSubject("英语");
        teacher2.setBio("英语专业研究生，有4年家教经验，擅长英语口语和写作");
        teacher2.setPricePerHour(180.0);
        teacher2.setAddress("上海市浦东新区");
        teacher2.setRating(5);
        teacher2.setReviewCount(15);
        teacherRepository.save(teacher2);

        // 家教求职信息2
        TeacherJobPost jobPost2 = new TeacherJobPost();
        jobPost2.setTeacher(teacher2);
        jobPost2.setTitle("英语家教，擅长口语和写作");
        jobPost2.setDescription("本人是上海交通大学英语专业研究生，有4年家教经验，擅长英语口语和写作教学");
        jobPost2.setSubject("英语");
        jobPost2.setPricePerHour(180.0);
        jobPost2.setLocation("上海市浦东新区");
        jobPost2.setAvailability("周内晚上，周末全天");
        jobPost2.setActive(true);
        teacherJobPostRepository.save(jobPost2);

        // 学生家教需求2
        StudentTutoringRequest tutoringRequest2 = new StudentTutoringRequest();
        tutoringRequest2.setStudent(student2);
        tutoringRequest2.setTitle("寻找英语家教，提高口语水平");
        tutoringRequest2.setDescription("需要一名有经验的英语家教，帮助我提高英语口语水平，准备托福考试");
        tutoringRequest2.setSubject("英语");
        tutoringRequest2.setBudgetPerHour(160.0);
        tutoringRequest2.setLocation("上海市杨浦区");
        tutoringRequest2.setPreferredTime("周内晚上");
        tutoringRequest2.setActive(true);
        studentTutoringRequestRepository.save(tutoringRequest2);

        System.out.println("样例数据初始化完成！");
    }
}