<template>
  <div class="admin-users">
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
        <div class="users-container">
          <h2>用户管理</h2>
          <el-row :gutter="20" style="margin-bottom: 20px;">
            <el-col :span="12">
              <el-input placeholder="请输入用户名或邮箱" v-model="searchQuery" style="width: 300px;">
                <template #append>
                  <el-button @click="searchUsers">搜索</el-button>
                </template>
              </el-input>
            </el-col>
            <el-col :span="12" style="text-align: right;">
              <el-button type="primary">添加用户</el-button>
            </el-col>
          </el-row>
          <el-table :data="usersList" style="width: 100%">
            <el-table-column prop="id" label="用户ID" width="100"></el-table-column>
            <el-table-column prop="username" label="用户名"></el-table-column>
            <el-table-column prop="name" label="姓名"></el-table-column>
            <el-table-column prop="email" label="邮箱"></el-table-column>
            <el-table-column prop="phone" label="手机号"></el-table-column>
            <el-table-column prop="role" label="角色">
              <template #default="scope">
                <el-tag :type="scope.row.role === 'ADMIN' ? 'danger' : (scope.row.role === 'TEACHER' ? 'success' : 'info')">
                  {{ scope.row.role === 'ADMIN' ? '管理员' : (scope.row.role === 'TEACHER' ? '家教老师' : '学生') }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="创建时间"></el-table-column>
            <el-table-column label="操作">
              <template #default="scope">
                <el-button type="primary" size="small">编辑</el-button>
                <el-button type="danger" size="small" @click="deleteUser(scope.row.id)">删除</el-button>
                <el-button type="warning" size="small">禁用</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="currentPage"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="totalUsers"
            style="margin-top: 20px; text-align: right;"
          ></el-pagination>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script>
export default {
  name: 'AdminUsersView',
  data() {
    return {
      activeIndex: '2',
      searchQuery: '',
      usersList: [
        {
          id: 1,
          username: 'admin',
          name: '管理员',
          email: 'admin@example.com',
          phone: '13800138000',
          role: 'ADMIN',
          createdAt: '2026-03-01 10:00:00'
        },
        {
          id: 2,
          username: 'teacher1',
          name: '李四',
          email: 'lisi@example.com',
          phone: '13900139000',
          role: 'TEACHER',
          createdAt: '2026-03-02 11:00:00'
        },
        {
          id: 3,
          username: 'student1',
          name: '张三',
          email: 'zhangsan@example.com',
          phone: '13800138001',
          role: 'STUDENT',
          createdAt: '2026-03-03 12:00:00'
        }
      ],
      currentPage: 1,
      pageSize: 10,
      totalUsers: 3
    }
  },
  methods: {
    searchUsers() {
      // 这里应该调用搜索用户API
      console.log('搜索用户', this.searchQuery);
    },
    deleteUser(id) {
      // 这里应该调用删除用户API
      console.log('删除用户', id);
      this.$message.success('用户已删除');
      // 刷新用户列表
    },
    handleSizeChange(size) {
      this.pageSize = size;
      // 这里应该调用分页API
    },
    handleCurrentChange(current) {
      this.currentPage = current;
      // 这里应该调用分页API
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
.admin-users {
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

.users-container {
  width: 1200px;
  margin: 50px auto;
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.users-container h2 {
  margin-bottom: 30px;
  color: #409EFF;
}
</style>