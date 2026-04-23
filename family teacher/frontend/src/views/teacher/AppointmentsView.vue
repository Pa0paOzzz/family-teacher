<template>
  <div class="teacher-appointments">
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
            <el-icon><Star /></el-icon>
            <span>我的收藏</span>
          </el-menu-item>
          <el-menu-item index="6">
            <el-icon><Comment /></el-icon>
            <span>我的评价</span>
          </el-menu-item>
          <el-menu-item index="7">
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
                <el-table-column prop="studentName" label="学生"></el-table-column>
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
                    <el-button type="success" size="small" @click="acceptAppointment(scope.row.id)">接受</el-button>
                    <el-button type="danger" size="small" @click="rejectAppointment(scope.row.id)">拒绝</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
            <el-tab-pane label="已接受">
              <el-table :data="acceptedAppointments" style="width: 100%">
                <el-table-column prop="studentName" label="学生"></el-table-column>
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
                    <el-button type="primary" size="small" @click="openDetailDialog(scope.row)">查看详情</el-button>
                    <el-button type="success" size="small" @click="completeAppointment(scope.row.id)">完成</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
            <el-tab-pane label="已完成">
              <el-table :data="completedAppointments" style="width: 100%">
                <el-table-column prop="studentName" label="学生"></el-table-column>
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
                    <el-button type="warning" size="small" @click="openEvaluateDialog(scope.row)" :disabled="scope.row.hasEvaluated">{{ scope.row.hasEvaluated ? '已评价' : '评价' }}</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
            <el-tab-pane label="已拒绝">
              <el-table :data="rejectedAppointments" style="width: 100%">
                <el-table-column prop="studentName" label="学生"></el-table-column>
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
            <el-descriptions-item label="学生姓名" :span="2">
              {{ selectedAppointment?.studentName || '未知' }}
            </el-descriptions-item>
            <el-descriptions-item label="学校">
              {{ selectedAppointment?.studentSchool || '未知' }}
            </el-descriptions-item>
            <el-descriptions-item label="年级">
              {{ selectedAppointment?.studentGrade || '未知' }}
            </el-descriptions-item>
            <el-descriptions-item label="联系电话">
              {{ selectedAppointment?.studentPhone || '未知' }}
            </el-descriptions-item>
            <el-descriptions-item label="邮箱">
              {{ selectedAppointment?.studentEmail || '未知' }}
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
        
        <el-dialog v-model="evaluateDialogVisible" title="评价学生" width="500px">
          <el-form :model="evaluateForm" label-width="100px">
            <el-form-item label="学生姓名">
              <el-input :value="selectedAppointment?.studentName" disabled></el-input>
            </el-form-item>
            <el-form-item label="学科">
              <el-input :value="selectedAppointment?.subject" disabled></el-input>
            </el-form-item>
            <el-form-item label="教学质量">
              <el-rate v-model="evaluateForm.teachingQuality" :colors="['#99A9BF', '#F7BA2A', '#FF9900']"></el-rate>
            </el-form-item>
            <el-form-item label="服务态度">
              <el-rate v-model="evaluateForm.attitude" :colors="['#99A9BF', '#F7BA2A', '#FF9900']"></el-rate>
            </el-form-item>
            <el-form-item label="满意度">
              <el-rate v-model="evaluateForm.satisfaction" :colors="['#99A9BF', '#F7BA2A', '#FF9900']"></el-rate>
            </el-form-item>
            <el-form-item label="评价内容">
              <el-input v-model="evaluateForm.comment" type="textarea" rows="4" placeholder="请输入您的评价"></el-input>
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="evaluateDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitEvaluation">提交评价</el-button>
          </template>
        </el-dialog>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { appointmentApi, evaluationApi } from '../../api/api';
import { User, HomeFilled, EditPen, Calendar, Star, SwitchButton, Comment } from '@element-plus/icons-vue';

