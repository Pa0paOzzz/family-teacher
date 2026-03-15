<template>
  <div class="admin-dashboard">
    <el-container>
      <el-header height="80px">
        <div class="header-content">
          <h1>后台管理系统</h1>
          <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal">
            <el-menu-item index="1">
              <router-link to="/admin/dashboard">仪表板</router-link>
            </el-menu-item>
            <el-menu-item index="2">
              <router-link to="/admin/users">用户管理</router-link>
            </el-menu-item>
            <el-menu-item index="3">
              <router-link to="/admin/appointments">预约管理</router-link>
            </el-menu-item>
            <el-menu-item index="4" @click="logout">退出登录</el-menu-item>
          </el-menu>
        </div>
      </el-header>
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
export default {
  name: 'AdminDashboardView',
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
.admin-dashboard {
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

.dashboard-container {
  width: 1200px;
  margin: 50px auto;
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
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