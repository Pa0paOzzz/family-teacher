<template>
  <div class="student-profile">
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
        <div class="profile-container">
          <h2>个人资料</h2>
          <el-form :model="studentForm" :rules="rules" ref="studentFormRef" label-width="100px">
            <el-form-item label="用户名">
              <el-input v-model="studentForm.username" disabled></el-input>
            </el-form-item>
            <el-form-item label="姓名" prop="name">
              <el-input v-model="studentForm.name"></el-input>
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="studentForm.email"></el-input>
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="studentForm.phone"></el-input>
            </el-form-item>
            <el-form-item label="学校" prop="school">
              <el-input v-model="studentForm.school"></el-input>
            </el-form-item>
            <el-form-item label="年级" prop="grade">
              <el-input v-model="studentForm.grade"></el-input>
            </el-form-item>
            <el-form-item label="专业" prop="major">
              <el-input v-model="studentForm.major"></el-input>
            </el-form-item>
            <el-form-item label="地址" prop="address">
              <el-input v-model="studentForm.address"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveProfile">保存修改</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script>
export default {
  name: 'StudentProfileView',
  data() {
    return {
      activeIndex: '1',
      studentForm: {
        username: 'student1',
        name: '张三',
        email: 'zhangsan@example.com',
        phone: '13800138000',
        school: '北京大学',
        grade: '大三',
        major: '计算机科学与技术',
        address: '北京市海淀区'
      },
      rules: {
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' }
        ],
        school: [
          { required: true, message: '请输入学校', trigger: 'blur' }
        ],
        grade: [
          { required: true, message: '请输入年级', trigger: 'blur' }
        ],
        major: [
          { required: true, message: '请输入专业', trigger: 'blur' }
        ],
        address: [
          { required: true, message: '请输入地址', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    saveProfile() {
      this.$refs.studentFormRef.validate((valid) => {
        if (valid) {
          // 这里应该调用更新个人资料API
          console.log('个人资料表单验证通过', this.studentForm);
          this.$message.success('保存成功');
        } else {
          console.log('个人资料表单验证失败');
          return false;
        }
      });
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
.student-profile {
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

.profile-container {
  width: 800px;
  margin: 50px auto;
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.profile-container h2 {
  margin-bottom: 30px;
  color: #409EFF;
}

.el-form-item {
  margin-bottom: 20px;
}
</style>