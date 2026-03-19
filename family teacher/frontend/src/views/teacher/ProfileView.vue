<template>
  <div class="teacher-profile">
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
            <el-icon><SwitchButton /></el-icon>
            <span>退出登录</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
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
import { userApi } from '../../api/api';
import { User, HomeFilled, EditPen, Calendar, SwitchButton } from '@element-plus/icons-vue';

export default {
  name: 'TeacherProfileView',
  components: {
    User,
    HomeFilled,
    EditPen,
    Calendar,
    SwitchButton
  },
  data() {
    return {
      activeIndex: '2',
      teacherForm: {
        username: '',
        name: '',
        email: '',
        phone: '',
        school: '',
        major: '',
        education: '',
        teachingExperience: '',
        subject: '',
        bio: '',
        pricePerHour: 0,
        address: ''
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
  mounted() {
    this.loadProfile();
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
          this.logout();
          break;
      }
    },
    async loadProfile() {
      try {
        const response = await userApi.getTeacherProfile();
        this.teacherForm = {
          username: response.username || '',
          name: response.name || '',
          email: response.email || '',
          phone: response.phone || '',
          school: response.school || '',
          major: response.major || '',
          education: response.education || '',
          teachingExperience: response.teachingExperience || '',
          subject: response.subject || '',
          bio: response.bio || '',
          pricePerHour: response.pricePerHour || 0,
          address: response.address || ''
        };
      } catch (error) {
        console.error('加载个人资料失败:', error);
        this.$message.error('加载个人资料失败');
      }
    },
    async saveProfile() {
      this.$refs.teacherFormRef.validate(async (valid) => {
        if (valid) {
          try {
            await userApi.updateTeacherProfile(this.teacherForm);
            this.$message.success('保存成功');
            this.loadProfile();
          } catch (error) {
            console.error('保存个人资料失败:', error);
            this.$message.error('保存失败: ' + (error.response?.data?.error || error.message));
          }
        } else {
          console.log('个人资料表单验证失败');
          return false;
        }
      });
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
.teacher-profile {
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

.profile-container {
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  min-height: calc(100vh - 40px);
  width: 100%;
  box-sizing: border-box;
}

.el-form {
  width: 100%;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-form-item__content {
  flex: 1;
}

.el-input {
  width: 100%;
}

.profile-container h2 {
  margin-bottom: 30px;
  color: #409EFF;
}

.el-form-item {
  margin-bottom: 20px;
}
</style>
