<template>
  <div class="admin-appointments">
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
        <div class="appointments-container">
          <h2>预约管理</h2>
          <el-row :gutter="20" style="margin-bottom: 20px;">
            <el-col :span="12">
              <el-input placeholder="请输入预约ID或用户姓名" v-model="searchQuery" style="width: 300px;">
                <template #append>
                  <el-button @click="searchAppointments">搜索</el-button>
                </template>
              </el-input>
            </el-col>
            <el-col :span="12" style="text-align: right;">
              <el-select v-model="statusFilter" placeholder="按状态筛选" style="width: 150px; margin-right: 10px;">
                <el-option label="全部" value=""></el-option>
                <el-option label="待处理" value="PENDING"></el-option>
                <el-option label="已接受" value="ACCEPTED"></el-option>
                <el-option label="已完成" value="COMPLETED"></el-option>
                <el-option label="已拒绝" value="REJECTED"></el-option>
              </el-select>
              <el-button type="primary">导出</el-button>
            </el-col>
          </el-row>
          <el-table :data="appointmentsList" style="width: 100%">
            <el-table-column prop="id" label="预约ID" width="100"></el-table-column>
            <el-table-column prop="studentName" label="学生"></el-table-column>
            <el-table-column prop="teacherName" label="家教老师"></el-table-column>
            <el-table-column prop="subject" label="学科"></el-table-column>
            <el-table-column prop="requestedDate" label="预约日期"></el-table-column>
            <el-table-column prop="requestedTime" label="预约时间"></el-table-column>
            <el-table-column prop="location" label="地点"></el-table-column>
            <el-table-column prop="pricePerHour" label="价格/小时"></el-table-column>
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <el-tag :type="scope.row.status === 'PENDING' ? 'warning' : (scope.row.status === 'ACCEPTED' ? 'success' : (scope.row.status === 'COMPLETED' ? 'info' : 'danger'))">
                  {{ scope.row.status === 'PENDING' ? '待处理' : (scope.row.status === 'ACCEPTED' ? '已接受' : (scope.row.status === 'COMPLETED' ? '已完成' : '已拒绝')) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作">
              <template #default="scope">
                <el-button type="primary" size="small">查看详情</el-button>
                <el-button type="danger" size="small">删除</el-button>
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
            :total="totalAppointments"
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
  name: 'AdminAppointmentsView',
  components: {
    User,
    DataLine,
    Calendar,
    Comment,
    SwitchButton
  },
  data() {
    return {
      activeIndex: '3',
      searchQuery: '',
      statusFilter: '',
      appointmentsList: [
        {
          id: 1,
          studentName: '张三',
          teacherName: '李四',
          subject: '数学',
          requestedDate: '2026-03-15',
          requestedTime: '14:00-16:00',
          location: '北京市海淀区',
          pricePerHour: 100,
          status: 'PENDING'
        },
        {
          id: 2,
          studentName: '李四',
          teacherName: '王五',
          subject: '英语',
          requestedDate: '2026-03-10',
          requestedTime: '10:00-12:00',
          location: '北京市朝阳区',
          pricePerHour: 120,
          status: 'ACCEPTED'
        },
        {
          id: 3,
          studentName: '王五',
          teacherName: '赵六',
          subject: '物理',
          requestedDate: '2026-03-05',
          requestedTime: '16:00-18:00',
          location: '北京市海淀区',
          pricePerHour: 150,
          status: 'COMPLETED'
        },
        {
          id: 4,
          studentName: '赵六',
          teacherName: '钱七',
          subject: '化学',
          requestedDate: '2026-03-01',
          requestedTime: '09:00-11:00',
          location: '北京市西城区',
          pricePerHour: 130,
          status: 'REJECTED'
        }
      ],
      currentPage: 1,
      pageSize: 10,
      totalAppointments: 4
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
    searchAppointments() {
      console.log('搜索预约', this.searchQuery);
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
.admin-appointments {
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

.appointments-container {
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  min-height: calc(100vh - 40px);
}

.appointments-container h2 {
  margin-bottom: 30px;
  color: #409EFF;
}
</style>
