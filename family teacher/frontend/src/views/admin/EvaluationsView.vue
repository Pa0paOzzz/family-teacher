<template>
  <div class="admin-evaluations">
    <el-container style="height: 100vh;">
      <el-aside width="200px" class="sidebar">
        <div class="logo">
          <h2>家教平台</h2>
          <p>管理员端</p>
        </div>
        <el-menu :default-active="activeIndex" class="sidebar-menu" @select="handleMenuSelect">
          <el-menu-item index="1">
            <el-icon><DataLine /></el-icon>
            <span>仪表盘</span>
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
            <el-icon><Document /></el-icon>
            <span>求职与需求</span>
          </el-menu-item>
          <el-menu-item index="5">
            <el-icon><Comment /></el-icon>
            <span>评价管理</span>
          </el-menu-item>
          <el-menu-item index="6">
            <el-icon><SwitchButton /></el-icon>
            <span>退出登录</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-main>
        <div class="evaluations-container">
          <div class="page-header">
            <h2>评价管理</h2>
            <el-button :icon="Refresh" :loading="loading" @click="loadEvaluations">刷新</el-button>
          </div>

          <div class="summary-grid">
            <div class="summary-item">
              <span class="summary-label">评价总数</span>
              <strong>{{ evaluations.length }}</strong>
            </div>
            <div class="summary-item">
              <span class="summary-label">平均评分</span>
              <strong>{{ overallAverageScore }}</strong>
            </div>
            <div class="summary-item">
              <span class="summary-label">学生评老师</span>
              <strong>{{ studentToTeacherCount }}</strong>
            </div>
            <div class="summary-item">
              <span class="summary-label">老师评学生</span>
              <strong>{{ teacherToStudentCount }}</strong>
            </div>
          </div>

          <div class="filter-bar">
            <el-input
              v-model="searchQuery"
              clearable
              placeholder="评价人、被评价人、学科或评价内容"
              class="search-input"
              @clear="resetPage"
              @keyup.enter="resetPage"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select v-model="directionFilter" clearable placeholder="评价方向" class="filter-select" @change="resetPage">
              <el-option label="学生评价老师" value="STUDENT_TO_TEACHER" />
              <el-option label="老师评价学生" value="TEACHER_TO_STUDENT" />
            </el-select>
            <el-select v-model="scoreFilter" clearable placeholder="评分区间" class="filter-select" @change="resetPage">
              <el-option label="高分 4-5" value="HIGH" />
              <el-option label="中等 3" value="MID" />
              <el-option label="低分 1-2" value="LOW" />
            </el-select>
          </div>

          <el-table v-loading="loading" :data="pagedEvaluations" style="width: 100%" empty-text="暂无评价数据">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column label="评价方向" width="130">
              <template #default="{ row }">
                <el-tag :type="getDirectionTagType(row)">{{ getDirectionText(row) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="评价人" min-width="140">
              <template #default="{ row }">
                <div class="user-cell">
                  <span>{{ row.evaluatorName || '未知用户' }}</span>
                  <el-tag size="small" :type="getRoleTagType(row.evaluatorRole)">
                    {{ formatRole(row.evaluatorRole) }}
                  </el-tag>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="被评价人" min-width="140">
              <template #default="{ row }">
                <div class="user-cell">
                  <span>{{ row.evaluatedName || '未知用户' }}</span>
                  <el-tag size="small" :type="getRoleTagType(row.evaluatedRole)">
                    {{ formatRole(row.evaluatedRole) }}
                  </el-tag>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="subject" label="学科" min-width="100">
              <template #default="{ row }">{{ row.subject || '未填写' }}</template>
            </el-table-column>
            <el-table-column label="平均评分" width="150">
              <template #default="{ row }">
                <div class="score-cell">
                  <el-rate :model-value="getAverageScore(row)" disabled />
                  <span>{{ getAverageScore(row) }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="comment" label="评价内容" min-width="260" show-overflow-tooltip>
              <template #default="{ row }">{{ row.comment || '暂无评价内容' }}</template>
            </el-table-column>
            <el-table-column label="评价时间" min-width="170">
              <template #default="{ row }">{{ formatDateTime(row.createdAt) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="130" fixed="right">
              <template #default="{ row }">
                <div class="table-actions">
                  <el-tooltip content="查看详情" placement="top">
                    <el-button :icon="View" size="small" @click="openDetailDialog(row)" />
                  </el-tooltip>
                  <el-tooltip content="删除评价" placement="top">
                    <el-button type="danger" :icon="Delete" size="small" @click="confirmDeleteEvaluation(row)" />
                  </el-tooltip>
                </div>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-row">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50, 100]"
              :total="filteredEvaluations.length"
              layout="total, sizes, prev, pager, next, jumper"
            />
          </div>
        </div>
      </el-main>
    </el-container>

    <el-dialog v-model="detailDialogVisible" title="评价详情" width="720px">
      <el-descriptions v-if="selectedEvaluation" :column="2" border>
        <el-descriptions-item label="评价ID">{{ selectedEvaluation.id }}</el-descriptions-item>
        <el-descriptions-item label="预约ID">{{ selectedEvaluation.appointmentId || '未知' }}</el-descriptions-item>
        <el-descriptions-item label="评价方向">{{ getDirectionText(selectedEvaluation) }}</el-descriptions-item>
        <el-descriptions-item label="学科">{{ selectedEvaluation.subject || '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="评价人">
          {{ selectedEvaluation.evaluatorName || '未知用户' }}（{{ formatRole(selectedEvaluation.evaluatorRole) }}）
        </el-descriptions-item>
        <el-descriptions-item label="被评价人">
          {{ selectedEvaluation.evaluatedName || '未知用户' }}（{{ formatRole(selectedEvaluation.evaluatedRole) }}）
        </el-descriptions-item>
        <el-descriptions-item label="教学质量">
          <el-rate :model-value="selectedEvaluation.teachingQuality || 0" disabled />
        </el-descriptions-item>
        <el-descriptions-item label="服务态度">
          <el-rate :model-value="selectedEvaluation.attitude || 0" disabled />
        </el-descriptions-item>
        <el-descriptions-item label="满意度">
          <el-rate :model-value="selectedEvaluation.satisfaction || 0" disabled />
        </el-descriptions-item>
        <el-descriptions-item label="平均评分">{{ getAverageScore(selectedEvaluation) }}</el-descriptions-item>
        <el-descriptions-item label="上课地点" :span="2">
          {{ selectedEvaluation.location || '未填写' }}
        </el-descriptions-item>
        <el-descriptions-item label="预约时间" :span="2">
          {{ formatAppointmentTime(selectedEvaluation) }}
        </el-descriptions-item>
        <el-descriptions-item label="评价时间" :span="2">
          {{ formatDateTime(selectedEvaluation.createdAt) }}
        </el-descriptions-item>
        <el-descriptions-item label="评价内容" :span="2">
          <div class="detail-comment">{{ selectedEvaluation.comment || '暂无评价内容' }}</div>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button type="danger" :icon="Delete" @click="confirmDeleteEvaluation(selectedEvaluation)">删除评价</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { adminApi } from '../../api/api';
import {
  Calendar,
  Comment,
  DataLine,
  Delete,
  Document,
  Refresh,
  Search,
  SwitchButton,
  User,
  View
} from '@element-plus/icons-vue';

export default {
  name: 'AdminEvaluationsView',
  components: {
    Calendar,
    Comment,
    DataLine,
    Document,
    Search,
    SwitchButton,
    User
  },
  data() {
    return {
      activeIndex: '5',
      Delete,
      Refresh,
      View,
      loading: false,
      searchQuery: '',
      directionFilter: '',
      scoreFilter: '',
      evaluations: [],
      currentPage: 1,
      pageSize: 10,
      detailDialogVisible: false,
      selectedEvaluation: null
    };
  },
  computed: {
    filteredEvaluations() {
      const keyword = this.searchQuery.trim().toLowerCase();
      return this.evaluations.filter(evaluation => {
        const matchesKeyword = !keyword || [
          evaluation.evaluatorName,
          evaluation.evaluatedName,
          evaluation.subject,
          evaluation.comment
        ].some(value => String(value || '').toLowerCase().includes(keyword));
        const matchesDirection = !this.directionFilter || this.getDirection(evaluation) === this.directionFilter;
        const matchesScore = !this.scoreFilter || this.getScoreLevel(evaluation) === this.scoreFilter;
        return matchesKeyword && matchesDirection && matchesScore;
      });
    },
    pagedEvaluations() {
      const start = (this.currentPage - 1) * this.pageSize;
      return this.filteredEvaluations.slice(start, start + this.pageSize);
    },
    overallAverageScore() {
      if (this.evaluations.length === 0) {
        return '0.0';
      }
      const total = this.evaluations.reduce((sum, evaluation) => sum + this.getAverageScore(evaluation), 0);
      return (total / this.evaluations.length).toFixed(1);
    },
    studentToTeacherCount() {
      return this.evaluations.filter(evaluation => this.getDirection(evaluation) === 'STUDENT_TO_TEACHER').length;
    },
    teacherToStudentCount() {
      return this.evaluations.filter(evaluation => this.getDirection(evaluation) === 'TEACHER_TO_STUDENT').length;
    }
  },
  watch: {
    filteredEvaluations() {
      const maxPage = Math.max(1, Math.ceil(this.filteredEvaluations.length / this.pageSize));
      if (this.currentPage > maxPage) {
        this.currentPage = maxPage;
      }
    }
  },
  mounted() {
    this.loadEvaluations();
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
          this.$router.push('/admin/resources');
          break;
        case '5':
          this.$router.push('/admin/evaluations');
          break;
        case '6':
          this.logout();
          break;
      }
    },
    async loadEvaluations() {
      this.loading = true;
      try {
        const evaluations = await adminApi.getEvaluations();
        this.evaluations = evaluations || [];
      } catch (error) {
        console.error('加载评价列表失败:', error);
        this.$message.error('加载评价列表失败');
      } finally {
        this.loading = false;
      }
    },
    resetPage() {
      this.currentPage = 1;
    },
    openDetailDialog(evaluation) {
      this.selectedEvaluation = evaluation;
      this.detailDialogVisible = true;
    },
    async confirmDeleteEvaluation(evaluation) {
      if (!evaluation) {
        return;
      }
      try {
        await this.$confirm(`确认删除评价 #${evaluation.id} 吗？`, '删除确认', {
          confirmButtonText: '删除',
          cancelButtonText: '取消',
          type: 'warning'
        });
        const response = await adminApi.deleteEvaluation(evaluation.id);
        if (response?.success === false) {
          this.$message.error(response.error || '删除失败');
          return;
        }
        this.$message.success('删除成功');
        this.detailDialogVisible = false;
        await this.loadEvaluations();
      } catch (error) {
        if (error !== 'cancel' && error !== 'close') {
          console.error('删除评价失败:', error);
          this.$message.error('删除评价失败');
        }
      }
    },
    getDirection(evaluation) {
      if (evaluation.evaluatorRole === 'STUDENT' && evaluation.evaluatedRole === 'TEACHER') {
        return 'STUDENT_TO_TEACHER';
      }
      if (evaluation.evaluatorRole === 'TEACHER' && evaluation.evaluatedRole === 'STUDENT') {
        return 'TEACHER_TO_STUDENT';
      }
      return 'OTHER';
    },
    getDirectionText(evaluation) {
      const direction = this.getDirection(evaluation);
      if (direction === 'STUDENT_TO_TEACHER') {
        return '学生评价老师';
      }
      if (direction === 'TEACHER_TO_STUDENT') {
        return '老师评价学生';
      }
      return '其他评价';
    },
    getDirectionTagType(evaluation) {
      return this.getDirection(evaluation) === 'STUDENT_TO_TEACHER' ? 'success' : 'info';
    },
    getAverageScore(evaluation) {
      if (evaluation?.averageScore != null) {
        return Number(evaluation.averageScore);
      }
      const scores = [
        evaluation?.teachingQuality || 0,
        evaluation?.attitude || 0,
        evaluation?.satisfaction || 0
      ];
      return Math.round(scores.reduce((sum, score) => sum + score, 0) / scores.length);
    },
    getScoreLevel(evaluation) {
      const score = this.getAverageScore(evaluation);
      if (score >= 4) {
        return 'HIGH';
      }
      if (score === 3) {
        return 'MID';
      }
      return 'LOW';
    },
    formatRole(role) {
      if (role === 'TEACHER') {
        return '老师';
      }
      if (role === 'STUDENT') {
        return '学生';
      }
      if (role === 'ADMIN') {
        return '管理员';
      }
      return role || '未知';
    },
    getRoleTagType(role) {
      if (role === 'TEACHER') {
        return 'success';
      }
      if (role === 'ADMIN') {
        return 'danger';
      }
      return 'info';
    },
    formatAppointmentTime(evaluation) {
      const date = evaluation?.requestedDate || '';
      const time = evaluation?.requestedTime || '';
      return [date, time].filter(Boolean).join(' ') || '未填写';
    },
    formatDateTime(value) {
      if (!value) {
        return '-';
      }
      const date = new Date(value);
      if (Number.isNaN(date.getTime())) {
        return value;
      }
      const pad = number => String(number).padStart(2, '0');
      return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`;
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
.admin-evaluations {
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

.evaluations-container {
  padding: 24px;
  background-color: white;
  border-radius: 8px;
  min-height: calc(100vh - 40px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 18px;
}

.page-header h2 {
  margin: 0;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(140px, 1fr));
  gap: 12px;
  margin-bottom: 18px;
}

.summary-item {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 14px 16px;
  background-color: #fafafa;
}

.summary-label {
  display: block;
  color: #606266;
  font-size: 13px;
  margin-bottom: 8px;
}

.summary-item strong {
  color: #303133;
  font-size: 24px;
}

.filter-bar,
.table-actions,
.score-cell,
.dialog-footer {
  display: flex;
  align-items: center;
}

.filter-bar {
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.search-input {
  width: 360px;
}

.filter-select {
  width: 150px;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.score-cell {
  gap: 8px;
}

.score-cell :deep(.el-rate) {
  height: 20px;
}

.table-actions {
  gap: 8px;
  justify-content: flex-end;
}

.table-actions .el-button {
  margin-left: 0;
}

.pagination-row {
  display: flex;
  justify-content: flex-end;
  margin-top: 18px;
}

.detail-comment {
  white-space: pre-wrap;
  line-height: 1.7;
}

.dialog-footer {
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 1000px) {
  .summary-grid {
    grid-template-columns: repeat(2, minmax(140px, 1fr));
  }

  .search-input,
  .filter-select {
    width: 100%;
  }
}
</style>
