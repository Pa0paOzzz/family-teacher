<template>
  <div class="admin-evaluations">
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
        <div class="evaluations-container">
          <h2>评价管理</h2>
          <div class="toolbar">
            <el-input
              v-model="searchQuery"
              placeholder="请输入评价人、被评价人或学科"
              clearable
              class="search-input"
            >
              <template #append>
                <el-button @click="applyFilters">搜索</el-button>
              </template>
            </el-input>
            <el-select v-model="roleFilter" placeholder="按角色筛选" clearable class="role-select" @change="applyFilters">
              <el-option label="全部" value="" />
              <el-option label="学生评价老师" value="STUDENT_TO_TEACHER" />
              <el-option label="老师评价学生" value="TEACHER_TO_STUDENT" />
            </el-select>
          </div>

          <el-table :data="pagedEvaluations" style="width: 100%" empty-text="暂无评价数据">
            <el-table-column prop="id" label="评价ID" width="90" />
            <el-table-column label="评价人" min-width="140">
              <template #default="scope">
                <div>{{ scope.row.evaluatorName || '未知用户' }}</div>
                <el-tag size="small" :type="scope.row.evaluatorRole === 'TEACHER' ? 'success' : 'info'">
                  {{ formatRole(scope.row.evaluatorRole) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="被评价人" min-width="140">
              <template #default="scope">
                <div>{{ scope.row.evaluatedName || '未知用户' }}</div>
                <el-tag size="small" :type="scope.row.evaluatedRole === 'TEACHER' ? 'success' : 'info'">
                  {{ formatRole(scope.row.evaluatedRole) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="subject" label="学科" min-width="100" />
            <el-table-column label="教学质量" width="150">
              <template #default="scope">
                <el-rate :model-value="scope.row.teachingQuality || 0" disabled />
              </template>
            </el-table-column>
            <el-table-column label="服务态度" width="150">
              <template #default="scope">
                <el-rate :model-value="scope.row.attitude || 0" disabled />
              </template>
            </el-table-column>
            <el-table-column label="满意度" width="150">
              <template #default="scope">
                <el-rate :model-value="scope.row.satisfaction || 0" disabled />
              </template>
            </el-table-column>
            <el-table-column prop="comment" label="评价内容" min-width="220" show-overflow-tooltip />
            <el-table-column label="评价时间" min-width="180">
              <template #default="scope">
                {{ formatDateTime(scope.row.createdAt) }}
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-if="filteredEvaluations.length > 0"
            :current-page="currentPage"
            :page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="filteredEvaluations.length"
            layout="total, sizes, prev, pager, next, jumper"
            style="margin-top: 20px; text-align: right;"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { adminApi } from '../../api/api';
import { User, DataLine, Calendar, SwitchButton, Comment } from '@element-plus/icons-vue';

export default {
  name: 'AdminEvaluationsView',
  components: {
    User,
    DataLine,
    Calendar,
    SwitchButton,
    Comment
  },
  data() {
    return {
      activeIndex: '4',
      searchQuery: '',
      roleFilter: '',
      evaluations: [],
      filteredEvaluations: [],
      currentPage: 1,
      pageSize: 10
    }
  },
  computed: {
    pagedEvaluations() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.filteredEvaluations.slice(start, end);
    }
  },
  mounted() {
    this.loadEvaluations();
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
    async loadEvaluations() {
      try {
        const evaluations = await adminApi.getEvaluations();
        this.evaluations = evaluations || [];
        this.applyFilters();
      } catch (error) {
        console.error('加载评价列表失败:', error);
        this.$message.error('加载评价列表失败');
      }
    },
    applyFilters() {
      const keyword = this.searchQuery.trim().toLowerCase();
      this.filteredEvaluations = this.evaluations.filter(evaluation => {
        const matchesKeyword = !keyword || [
          evaluation.evaluatorName,
          evaluation.evaluatedName,
          evaluation.subject
        ].some(value => (value || '').toLowerCase().includes(keyword));

        const matchesRole = !this.roleFilter || this.getRoleDirection(evaluation) === this.roleFilter;

        return matchesKeyword && matchesRole;
      });
      this.currentPage = 1;
    },
    getRoleDirection(evaluation) {
      if (evaluation.evaluatorRole === 'STUDENT' && evaluation.evaluatedRole === 'TEACHER') {
        return 'STUDENT_TO_TEACHER';
      }
      if (evaluation.evaluatorRole === 'TEACHER' && evaluation.evaluatedRole === 'STUDENT') {
        return 'TEACHER_TO_STUDENT';
      }
      return '';
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
    handleSizeChange(size) {
      this.pageSize = size;
      this.currentPage = 1;
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

.evaluations-container {
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  min-height: calc(100vh - 40px);
}

.evaluations-container h2 {
  margin-bottom: 30px;
  color: #409EFF;
}

.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.search-input {
  width: 320px;
}

.role-select {
  width: 180px;
}
</style>
