<template>
  <div class="admin-users">
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
import { User, DataLine, Calendar, SwitchButton, Comment } from '@element-plus/icons-vue';

export default {
  name: 'AdminUsersView',
  components: {
    User,
    DataLine,
    Calendar,
    Comment,
    SwitchButton
  },
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
    searchUsers() {
      console.log('搜索用户', this.searchQuery);
    },
    deleteUser(id) {
      console.log('删除用户', id);
      this.$message.success('用户已删除');
    },
    handleSizeChange(size) {
      this.pageSize = size;
    },
    handleCurrentChange(current) {
      this.currentPage = current;
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
.admin-users {
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

.users-container {
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  min-height: calc(100vh - 40px);
  width: 100%;
  box-sizing: border-box;
}

.el-input {
  width: 100% !important;
}

.el-table {
  width: 100% !important;
}

.users-container h2 {
  margin-bottom: 30px;
  color: #409EFF;
}
</style>
