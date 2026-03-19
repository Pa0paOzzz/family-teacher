<template>
  <div class="student-home">
    <el-container style="height: 100vh;">
      <el-aside width="200px" class="sidebar">
        <div class="logo">
          <h2>家教平台</h2>
          <p>学生端</p>
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
            <span>发布需求</span>
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
        <div class="main-content">
          <div class="search-container">
            <el-input
              v-model="searchSubject"
              placeholder="搜索学科"
              style="width: 300px; margin-right: 10px;"
              @keyup.enter="searchJobPosts"
            ></el-input>
            <el-button type="primary" @click="searchJobPosts">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </div>
          
          <div class="job-posts-container">
            <h2>教师求职信息</h2>
            <el-row :gutter="20">
              <el-col :span="8" v-for="jobPost in jobPostList" :key="jobPost.id">
                <el-card class="job-post-card" shadow="hover">
                  <template #header>
                    <div class="card-header">
                      <span class="title">{{ jobPost.title }}</span>
                      <el-tag type="success">{{ jobPost.subject }}</el-tag>
                    </div>
                  </template>
                  <div class="card-content">
                    <p class="description">{{ jobPost.description }}</p>
                    <div class="info-row">
                      <el-icon><User /></el-icon>
                      <span>{{ jobPost.teacher?.user?.name || '未知' }}</span>
                    </div>
                    <div class="info-row">
                      <el-icon><School /></el-icon>
                      <span>{{ jobPost.teacher?.school || '未知学校' }}</span>
                    </div>
                    <div class="info-row">
                      <el-icon><Location /></el-icon>
                      <span>{{ jobPost.location }}</span>
                    </div>
                    <div class="info-row">
                      <el-icon><Clock /></el-icon>
                      <span>{{ jobPost.availability }}</span>
                    </div>
                    <div class="price-row">
                      <span class="price">¥{{ jobPost.pricePerHour }}/小时</span>
                      <el-button type="primary" size="small" @click="openAppointmentDialog(jobPost)">预约</el-button>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
            <el-empty v-if="jobPostList.length === 0" description="暂无教师求职信息"></el-empty>
          </div>
        </div>
        
        <el-dialog v-model="appointmentDialogVisible" title="预约教师" width="500px">
          <el-form :model="appointmentForm" label-width="100px">
            <el-form-item label="老师姓名">
              <el-input :value="selectedJobPost?.teacher?.user?.name" disabled></el-input>
            </el-form-item>
            <el-form-item label="学科">
              <el-input :value="selectedJobPost?.subject" disabled></el-input>
            </el-form-item>
            <el-form-item label="预约时间">
              <el-date-picker
                v-model="appointmentForm.appointmentTime"
                type="datetime"
                placeholder="选择预约时间"
                style="width: 100%"
              ></el-date-picker>
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="appointmentForm.remark" type="textarea" rows="3" placeholder="请输入备注信息"></el-input>
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="appointmentDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitAppointment">确认预约</el-button>
          </template>
        </el-dialog>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { jobPostApi, appointmentApi } from '../../api/api';
import { User, School, Location, Clock, HomeFilled, EditPen, Calendar, SwitchButton } from '@element-plus/icons-vue';

export default {
  name: 'StudentHomeView',
  components: {
    User,
    School,
    Location,
    Clock,
    HomeFilled,
    EditPen,
    Calendar,
    SwitchButton
  },
  data() {
    return {
      activeIndex: '1',
      searchSubject: '',
      jobPostList: [],
      appointmentDialogVisible: false,
      selectedJobPost: null,
      appointmentForm: {
        appointmentTime: '',
        remark: ''
      }
    }
  },
  mounted() {
    this.loadJobPosts();
  },
  methods: {
    handleMenuSelect(index) {
      switch(index) {
        case '1':
          this.$router.push('/student/home');
          break;
        case '2':
          this.$router.push('/student/profile');
          break;
        case '3':
          this.$router.push('/student/tutoring-request');
          break;
        case '4':
          this.$router.push('/student/appointments');
          break;
        case '5':
          this.logout();
          break;
      }
    },
    async loadJobPosts() {
      try {
        const response = await jobPostApi.getList();
        this.jobPostList = response || [];
      } catch (error) {
        console.error('加载教师求职信息失败:', error);
        this.$message.error('加载教师求职信息失败');
      }
    },
    async searchJobPosts() {
      if (!this.searchSubject.trim()) {
        this.loadJobPosts();
        return;
      }
      try {
        const response = await jobPostApi.getList();
        this.jobPostList = (response || []).filter(post => 
          post.subject && post.subject.toLowerCase().includes(this.searchSubject.toLowerCase())
        );
      } catch (error) {
        console.error('搜索失败:', error);
        this.$message.error('搜索失败');
      }
    },
    resetSearch() {
      this.searchSubject = '';
      this.loadJobPosts();
    },
    openAppointmentDialog(jobPost) {
      this.selectedJobPost = jobPost;
      this.appointmentDialogVisible = true;
    },
    async submitAppointment() {
      if (!this.appointmentForm.appointmentTime) {
        this.$message.warning('请选择预约时间');
        return;
      }
      try {
        await appointmentApi.create({
          teacherId: this.selectedJobPost.teacher?.id,
          appointmentTime: this.appointmentForm.appointmentTime,
          remark: this.appointmentForm.remark
        });
        this.$message.success('预约成功');
        this.appointmentDialogVisible = false;
        this.appointmentForm = {
          appointmentTime: '',
          remark: ''
        };
      } catch (error) {
        console.error('预约失败:', error);
        this.$message.error('预约失败: ' + (error.response?.data?.error || error.message));
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
.student-home {
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

.main-content {
  min-height: calc(100vh - 40px);
}

.search-container {
  margin-bottom: 20px;
  padding: 20px;
  background-color: white;
  border-radius: 8px;
}

.job-posts-container {
  padding: 20px;
  background-color: white;
  border-radius: 8px;
  min-height: calc(100vh - 140px);
}

.job-posts-container h2 {
  margin-bottom: 20px;
  color: #303133;
}

.job-post-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header .title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.card-content .description {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 15px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.info-row {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  color: #606266;
  font-size: 14px;
}

.info-row .el-icon {
  margin-right: 8px;
}

.price-row {
  margin-top: 15px;
  padding-top: 10px;
  border-top: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  font-size: 20px;
  font-weight: bold;
  color: #f56c6c;
}
</style>
