<template>
  <div class="student-appointments">
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
            <el-icon><Star /></el-icon>
            <span>我的收藏</span>
          </el-menu-item>
          <el-menu-item index="6">
            <el-icon><SwitchButton /></el-icon>
            <span>退出登录</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main>
        <div class="appointments-container">
          <h2>我的预约</h2>
          <el-tabs type="border-card">
            <el-tab-pane label="待处理">
              <el-table :data="pendingAppointments" style="width: 100%">
                <el-table-column prop="teacherName" label="家教老师"></el-table-column>
                <el-table-column prop="subject" label="学科"></el-table-column>
                <el-table-column prop="requestedDate" label="预约日期"></el-table-column>
                <el-table-column prop="requestedTime" label="预约时间"></el-table-column>
                <el-table-column prop="location" label="地点"></el-table-column>
                <el-table-column prop="pricePerHour" label="价格/小时"></el-table-column>
                <el-table-column prop="status" label="状态">
                  <template #default="scope">
                    <el-tag type="warning">{{ scope.row.status }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作">
                  <template #default="scope">
                    <el-button type="info" size="small" @click="openDetailDialog(scope.row)">查看详情</el-button>
                    <el-button type="danger" size="small" @click="cancelAppointment(scope.row.id)">取消</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
            <el-tab-pane label="已接受">
              <el-table :data="acceptedAppointments" style="width: 100%">
                <el-table-column prop="teacherName" label="家教老师"></el-table-column>
                <el-table-column prop="subject" label="学科"></el-table-column>
                <el-table-column prop="requestedDate" label="预约日期"></el-table-column>
                <el-table-column prop="requestedTime" label="预约时间"></el-table-column>
                <el-table-column prop="location" label="地点"></el-table-column>
                <el-table-column prop="pricePerHour" label="价格/小时"></el-table-column>
                <el-table-column prop="status" label="状态">
                  <template #default="scope">
                    <el-tag type="success">{{ scope.row.status }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作">
                  <template #default="scope">
                    <el-button type="info" size="small" @click="openDetailDialog(scope.row)">查看详情</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
            <el-tab-pane label="已完成">
              <el-table :data="completedAppointments" style="width: 100%">
                <el-table-column prop="teacherName" label="家教老师"></el-table-column>
                <el-table-column prop="subject" label="学科"></el-table-column>
                <el-table-column prop="requestedDate" label="预约日期"></el-table-column>
                <el-table-column prop="requestedTime" label="预约时间"></el-table-column>
                <el-table-column prop="location" label="地点"></el-table-column>
                <el-table-column prop="pricePerHour" label="价格/小时"></el-table-column>
                <el-table-column prop="status" label="状态">
                  <template #default="scope">
                    <el-tag type="info">{{ scope.row.status }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作">
                  <template #default="scope">
                    <el-button type="info" size="small" @click="openDetailDialog(scope.row)">查看详情</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
            <el-tab-pane label="已拒绝">
              <el-table :data="rejectedAppointments" style="width: 100%">
                <el-table-column prop="teacherName" label="家教老师"></el-table-column>
                <el-table-column prop="subject" label="学科"></el-table-column>
                <el-table-column prop="requestedDate" label="预约日期"></el-table-column>
                <el-table-column prop="requestedTime" label="预约时间"></el-table-column>
                <el-table-column prop="location" label="地点"></el-table-column>
                <el-table-column prop="pricePerHour" label="价格/小时"></el-table-column>
                <el-table-column prop="status" label="状态">
                  <template #default="scope">
                    <el-tag type="danger">{{ scope.row.status }}</el-tag>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </div>
        
        <el-dialog v-model="detailDialogVisible" title="预约详情" width="700px">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="预约ID" :span="2">
              {{ selectedAppointment?.id || '未知' }}
            </el-descriptions-item>
            <el-descriptions-item label="家教老师" :span="2">
              {{ selectedAppointment?.teacherName || '未知' }}
            </el-descriptions-item>
            <el-descriptions-item label="学科">
              {{ selectedAppointment?.subject || '未知' }}
            </el-descriptions-item>
            <el-descriptions-item label="预约日期">
              {{ selectedAppointment?.requestedDate || '未知' }}
            </el-descriptions-item>
            <el-descriptions-item label="预约时间">
              {{ selectedAppointment?.requestedTime || '未知' }}
            </el-descriptions-item>
            <el-descriptions-item label="地点" :span="2">
              {{ selectedAppointment?.location || '未知' }}
            </el-descriptions-item>
            <el-descriptions-item label="价格/小时">
              ¥{{ selectedAppointment?.pricePerHour || 0 }}
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(selectedAppointment?.status)">{{ selectedAppointment?.status || '未知' }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="备注" :span="2">
              {{ selectedAppointment?.notes || '无' }}
            </el-descriptions-item>
          </el-descriptions>
          
          <template #footer>
            <el-button @click="detailDialogVisible = false">关闭</el-button>
          </template>
        </el-dialog>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { appointmentApi } from '../../api/api';
import { User, HomeFilled, EditPen, Calendar, Star, SwitchButton } from '@element-plus/icons-vue';

export default {
  name: 'StudentAppointmentsView',
  components: {
    User,
    HomeFilled,
    EditPen,
    Calendar,
    Star,
    SwitchButton
  },
  data() {
    return {
      activeIndex: '4',
      pendingAppointments: [],
      acceptedAppointments: [],
      completedAppointments: [],
      rejectedAppointments: [],
      detailDialogVisible: false,
      selectedAppointment: null,
      refreshInterval: null
    }
  },
  mounted() {
    this.loadAppointments();
    this.startAutoRefresh();
  },
  beforeUnmount() {
    this.stopAutoRefresh();
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
          this.$router.push('/student/favorites');
          break;
        case '6':
          this.logout();
          break;
      }
    },
    async loadAppointments() {
      try {
        const response = await appointmentApi.getList();
        this.pendingAppointments = (response || []).filter(app => app.status === 'PENDING');
        this.acceptedAppointments = (response || []).filter(app => app.status === 'ACCEPTED');
        this.completedAppointments = (response || []).filter(app => app.status === 'COMPLETED');
        this.rejectedAppointments = (response || []).filter(app => app.status === 'REJECTED');
      } catch (error) {
        console.error('加载预约列表失败:', error);
        this.$message.error('加载预约列表失败');
      }
    },
    async cancelAppointment(id) {
      try {
        await appointmentApi.delete(id);
        this.$message.success('预约已取消');
        this.loadAppointments();
      } catch (error) {
        console.error('取消预约失败:', error);
        this.$message.error('取消预约失败');
      }
    },
    openDetailDialog(appointment) {
      this.selectedAppointment = appointment;
      this.detailDialogVisible = true;
    },
    getStatusType(status) {
      switch(status) {
        case 'PENDING':
          return 'warning';
        case 'ACCEPTED':
          return 'success';
        case 'COMPLETED':
          return 'info';
        case 'REJECTED':
          return 'danger';
        default:
          return '';
      }
    },
    startAutoRefresh() {
      this.refreshInterval = setInterval(() => {
        this.loadAppointments();
      }, 5000);
    },
    stopAutoRefresh() {
      if (this.refreshInterval) {
        clearInterval(this.refreshInterval);
        this.refreshInterval = null;
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
.student-appointments {
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
