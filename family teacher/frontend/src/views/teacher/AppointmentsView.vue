<template>
  <div class="teacher-appointments">
    <el-container style="height: 100vh;">
      <el-aside width="200px" class="sidebar">
        <div class="logo">
          <h2>家教平台</h2>
          <p>教师端</p>
        </div>
        <el-menu :default-active="activeIndex" class="sidebar-menu" @select="handleMenuSelect">
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
            <el-icon><ChatDotRound /></el-icon>
            <span>在线对话</span>
          </el-menu-item>
          <el-menu-item index="6">
            <el-icon><Star /></el-icon>
            <span>我的收藏</span>
          </el-menu-item>
          <el-menu-item index="7">
            <el-icon><Comment /></el-icon>
            <span>我的评价</span>
          </el-menu-item>
          <el-menu-item index="8">
            <el-icon><SwitchButton /></el-icon>
            <span>退出登录</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-main>
        <div class="appointments-container">
          <h2>试课 / 面试记录</h2>
          <el-tabs type="border-card">
            <el-tab-pane label="待处理">
              <el-table :data="pendingAppointments" style="width: 100%">
                <el-table-column prop="studentName" label="学生" />
                <el-table-column prop="subject" label="学科" />
                <el-table-column prop="requestedDate" label="日期" />
                <el-table-column prop="requestedTime" label="时间" />
                <el-table-column prop="location" label="地点" />
                <el-table-column prop="pricePerHour" label="试课价/小时" />
                <el-table-column label="状态" min-width="120">
                  <template #default="{ row }">
                    <el-tag class="status-tag" type="warning">{{ getStatusText(row.status) }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" min-width="220">
                  <template #default="{ row }">
                    <el-button type="info" size="small" @click="openDetailDialog(row)">详情</el-button>
                    <el-button
                      v-if="canAcceptAppointment(row)"
                      type="success"
                      size="small"
                      @click="acceptAppointment(row.id)"
                    >
                      接受
                    </el-button>
                    <el-button
                      v-if="canRejectAppointment(row)"
                      type="danger"
                      size="small"
                      @click="rejectAppointment(row.id)"
                    >
                      拒绝
                    </el-button>
                    <el-button
                      v-if="canCancelAppointment(row)"
                      type="danger"
                      size="small"
                      @click="cancelAppointment(row.id)"
                    >
                      取消
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>

            <el-tab-pane label="已接受">
              <el-table :data="acceptedAppointments" style="width: 100%">
                <el-table-column prop="studentName" label="学生" />
                <el-table-column prop="subject" label="学科" />
                <el-table-column prop="requestedDate" label="日期" />
                <el-table-column prop="requestedTime" label="时间" />
                <el-table-column prop="location" label="地点" />
                <el-table-column prop="pricePerHour" label="试课价/小时" />
                <el-table-column label="状态" min-width="120">
                  <template #default="{ row }">
                    <el-tag class="status-tag" type="success">{{ getStatusText(row.status) }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作">
                  <template #default="{ row }">
                    <el-button type="info" size="small" @click="openDetailDialog(row)">详情</el-button>
                    <el-button type="success" size="small" @click="completeAppointment(row.id)">完成试课</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>

            <el-tab-pane label="已完成试课">
              <el-table :data="completedAppointments" style="width: 100%">
                <el-table-column prop="studentName" label="学生" />
                <el-table-column prop="subject" label="学科" />
                <el-table-column prop="requestedDate" label="日期" />
                <el-table-column prop="requestedTime" label="时间" />
                <el-table-column prop="location" label="地点" />
                <el-table-column prop="pricePerHour" label="试课价/小时" />
                <el-table-column label="长期进度" min-width="220">
                  <template #default="{ row }">
                    <el-tag class="status-tag long-term-status" :type="getLongTermTagType(row)">
                      {{ getLongTermStatusText(row) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" min-width="360">
                  <template #default="{ row }">
                    <el-button type="info" size="small" @click="openDetailDialog(row)">详情</el-button>
                    <el-button
                      v-if="row.canEvaluate || row.hasEvaluated"
                      type="warning"
                      size="small"
                      :disabled="row.hasEvaluated"
                      @click="openEvaluateDialog(row)"
                    >
                      {{ row.hasEvaluated ? '已评价' : '评价学生' }}
                    </el-button>
                    <el-button
                      type="success"
                      size="small"
                      :disabled="!canConfirmLongTerm(row)"
                      @click="confirmLongTerm(row)"
                    >
                      {{ row.teacherConfirmedLongTerm ? '已确认长期合作' : '确认长期合作' }}
                    </el-button>
                    <el-button
                      v-if="canRejectLongTerm(row)"
                      type="danger"
                      size="small"
                      @click="rejectLongTerm(row)"
                    >
                      拒绝长期合作
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>

            <el-tab-pane label="长期授课">
              <el-table :data="longTermAppointments" style="width: 100%">
                <el-table-column prop="studentName" label="学生" />
                <el-table-column prop="subject" label="学科" />
                <el-table-column prop="requestedDate" label="试课日期" />
                <el-table-column prop="requestedTime" label="试课时间" />
                <el-table-column prop="location" label="地点" />
                <el-table-column label="合作状态" min-width="220">
                  <template #default="{ row }">
                    <el-tag class="status-tag long-term-status" :type="getLongTermTagType(row)">
                      {{ getLongTermStatusText(row) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" min-width="260">
                  <template #default="{ row }">
                    <el-button type="info" size="small" @click="openDetailDialog(row)">详情</el-button>
                    <el-button
                      v-if="row.status === 'LONG_TERM_CONFIRMED' && !row.teacherConfirmedLongTermCompletion"
                      type="primary"
                      size="small"
                      @click="completeLongTermAppointment(row.id)"
                    >
                      确认完成长期授课
                    </el-button>
                    <el-button
                      v-if="row.canEvaluate || row.hasEvaluated"
                      type="warning"
                      size="small"
                      :disabled="row.hasEvaluated"
                      @click="openEvaluateDialog(row)"
                    >
                      {{ row.hasEvaluated ? '已评价' : '评价学生' }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>

            <el-tab-pane label="已拒绝">
              <el-table :data="rejectedAppointments" style="width: 100%">
                <el-table-column prop="studentName" label="学生" />
                <el-table-column prop="subject" label="学科" />
                <el-table-column prop="requestedDate" label="日期" />
                <el-table-column prop="requestedTime" label="时间" />
                <el-table-column prop="location" label="地点" />
                <el-table-column prop="pricePerHour" label="试课价/小时" />
                <el-table-column label="状态" min-width="160">
                  <template #default="{ row }">
                    <el-tag class="status-tag" type="danger">{{ getStatusText(row.status) }}</el-tag>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </div>

        <el-dialog v-model="detailDialogVisible" title="试课详情" width="760px">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="记录 ID" :span="2">
              {{ selectedAppointment?.id || '未知' }}
            </el-descriptions-item>
            <el-descriptions-item label="学生" :span="2">
              {{ selectedAppointment?.studentName || '未知' }}
            </el-descriptions-item>
            <el-descriptions-item label="学校">
              {{ selectedAppointment?.studentSchool || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="年级">
              {{ selectedAppointment?.studentGrade || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="电话">
              {{ selectedAppointment?.studentPhone || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="邮箱">
              {{ selectedAppointment?.studentEmail || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="学科">
              {{ selectedAppointment?.subject || '未知' }}
            </el-descriptions-item>
            <el-descriptions-item label="类型">
              {{ selectedAppointment?.appointmentType || 'TRIAL_INTERVIEW' }}
            </el-descriptions-item>
            <el-descriptions-item label="日期">
              {{ selectedAppointment?.requestedDate || '未知' }}
            </el-descriptions-item>
            <el-descriptions-item label="时间">
              {{ selectedAppointment?.requestedTime || '未知' }}
            </el-descriptions-item>
            <el-descriptions-item label="地点" :span="2">
              {{ selectedAppointment?.location || '未知' }}
            </el-descriptions-item>
            <el-descriptions-item label="试课价/小时">
              {{ selectedAppointment?.pricePerHour || 0 }}
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag class="status-tag" :type="getStatusType(selectedAppointment?.status)">
                {{ getStatusText(selectedAppointment?.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="长期合作进度" :span="2">
              {{ getLongTermStatusText(selectedAppointment) }}
            </el-descriptions-item>
            <el-descriptions-item label="备注" :span="2">
              {{ selectedAppointment?.notes || '无' }}
            </el-descriptions-item>
          </el-descriptions>

          <template #footer>
            <el-button
              v-if="selectedAppointment?.studentUserId"
              type="primary"
              plain
              @click="goToChat"
            >
              立即聊天
            </el-button>
            <el-button @click="detailDialogVisible = false">关闭</el-button>
          </template>
        </el-dialog>

        <el-dialog v-model="evaluateDialogVisible" title="评价学生" width="500px">
          <el-form :model="evaluateForm" label-width="100px">
            <el-form-item label="学生">
              <el-input :value="selectedAppointment?.studentName" disabled />
            </el-form-item>
            <el-form-item label="学科">
              <el-input :value="selectedAppointment?.subject" disabled />
            </el-form-item>
            <el-form-item label="教学配合">
              <el-rate v-model="evaluateForm.teachingQuality" :colors="['#99A9BF', '#F7BA2A', '#FF9900']" />
            </el-form-item>
            <el-form-item label="沟通态度">
              <el-rate v-model="evaluateForm.attitude" :colors="['#99A9BF', '#F7BA2A', '#FF9900']" />
            </el-form-item>
            <el-form-item label="整体满意">
              <el-rate v-model="evaluateForm.satisfaction" :colors="['#99A9BF', '#F7BA2A', '#FF9900']" />
            </el-form-item>
            <el-form-item label="评价内容">
              <el-input v-model="evaluateForm.comment" type="textarea" rows="4" placeholder="请输入评价" />
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
import { User, HomeFilled, EditPen, Calendar, Star, SwitchButton, Comment, ChatDotRound } from '@element-plus/icons-vue';

export default {
  name: 'TeacherAppointmentsView',
  components: {
    User,
    HomeFilled,
    EditPen,
    Calendar,
    ChatDotRound,
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
      longTermAppointments: [],
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
    };
  },
  mounted() {
    this.loadAppointments();
    this.startAutoRefresh();
  },
  beforeUnmount() {
    this.stopAutoRefresh();
  },
  methods: {
    assertApiSuccess(response) {
      if (response && (response.success === false || response.error)) {
        throw new Error(response.error || '操作失败');
      }
      return response;
    },
    handleMenuSelect(index) {
      switch (index) {
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
          this.$router.push('/teacher/chat');
          break;
        case '6':
          this.$router.push('/teacher/favorites');
          break;
        case '7':
          this.$router.push('/teacher/evaluations');
          break;
        case '8':
          this.logout();
          break;
      }
    },
    async loadAppointments() {
      try {
        const response = await appointmentApi.getList();
        const appointments = response || [];

        for (const appointment of appointments) {
          if (['COMPLETED', 'LONG_TERM_CONFIRMED', 'LONG_TERM_COMPLETED'].includes(appointment.status)) {
            try {
              const checkResult = await evaluationApi.check(appointment.id);
              appointment.hasEvaluated = checkResult.hasEvaluated;
              appointment.canEvaluate = checkResult.canEvaluate;
            } catch {
              appointment.hasEvaluated = false;
              appointment.canEvaluate = false;
            }
          } else {
            appointment.hasEvaluated = false;
            appointment.canEvaluate = false;
          }
        }

        this.pendingAppointments = appointments.filter(item => item.status === 'PENDING');
        this.acceptedAppointments = appointments.filter(item => item.status === 'ACCEPTED');
        this.completedAppointments = appointments.filter(item => item.status === 'COMPLETED');
        this.longTermAppointments = appointments.filter(item => ['LONG_TERM_CONFIRMED', 'LONG_TERM_COMPLETED'].includes(item.status));
        this.rejectedAppointments = appointments.filter(item => ['REJECTED', 'LONG_TERM_REJECTED'].includes(item.status));
      } catch (error) {
        console.error('加载预约列表失败:', error);
        this.$message.error('加载预约列表失败');
      }
    },
    async acceptAppointment(id) {
      try {
        this.assertApiSuccess(await appointmentApi.update(id, { status: 'ACCEPTED' }));
        this.$message.success('已接受试课申请');
        this.loadAppointments();
      } catch (error) {
        console.error('接受试课失败:', error);
        this.$message.error('接受试课失败，请稍后重试');
      }
    },
    async rejectAppointment(id) {
      try {
        this.assertApiSuccess(await appointmentApi.update(id, { status: 'REJECTED' }));
        this.$message.success('已拒绝试课申请');
        this.loadAppointments();
      } catch (error) {
        console.error('拒绝试课失败:', error);
        this.$message.error('拒绝试课失败，请稍后重试');
      }
    },
    async cancelAppointment(id) {
      try {
        this.assertApiSuccess(await appointmentApi.delete(id));
        this.$message.success('试课申请已取消');
        this.loadAppointments();
      } catch (error) {
        console.error('取消试课失败:', error);
        this.$message.error('取消试课失败，请稍后重试');
      }
    },
    async completeAppointment(id) {
      try {
        await appointmentApi.update(id, { status: 'COMPLETED' });
        this.$message.success('试课已标记完成');
        this.loadAppointments();
      } catch (error) {
        console.error('完成试课失败:', error);
        this.$message.error('完成试课失败');
      }
    },
    openDetailDialog(appointment) {
      this.selectedAppointment = appointment;
      this.detailDialogVisible = true;
    },
    goToChat() {
      if (!this.selectedAppointment?.studentUserId) {
        this.$message.warning('未找到可聊天的学生');
        return;
      }
      this.detailDialogVisible = false;
      this.$router.push({
        path: '/teacher/chat',
        query: {
          userId: this.selectedAppointment.studentUserId,
          name: this.selectedAppointment.studentName || ''
        }
      });
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
    canConfirmLongTerm(appointment) {
      return appointment?.status === 'COMPLETED' && !appointment?.teacherConfirmedLongTerm;
    },
    canAcceptAppointment(appointment) {
      return appointment?.status === 'PENDING' && appointment?.initiatorRole === 'STUDENT';
    },
    canRejectAppointment(appointment) {
      return appointment?.status === 'PENDING' && appointment?.initiatorRole === 'STUDENT';
    },
    canCancelAppointment(appointment) {
      return appointment?.status === 'PENDING' && appointment?.initiatorRole === 'TEACHER';
    },
    canRejectLongTerm(appointment) {
      return appointment?.status === 'COMPLETED';
    },
    getLongTermStatusText(appointment) {
      if (!appointment) {
        return '待确认';
      }
      if (appointment.status === 'LONG_TERM_REJECTED') {
        return '已拒绝长期合作';
      }
      if (appointment.status === 'LONG_TERM_COMPLETED') {
        return appointment.hasEvaluated ? '长期授课已完成，已评价' : '长期授课已完成，待评价';
      }
      if (appointment.status === 'LONG_TERM_CONFIRMED') {
        if (appointment.studentConfirmedLongTermCompletion && appointment.teacherConfirmedLongTermCompletion) {
          return '双方已确认完成长期授课';
        }
        if (appointment.teacherConfirmedLongTermCompletion) {
          return '你已确认完成，等待学生确认';
        }
        if (appointment.studentConfirmedLongTermCompletion) {
          return '学生已确认完成，等待你确认';
        }
        return '长期授课进行中';
      }
      if (appointment.studentConfirmedLongTerm && appointment.teacherConfirmedLongTerm) {
        return '双方已确认长期合作';
      }
      if (appointment.teacherConfirmedLongTerm) {
        return '你已确认，等待学生确认';
      }
      if (appointment.studentConfirmedLongTerm) {
        return '学生已确认，等待你确认';
      }
      return '试课已结束，待双方确认';
    },
    getLongTermTagType(appointment) {
      if (appointment?.status === 'LONG_TERM_REJECTED') {
        return 'danger';
      }
      if (appointment?.status === 'LONG_TERM_COMPLETED') {
        return appointment?.hasEvaluated ? 'success' : 'warning';
      }
      if (appointment?.status === 'LONG_TERM_CONFIRMED') {
        return appointment?.studentConfirmedLongTermCompletion || appointment?.teacherConfirmedLongTermCompletion ? 'warning' : 'success';
      }
      if (appointment?.studentConfirmedLongTerm || appointment?.teacherConfirmedLongTerm) {
        return 'warning';
      }
      return 'info';
    },
    async confirmLongTerm(appointment) {
      try {
        const response = this.assertApiSuccess(await appointmentApi.confirmLongTerm(appointment.id));
        this.$message.success('已确认长期合作');
        this.loadAppointments();
      } catch (error) {
        console.error('确认长期合作失败:', error);
        this.$message.error('确认长期合作失败，请稍后重试');
      }
    },
    async completeLongTermAppointment(id) {
      try {
        const response = this.assertApiSuccess(await appointmentApi.update(id, { status: 'LONG_TERM_COMPLETED' }));
        this.$message.success('已确认完成长期授课');
        this.loadAppointments();
      } catch (error) {
        console.error('完成长期授课失败:', error);
        this.$message.error('完成长期授课失败，请稍后重试');
      }
    },
    async rejectLongTerm(appointment) {
      try {
        const response = this.assertApiSuccess(await appointmentApi.update(appointment.id, { status: 'LONG_TERM_REJECTED' }));
        this.$message.success('已拒绝长期合作');
        this.loadAppointments();
      } catch (error) {
        console.error('拒绝长期合作失败:', error);
        this.$message.error('拒绝长期合作失败，请稍后重试');
      }
    },
    async submitEvaluation() {
      if (this.evaluateForm.teachingQuality === 0 || this.evaluateForm.attitude === 0 || this.evaluateForm.satisfaction === 0) {
        this.$message.warning('请完成所有评分项');
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
          this.$message.error('提交评价失败');
        }
      } catch (error) {
        console.error('提交评价失败:', error);
        this.$message.error('提交评价失败，请稍后重试');
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
      switch (status) {
        case 'PENDING':
          return 'warning';
        case 'ACCEPTED':
          return 'success';
        case 'COMPLETED':
          return 'info';
        case 'LONG_TERM_CONFIRMED':
          return 'success';
        case 'LONG_TERM_COMPLETED':
          return 'warning';
        case 'LONG_TERM_REJECTED':
        case 'REJECTED':
          return 'danger';
        default:
          return '';
      }
    },
    getStatusText(status) {
      switch (status) {
        case 'PENDING':
          return '待处理';
        case 'ACCEPTED':
          return '已接受';
        case 'COMPLETED':
          return '试课已完成';
        case 'REJECTED':
          return '试课已拒绝';
        case 'LONG_TERM_CONFIRMED':
          return '已确认长期合作';
        case 'LONG_TERM_COMPLETED':
          return '长期授课已完成';
        case 'LONG_TERM_REJECTED':
          return '已拒绝长期合作';
        default:
          return status || '未知';
      }
    },
    logout() {
      localStorage.removeItem('token');
      localStorage.removeItem('username');
      localStorage.removeItem('role');
      this.$router.push('/login');
    }
  }
};
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
  color: #409eff;
  font-size: 20px;
}

.logo p {
  margin: 5px 0 0;
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
  color: #409eff;
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

:deep(.status-tag.el-tag) {
  height: auto;
  min-height: 24px;
  padding: 4px 8px;
  line-height: 1.4;
  white-space: normal;
}

:deep(.long-term-status.el-tag) {
  min-width: 160px;
  justify-content: center;
}
</style>
