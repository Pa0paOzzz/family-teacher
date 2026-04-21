<template>
  <div class="admin-dashboard">
    <el-container style="height: 100vh;">
      <el-aside width="200px" class="sidebar">
        <div class="logo">
          <h2>家教平台</h2>
          <p>管理员端</p>
        </div>
        <el-menu
          :default-active="activeIndex"
          class="sidebar-menu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="1">
            <el-icon><DataLine /></el-icon>
            <span>仪表板</span>
          </el-menu-item>
          <el-menu-item index="2">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="3">
            <el-icon><Calendar /></el-icon>
            <span>预约管理</span>
          </el-menu-item>
          <el-menu-item index="4">
            <el-icon><Comment /></el-icon>
            <span>评价管理</span>
          </el-menu-item>
          <el-menu-item index="5">
            <el-icon><SwitchButton /></el-icon>
            <span>退出登录</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main>
        <div class="dashboard-container">
          <h2>系统概览</h2>
          <el-row :gutter="20">
            <el-col :span="6">
              <el-card shadow="hover">
                <template #header>
                  <div class="card-header">
                    <span>总用户数</span>
                  </div>
                </template>
                <div class="card-content">
                  <el-statistic :value="statistics.totalUsers" suffix="人"></el-statistic>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover">
                <template #header>
                  <div class="card-header">
                    <span>家教老师</span>
                  </div>
                </template>
                <div class="card-content">
                  <el-statistic :value="statistics.totalTeachers" suffix="人"></el-statistic>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover">
                <template #header>
                  <div class="card-header">
                    <span>学生</span>
                  </div>
                </template>
                <div class="card-content">
                  <el-statistic :value="statistics.totalStudents" suffix="人"></el-statistic>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover">
                <template #header>
                  <div class="card-header">
                    <span>总订单数</span>
                  </div>
                </template>
                <div class="card-content">
                  <el-statistic :value="statistics.totalOrders" suffix="单"></el-statistic>
                </div>
              </el-card>
            </el-col>
          </el-row>
          <el-row :gutter="20" style="margin-top: 20px;">
            <el-col :span="8">
              <el-card shadow="hover">
                <template #header>
                  <div class="card-header">
                    <span>总预约数</span>
                  </div>
                </template>
                <div class="card-content">
                  <el-statistic :value="statistics.totalAppointments" suffix="次"></el-statistic>
                </div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card shadow="hover">
                <template #header>
                  <div class="card-header">
                    <span>待处理预约</span>
                  </div>
                </template>
                <div class="card-content">
                  <el-statistic :value="statistics.pendingAppointments" suffix="次"></el-statistic>
                </div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card shadow="hover">
                <template #header>
                  <div class="card-header">
                    <span>已完成预约</span>
                  </div>
                </template>
                <div class="card-content">
                  <el-statistic :value="statistics.completedAppointments" suffix="次"></el-statistic>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { User, DataLine, Calendar, SwitchButton, Comment } from '@element-plus/icons-vue';

export default {
  name: 'AdminDashboardView',
  components: {
    User,
    DataLine,
    Calendar,
    Comment,
    SwitchButton
  },
  data() {
    return {
      activeIndex: '1',
      statistics: {
        totalUsers: 100,
        totalTeachers: 50,
        totalStudents: 50,
        totalOrders: 200,
        totalAppointments: 250,
        pendingAppointments: 20,
        completedAppointments: 230
      }
    }
  },
  methods: {
    handleMenuSelect(index) {
      switch(index) {
        case '1':
          this.$router.push('/admin/dashboard');
          break;
        case '2':
          this.$router.push('/admin/users');
          break;
        case '3':
          this.$router.push('/admin/appointments');
          break;
        case '4':
          this.$router.push('/admin/evaluations');
          break;
        case '5':
          this.logout();
          break;
      }
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
.admin-dashboard {
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

.dashboard-container {
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  min-height: calc(100vh - 40px);
}

.dashboard-container h2 {
  margin-bottom: 30px;
  color: #409EFF;
}

.card-header {
  display: flex;
  justify-content: center;
  font-size: 16px;
  font-weight: bold;
}

.card-content {
  text-align: center;
  padding: 20px 0;
}
</style>
