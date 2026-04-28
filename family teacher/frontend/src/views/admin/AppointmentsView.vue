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

          <div class="toolbar">
            <el-input
              v-model="searchQuery"
              placeholder="请输入预约ID、学生、老师或学科"
              clearable
              class="search-input"
            >
              <template #append>
                <el-button @click="loadAppointments">搜索</el-button>
              </template>
            </el-input>
            <el-select
              v-model="statusFilter"
              placeholder="按状态筛选"
              clearable
              class="status-select"
            >
              <el-option label="全部" value="" />
              <el-option label="待处理" value="PENDING" />
              <el-option label="已接受" value="ACCEPTED" />
              <el-option label="试课已完成" value="COMPLETED" />
              <el-option label="试课已拒绝" value="REJECTED" />
              <el-option label="已确认长期合作" value="LONG_TERM_CONFIRMED" />
              <el-option label="长期授课已完成" value="LONG_TERM_COMPLETED" />
              <el-option label="已拒绝长期合作" value="LONG_TERM_REJECTED" />
            </el-select>
          </div>

          <el-table :data="pagedAppointments" style="width: 100%" empty-text="暂无预约数据">
            <el-table-column prop="id" label="预约ID" width="90" />
            <el-table-column prop="studentName" label="学生" min-width="120" />
            <el-table-column prop="teacherName" label="老师" min-width="120" />
            <el-table-column prop="subject" label="学科" min-width="100" />
            <el-table-column prop="requestedDate" label="预约日期" min-width="110" />
            <el-table-column prop="requestedTime" label="预约时间" min-width="110" />
            <el-table-column prop="location" label="地点" min-width="160" show-overflow-tooltip />
            <el-table-column prop="pricePerHour" label="价格/小时" width="110">
              <template #default="scope">
                ¥{{ scope.row.pricePerHour || 0 }}
              </template>
            </el-table-column>
            <el-table-column label="状态" min-width="160">
              <template #default="scope">
                <el-tag class="status-tag" :type="getStatusType(scope.row.status)">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="处理备注" min-width="180" show-overflow-tooltip>
              <template #default="scope">
                {{ scope.row.notes || '无' }}
              </template>
            </el-table-column>
            <el-table-column label="更新时间" min-width="170">
              <template #default="scope">
                {{ formatDateTime(scope.row.updatedAt) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" min-width="220" fixed="right">
              <template #default="scope">
                <el-button type="primary" size="small" @click="openDetailDialog(scope.row)">详情</el-button>
                <el-button
                  type="danger"
                  size="small"
                  :disabled="!canForceClose(scope.row)"
                  @click="forceCloseAppointment(scope.row)"
                >
                  强制关闭
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-if="filteredAppointments.length > 0"
            :current-page="currentPage"
            :page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="filteredAppointments.length"
            layout="total, sizes, prev, pager, next, jumper"
            class="pagination"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>

        <el-dialog v-model="detailDialogVisible" title="预约详情" width="860px">
          <template v-if="selectedAppointment">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="预约ID" :span="2">
                {{ selectedAppointment.id }}
              </el-descriptions-item>
              <el-descriptions-item label="状态">
                <el-tag class="status-tag" :type="getStatusType(selectedAppointment.status)">
                  {{ getStatusText(selectedAppointment.status) }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="长期合作进度">
                {{ getLongTermStatusText(selectedAppointment) }}
              </el-descriptions-item>
              <el-descriptions-item label="学科">
                {{ selectedAppointment.subject || '未填写' }}
              </el-descriptions-item>
              <el-descriptions-item label="类型">
                {{ selectedAppointment.appointmentType || 'TRIAL_INTERVIEW' }}
              </el-descriptions-item>
              <el-descriptions-item label="预约日期">
                {{ selectedAppointment.requestedDate || '未填写' }}
              </el-descriptions-item>
              <el-descriptions-item label="预约时间">
                {{ selectedAppointment.requestedTime || '未填写' }}
              </el-descriptions-item>
              <el-descriptions-item label="地点" :span="2">
                {{ selectedAppointment.location || '未填写' }}
              </el-descriptions-item>
              <el-descriptions-item label="价格/小时">
                ¥{{ selectedAppointment.pricePerHour || 0 }}
              </el-descriptions-item>
              <el-descriptions-item label="创建时间">
                {{ formatDateTime(selectedAppointment.createdAt) }}
              </el-descriptions-item>
              <el-descriptions-item label="更新时间">
                {{ formatDateTime(selectedAppointment.updatedAt) }}
              </el-descriptions-item>
              <el-descriptions-item label="处理备注" :span="2">
                <div class="detail-text">{{ selectedAppointment.notes || '无' }}</div>
              </el-descriptions-item>
            </el-descriptions>

            <el-divider content-position="left">学生信息</el-divider>

            <el-descriptions :column="2" border>
              <el-descriptions-item label="学生姓名">
                {{ selectedAppointment.studentName || '未填写' }}
              </el-descriptions-item>
              <el-descriptions-item label="学校">
                {{ selectedAppointment.studentSchool || '未填写' }}
              </el-descriptions-item>
              <el-descriptions-item label="年级">
                {{ selectedAppointment.studentGrade || '未填写' }}
              </el-descriptions-item>
              <el-descriptions-item label="专业">
                {{ selectedAppointment.studentMajor || '未填写' }}
              </el-descriptions-item>
              <el-descriptions-item label="电话">
                {{ selectedAppointment.studentPhone || '未填写' }}
              </el-descriptions-item>
              <el-descriptions-item label="邮箱">
                {{ selectedAppointment.studentEmail || '未填写' }}
              </el-descriptions-item>
              <el-descriptions-item label="地址" :span="2">
                {{ selectedAppointment.studentAddress || '未填写' }}
              </el-descriptions-item>
            </el-descriptions>

            <el-divider content-position="left">老师信息</el-divider>

            <el-descriptions :column="2" border>
              <el-descriptions-item label="老师姓名">
                {{ selectedAppointment.teacherName || '未填写' }}
              </el-descriptions-item>
              <el-descriptions-item label="学校">
                {{ selectedAppointment.teacherSchool || '未填写' }}
              </el-descriptions-item>
              <el-descriptions-item label="专业">
                {{ selectedAppointment.teacherMajor || '未填写' }}
              </el-descriptions-item>
              <el-descriptions-item label="电话">
                {{ selectedAppointment.teacherPhone || '未填写' }}
              </el-descriptions-item>
              <el-descriptions-item label="邮箱" :span="2">
                {{ selectedAppointment.teacherEmail || '未填写' }}
              </el-descriptions-item>
            </el-descriptions>

            <el-divider content-position="left">管理员处理</el-divider>

            <el-form :model="processingForm" label-width="100px">
              <el-form-item label="处理状态">
                <el-select v-model="processingForm.status" style="width: 100%">
                  <el-option label="待处理" value="PENDING" />
                  <el-option label="已接受" value="ACCEPTED" />
                  <el-option label="试课已完成" value="COMPLETED" />
                  <el-option label="试课已拒绝" value="REJECTED" />
                  <el-option label="已确认长期合作" value="LONG_TERM_CONFIRMED" />
                  <el-option label="长期授课已完成" value="LONG_TERM_COMPLETED" />
                  <el-option label="已拒绝长期合作" value="LONG_TERM_REJECTED" />
                </el-select>
              </el-form-item>
              <el-form-item label="处理备注">
                <el-input
                  v-model="processingForm.notes"
                  type="textarea"
                  rows="4"
                  placeholder="请输入管理员处理说明"
                />
              </el-form-item>
            </el-form>
          </template>

          <template #footer>
            <el-button @click="detailDialogVisible = false">取消</el-button>
            <el-button
              type="danger"
              :disabled="!selectedAppointment || !canForceClose(selectedAppointment)"
              @click="forceCloseAppointment(selectedAppointment, true)"
            >
              强制关闭
            </el-button>
            <el-button type="primary" @click="saveAppointmentProcessing">保存处理</el-button>
          </template>
        </el-dialog>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { adminApi } from '../../api/api';
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
      appointmentsList: [],
      currentPage: 1,
      pageSize: 10,
      detailDialogVisible: false,
      selectedAppointment: null,
      processingForm: {
        status: '',
        notes: ''
      }
    };
  },
  computed: {
    filteredAppointments() {
      const keyword = this.searchQuery.trim().toLowerCase();
      return this.appointmentsList.filter((appointment) => {
        const matchesKeyword = !keyword || [
          String(appointment.id || ''),
          appointment.studentName,
          appointment.teacherName,
          appointment.subject
        ].some((value) => (value || '').toLowerCase().includes(keyword));

        const matchesStatus = !this.statusFilter || appointment.status === this.statusFilter;
        return matchesKeyword && matchesStatus;
      });
    },
    pagedAppointments() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.filteredAppointments.slice(start, end);
    }
  },
  mounted() {
    this.loadAppointments();
  },
  methods: {
    handleMenuSelect(index) {
      switch (index) {
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
    async loadAppointments() {
      try {
        const appointments = await adminApi.getAppointments();
        this.appointmentsList = appointments || [];
        this.currentPage = 1;
      } catch (error) {
        console.error('加载预约列表失败:', error);
        this.$message.error('加载预约列表失败');
      }
    },
    async openDetailDialog(appointment) {
      try {
        const detail = await adminApi.getAppointmentById(appointment.id);
        if (detail?.success === false) {
          throw new Error(detail.error || '加载预约详情失败');
        }
        this.selectedAppointment = detail;
        this.processingForm = {
          status: detail.status || '',
          notes: detail.notes || ''
        };
        this.detailDialogVisible = true;
      } catch (error) {
        console.error('加载预约详情失败:', error);
        this.$message.error('加载预约详情失败');
      }
    },
    async saveAppointmentProcessing() {
      if (!this.selectedAppointment) {
        return;
      }
      try {
        const response = await adminApi.updateAppointment(this.selectedAppointment.id, {
          status: this.processingForm.status,
          notes: this.processingForm.notes
        });
        if (response?.success === false) {
          throw new Error(response.error || '保存处理失败');
        }
        this.$message.success(response.message || '保存处理成功');
        await this.loadAppointments();
        await this.openDetailDialog({ id: this.selectedAppointment.id });
      } catch (error) {
        console.error('保存处理失败:', error);
        this.$message.error('保存处理失败: ' + (error.response?.data?.error || error.message));
      }
    },
    canForceClose(appointment) {
      return ['PENDING', 'ACCEPTED', 'COMPLETED', 'LONG_TERM_CONFIRMED'].includes(appointment?.status);
    },
    async forceCloseAppointment(appointment, keepDialogOpen = false) {
      const targetStatus = ['PENDING', 'ACCEPTED'].includes(appointment.status) ? 'REJECTED' : 'LONG_TERM_REJECTED';
      const notes = this.processingForm.notes || appointment.notes || '管理员强制关闭预约';

      try {
        const response = await adminApi.updateAppointment(appointment.id, {
          status: targetStatus,
          notes
        });
        if (response?.success === false) {
          throw new Error(response.error || '强制关闭失败');
        }
        this.$message.success('预约已强制关闭');
        await this.loadAppointments();
        if (keepDialogOpen) {
          await this.openDetailDialog({ id: appointment.id });
        } else if (this.selectedAppointment?.id === appointment.id) {
          this.detailDialogVisible = false;
        }
      } catch (error) {
        console.error('强制关闭失败:', error);
        this.$message.error('强制关闭失败: ' + (error.response?.data?.error || error.message));
      }
    },
    handleSizeChange(size) {
      this.pageSize = size;
      this.currentPage = 1;
    },
    handleCurrentChange(current) {
      this.currentPage = current;
    },
    getStatusType(status) {
      switch (status) {
        case 'PENDING':
          return 'warning';
        case 'ACCEPTED':
        case 'LONG_TERM_CONFIRMED':
          return 'success';
        case 'COMPLETED':
          return 'info';
        case 'LONG_TERM_COMPLETED':
          return 'warning';
        case 'REJECTED':
        case 'LONG_TERM_REJECTED':
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
    getLongTermStatusText(appointment) {
      if (!appointment) {
        return '待确认';
      }
      if (appointment.status === 'LONG_TERM_REJECTED') {
        return '已拒绝长期合作';
      }
      if (appointment.status === 'LONG_TERM_COMPLETED') {
        return '长期授课已完成';
      }
      if (appointment.status === 'LONG_TERM_CONFIRMED') {
        if (appointment.studentConfirmedLongTermCompletion && appointment.teacherConfirmedLongTermCompletion) {
          return '双方已确认完成长期授课';
        }
        if (appointment.studentConfirmedLongTermCompletion) {
          return '学生已确认完成，等待老师确认';
        }
        if (appointment.teacherConfirmedLongTermCompletion) {
          return '老师已确认完成，等待学生确认';
        }
        return '长期授课进行中';
      }
      if (appointment.studentConfirmedLongTerm && appointment.teacherConfirmedLongTerm) {
        return '双方已确认长期合作';
      }
      if (appointment.studentConfirmedLongTerm) {
        return '学生已确认，等待老师确认';
      }
      if (appointment.teacherConfirmedLongTerm) {
        return '老师已确认，等待学生确认';
      }
      return '试课结束，待双方确认';
    },
    formatDateTime(value) {
      if (!value) {
        return '-';
      }
      const date = new Date(value);
      if (Number.isNaN(date.getTime())) {
        return value;
      }
      return date.toLocaleString('zh-CN', { hour12: false });
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
  color: #409eff;
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
  margin-bottom: 24px;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.search-input {
  width: 360px;
  max-width: 100%;
}

.status-select {
  width: 220px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.detail-text {
  line-height: 1.6;
  white-space: pre-wrap;
}

:deep(.status-tag.el-tag) {
  height: auto;
  min-height: 24px;
  padding: 4px 8px;
  line-height: 1.4;
  white-space: normal;
}
</style>
