<template>
  <div class="student-tutoring-request">
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
        <div class="request-container">
          <h2>发布家教需求</h2>
          <el-form :model="requestForm" :rules="rules" ref="requestFormRef" label-width="100px">
            <el-form-item label="标题" prop="title">
              <el-input v-model="requestForm.title" placeholder="请输入需求标题"></el-input>
            </el-form-item>
            <el-form-item label="描述" prop="description">
              <el-input v-model="requestForm.description" type="textarea" rows="4" placeholder="请详细描述家教需求"></el-input>
            </el-form-item>
            <el-form-item label="学科" prop="subject">
              <el-input v-model="requestForm.subject" placeholder="请输入学科"></el-input>
            </el-form-item>
            <el-form-item label="预算/小时" prop="budgetPerHour">
              <el-input v-model.number="requestForm.budgetPerHour" type="number" placeholder="请输入每小时预算"></el-input>
            </el-form-item>
            <el-form-item label="地点" prop="location">
              <el-input v-model="requestForm.location" placeholder="请输入家教地点"></el-input>
            </el-form-item>
            <el-form-item label=" preferredTime" prop="preferredTime">
              <el-input v-model="requestForm.preferredTime" placeholder="请输入 preferredTime 时间"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="submitRequest">发布需求</el-button>
            </el-form-item>
          </el-form>
          
          <h3>我的需求列表</h3>
          <el-table :data="requestList" style="width: 100%">
            <el-table-column prop="title" label="标题" width="200"></el-table-column>
            <el-table-column prop="subject" label="学科"></el-table-column>
            <el-table-column prop="budgetPerHour" label="预算/小时"></el-table-column>
            <el-table-column prop="location" label="地点"></el-table-column>
            <el-table-column prop="createdAt" label="发布时间"></el-table-column>
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <el-tag :type="scope.row.status === 'active' ? 'success' : 'danger'">
                  {{ scope.row.status === 'active' ? '活跃' : '已关闭' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作">
              <template #default="scope">
                <el-button type="primary" size="small">编辑</el-button>
                <el-button type="danger" size="small">关闭</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script>
export default {
  name: 'StudentTutoringRequestView',
  data() {
    return {
      activeIndex: '2',
      requestForm: {
        title: '',
        description: '',
        subject: '',
        budgetPerHour: '',
        location: '',
        preferredTime: ''
      },
      rules: {
        title: [
          { required: true, message: '请输入需求标题', trigger: 'blur' }
        ],
        description: [
          { required: true, message: '请详细描述家教需求', trigger: 'blur' }
        ],
        subject: [
          { required: true, message: '请输入学科', trigger: 'blur' }
        ],
        budgetPerHour: [
          { required: true, message: '请输入每小时预算', trigger: 'blur' }
        ],
        location: [
          { required: true, message: '请输入家教地点', trigger: 'blur' }
        ],
        preferredTime: [
          { required: true, message: '请输入 preferredTime 时间', trigger: 'blur' }
        ]
      },
      requestList: [
        {
          id: 1,
          title: '寻找数学家教',
          subject: '数学',
          budgetPerHour: 100,
          location: '北京市海淀区',
          createdAt: '2026-03-01',
          status: 'active'
        },
        {
          id: 2,
          title: '寻找英语家教',
          subject: '英语',
          budgetPerHour: 120,
          location: '北京市朝阳区',
          createdAt: '2026-02-28',
          status: 'closed'
        }
      ]
    }
  },
  methods: {
    submitRequest() {
      this.$refs.requestFormRef.validate((valid) => {
        if (valid) {
          // 这里应该调用发布需求API
          console.log('需求表单验证通过', this.requestForm);
          this.$message.success('发布成功');
          // 清空表单
          this.$refs.requestFormRef.resetFields();
          // 刷新需求列表
        } else {
          console.log('需求表单验证失败');
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
.student-tutoring-request {
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

.request-container {
  width: 1000px;
  margin: 50px auto;
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.request-container h2 {
  margin-bottom: 30px;
  color: #409EFF;
}

.request-container h3 {
  margin: 40px 0 20px 0;
  color: #409EFF;
}

.el-form-item {
  margin-bottom: 20px;
}
</style>