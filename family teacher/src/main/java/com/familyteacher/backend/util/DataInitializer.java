package com.familyteacher.backend.util;

import com.familyteacher.backend.entity.*;
import com.familyteacher.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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
    public void run(String... args) {
        User adminUser = ensureUser("admin", "admin123", "admin@example.com", "ADMIN", "管理员", "13800138000");

        Student student1 = ensureStudent(
                ensureUser("student1", "student123", "student1@example.com", "STUDENT", "张三", "13800138001"),
                "北京大学", "大一", "计算机科学与技术", "北京市海淀区"
        );
        Teacher teacher1 = ensureTeacher(
                ensureUser("teacher1", "teacher123", "teacher1@example.com", "TEACHER", "李四", "13900139001"),
                "清华大学", "数学", "本科", "3年", "数学",
                "擅长数学和英语教学，有3年家教经验", 150.0, "北京市朝阳区", 5, 10
        );
        TeacherJobPost jobPost1 = ensureTeacherJobPost(
                teacher1,
                "数学家教，擅长高中数学",
                "本人是清华大学数学专业大四学生，有3年家教经验，擅长高中数学教学，特别是三角函数和立体几何",
                "数学", 150.0, "北京市海淀区", "周末全天，周内晚上"
        );
        StudentTutoringRequest tutoringRequest1 = ensureStudentTutoringRequest(
                student1,
                "寻找高中数学家教",
                "需要一名有经验的数学家教，帮助我提高高中数学成绩，特别是三角函数部分",
                "数学", 120.0, "北京市海淀区", "周末上午"
        );
        AppointmentRequest appointmentRequest1 = ensureAppointment(
                student1, teacher1, "预约-张三-李四-数学", "数学", "北京市海淀区", 150.0,
                "周末上午", null, "想先了解老师的教学风格", "PENDING"
        );
        ensureOrder(appointmentRequest1, 150.0, "PAID");
        ensureEvaluation(
                appointmentRequest1,
                student1.getUser(),
                teacher1.getUser(),
                5,
                5,
                5,
                "老师教学非常认真，讲解清晰，对我的数学成绩提高帮助很大"
        );

        Student student2 = ensureStudent(
                ensureUser("student2", "student123", "student2@example.com", "STUDENT", "王五", "13700137001"),
                "复旦大学", "高二", "英语", "上海市杨浦区"
        );
        Teacher teacher2 = ensureTeacher(
                ensureUser("teacher2", "teacher123", "teacher2@example.com", "TEACHER", "赵六", "13600136001"),
                "上海交通大学", "英语", "研究生", "4年", "英语",
                "英语专业研究生，有4年家教经验，擅长英语口语和写作", 180.0, "上海市浦东新区", 5, 15
        );
        ensureTeacherJobPost(
                teacher2,
                "英语家教，擅长口语和写作",
                "本人是上海交通大学英语专业研究生，有4年家教经验，擅长英语口语和写作教学",
                "英语", 180.0, "上海市浦东新区", "周内晚上，周末全天"
        );
        ensureStudentTutoringRequest(
                student2,
                "寻找英语家教，提高口语水平",
                "需要一名有经验的英语家教，帮助我提高英语口语水平，准备托福考试",
                "英语", 160.0, "上海市杨浦区", "周内晚上"
        );
        AppointmentRequest appointmentRequest2 = ensureAppointment(
                student2, teacher2, "预约-王五-赵六-英语", "英语", "上海市浦东新区", 180.0,
                "周六下午 14:00", 2, "想重点练习口语表达和托福面试", "ACCEPTED"
        );
        ensureOrder(appointmentRequest2, 360.0, "PAID");
        ensureEvaluation(
                appointmentRequest2,
                student2.getUser(),
                teacher2.getUser(),
                5,
                4,
                5,
                "老师互动很多，课堂氛围很好，口语纠错非常及时"
        );

        Student student3 = ensureStudent(
                ensureUser("student3", "student123", "student3@example.com", "STUDENT", "陈小明", "13500135001"),
                "浙江大学", "高三", "物理学", "杭州市西湖区"
        );
        Teacher teacher3 = ensureTeacher(
                ensureUser("teacher3", "teacher123", "teacher3@example.com", "TEACHER", "孙老师", "13300133001"),
                "北京大学", "物理学", "博士", "5年", "物理",
                "北大物理博士，5年教学经验，擅长高考物理辅导", 200.0, "北京市海淀区", 5, 20
        );
        ensureTeacherJobPost(
                teacher3,
                "物理家教，专注高考冲刺",
                "北大物理博士，5年家教经验，擅长力学、电磁学，帮助学生快速提分",
                "物理", 200.0, "北京市海淀区", "周末全天"
        );
        StudentTutoringRequest tutoringRequest3 = ensureStudentTutoringRequest(
                student3,
                "寻找物理家教，备战高考",
                "高三学生，需要物理家教帮助复习，重点攻克力学和电磁学",
                "物理", 200.0, "杭州市西湖区", "周末下午"
        );

        Student student4 = ensureStudent(
                ensureUser("student4", "student123", "student4@example.com", "STUDENT", "林小红", "13400134001"),
                "南京大学", "高二", "化学", "南京市鼓楼区"
        );
        Teacher teacher4 = ensureTeacher(
                ensureUser("teacher4", "teacher123", "teacher4@example.com", "TEACHER", "周老师", "13200132001"),
                "南京大学", "化学", "硕士", "4年", "化学",
                "南大化学硕士，擅长有机化学和无机化学教学", 160.0, "南京市鼓楼区", 4, 12
        );
        ensureTeacherJobPost(
                teacher4,
                "化学家教，系统讲解化学原理",
                "南大化学硕士，4年教学经验，帮助学生建立化学知识体系",
                "化学", 160.0, "南京市鼓楼区", "周末上午，周内晚上"
        );
        ensureStudentTutoringRequest(
                student4,
                "寻找化学家教",
                "需要化学家教，帮助理解有机化学和化学反应原理",
                "化学", 150.0, "南京市鼓楼区", "周六上午"
        );

        Teacher teacher5 = ensureTeacher(
                ensureUser("teacher5", "teacher123", "teacher5@example.com", "TEACHER", "吴老师", "13100131001"),
                "复旦大学", "数学", "硕士", "6年", "数学",
                "复旦数学硕士，6年教学经验，擅长初中高中数学", 180.0, "上海市徐汇区", 5, 25
        );
        ensureTeacherJobPost(
                teacher5,
                "数学家教，从基础到提高",
                "复旦数学硕士，6年教学经验，针对不同学生制定个性化教学方案",
                "数学", 180.0, "上海市徐汇区", "周一至周五晚上，周末全天"
        );

        Teacher teacher6 = ensureTeacher(
                ensureUser("teacher6", "teacher123", "teacher6@example.com", "TEACHER", "郑老师", "13000130001"),
                "浙江大学", "计算机科学", "硕士", "3年", "编程",
                "浙大计算机硕士，擅长Python、Java、C++编程教学", 220.0, "杭州市西湖区", 5, 8
        );
        ensureTeacherJobPost(
                teacher6,
                "编程家教，Python/Java入门到进阶",
                "浙大计算机硕士，3年编程教学经验，适合零基础和进阶学员",
                "编程", 220.0, "杭州市西湖区", "周末全天"
        );

        Teacher teacher7 = ensureTeacher(
                ensureUser("teacher7", "teacher123", "teacher7@example.com", "TEACHER", "许老师", "12700127001"),
                "华东师范大学", "汉语言文学", "硕士", "7年", "语文",
                "华东师大语文教育硕士，擅长阅读理解、作文提升与中高考语文辅导", 170.0, "上海市静安区", 5, 18
        );
        ensureTeacherJobPost(
                teacher7,
                "语文家教，作文与阅读专项提升",
                "7年语文教学经验，擅长中学语文阅读理解、写作结构训练与答题技巧梳理",
                "语文", 170.0, "上海市静安区", "周三晚上、周末全天"
        );

        Teacher teacher8 = ensureTeacher(
                ensureUser("teacher8", "teacher123", "teacher8@example.com", "TEACHER", "何老师", "12600126001"),
                "北京外国语大学", "英语语言文学", "硕士", "5年", "英语",
                "北外英语硕士，擅长英语口语、听力与国际课程辅导", 190.0, "北京市朝阳区", 4, 14
        );
        ensureTeacherJobPost(
                teacher8,
                "英语口语陪练与写作辅导",
                "适合想提升英语表达、口语面试与写作能力的学生，课程灵活安排",
                "英语", 190.0, "北京市朝阳区", "周二、周四晚上，周日白天"
        );

        Teacher teacher9 = ensureTeacher(
                ensureUser("teacher9", "teacher123", "teacher9@example.com", "TEACHER", "彭老师", "12500125001"),
                "华中师范大学", "数学与应用数学", "本科", "8年", "数学",
                "专注初中数学培优与基础补差，善于帮助学生建立学习习惯和题感", 140.0, "武汉市武昌区", 5, 30
        );
        ensureTeacherJobPost(
                teacher9,
                "初中数学提分辅导",
                "面向初一到初三学生，查漏补缺，专项突破函数、几何与压轴题",
                "数学", 140.0, "武汉市武昌区", "周一至周五晚上"
        );

        Student student5 = ensureStudent(
                ensureUser("student5", "student123", "student5@example.com", "STUDENT", "黄小龙", "12900129001"),
                "武汉大学", "大二", "软件工程", "武汉市洪山区"
        );
        ensureStudentTutoringRequest(
                student5,
                "寻找编程家教，学习Python",
                "大二学生，想学习Python编程，为零基础，希望从基础开始学习",
                "编程", 180.0, "武汉市洪山区", "周末下午"
        );
        AppointmentRequest appointmentRequest3 = ensureAppointment(
                student5, teacher6, "预约-黄小龙-郑老师-编程", "编程", "杭州市西湖区", 220.0,
                "周日上午 10:00", 2, "希望从 Python 基础语法开始学习", "PENDING"
        );

        Student student6 = ensureStudent(
                ensureUser("student6", "student123", "student6@example.com", "STUDENT", "刘小芳", "12800128001"),
                "中山大学", "高一", "中文", "广州市天河区"
        );
        ensureStudentTutoringRequest(
                student6,
                "寻找语文家教，提高作文水平",
                "高一学生，语文作文较弱，需要老师指导写作技巧",
                "语文", 120.0, "广州市天河区", "周六上午"
        );

        Student student7 = ensureStudent(
                ensureUser("student7", "student123", "student7@example.com", "STUDENT", "谢晓宇", "12700127002"),
                "华中师范大学第一附属中学", "初二", "初中课程", "武汉市洪山区"
        );
        ensureStudentTutoringRequest(
                student7,
                "寻找初中数学老师，补基础",
                "初二学生，想系统补习函数和几何，希望老师有耐心，能够长期辅导",
                "数学", 130.0, "武汉市洪山区", "周三晚上、周日下午"
        );

        Student student8 = ensureStudent(
                ensureUser("student8", "student123", "student8@example.com", "STUDENT", "宋雨晴", "12600126002"),
                "北京第二外国语学院附中", "高二", "国际课程", "北京市通州区"
        );
        ensureStudentTutoringRequest(
                student8,
                "寻找英语口语老师，准备面试",
                "希望提升英语口语表达和临场沟通能力，准备国际学校面试",
                "英语", 210.0, "北京市通州区", "周二、周四晚上"
        );

        Student student9 = ensureStudent(
                ensureUser("student9", "student123", "student9@example.com", "STUDENT", "顾子轩", "12500125002"),
                "杭州市第二中学", "高二", "高中课程", "杭州市滨江区"
        );
        ensureStudentTutoringRequest(
                student9,
                "寻找物理竞赛辅导老师",
                "高二学生，想参加物理竞赛，希望老师擅长题型分析与拔高训练",
                "物理", 260.0, "杭州市滨江区", "周末上午"
        );
        AppointmentRequest appointmentRequest4 = ensureAppointment(
                student9, teacher3, "预约-顾子轩-孙老师-物理", "物理", "杭州市滨江区", 200.0,
                "周日下午 15:00", 3, "想先试上一节竞赛提高课", "COMPLETED"
        );
        ensureOrder(appointmentRequest4, 600.0, "PAID");
        ensureEvaluation(
                appointmentRequest4,
                student9.getUser(),
                teacher3.getUser(),
                5,
                5,
                4,
                "老师讲题思路清晰，尤其是电磁学专题收获很大"
        );

        System.out.println("样例数据初始化完成或已补齐！管理员: " + adminUser.getUsername());
    }

    private User ensureUser(String username, String password, String email, String role, String name, String phone) {
        return userRepository.findByUsername(username).orElseGet(() -> {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setEmail(email);
            user.setRole(role);
            user.setName(name);
            user.setPhone(phone);
            return userRepository.save(user);
        });
    }

    private Student ensureStudent(User user, String school, String grade, String major, String address) {
        return studentRepository.findByUser(user).orElseGet(() -> {
            Student student = new Student();
            student.setUser(user);
            student.setSchool(school);
            student.setGrade(grade);
            student.setMajor(major);
            student.setAddress(address);
            return studentRepository.save(student);
        });
    }

    private Teacher ensureTeacher(User user, String school, String major, String education, String teachingExperience,
                                  String subject, String bio, Double pricePerHour, String address,
                                  Integer rating, Integer reviewCount) {
        return teacherRepository.findByUser(user).orElseGet(() -> {
            Teacher teacher = new Teacher();
            teacher.setUser(user);
            teacher.setSchool(school);
            teacher.setMajor(major);
            teacher.setEducation(education);
            teacher.setTeachingExperience(teachingExperience);
            teacher.setSubject(subject);
            teacher.setBio(bio);
            teacher.setPricePerHour(pricePerHour);
            teacher.setAddress(address);
            teacher.setRating(rating);
            teacher.setReviewCount(reviewCount);
            return teacherRepository.save(teacher);
        });
    }

    private TeacherJobPost ensureTeacherJobPost(Teacher teacher, String title, String description, String subject,
                                                Double pricePerHour, String location, String availability) {
        return teacherJobPostRepository.findByTeacher(teacher).stream()
                .filter(post -> title.equals(post.getTitle()))
                .findFirst()
                .orElseGet(() -> {
                    TeacherJobPost jobPost = new TeacherJobPost();
                    jobPost.setTeacher(teacher);
                    jobPost.setTitle(title);
                    jobPost.setDescription(description);
                    jobPost.setSubject(subject);
                    jobPost.setPricePerHour(pricePerHour);
                    jobPost.setLocation(location);
                    jobPost.setAvailability(availability);
                    jobPost.setActive(true);
                    return teacherJobPostRepository.save(jobPost);
                });
    }

    private StudentTutoringRequest ensureStudentTutoringRequest(Student student, String title, String description,
                                                                String subject, Double budgetPerHour, String location,
                                                                String preferredTime) {
        return studentTutoringRequestRepository.findByStudent(student).stream()
                .filter(request -> title.equals(request.getTitle()))
                .findFirst()
                .orElseGet(() -> {
                    StudentTutoringRequest tutoringRequest = new StudentTutoringRequest();
                    tutoringRequest.setStudent(student);
                    tutoringRequest.setTitle(title);
                    tutoringRequest.setDescription(description);
                    tutoringRequest.setSubject(subject);
                    tutoringRequest.setBudgetPerHour(budgetPerHour);
                    tutoringRequest.setLocation(location);
                    tutoringRequest.setPreferredTime(preferredTime);
                    tutoringRequest.setActive(true);
                    return studentTutoringRequestRepository.save(tutoringRequest);
                });
    }

    private AppointmentRequest ensureAppointment(Student student, Teacher teacher, String uniqueNote, String subject,
                                                 String location, Double pricePerHour, String requestedTime,
                                                 Integer durationHours, String notes, String status) {
        return appointmentRequestRepository.findByStudent(student).stream()
                .filter(appointment -> teacher.equals(appointment.getTeacher()))
                .filter(appointment -> uniqueNote.equals(appointment.getNotes()))
                .findFirst()
                .orElseGet(() -> {
                    AppointmentRequest appointmentRequest = new AppointmentRequest();
                    appointmentRequest.setStudent(student);
                    appointmentRequest.setTeacher(teacher);
                    appointmentRequest.setSubject(subject);
                    appointmentRequest.setLocation(location);
                    appointmentRequest.setPricePerHour(pricePerHour);
                    appointmentRequest.setRequestedTime(requestedTime);
                    appointmentRequest.setDurationHours(durationHours);
                    appointmentRequest.setNotes(uniqueNote);
                    appointmentRequest.setStatus(status);
                    return appointmentRequestRepository.save(appointmentRequest);
                });
    }

    private Order ensureOrder(AppointmentRequest appointmentRequest, Double totalAmount, String paymentStatus) {
        return orderRepository.findByAppointment(appointmentRequest).orElseGet(() -> {
            Order order = new Order();
            order.setAppointment(appointmentRequest);
            order.setTotalAmount(totalAmount);
            order.setPaymentStatus(paymentStatus);
            return orderRepository.save(order);
        });
    }

    private Evaluation ensureEvaluation(AppointmentRequest appointmentRequest, User evaluator, User evaluated,
                                        Integer teachingQuality, Integer attitude, Integer satisfaction,
                                        String comment) {
        return evaluationRepository.findByAppointment(appointmentRequest).stream()
                .findFirst()
                .orElseGet(() -> {
                    Evaluation evaluation = new Evaluation();
                    evaluation.setAppointment(appointmentRequest);
                    evaluation.setEvaluator(evaluator);
                    evaluation.setEvaluated(evaluated);
                    evaluation.setTeachingQuality(teachingQuality);
                    evaluation.setAttitude(attitude);
                    evaluation.setSatisfaction(satisfaction);
                    evaluation.setComment(comment);
                    return evaluationRepository.save(evaluation);
                });
    }
}