<template>
  <div class="student-appointments">
    <el-container>
      <el-header height="80px">
        <div class="header-content">
          <h1>学生个人中心</h1>
          <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal">
            <el-menu-item index="1">
              <router-link to="/student/profile">个人资料</router-link>
            </el-menu-item>
            <el-menu-item index="2">
              <router-link to="/student/tutoring-request">发布需求</router-link>
            </el-menu-item>
            <el-menu-item index="3">
              <router-link to="/student/appointments">我的预约</router-link>
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
                <el-table-column prop="teacherName" label="家教老师"></el-table-column>
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
                    <el-button type="danger" size="small">取消</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
            
            <el-tab-pane label="已接受">
              <el-table :data="acceptedAppointments" style="width: 100%">
                <el-table-column prop="id" label="预约ID"></el-table-column>
                <el-table-column prop="teacherName" label="家教老师"></el-table-column>
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
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
            
            <el-tab-pane label="已完成">
              <el-table :data="completedAppointments" style="width: 100%">
                <el-table-column prop="id" label="预约ID"></el-table-column>
                <el-table-column prop="teacherName" label="家教老师"></el-table-column>
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
                <el-table-column prop="teacherName" label="家教老师"></el-table-column>
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
  name: 'StudentAppointmentsView',
  data() {
    return {
      activeIndex: '3',
      pendingAppointments: [
        {
          id: 1,
          teacherName: '李四',
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
          teacherName: '王五',
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
          teacherName: '赵六',
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
          teacherName: '钱七',
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
.student-appointments {
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