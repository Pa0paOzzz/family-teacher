<template>
  <div class="teacher-profile">
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
        <div class="profile-container">
          <h2>个人资料</h2>
          <el-form :model="teacherForm" :rules="rules" ref="teacherFormRef" label-width="100px">
            <el-form-item label="用户名">
              <el-input v-model="teacherForm.username" disabled></el-input>
            </el-form-item>
            <el-form-item label="姓名" prop="name">
              <el-input v-model="teacherForm.name"></el-input>
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="teacherForm.email"></el-input>
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="teacherForm.phone"></el-input>
            </el-form-item>
            <el-form-item label="学校" prop="school">
              <el-input v-model="teacherForm.school"></el-input>
            </el-form-item>
            <el-form-item label="专业" prop="major">
              <el-input v-model="teacherForm.major"></el-input>
            </el-form-item>
            <el-form-item label="学历" prop="education">
              <el-input v-model="teacherForm.education"></el-input>
            </el-form-item>
            <el-form-item label="教学经验" prop="teachingExperience">
              <el-input v-model="teacherForm.teachingExperience" type="textarea" rows="3"></el-input>
            </el-form-item>
            <el-form-item label="擅长学科" prop="subject">
              <el-input v-model="teacherForm.subject"></el-input>
            </el-form-item>
            <el-form-item label="个人简介" prop="bio">
              <el-input v-model="teacherForm.bio" type="textarea" rows="4"></el-input>
            </el-form-item>
            <el-form-item label="价格/小时" prop="pricePerHour">
              <el-input v-model.number="teacherForm.pricePerHour" type="number"></el-input>
            </el-form-item>
            <el-form-item label="地址" prop="address">
              <el-input v-model="teacherForm.address"></el-input>
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
  name: 'TeacherProfileView',
  data() {
    return {
      activeIndex: '1',
      teacherForm: {
        username: 'teacher1',
        name: '李四',
        email: 'lisi@example.com',
        phone: '13900139000',
        school: '清华大学',
        major: '数学',
        education: '硕士',
        teachingExperience: '3年教学经验，擅长高中数学',
        subject: '数学',
        bio: '热爱教育事业，有耐心，善于与学生沟通',
        pricePerHour: 150,
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
        major: [
          { required: true, message: '请输入专业', trigger: 'blur' }
        ],
        education: [
          { required: true, message: '请输入学历', trigger: 'blur' }
        ],
        teachingExperience: [
          { required: true, message: '请输入教学经验', trigger: 'blur' }
        ],
        subject: [
          { required: true, message: '请输入擅长学科', trigger: 'blur' }
        ],
        bio: [
          { required: true, message: '请输入个人简介', trigger: 'blur' }
        ],
        pricePerHour: [
          { required: true, message: '请输入价格/小时', trigger: 'blur' }
        ],
        address: [
          { required: true, message: '请输入地址', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    saveProfile() {
      this.$refs.teacherFormRef.validate((valid) => {
        if (valid) {
          // 这里应该调用更新个人资料API
          console.log('个人资料表单验证通过', this.teacherForm);
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
.teacher-profile {
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