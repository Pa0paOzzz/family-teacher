package com.familyteacher.backend.util;

import com.familyteacher.backend.entity.AppointmentRequest;
import com.familyteacher.backend.entity.Evaluation;
import com.familyteacher.backend.entity.Favorite;
import com.familyteacher.backend.entity.Order;
import com.familyteacher.backend.entity.Student;
import com.familyteacher.backend.entity.StudentTutoringRequest;
import com.familyteacher.backend.entity.Teacher;
import com.familyteacher.backend.entity.TeacherJobPost;
import com.familyteacher.backend.entity.User;
import com.familyteacher.backend.repository.AppointmentRequestRepository;
import com.familyteacher.backend.repository.EvaluationRepository;
import com.familyteacher.backend.repository.FavoriteRepository;
import com.familyteacher.backend.repository.OrderRepository;
import com.familyteacher.backend.repository.StudentRepository;
import com.familyteacher.backend.repository.StudentTutoringRequestRepository;
import com.familyteacher.backend.repository.TeacherJobPostRepository;
import com.familyteacher.backend.repository.TeacherRepository;
import com.familyteacher.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

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
    private FavoriteRepository favoriteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        User adminUser = ensureUser("admin", "admin123", "admin@example.com", "ADMIN", "管理员", "13800138000");

        Student student1 = ensureStudent(
                ensureUser("student1", "student123", "student1@example.com", "STUDENT", "张三", "13800138001"),
                "北京大学", "大一", "计算机科学与技术", "北京市", "北京市", "海淀区"
        );
        Teacher teacher1 = ensureTeacher(
                ensureUser("teacher1", "teacher123", "teacher1@example.com", "TEACHER", "李四", "13900139001"),
                "清华大学", "数学", "本科", "3年", "数学",
                "擅长数学和英语教学，有3年家教经验", 150.0, "北京市", "北京市", "朝阳区"
        );
        TeacherJobPost jobPost1 = ensureTeacherJobPost(
                teacher1,
                "数学家教，擅长高中数学",
                "本人是清华大学数学专业大四学生，有3年家教经验，擅长高中数学教学，特别是三角函数和立体几何",
                "数学", 150.0, "北京市", "北京市", "海淀区", "周末全天，周内晚上"
        );
        StudentTutoringRequest tutoringRequest1 = ensureStudentTutoringRequest(
                student1,
                "寻找高中数学家教",
                "需要一名有经验的数学家教，帮助我提高高中数学成绩，特别是三角函数部分",
                "数学", 120.0, "北京市", "北京市", "海淀区", "周末上午"
        );
        AppointmentRequest appointmentRequest1 = ensureAppointment(
                student1, teacher1, "数学", "北京市海淀区", 150.0,
                parseDate("2026-04-27"), "周末上午", 2, "想先了解老师的教学风格", "PENDING"
        );
        ensureOrder(appointmentRequest1, 300.0, "PENDING");
        ensureFavorite(student1.getUser(), "TEACHER_JOB_POST", jobPost1.getId());
        ensureFavorite(teacher1.getUser(), "STUDENT_TUTORING_REQUEST", tutoringRequest1.getId());

        Student student2 = ensureStudent(
                ensureUser("student2", "student123", "student2@example.com", "STUDENT", "王五", "13700137001"),
                "复旦大学", "高二", "英语", "上海市", "上海市", "杨浦区"
        );
        Teacher teacher2 = ensureTeacher(
                ensureUser("teacher2", "teacher123", "teacher2@example.com", "TEACHER", "赵六", "13600136001"),
                "上海交通大学", "英语", "研究生", "4年", "英语",
                "英语专业研究生，有4年家教经验，擅长英语口语和写作", 180.0, "上海市", "上海市", "浦东新区"
        );
        TeacherJobPost jobPost2 = ensureTeacherJobPost(
                teacher2,
                "英语家教，擅长口语和写作",
                "本人是上海交通大学英语专业研究生，有4年家教经验，擅长英语口语和写作教学",
                "英语", 180.0, "上海市", "上海市", "浦东新区", "周内晚上，周末全天"
        );
        StudentTutoringRequest tutoringRequest2 = ensureStudentTutoringRequest(
                student2,
                "寻找英语家教，提高口语水平",
                "需要一名有经验的英语家教，帮助我提高英语口语水平，准备托福考试",
                "英语", 160.0, "上海市", "上海市", "杨浦区", "周内晚上"
        );
        AppointmentRequest appointmentRequest2 = ensureAppointment(
                student2, teacher2, "英语", "上海市浦东新区", 180.0,
                parseDate("2026-04-19"), "周六下午 14:00", 2, "想重点练习口语表达和托福面试", "LONG_TERM_COMPLETED"
        );
        appointmentRequest2.setStudentConfirmedLongTerm(true);
        appointmentRequest2.setTeacherConfirmedLongTerm(true);
        appointmentRequest2.setLongTermConfirmedAt(parseDate("2026-04-20"));
        appointmentRequestRepository.save(appointmentRequest2);
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
        ensureFavorite(student2.getUser(), "TEACHER_JOB_POST", jobPost2.getId());
        ensureFavorite(teacher2.getUser(), "STUDENT_TUTORING_REQUEST", tutoringRequest2.getId());

        Student student3 = ensureStudent(
                ensureUser("student3", "student123", "student3@example.com", "STUDENT", "陈小明", "13500135001"),
                "浙江大学", "高三", "物理学", "浙江省", "杭州市", "西湖区"
        );
        Teacher teacher3 = ensureTeacher(
                ensureUser("teacher3", "teacher123", "teacher3@example.com", "TEACHER", "孙老师", "13300133001"),
                "北京大学", "物理学", "博士", "5年", "物理",
                "北大物理博士，5年教学经验，擅长高考物理辅导", 200.0, "北京市", "北京市", "海淀区"
        );
        TeacherJobPost jobPost3 = ensureTeacherJobPost(
                teacher3,
                "物理家教，专注高考冲刺",
                "北大物理博士，5年家教经验，擅长力学、电磁学，帮助学生快速提分",
                "物理", 200.0, "北京市", "北京市", "海淀区", "周末全天"
        );
        StudentTutoringRequest tutoringRequest3 = ensureStudentTutoringRequest(
                student3,
                "寻找物理家教，备战高考",
                "高三学生，需要物理家教帮助复习，重点攻克力学和电磁学",
                "物理", 200.0, "浙江省", "杭州市", "西湖区", "周末下午"
        );
        ensureFavorite(student3.getUser(), "TEACHER_JOB_POST", jobPost3.getId());

        Student student4 = ensureStudent(
                ensureUser("student4", "student123", "student4@example.com", "STUDENT", "林小红", "13400134001"),
                "南京大学", "高二", "化学", "江苏省", "南京市", "鼓楼区"
        );
        Teacher teacher4 = ensureTeacher(
                ensureUser("teacher4", "teacher123", "teacher4@example.com", "TEACHER", "周老师", "13200132001"),
                "南京大学", "化学", "硕士", "4年", "化学",
                "南大化学硕士，擅长有机化学和无机化学教学", 160.0, "江苏省", "南京市", "鼓楼区"
        );
        TeacherJobPost jobPost4 = ensureTeacherJobPost(
                teacher4,
                "化学家教，系统讲解化学原理",
                "南大化学硕士，4年教学经验，帮助学生建立化学知识体系",
                "化学", 160.0, "江苏省", "南京市", "鼓楼区", "周末上午，周内晚上"
        );
        StudentTutoringRequest tutoringRequest4 = ensureStudentTutoringRequest(
                student4,
                "寻找化学家教",
                "需要化学家教，帮助理解有机化学和化学反应原理",
                "化学", 150.0, "江苏省", "南京市", "鼓楼区", "周六上午"
        );
        AppointmentRequest appointmentRequest4 = ensureAppointment(
                student4, teacher4, "化学", "南京市鼓楼区", 160.0,
                parseDate("2026-04-25"), "周六上午 09:00", 2, "希望先从有机化学基础开始", "ACCEPTED"
        );
        ensureOrder(appointmentRequest4, 320.0, "PENDING");
        ensureFavorite(student4.getUser(), "TEACHER_JOB_POST", jobPost4.getId());
        ensureFavorite(teacher4.getUser(), "STUDENT_TUTORING_REQUEST", tutoringRequest4.getId());

        Teacher teacher5 = ensureTeacher(
                ensureUser("teacher5", "teacher123", "teacher5@example.com", "TEACHER", "吴老师", "13100131001"),
                "复旦大学", "数学", "硕士", "6年", "数学",
                "复旦数学硕士，6年教学经验，擅长初中高中数学", 180.0, "上海市", "上海市", "徐汇区"
        );
        TeacherJobPost jobPost5 = ensureTeacherJobPost(
                teacher5,
                "数学家教，从基础到提高",
                "复旦数学硕士，6年教学经验，针对不同学生制定个性化教学方案",
                "数学", 180.0, "上海市", "上海市", "徐汇区", "周一至周五晚上，周末全天"
        );
        Teacher teacher6 = ensureTeacher(
                ensureUser("teacher6", "teacher123", "teacher6@example.com", "TEACHER", "郑老师", "13000130001"),
                "浙江大学", "计算机科学", "硕士", "3年", "编程",
                "浙大计算机硕士，擅长Python、Java、C++编程教学", 220.0, "浙江省", "杭州市", "西湖区"
        );
        TeacherJobPost jobPost6 = ensureTeacherJobPost(
                teacher6,
                "编程家教，Python/Java入门到进阶",
                "浙大计算机硕士，3年编程教学经验，适合零基础和进阶学员",
                "编程", 220.0, "浙江省", "杭州市", "西湖区", "周末全天"
        );
        Teacher teacher7 = ensureTeacher(
                ensureUser("teacher7", "teacher123", "teacher7@example.com", "TEACHER", "许老师", "12700127001"),
                "华东师范大学", "汉语言文学", "硕士", "7年", "语文",
                "华东师大语文教育硕士，擅长阅读理解、作文提升与中高考语文辅导", 170.0, "上海市", "上海市", "静安区"
        );
        TeacherJobPost jobPost7 = ensureTeacherJobPost(
                teacher7,
                "语文家教，作文与阅读专项提升",
                "7年语文教学经验，擅长中学语文阅读理解、写作结构训练与答题技巧梳理",
                "语文", 170.0, "上海市", "上海市", "静安区", "周三晚上、周末全天"
        );
        Teacher teacher8 = ensureTeacher(
                ensureUser("teacher8", "teacher123", "teacher8@example.com", "TEACHER", "何老师", "12600126001"),
                "北京外国语大学", "英语语言文学", "硕士", "5年", "英语",
                "北外英语硕士，擅长英语口语、听力与国际课程辅导", 190.0, "北京市", "北京市", "朝阳区"
        );
        TeacherJobPost jobPost8 = ensureTeacherJobPost(
                teacher8,
                "英语口语陪练与写作辅导",
                "适合想提升英语表达、口语面试与写作能力的学生，课程灵活安排",
                "英语", 190.0, "北京市", "北京市", "朝阳区", "周二、周四晚上，周日白天"
        );
        Teacher teacher9 = ensureTeacher(
                ensureUser("teacher9", "teacher123", "teacher9@example.com", "TEACHER", "彭老师", "12500125001"),
                "华中师范大学", "数学与应用数学", "本科", "8年", "数学",
                "专注初中数学培优与基础补差，善于帮助学生建立学习习惯和题感", 140.0, "湖北省", "武汉市", "武昌区"
        );
        TeacherJobPost jobPost9 = ensureTeacherJobPost(
                teacher9,
                "初中数学提分辅导",
                "面向初一到初三学生，查漏补缺，专项突破函数、几何与压轴题",
                "数学", 140.0, "湖北省", "武汉市", "武昌区", "周一至周五晚上"
        );

        Student student5 = ensureStudent(
                ensureUser("student5", "student123", "student5@example.com", "STUDENT", "黄小龙", "12900129001"),
                "武汉大学", "大二", "软件工程", "湖北省", "武汉市", "洪山区"
        );
        StudentTutoringRequest tutoringRequest5 = ensureStudentTutoringRequest(
                student5,
                "寻找编程家教，学习Python",
                "大二学生，想学习Python编程，为零基础，希望从基础开始学习",
                "编程", 180.0, "湖北省", "武汉市", "洪山区", "周末下午"
        );
        AppointmentRequest appointmentRequest5 = ensureAppointment(
                student5, teacher6, "编程", "杭州市西湖区", 220.0,
                parseDate("2026-05-03"), "周日上午 10:00", 2, "希望从 Python 基础语法开始学习", "REJECTED"
        );
        ensureFavorite(student5.getUser(), "TEACHER_JOB_POST", jobPost6.getId());
        ensureFavorite(teacher6.getUser(), "STUDENT_TUTORING_REQUEST", tutoringRequest5.getId());

        Student student6 = ensureStudent(
                ensureUser("student6", "student123", "student6@example.com", "STUDENT", "刘小芳", "12800128001"),
                "中山大学", "高一", "中文", "广东省", "广州市", "天河区"
        );
        StudentTutoringRequest tutoringRequest6 = ensureStudentTutoringRequest(
                student6,
                "寻找语文家教，提高作文水平",
                "高一学生，语文作文较弱，需要老师指导写作技巧",
                "语文", 120.0, "广东省", "广州市", "天河区", "周六上午"
        );

        Student student7 = ensureStudent(
                ensureUser("student7", "student123", "student7@example.com", "STUDENT", "谢晓宇", "12700127002"),
                "华中师范大学第一附属中学", "初二", "初中课程", "湖北省", "武汉市", "洪山区"
        );
        StudentTutoringRequest tutoringRequest7 = ensureStudentTutoringRequest(
                student7,
                "寻找初中数学老师，补基础",
                "初二学生，想系统补习函数和几何，希望老师有耐心，能够长期辅导",
                "数学", 130.0, "湖北省", "武汉市", "洪山区", "周三晚上、周日下午"
        );
        ensureFavorite(teacher9.getUser(), "STUDENT_TUTORING_REQUEST", tutoringRequest7.getId());

        Student student8 = ensureStudent(
                ensureUser("student8", "student123", "student8@example.com", "STUDENT", "宋雨晴", "12600126002"),
                "北京第二外国语学院附中", "高二", "国际课程", "北京市", "北京市", "通州区"
        );
        StudentTutoringRequest tutoringRequest8 = ensureStudentTutoringRequest(
                student8,
                "寻找英语口语老师，准备面试",
                "希望提升英语口语表达和临场沟通能力，准备国际学校面试",
                "英语", 210.0, "北京市", "北京市", "通州区", "周二、周四晚上"
        );
        AppointmentRequest appointmentRequest8 = ensureAppointment(
                student8, teacher8, "英语", "北京市朝阳区", 190.0,
                parseDate("2026-04-20"), "周日白天", 2, "想加强面试中的英语表达和听力反应", "LONG_TERM_COMPLETED"
        );
        appointmentRequest8.setStudentConfirmedLongTerm(true);
        appointmentRequest8.setTeacherConfirmedLongTerm(true);
        appointmentRequest8.setLongTermConfirmedAt(parseDate("2026-04-21"));
        appointmentRequestRepository.save(appointmentRequest8);
        ensureOrder(appointmentRequest8, 380.0, "REFUNDED");
        ensureEvaluation(
                appointmentRequest8,
                student8.getUser(),
                teacher8.getUser(),
                4,
                4,
                4,
                "老师讲解细致，口语训练有帮助，希望后续能增加更多模拟面试场景"
        );
        ensureFavorite(student8.getUser(), "TEACHER_JOB_POST", jobPost8.getId());

        Student student9 = ensureStudent(
                ensureUser("student9", "student123", "student9@example.com", "STUDENT", "顾子轩", "12500125002"),
                "杭州市第二中学", "高二", "高中课程", "浙江省", "杭州市", "滨江区"
        );
        StudentTutoringRequest tutoringRequest9 = ensureStudentTutoringRequest(
                student9,
                "寻找物理竞赛辅导老师",
                "高二学生，想参加物理竞赛，希望老师擅长题型分析与拔高训练",
                "物理", 260.0, "浙江省", "杭州市", "滨江区", "周末上午"
        );
        AppointmentRequest appointmentRequest9 = ensureAppointment(
                student9, teacher3, "物理", "杭州市滨江区", 200.0,
                parseDate("2026-04-13"), "周日下午 15:00", 3, "想先试上一节竞赛提高课", "LONG_TERM_COMPLETED"
        );
        appointmentRequest9.setStudentConfirmedLongTerm(true);
        appointmentRequest9.setTeacherConfirmedLongTerm(true);
        appointmentRequest9.setLongTermConfirmedAt(parseDate("2026-04-14"));
        appointmentRequestRepository.save(appointmentRequest9);
        ensureOrder(appointmentRequest9, 600.0, "PAID");
        ensureEvaluation(
                appointmentRequest9,
                student9.getUser(),
                teacher3.getUser(),
                5,
                5,
                4,
                "老师讲题思路清晰，尤其是电磁学专题收获很大"
        );
        ensureFavorite(student9.getUser(), "TEACHER_JOB_POST", jobPost3.getId());
        ensureFavorite(teacher3.getUser(), "STUDENT_TUTORING_REQUEST", tutoringRequest9.getId());

        Teacher teacher10 = ensureTeacher(
                ensureUser("teacher10", "teacher123", "teacher10@example.com", "TEACHER", "顾老师", "12400124001"),
                "首都师范大学", "数学教育", "硕士", "5年", "数学",
                "擅长初高中数学提分，讲解细致，熟悉北京本地教材与考试重点", 165.0, "北京市", "北京市", "通州区"
        );
        TeacherJobPost jobPost10 = ensureTeacherJobPost(
                teacher10,
                "北京数学家教，基础巩固与提分",
                "面向初高中学生，擅长函数、解析几何与压轴题训练",
                "数学", 165.0, "北京市", "北京市", "通州区", "周末下午，周三晚上"
        );
        Teacher teacher11 = ensureTeacher(
                ensureUser("teacher11", "teacher123", "teacher11@example.com", "TEACHER", "韩老师", "12300123001"),
                "上海师范大学", "英语教育", "硕士", "6年", "英语",
                "长期从事中学英语教学，擅长阅读、完形和写作专项提升", 175.0, "上海市", "上海市", "杨浦区"
        );
        TeacherJobPost jobPost11 = ensureTeacherJobPost(
                teacher11,
                "上海英语家教，应试与写作提升",
                "适合中学生和备考学生，强调阅读训练和写作思路梳理",
                "英语", 175.0, "上海市", "上海市", "杨浦区", "周内晚上，周六全天"
        );
        Teacher teacher12 = ensureTeacher(
                ensureUser("teacher12", "teacher123", "teacher12@example.com", "TEACHER", "陆老师", "12200122001"),
                "浙江工业大学", "软件工程", "硕士", "4年", "编程",
                "专注青少年编程和 Python 入门，注重项目实战与逻辑训练", 190.0, "浙江省", "杭州市", "滨江区"
        );
        TeacherJobPost jobPost12 = ensureTeacherJobPost(
                teacher12,
                "杭州 Python 编程辅导",
                "从语法基础到项目练习，适合零基础和学校课程同步提升",
                "编程", 190.0, "浙江省", "杭州市", "滨江区", "周末全天，周二晚上"
        );

        Student student10 = ensureStudent(
                ensureUser("student10", "student123", "student10@example.com", "STUDENT", "唐佳怡", "12100121001"),
                "上海中学", "高一", "高中课程", "上海市", "上海市", "徐汇区"
        );
        StudentTutoringRequest tutoringRequest10 = ensureStudentTutoringRequest(
                student10,
                "寻找数学老师，提升函数成绩",
                "想系统提升函数与数列成绩，希望老师能安排阶段练习和错题复盘",
                "数学", 170.0, "上海市", "上海市", "徐汇区", "周六下午"
        );
        Student student11 = ensureStudent(
                ensureUser("student11", "student123", "student11@example.com", "STUDENT", "罗俊杰", "12000120001"),
                "杭州市学军中学", "高一", "高中课程", "浙江省", "杭州市", "西湖区"
        );
        StudentTutoringRequest tutoringRequest11 = ensureStudentTutoringRequest(
                student11,
                "寻找编程老师，准备信息技术课程",
                "希望学习 Python 和基础算法，兼顾学校信息技术课程成绩提升",
                "编程", 200.0, "浙江省", "杭州市", "西湖区", "周六下午、周日晚上"
        );
        Student student12 = ensureStudent(
                ensureUser("student12", "student123", "student12@example.com", "STUDENT", "邹可欣", "11900119001"),
                "武汉外国语学校", "高二", "英语", "湖北省", "武汉市", "武昌区"
        );
        StudentTutoringRequest tutoringRequest12 = ensureStudentTutoringRequest(
                student12,
                "寻找英语写作老师",
                "想重点提升英语写作和阅读，准备校内阶段考试和竞赛选拔",
                "英语", 150.0, "湖北省", "武汉市", "武昌区", "周日晚"
        );

        ensureFavorite(student10.getUser(), "TEACHER_JOB_POST", jobPost5.getId());
        ensureFavorite(student11.getUser(), "TEACHER_JOB_POST", jobPost12.getId());
        ensureFavorite(student12.getUser(), "TEACHER_JOB_POST", jobPost11.getId());
        ensureFavorite(teacher5.getUser(), "STUDENT_TUTORING_REQUEST", tutoringRequest10.getId());
        ensureFavorite(teacher11.getUser(), "STUDENT_TUTORING_REQUEST", tutoringRequest2.getId());
        ensureFavorite(teacher12.getUser(), "STUDENT_TUTORING_REQUEST", tutoringRequest11.getId());
        ensureFavorite(teacher8.getUser(), "STUDENT_TUTORING_REQUEST", tutoringRequest12.getId());
        ensureFavorite(student6.getUser(), "TEACHER_JOB_POST", jobPost7.getId());
        ensureFavorite(student7.getUser(), "TEACHER_JOB_POST", jobPost9.getId());
        ensureFavorite(student8.getUser(), "TEACHER_JOB_POST", jobPost10.getId());

        refreshTeacherRating(teacher1);
        refreshTeacherRating(teacher2);
        refreshTeacherRating(teacher3);
        refreshTeacherRating(teacher4);
        refreshTeacherRating(teacher5);
        refreshTeacherRating(teacher6);
        refreshTeacherRating(teacher7);
        refreshTeacherRating(teacher8);
        refreshTeacherRating(teacher9);
        refreshTeacherRating(teacher10);
        refreshTeacherRating(teacher11);
        refreshTeacherRating(teacher12);

        System.out.println("样例数据初始化完成或已补齐！管理员: " + adminUser.getUsername());
    }

    private User ensureUser(String username, String password, String email, String role, String name, String phone) {
        return userRepository.findByUsername(username).map(existing -> {
            existing.setEmail(email);
            existing.setRole(role);
            existing.setName(name);
            existing.setPhone(phone);
            if (existing.getEnabled() == null) {
                existing.setEnabled(true);
            }
            existing.setDeleted(false);
            if (existing.getPassword() == null || existing.getPassword().isBlank()) {
                existing.setPassword(passwordEncoder.encode(password));
            }
            return userRepository.save(existing);
        }).orElseGet(() -> {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setEmail(email);
            user.setRole(role);
            user.setName(name);
            user.setPhone(phone);
            user.setEnabled(true);
            user.setDeleted(false);
            return userRepository.save(user);
        });
    }

    private Student ensureStudent(User user, String school, String grade, String major,
                                  String province, String city, String district) {
        Student student = studentRepository.findByUser(user).orElseGet(Student::new);
        student.setUser(user);
        student.setSchool(school);
        student.setGrade(grade);
        student.setMajor(major);
        applyAddress(student, province, city, district);
        return studentRepository.save(student);
    }

    private Teacher ensureTeacher(User user, String school, String major, String education, String teachingExperience,
                                  String subject, String bio, Double pricePerHour,
                                  String province, String city, String district) {
        Teacher teacher = teacherRepository.findByUser(user).orElseGet(Teacher::new);
        teacher.setUser(user);
        teacher.setSchool(school);
        teacher.setMajor(major);
        teacher.setEducation(education);
        teacher.setTeachingExperience(teachingExperience);
        teacher.setSubject(subject);
        teacher.setBio(bio);
        teacher.setPricePerHour(pricePerHour);
        applyAddress(teacher, province, city, district);
        if (teacher.getRating() == null) {
            teacher.setRating(0);
        }
        if (teacher.getReviewCount() == null) {
            teacher.setReviewCount(0);
        }
        return teacherRepository.save(teacher);
    }

    private TeacherJobPost ensureTeacherJobPost(Teacher teacher, String title, String description, String subject,
                                                Double pricePerHour, String province, String city, String district,
                                                String availability) {
        TeacherJobPost jobPost = teacherJobPostRepository.findByTeacher(teacher).stream()
                .filter(post -> title.equals(post.getTitle()))
                .findFirst()
                .orElseGet(TeacherJobPost::new);
        jobPost.setTeacher(teacher);
        jobPost.setTitle(title);
        jobPost.setDescription(description);
        jobPost.setSubject(subject);
        jobPost.setPricePerHour(pricePerHour);
        applyLocation(jobPost, province, city, district);
        jobPost.setAvailability(availability);
        jobPost.setActive(true);
        return teacherJobPostRepository.save(jobPost);
    }

    private StudentTutoringRequest ensureStudentTutoringRequest(Student student, String title, String description,
                                                                String subject, Double budgetPerHour,
                                                                String province, String city, String district,
                                                                String preferredTime) {
        StudentTutoringRequest tutoringRequest = studentTutoringRequestRepository.findByStudent(student).stream()
                .filter(request -> title.equals(request.getTitle()))
                .findFirst()
                .orElseGet(StudentTutoringRequest::new);
        tutoringRequest.setStudent(student);
        tutoringRequest.setTitle(title);
        tutoringRequest.setDescription(description);
        tutoringRequest.setSubject(subject);
        tutoringRequest.setBudgetPerHour(budgetPerHour);
        applyLocation(tutoringRequest, province, city, district);
        tutoringRequest.setPreferredTime(preferredTime);
        tutoringRequest.setActive(true);
        return studentTutoringRequestRepository.save(tutoringRequest);
    }

    private AppointmentRequest ensureAppointment(Student student, Teacher teacher, String subject,
                                                 String location, Double pricePerHour, Date requestedDate,
                                                 String requestedTime, Integer durationHours,
                                                 String notes, String status) {
        AppointmentRequest appointmentRequest = appointmentRequestRepository.findByStudent(student).stream()
                .filter(appointment -> teacher.equals(appointment.getTeacher()))
                .filter(appointment -> subject.equals(appointment.getSubject()))
                .filter(appointment -> requestedDate.equals(appointment.getRequestedDate()))
                .filter(appointment -> requestedTime.equals(appointment.getRequestedTime()))
                .findFirst()
                .orElseGet(AppointmentRequest::new);
        appointmentRequest.setStudent(student);
        appointmentRequest.setTeacher(teacher);
        appointmentRequest.setSubject(subject);
        appointmentRequest.setLocation(location);
        appointmentRequest.setPricePerHour(pricePerHour);
        appointmentRequest.setRequestedDate(requestedDate);
        appointmentRequest.setRequestedTime(requestedTime);
        appointmentRequest.setDurationHours(durationHours);
        appointmentRequest.setNotes(notes);
        appointmentRequest.setStatus(status);
        appointmentRequest.setStudentConfirmedLongTerm("LONG_TERM_CONFIRMED".equals(status) || "LONG_TERM_COMPLETED".equals(status));
        appointmentRequest.setTeacherConfirmedLongTerm("LONG_TERM_CONFIRMED".equals(status) || "LONG_TERM_COMPLETED".equals(status));
        appointmentRequest.setStudentConfirmedLongTermCompletion("LONG_TERM_COMPLETED".equals(status));
        appointmentRequest.setTeacherConfirmedLongTermCompletion("LONG_TERM_COMPLETED".equals(status));
        if ("LONG_TERM_CONFIRMED".equals(status) || "LONG_TERM_COMPLETED".equals(status)) {
            appointmentRequest.setLongTermConfirmedAt(requestedDate);
        } else {
            appointmentRequest.setLongTermConfirmedAt(null);
        }
        if ("LONG_TERM_COMPLETED".equals(status)) {
            appointmentRequest.setLongTermCompletedAt(requestedDate);
        } else {
            appointmentRequest.setLongTermCompletedAt(null);
        }
        return appointmentRequestRepository.save(appointmentRequest);
    }

    private Order ensureOrder(AppointmentRequest appointmentRequest, Double totalAmount, String paymentStatus) {
        Order order = orderRepository.findByAppointment(appointmentRequest).orElseGet(Order::new);
        order.setAppointment(appointmentRequest);
        order.setTotalAmount(totalAmount);
        order.setPaymentStatus(paymentStatus);
        if (order.getTransactionId() == null || order.getTransactionId().isBlank()) {
            order.setTransactionId("TXN-" + appointmentRequest.getStudent().getUser().getUsername() + "-" + appointmentRequest.getTeacher().getUser().getUsername() + "-" + appointmentRequest.getRequestedTime().hashCode());
        }
        return orderRepository.save(order);
    }

    private Evaluation ensureEvaluation(AppointmentRequest appointmentRequest, User evaluator, User evaluated,
                                        Integer teachingQuality, Integer attitude, Integer satisfaction,
                                        String comment) {
        Evaluation evaluation = evaluationRepository.findByAppointment(appointmentRequest).stream()
                .findFirst()
                .orElseGet(Evaluation::new);
        evaluation.setAppointment(appointmentRequest);
        evaluation.setEvaluator(evaluator);
        evaluation.setEvaluated(evaluated);
        evaluation.setTeachingQuality(teachingQuality);
        evaluation.setAttitude(attitude);
        evaluation.setSatisfaction(satisfaction);
        evaluation.setComment(comment);
        return evaluationRepository.save(evaluation);
    }

    private Favorite ensureFavorite(User user, String resourceType, Long resourceId) {
        return favoriteRepository.findByUserIdAndResourceIdAndResourceType(user.getId(), resourceId, resourceType)
                .orElseGet(() -> {
                    Favorite favorite = new Favorite();
                    favorite.setUser(user);
                    favorite.setResourceType(resourceType);
                    favorite.setResourceId(resourceId);
                    return favoriteRepository.save(favorite);
                });
    }

    private void refreshTeacherRating(Teacher teacher) {
        List<Evaluation> evaluations = evaluationRepository.findByEvaluated(teacher.getUser());
        teacher.setReviewCount(evaluations.size());
        if (evaluations.isEmpty()) {
            teacher.setRating(0);
            teacherRepository.save(teacher);
            return;
        }

        double average = evaluations.stream()
                .mapToDouble(evaluation -> (evaluation.getTeachingQuality() + evaluation.getAttitude() + evaluation.getSatisfaction()) / 3.0)
                .average()
                .orElse(0.0);
        teacher.setRating(BigDecimal.valueOf(average).setScale(0, RoundingMode.HALF_UP).intValue());
        teacherRepository.save(teacher);
    }

    private void applyAddress(Student student, String province, String city, String district) {
        String formatted = formatLocation(province, city, district);
        student.setAddress(formatted);
        student.setAddressProvince(province);
        student.setAddressCity(city);
        student.setAddressDistrict(district);
        student.setAddressFormatted(formatted);
        student.setAddressLongitude(null);
        student.setAddressLatitude(null);
    }

    private void applyAddress(Teacher teacher, String province, String city, String district) {
        String formatted = formatLocation(province, city, district);
        teacher.setAddress(formatted);
        teacher.setAddressProvince(province);
        teacher.setAddressCity(city);
        teacher.setAddressDistrict(district);
        teacher.setAddressFormatted(formatted);
        teacher.setAddressLongitude(null);
        teacher.setAddressLatitude(null);
    }

    private void applyLocation(TeacherJobPost jobPost, String province, String city, String district) {
        String formatted = formatLocation(province, city, district);
        jobPost.setLocation(formatted);
        jobPost.setLocationProvince(province);
        jobPost.setLocationCity(city);
        jobPost.setLocationDistrict(district);
        jobPost.setLocationFormatted(formatted);
        jobPost.setLocationLongitude(null);
        jobPost.setLocationLatitude(null);
    }

    private void applyLocation(StudentTutoringRequest tutoringRequest, String province, String city, String district) {
        String formatted = formatLocation(province, city, district);
        tutoringRequest.setLocation(formatted);
        tutoringRequest.setLocationProvince(province);
        tutoringRequest.setLocationCity(city);
        tutoringRequest.setLocationDistrict(district);
        tutoringRequest.setLocationFormatted(formatted);
        tutoringRequest.setLocationLongitude(null);
        tutoringRequest.setLocationLatitude(null);
    }

    private String formatLocation(String province, String city, String district) {
        if (province != null && province.equals(city)) {
            return city + district;
        }
        return province + city + district;
    }

    private Date parseDate(String value) {
        try {
            return DATE_FORMAT.parse(value);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date: " + value, e);
        }
    }
}
