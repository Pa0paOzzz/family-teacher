<template>
  <div class="admin-appointments">
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
export default {
  name: 'AdminAppointmentsView',
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
    searchAppointments() {
      // 这里应该调用搜索预约API
      console.log('搜索预约', this.searchQuery);
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
.admin-appointments {
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

.appointments-container {
  width: 1200px;
  margin: 50px auto;
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.appointments-container h2 {
  margin-bottom: 30px;
  color: #409EFF;
}
</style>