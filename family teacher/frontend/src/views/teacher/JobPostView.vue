<template>
  <div class="teacher-job-post">
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
        <div class="job-post-container">
          <h2>发布求职信息</h2>
          <el-form :model="jobPostForm" :rules="rules" ref="jobPostFormRef" label-width="100px">
            <el-form-item label="标题" prop="title">
              <el-input v-model="jobPostForm.title" placeholder="请输入求职标题"></el-input>
            </el-form-item>
            <el-form-item label="描述" prop="description">
              <el-input v-model="jobPostForm.description" type="textarea" rows="4" placeholder="请详细描述求职信息"></el-input>
            </el-form-item>
            <el-form-item label="学科" prop="subject">
              <el-input v-model="jobPostForm.subject" placeholder="请输入擅长学科"></el-input>
            </el-form-item>
            <el-form-item label="价格/小时" prop="pricePerHour">
              <el-input v-model.number="jobPostForm.pricePerHour" type="number" placeholder="请输入每小时价格"></el-input>
            </el-form-item>
            <el-form-item label="地点" prop="location">
              <el-input v-model="jobPostForm.location" placeholder="请输入服务地点"></el-input>
            </el-form-item>
            <el-form-item label=" availability" prop="availability">
              <el-input v-model="jobPostForm.availability" placeholder="请输入 availability 时间"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="submitJobPost">发布求职</el-button>
            </el-form-item>
          </el-form>
          
          <h3>我的求职列表</h3>
          <el-table :data="jobPostList" style="width: 100%">
            <el-table-column prop="title" label="标题" width="200"></el-table-column>
            <el-table-column prop="subject" label="学科"></el-table-column>
            <el-table-column prop="pricePerHour" label="价格/小时"></el-table-column>
            <el-table-column prop="location" label="地点"></el-table-column>
            <el-table-column prop="createdAt" label="发布时间"></el-table-column>
            <el-table-column prop="active" label="状态">
              <template #default="scope">
                <el-tag :type="scope.row.active ? 'success' : 'danger'">
                  {{ scope.row.active ? '活跃' : '已关闭' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作">
              <template #default="scope">
                <el-button type="primary" size="small" @click="editJobPost(scope.row)">编辑</el-button>
                <el-button type="danger" size="small" @click="closeJobPost(scope.row.id)">关闭</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { jobPostApi } from '../../api/api';

export default {
  name: 'TeacherJobPostView',
  data() {
    return {
      activeIndex: '2',
      jobPostForm: {
        title: '',
        description: '',
        subject: '',
        pricePerHour: '',
        location: '',
        availability: ''
      },
      rules: {
        title: [
          { required: true, message: '请输入求职标题', trigger: 'blur' }
        ],
        description: [
          { required: true, message: '请详细描述求职信息', trigger: 'blur' }
        ],
        subject: [
          { required: true, message: '请输入擅长学科', trigger: 'blur' }
        ],
        pricePerHour: [
          { required: true, message: '请输入每小时价格', trigger: 'blur' }
        ],
        location: [
          { required: true, message: '请输入服务地点', trigger: 'blur' }
        ],
        availability: [
          { required: true, message: '请输入 availability 时间', trigger: 'blur' }
        ]
      },
      jobPostList: []
    }
  },
  mounted() {
    this.getJobPostList();
  },
  methods: {
    async getJobPostList() {
      try {
        const response = await jobPostApi.getList();
        this.jobPostList = response;
      } catch (error) {
        console.error('获取求职列表失败:', error);
        this.$message.error('获取求职列表失败');
      }
    },
    async submitJobPost() {
      this.$refs.jobPostFormRef.validate(async (valid) => {
        if (valid) {
          try {
            await jobPostApi.create(this.jobPostForm);
            this.$message.success('发布成功');
            // 清空表单
            this.$refs.jobPostFormRef.resetFields();
            // 刷新求职列表
            this.getJobPostList();
          } catch (error) {
            console.error('发布求职失败:', error);
            this.$message.error('发布求职失败');
          }
        } else {
          console.log('求职表单验证失败');
          return false;
        }
      });
    },
    async editJobPost(jobPost) {
      // 这里可以实现编辑功能
      console.log('编辑求职:', jobPost);
    },
    async closeJobPost(id) {
      try {
        await jobPostApi.deactivate(id);
        this.$message.success('关闭成功');
        // 刷新求职列表
        this.getJobPostList();
      } catch (error) {
        console.error('关闭求职失败:', error);
        this.$message.error('关闭求职失败');
      }
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
.teacher-job-post {
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

.job-post-container {
  width: 1000px;
  margin: 50px auto;
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.job-post-container h2 {
  margin-bottom: 30px;
  color: #409EFF;
}

.job-post-container h3 {
  margin: 40px 0 20px 0;
  color: #409EFF;
}

.el-form-item {
  margin-bottom: 20px;
}
</style>