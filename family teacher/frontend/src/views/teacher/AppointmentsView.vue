<template>
  <div class="teacher-appointments">
    <el-container>
      <el-header height="80px">
        <div class="header-content">
          <h1>家教老师个人中心</h1>
          <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal">
            <el-menu-item index="1">
              <router-link to="/teacher/profile">个人资料</router-link>
            </el-menu-item>
            <el-menu-item index="2">
              <router-link to="/teacher/job-post">发布求职</router-link>
            </el-menu-item>
            <el-menu-item index="3">
              <router-link to="/teacher/appointments">我的预约</router-link>
            </el-menu-item>
            <el-menu-item index="4" @click="logout">退出登录</el-menu-item>
          </el-menu>
        </div>
      </el-header>
      <el-main>
        <div class="appointments-container">
          <h2>我的预约</h2>
          
          <el-tabs type="border-card">
            <el-tab-pane label="待处理">
              <el-table :data="pendingAppointments" style="width: 100%">
                <el-table-column prop="id" label="预约ID"></el-table-column>
                <el-table-column prop="studentName" label="学生"></el-table-column>
                <el-table-column prop="subject" label="学科"></el-table-column>
                <el-table-column prop="requestedDate" label="预约日期"></el-table-column>
                <el-table-column prop="requestedTime" label="预约时间"></el-table-column>
                <el-table-column prop="location" label="地点"></el-table-column>
                <el-table-column prop="pricePerHour" label="价格/小时"></el-table-column>
                <el-table-column prop="status" label="状态">
                  <template #default="scope">
                    <el-tag type="warning">{{ scope.row.status }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作">
                  <template #default="scope">
                    <el-button type="success" size="small" @click="acceptAppointment(scope.row.id)">接受</el-button>
                    <el-button type="danger" size="small" @click="rejectAppointment(scope.row.id)">拒绝</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
            
            <el-tab-pane label="已接受">
              <el-table :data="acceptedAppointments" style="width: 100%">
                <el-table-column prop="id" label="预约ID"></el-table-column>
                <el-table-column prop="studentName" label="学生"></el-table-column>
                <el-table-column prop="subject" label="学科"></el-table-column>
                <el-table-column prop="requestedDate" label="预约日期"></el-table-column>
                <el-table-column prop="requestedTime" label="预约时间"></el-table-column>
                <el-table-column prop="location" label="地点"></el-table-column>
                <el-table-column prop="pricePerHour" label="价格/小时"></el-table-column>
                <el-table-column prop="status" label="状态">
                  <template #default="scope">
                    <el-tag type="success">{{ scope.row.status }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作">
                  <template #default="scope">
                    <el-button type="primary" size="small">查看详情</el-button>
                    <el-button type="success" size="small" @click="completeAppointment(scope.row.id)">完成</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
            
            <el-tab-pane label="已完成">
              <el-table :data="completedAppointments" style="width: 100%">
                <el-table-column prop="id" label="预约ID"></el-table-column>
                <el-table-column prop="studentName" label="学生"></el-table-column>
                <el-table-column prop="subject" label="学科"></el-table-column>
                <el-table-column prop="requestedDate" label="预约日期"></el-table-column>
                <el-table-column prop="requestedTime" label="预约时间"></el-table-column>
                <el-table-column prop="location" label="地点"></el-table-column>
                <el-table-column prop="pricePerHour" label="价格/小时"></el-table-column>
                <el-table-column prop="status" label="状态">
                  <template #default="scope">
                    <el-tag type="info">{{ scope.row.status }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作">
                  <template #default="scope">
                    <el-button type="primary" size="small">评价</el-button>
                    <el-button size="small">查看订单</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
            
            <el-tab-pane label="已拒绝">
              <el-table :data="rejectedAppointments" style="width: 100%">
                <el-table-column prop="id" label="预约ID"></el-table-column>
                <el-table-column prop="studentName" label="学生"></el-table-column>
                <el-table-column prop="subject" label="学科"></el-table-column>
                <el-table-column prop="requestedDate" label="预约日期"></el-table-column>
                <el-table-column prop="requestedTime" label="预约时间"></el-table-column>
                <el-table-column prop="location" label="地点"></el-table-column>
                <el-table-column prop="pricePerHour" label="价格/小时"></el-table-column>
                <el-table-column prop="status" label="状态">
                  <template #default="scope">
                    <el-tag type="danger">{{ scope.row.status }}</el-tag>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script>
export default {
  name: 'TeacherAppointmentsView',
  data() {
    return {
      activeIndex: '3',
      pendingAppointments: [
        {
          id: 1,
          studentName: '张三',
          subject: '数学',
          requestedDate: '2026-03-15',
          requestedTime: '14:00-16:00',
          location: '北京市海淀区',
          pricePerHour: 100,
          status: 'PENDING'
        }
      ],
      acceptedAppointments: [
        {
          id: 2,
          studentName: '李四',
          subject: '英语',
          requestedDate: '2026-03-10',
          requestedTime: '10:00-12:00',
          location: '北京市朝阳区',
          pricePerHour: 120,
          status: 'ACCEPTED'
        }
      ],
      completedAppointments: [
        {
          id: 3,
          studentName: '王五',
          subject: '物理',
          requestedDate: '2026-03-05',
          requestedTime: '16:00-18:00',
          location: '北京市海淀区',
          pricePerHour: 150,
          status: 'COMPLETED'
        }
      ],
      rejectedAppointments: [
        {
          id: 4,
          studentName: '赵六',
          subject: '化学',
          requestedDate: '2026-03-01',
          requestedTime: '09:00-11:00',
          location: '北京市西城区',
          pricePerHour: 130,
          status: 'REJECTED'
        }
      ]
    }
  },
  methods: {
    acceptAppointment(id) {
      // 这里应该调用接受预约API
      console.log('接受预约', id);
      this.$message.success('预约已接受');
      // 刷新预约列表
    },
    rejectAppointment(id) {
      // 这里应该调用拒绝预约API
      console.log('拒绝预约', id);
      this.$message.success('预约已拒绝');
      // 刷新预约列表
    },
    completeAppointment(id) {
      // 这里应该调用完成预约API
      console.log('完成预约', id);
      this.$message.success('预约已完成');
      // 刷新预约列表
    },
    logout() {
      // 清除本地存储的token和用户信息
      localStorage.removeItem('token');
      localStorage.removeItem('username');
      localStorage.removeItem('role');
      this.$router.push('/login');
    }
  }
}
</script>

<style scoped>
.teacher-appointments {
  min-height: 100vh;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 20px;
}

.header-content h1 {
  color: #409EFF;
  margin: 0;
}

.appointments-container {
  width: 1200px;
  margin: 50px auto;
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.appointments-container h2 {
  margin-bottom: 30px;
  color: #409EFF;
}
</style>