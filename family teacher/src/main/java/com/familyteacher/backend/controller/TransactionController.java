package com.familyteacher.backend.controller;

import com.familyteacher.backend.entity.AppointmentRequest;
import com.familyteacher.backend.entity.Order;
import com.familyteacher.backend.service.AppointmentService;
import com.familyteacher.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private OrderService orderService;
    
    // 预约相关API
    @PostMapping("/appointment")
    public AppointmentRequest createAppointmentRequest(@RequestBody AppointmentRequest request) {
        return appointmentService.createAppointmentRequest(request);
    }
    
    @GetMapping("/appointments/student")
    public List<AppointmentRequest> getStudentAppointments(@RequestBody StudentRequest studentRequest) {
        return appointmentService.getAppointmentsByStudent(studentRequest.getStudent());
    }
    
    @GetMapping("/appointments/teacher")
    public List<AppointmentRequest> getTeacherAppointments(@RequestBody TeacherRequest teacherRequest) {
        return appointmentService.getAppointmentsByTeacher(teacherRequest.getTeacher());
    }
    
    @PutMapping("/appointment/{id}/accept")
    public AppointmentRequest acceptAppointment(@PathVariable Long id) {
        return appointmentService.updateAppointmentStatus(id, "ACCEPTED");
    }
    
    @PutMapping("/appointment/{id}/reject")
    public AppointmentRequest rejectAppointment(@PathVariable Long id) {
        return appointmentService.updateAppointmentStatus(id, "REJECTED");
    }
    
    @PutMapping("/appointment/{id}/complete")
    public AppointmentRequest completeAppointment(@PathVariable Long id) {
        return appointmentService.updateAppointmentStatus(id, "COMPLETED");
    }
    
    // 订单相关API
    @PostMapping("/order")
    public Order createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }
    
    @PutMapping("/order/{id}/pay")
    public Order payOrder(@PathVariable Long id) {
        return orderService.updateOrderStatus(id, "PAID");
    }
    
    @PutMapping("/order/{id}/refund")
    public Order refundOrder(@PathVariable Long id) {
        return orderService.updateOrderStatus(id, "REFUNDED");
    }
    
    // 请求参数类
    public static class StudentRequest {
        private com.familyteacher.backend.entity.Student student;
        public com.familyteacher.backend.entity.Student getStudent() { return student; }
        public void setStudent(com.familyteacher.backend.entity.Student student) { this.student = student; }
    }
    
    public static class TeacherRequest {
        private com.familyteacher.backend.entity.Teacher teacher;
        public com.familyteacher.backend.entity.Teacher getTeacher() { return teacher; }
        public void setTeacher(com.familyteacher.backend.entity.Teacher teacher) { this.teacher = teacher; }
    }
}