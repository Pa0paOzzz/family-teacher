<template>
  <div class="home">
    <el-container>
      <el-header height="80px">
        <div class="header-content">
          <h1>大学生家教服务平台</h1>
          <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal">
            <el-menu-item index="1">
              <router-link to="/home">首页</router-link>
            </el-menu-item>
            <el-menu-item index="2">
              <router-link to="/teacher/job-post">找家教</router-link>
            </el-menu-item>
            <el-menu-item index="3">
              <router-link to="/student/tutoring-request">找学生</router-link>
            </el-menu-item>
            <el-menu-item index="4">关于我们</el-menu-item>
            <el-menu-item index="5" v-if="!isLoggedIn">
              <router-link to="/login">登录</router-link>
            </el-menu-item>
            <el-menu-item index="6" v-if="!isLoggedIn">
              <router-link to="/register">注册</router-link>
            </el-menu-item>
            <el-menu-item index="7" v-else>
              <span>{{ username }}</span>
            </el-menu-item>
          </el-menu>
        </div>
      </el-header>
      <el-container class="main-layout">
        <el-aside width="200px" class="side-nav">
          <el-menu :default-active="sideActiveIndex" class="side-menu" mode="vertical">
            <el-menu-item index="1">
              <router-link to="/home">Home</router-link>
            </el-menu-item>
            <el-menu-item index="2">
              <router-link to="/teacher/job-post">Teacher Jobs</router-link>
            </el-menu-item>
            <el-menu-item index="3">
              <router-link to="/student/tutoring-request">Student Requests</router-link>
            </el-menu-item>
            <el-menu-item index="4">
              <router-link to="/teacher/appointments">Teacher Appointments</router-link>
            </el-menu-item>
            <el-menu-item index="5">
              <router-link to="/student/appointments">Student Appointments</router-link>
            </el-menu-item>
          </el-menu>
        </el-aside>
        <el-main>
        <div class="banner">
          <h2>智能匹配，轻松找到合适的家教/学生</h2>
          <p>基于推荐算法，为您推荐最适合的家教或学生</p>
          <el-button type="primary" size="large">立即开始</el-button>
        </div>
        <div class="features">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-card shadow="hover">
                <template #header>
                  <div class="card-header">
                    <span>智能推荐</span>
                  </div>
                </template>
                <p>基于多维度匹配算法，为您推荐最适合的家教或学生</p>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card shadow="hover">
                <template #header>
                  <div class="card-header">
                    <span>安全交易</span>
                  </div>
                </template>
                <p>在线预约、支付，保障交易安全</p>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card shadow="hover">
                <template #header>
                  <div class="card-header">
                    <span>评价系统</span>
                  </div>
                </template>
                <p>真实评价，建立信用体系</p>
              </el-card>
            </el-col>
          </el-row>
        </div>
        </el-main>
      </el-container>
      <el-footer height="60px">
        <p>© 2026 大学生家教服务平台</p>
      </el-footer>
    </el-container>
  </div>
</template>

<script>
export default {
  name: 'HomeView',
  data() {
    return {
      activeIndex: '1',
      sideActiveIndex: '1',
      isLoggedIn: false,
      username: ''
    }
  }
}
</script>

<style scoped>
.home {
  min-height: 100vh;
}

.main-layout {
  min-height: calc(100vh - 140px);
}

.side-nav {
  background-color: #ffffff;
  border-right: 1px solid #e4e7ed;
}

.side-menu {
  height: 100%;
  border-right: none;
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

.banner {
  text-align: center;
  padding: 60px 0;
  background-color: #f5f7fa;
  margin-bottom: 40px;
}

.banner h2 {
  font-size: 36px;
  margin-bottom: 20px;
}

.banner p {
  font-size: 18px;
  margin-bottom: 30px;
  color: #606266;
}

.features {
  margin-top: 40px;
}

.card-header {
  display: flex;
  justify-content: center;
  font-size: 18px;
  font-weight: bold;
}

.el-footer {
  text-align: center;
  line-height: 60px;
  background-color: #f5f7fa;
}
</style>
