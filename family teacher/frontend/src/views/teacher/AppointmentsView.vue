<template>
  <div class="teacher-appointments">
    <el-container style="height: 100vh;">
      <el-aside width="200px" class="sidebar">
        <div class="logo">
          <h2>家教平台</h2>
          <p>教师端</p>
        </div>
        <el-menu
          :default-active="activeIndex"
          class="sidebar-menu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="1">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="2">
            <el-icon><User /></el-icon>
            <span>个人资料</span>
          </el-menu-item>
          <el-menu-item index="3">
            <el-icon><EditPen /></el-icon>
            <span>发布求职</span>
          </el-menu-item>
          <el-menu-item index="4">
            <el-icon><Calendar /></el-icon>
            <span>我的预约</span>
          </el-menu-item>
          <el-menu-item index="5">
            <el-icon><Star /></el-icon>
            <span>我的收藏</span>
          </el-menu-item>
          <el-menu-item index="6">
            <el-icon><SwitchButton /></el-icon>
            <span>退出登录</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
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
import { User, HomeFilled, EditPen, Calendar, Star, SwitchButton } from '@element-plus/icons-vue';

export default {
  name: 'TeacherAppointmentsView',
  components: {
    User,
    HomeFilled,
    EditPen,
    Calendar,
    Star,
    SwitchButton
  },
  data() {
    return {
      activeIndex: '4',
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
    handleMenuSelect(index) {
      switch(index) {
        case '1':
          this.$router.push('/teacher/home');
          break;
        case '2':
          this.$router.push('/teacher/profile');
          break;
        case '3':
          this.$router.push('/teacher/job-post');
          break;
        case '4':
          this.$router.push('/teacher/appointments');
          break;
        case '5':
          this.$router.push('/teacher/favorites');
          break;
        case '6':
          this.logout();
          break;
      }
    },
    acceptAppointment(id) {
      console.log('接受预约', id);
      this.$message.success('预约已接受');
    },
    rejectAppointment(id) {
      console.log('拒绝预约', id);
      this.$message.success('预约已拒绝');
    },
    completeAppointment(id) {
      console.log('完成预约', id);
      this.$message.success('预约已完成');
    },
    logout() {
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
  background-color: #f5f7fa;
}

.sidebar {
  position: fixed;
  left: 0;
  top: 0;
  height: 100vh;
  background-color: #304156;
  color: white;
  z-index: 1000;
}

.el-main {
  padding: 20px;
  background-color: #f5f7fa;
  margin-left: 200px;
  height: 100vh;
  overflow-y: auto;
}

.logo {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid #3a4a5b;
}

.logo h2 {
  margin: 0;
  color: #409EFF;
  font-size: 20px;
}

.logo p {
  margin: 5px 0 0 0;
  color: #bfcbd9;
  font-size: 12px;
}

.sidebar-menu {
  border: none;
  background-color: #304156;
}

.sidebar-menu .el-menu-item {
  color: #bfcbd9;
}

.sidebar-menu .el-menu-item:hover {
  background-color: #263445;
}

.sidebar-menu .el-menu-item.is-active {
  color: #409EFF;
  background-color: #263445;
}

.appointments-container {
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  min-height: calc(100vh - 40px);
}

.appointments-container h2 {
  margin-bottom: 30px;
  color: #409EFF;
}
</style>
