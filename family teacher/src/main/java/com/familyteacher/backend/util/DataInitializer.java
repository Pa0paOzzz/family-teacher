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

        // 学生3 - 物理需求
        User studentUser3 = new User();
        studentUser3.setUsername("student3");
        studentUser3.setPassword(passwordEncoder.encode("student123"));
        studentUser3.setEmail("student3@example.com");
        studentUser3.setRole("STUDENT");
        studentUser3.setName("陈小明");
        studentUser3.setPhone("13500135001");
        userRepository.save(studentUser3);

        Student student3 = new Student();
        student3.setUser(studentUser3);
        student3.setSchool("浙江大学");
        student3.setMajor("物理学");
        student3.setGrade("高三");
        student3.setAddress("杭州市西湖区");
        studentRepository.save(student3);

        StudentTutoringRequest tutoringRequest3 = new StudentTutoringRequest();
        tutoringRequest3.setStudent(student3);
        tutoringRequest3.setTitle("寻找物理家教，备战高考");
        tutoringRequest3.setDescription("高三学生，需要物理家教帮助复习，重点攻克力学和电磁学");
        tutoringRequest3.setSubject("物理");
        tutoringRequest3.setBudgetPerHour(200.0);
        tutoringRequest3.setLocation("杭州市西湖区");
        tutoringRequest3.setPreferredTime("周末下午");
        tutoringRequest3.setActive(true);
        studentTutoringRequestRepository.save(tutoringRequest3);

        // 学生4 - 化学需求
        User studentUser4 = new User();
        studentUser4.setUsername("student4");
        studentUser4.setPassword(passwordEncoder.encode("student123"));
        studentUser4.setEmail("student4@example.com");
        studentUser4.setRole("STUDENT");
        studentUser4.setName("林小红");
        studentUser4.setPhone("13400134001");
        userRepository.save(studentUser4);

        Student student4 = new Student();
        student4.setUser(studentUser4);
        student4.setSchool("南京大学");
        student4.setMajor("化学");
        student4.setGrade("高二");
        student4.setAddress("南京市鼓楼区");
        studentRepository.save(student4);

        StudentTutoringRequest tutoringRequest4 = new StudentTutoringRequest();
        tutoringRequest4.setStudent(student4);
        tutoringRequest4.setTitle("寻找化学家教");
        tutoringRequest4.setDescription("需要化学家教，帮助理解有机化学和化学反应原理");
        tutoringRequest4.setSubject("化学");
        tutoringRequest4.setBudgetPerHour(150.0);
        tutoringRequest4.setLocation("南京市鼓楼区");
        tutoringRequest4.setPreferredTime("周六上午");
        tutoringRequest4.setActive(true);
        studentTutoringRequestRepository.save(tutoringRequest4);

        // 教师3 - 物理老师
        User teacherUser3 = new User();
        teacherUser3.setUsername("teacher3");
        teacherUser3.setPassword(passwordEncoder.encode("teacher123"));
        teacherUser3.setEmail("teacher3@example.com");
        teacherUser3.setRole("TEACHER");
        teacherUser3.setName("孙老师");
        teacherUser3.setPhone("13300133001");
        userRepository.save(teacherUser3);

        Teacher teacher3 = new Teacher();
        teacher3.setUser(teacherUser3);
        teacher3.setSchool("北京大学");
        teacher3.setMajor("物理学");
        teacher3.setEducation("博士");
        teacher3.setTeachingExperience("5年");
        teacher3.setSubject("物理");
        teacher3.setBio("北大物理博士，5年教学经验，擅长高考物理辅导");
        teacher3.setPricePerHour(200.0);
        teacher3.setAddress("北京市海淀区");
        teacher3.setRating(5);
        teacher3.setReviewCount(20);
        teacherRepository.save(teacher3);

        TeacherJobPost jobPost3 = new TeacherJobPost();
        jobPost3.setTeacher(teacher3);
        jobPost3.setTitle("物理家教，专注高考冲刺");
        jobPost3.setDescription("北大物理博士，5年家教经验，擅长力学、电磁学，帮助学生快速提分");
        jobPost3.setSubject("物理");
        jobPost3.setPricePerHour(200.0);
        jobPost3.setLocation("北京市海淀区");
        jobPost3.setAvailability("周末全天");
        jobPost3.setActive(true);
        teacherJobPostRepository.save(jobPost3);

        // 教师4 - 化学老师
        User teacherUser4 = new User();
        teacherUser4.setUsername("teacher4");
        teacherUser4.setPassword(passwordEncoder.encode("teacher123"));
        teacherUser4.setEmail("teacher4@example.com");
        teacherUser4.setRole("TEACHER");
        teacherUser4.setName("周老师");
        teacherUser4.setPhone("13200132001");
        userRepository.save(teacherUser4);

        Teacher teacher4 = new Teacher();
        teacher4.setUser(teacherUser4);
        teacher4.setSchool("南京大学");
        teacher4.setMajor("化学");
        teacher4.setEducation("硕士");
        teacher4.setTeachingExperience("4年");
        teacher4.setSubject("化学");
        teacher4.setBio("南大化学硕士，擅长有机化学和无机化学教学");
        teacher4.setPricePerHour(160.0);
        teacher4.setAddress("南京市鼓楼区");
        teacher4.setRating(4);
        teacher4.setReviewCount(12);
        teacherRepository.save(teacher4);

        TeacherJobPost jobPost4 = new TeacherJobPost();
        jobPost4.setTeacher(teacher4);
        jobPost4.setTitle("化学家教，系统讲解化学原理");
        jobPost4.setDescription("南大化学硕士，4年教学经验，帮助学生建立化学知识体系");
        jobPost4.setSubject("化学");
        jobPost4.setPricePerHour(160.0);
        jobPost4.setLocation("南京市鼓楼区");
        jobPost4.setAvailability("周末上午，周内晚上");
        jobPost4.setActive(true);
        teacherJobPostRepository.save(jobPost4);

        // 教师5 - 数学老师
        User teacherUser5 = new User();
        teacherUser5.setUsername("teacher5");
        teacherUser5.setPassword(passwordEncoder.encode("teacher123"));
        teacherUser5.setEmail("teacher5@example.com");
        teacherUser5.setRole("TEACHER");
        teacherUser5.setName("吴老师");
        teacherUser5.setPhone("13100131001");
        userRepository.save(teacherUser5);

        Teacher teacher5 = new Teacher();
        teacher5.setUser(teacherUser5);
        teacher5.setSchool("复旦大学");
        teacher5.setMajor("数学");
        teacher5.setEducation("硕士");
        teacher5.setTeachingExperience("6年");
        teacher5.setSubject("数学");
        teacher5.setBio("复旦数学硕士，6年教学经验，擅长初中高中数学");
        teacher5.setPricePerHour(180.0);
        teacher5.setAddress("上海市徐汇区");
        teacher5.setRating(5);
        teacher5.setReviewCount(25);
        teacherRepository.save(teacher5);

        TeacherJobPost jobPost5 = new TeacherJobPost();
        jobPost5.setTeacher(teacher5);
        jobPost5.setTitle("数学家教，从基础到提高");
        jobPost5.setDescription("复旦数学硕士，6年教学经验，针对不同学生制定个性化教学方案");
        jobPost5.setSubject("数学");
        jobPost5.setPricePerHour(180.0);
        jobPost5.setLocation("上海市徐汇区");
        jobPost5.setAvailability("周一至周五晚上，周末全天");
        jobPost5.setActive(true);
        teacherJobPostRepository.save(jobPost5);

        // 教师6 - 编程老师
        User teacherUser6 = new User();
        teacherUser6.setUsername("teacher6");
        teacherUser6.setPassword(passwordEncoder.encode("teacher123"));
        teacherUser6.setEmail("teacher6@example.com");
        teacherUser6.setRole("TEACHER");
        teacherUser6.setName("郑老师");
        teacherUser6.setPhone("13000130001");
        userRepository.save(teacherUser6);

        Teacher teacher6 = new Teacher();
        teacher6.setUser(teacherUser6);
        teacher6.setSchool("浙江大学");
        teacher6.setMajor("计算机科学");
        teacher6.setEducation("硕士");
        teacher6.setTeachingExperience("3年");
        teacher6.setSubject("编程");
        teacher6.setBio("浙大计算机硕士，擅长Python、Java、C++编程教学");
        teacher6.setPricePerHour(220.0);
        teacher6.setAddress("杭州市西湖区");
        teacher6.setRating(5);
        teacher6.setReviewCount(8);
        teacherRepository.save(teacher6);

        TeacherJobPost jobPost6 = new TeacherJobPost();
        jobPost6.setTeacher(teacher6);
        jobPost6.setTitle("编程家教，Python/Java入门到进阶");
        jobPost6.setDescription("浙大计算机硕士，3年编程教学经验，适合零基础和进阶学员");
        jobPost6.setSubject("编程");
        jobPost6.setPricePerHour(220.0);
        jobPost6.setLocation("杭州市西湖区");
        jobPost6.setAvailability("周末全天");
        jobPost6.setActive(true);
        teacherJobPostRepository.save(jobPost6);

        // 学生5 - 编程需求
        User studentUser5 = new User();
        studentUser5.setUsername("student5");
        studentUser5.setPassword(passwordEncoder.encode("student123"));
        studentUser5.setEmail("student5@example.com");
        studentUser5.setRole("STUDENT");
        studentUser5.setName("黄小龙");
        studentUser5.setPhone("12900129001");
        userRepository.save(studentUser5);

        Student student5 = new Student();
        student5.setUser(studentUser5);
        student5.setSchool("武汉大学");
        student5.setMajor("软件工程");
        student5.setGrade("大二");
        student5.setAddress("武汉市洪山区");
        studentRepository.save(student5);

        StudentTutoringRequest tutoringRequest5 = new StudentTutoringRequest();
        tutoringRequest5.setStudent(student5);
        tutoringRequest5.setTitle("寻找编程家教，学习Python");
        tutoringRequest5.setDescription("大二学生，想学习Python编程，为零基础，希望从基础开始学习");
        tutoringRequest5.setSubject("编程");
        tutoringRequest5.setBudgetPerHour(180.0);
        tutoringRequest5.setLocation("武汉市洪山区");
        tutoringRequest5.setPreferredTime("周末下午");
        tutoringRequest5.setActive(true);
        studentTutoringRequestRepository.save(tutoringRequest5);

        // 学生6 - 语文需求
        User studentUser6 = new User();
        studentUser6.setUsername("student6");
        studentUser6.setPassword(passwordEncoder.encode("student123"));
        studentUser6.setEmail("student6@example.com");
        studentUser6.setRole("STUDENT");
        studentUser6.setName("刘小芳");
        studentUser6.setPhone("12800128001");
        userRepository.save(studentUser6);

        Student student6 = new Student();
        student6.setUser(studentUser6);
        student6.setSchool("中山大学");
        student6.setMajor("中文");
        student6.setGrade("高一");
        student6.setAddress("广州市天河区");
        studentRepository.save(student6);

        StudentTutoringRequest tutoringRequest6 = new StudentTutoringRequest();
        tutoringRequest6.setStudent(student6);
        tutoringRequest6.setTitle("寻找语文家教，提高作文水平");
        tutoringRequest6.setDescription("高一学生，语文作文较弱，需要老师指导写作技巧");
        tutoringRequest6.setSubject("语文");
        tutoringRequest6.setBudgetPerHour(120.0);
        tutoringRequest6.setLocation("广州市天河区");
        tutoringRequest6.setPreferredTime("周六上午");
        tutoringRequest6.setActive(true);
        studentTutoringRequestRepository.save(tutoringRequest6);

        System.out.println("样例数据初始化完成！");
    }
}