export default {
  name: 'TeacherAppointmentsView',
  components: {
    User,
    HomeFilled,
    EditPen,
    Calendar,
    Star,
    SwitchButton,
    Comment
  },
  data() {
    return {
      activeIndex: '4',
      pendingAppointments: [],
      acceptedAppointments: [],
      completedAppointments: [],
      rejectedAppointments: [],
      detailDialogVisible: false,
      evaluateDialogVisible: false,
      selectedAppointment: null,
      evaluateForm: {
        teachingQuality: 0,
        attitude: 0,
        satisfaction: 0,
        comment: ''
      },
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
          this.$router.push('/teacher/favorites');
          break;
        case '6':
          this.$router.push('/teacher/evaluations');
          break;
        case '7':
          this.logout();
          break;
      }
    },
    async loadAppointments() {
      try {
        const response = await appointmentApi.getList();
        const appointments = response || [];
        for (const app of appointments) {
          if (app.status === 'COMPLETED') {
            try {
              const checkResult = await evaluationApi.check(app.id);
              app.hasEvaluated = checkResult.hasEvaluated;
            } catch {
              app.hasEvaluated = false;
            }
          } else {
            app.hasEvaluated = false;
          }
        }
        this.pendingAppointments = appointments.filter(app => app.status === 'PENDING');
        this.acceptedAppointments = appointments.filter(app => app.status === 'ACCEPTED');
        this.completedAppointments = appointments.filter(app => app.status === 'COMPLETED');
        this.rejectedAppointments = appointments.filter(app => app.status === 'REJECTED');
      } catch (error) {
        console.error('加载预约列表失败:', error);
        this.$message.error('加载预约列表失败');
      }
    },
    async acceptAppointment(id) {
      try {
        await appointmentApi.update(id, { status: 'ACCEPTED' });
        this.$message.success('预约已接受');
        this.loadAppointments();
      } catch (error) {
        console.error('接受预约失败:', error);
        this.$message.error('接受预约失败');
      }
    },
    async rejectAppointment(id) {
      try {
        await appointmentApi.update(id, { status: 'REJECTED' });
        this.$message.success('预约已拒绝');
        this.loadAppointments();
      } catch (error) {
        console.error('拒绝预约失败:', error);
        this.$message.error('拒绝预约失败');
      }
    },
    async completeAppointment(id) {
      try {
        await appointmentApi.update(id, { status: 'COMPLETED' });
        this.$message.success('预约已完成');
        this.loadAppointments();
      } catch (error) {
        console.error('完成预约失败:', error);
        this.$message.error('完成预约失败');
      }
    },
    openDetailDialog(appointment) {
      this.selectedAppointment = appointment;
      this.detailDialogVisible = true;
    },
    openEvaluateDialog(appointment) {
      this.selectedAppointment = appointment;
      this.evaluateForm = {
        teachingQuality: 0,
        attitude: 0,
        satisfaction: 0,
        comment: ''
      };
      this.evaluateDialogVisible = true;
    },
    async submitEvaluation() {
      if (this.evaluateForm.teachingQuality === 0 || this.evaluateForm.attitude === 0 || this.evaluateForm.satisfaction === 0) {
        this.$message.warning('请完成所有打分项');
        return;
      }
      try {
        const response = await evaluationApi.create({
          appointmentId: this.selectedAppointment.id,
          evaluatedId: this.selectedAppointment.studentUserId,
          teachingQuality: this.evaluateForm.teachingQuality,
          attitude: this.evaluateForm.attitude,
          satisfaction: this.evaluateForm.satisfaction,
          comment: this.evaluateForm.comment
        });
        if (response.success) {
          this.$message.success('评价提交成功');
          this.evaluateDialogVisible = false;
          this.loadAppointments();
        } else {
          this.$message.error(response.error || '提交评价失败');
        }
      } catch (error) {
        console.error('提交评价失败:', error);
        this.$message.error('提交评价失败: ' + (error.response?.data?.error || error.message));
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
.teacher-appointments {
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

.sidebar-menu :deep(.el-menu-item) {
  color: white;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  color: white;
  background-color: #263445;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
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
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.appointments-container :deep(.el-tabs__item) {
  font-size: 15px;
  font-weight: 500;
}

.appointments-container :deep(.el-table) {
  font-size: 14px;
}

.appointments-container :deep(.el-table th) {
  background-color: #f5f7fa;
  color: #303133;
  font-weight: 600;
  font-size: 14px;
}

.appointments-container :deep(.el-table td) {
  color: #606266;
}
</style>
