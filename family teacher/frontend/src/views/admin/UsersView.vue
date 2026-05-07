<template>
  <div class="admin-users">
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
        <div class="users-container">
          <div class="page-header">
            <h2>用户管理</h2>
            <div class="header-actions">
              <el-button :icon="Refresh" :loading="loading" @click="loadUsers">刷新</el-button>
              <el-button type="primary" :icon="Plus" @click="openCreateDialog">添加用户</el-button>
            </div>
          </div>

          <div class="filter-bar">
            <el-input
              v-model="searchQuery"
              clearable
              placeholder="用户名、姓名、邮箱或手机号"
              class="search-input"
              @clear="resetPage"
              @keyup.enter="resetPage"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>

            <el-select v-model="roleFilter" clearable placeholder="角色" class="filter-select" @change="resetPage">
              <el-option label="管理员" value="ADMIN" />
              <el-option label="教师" value="TEACHER" />
              <el-option label="学生" value="STUDENT" />
            </el-select>

            <el-select v-model="statusFilter" clearable placeholder="状态" class="filter-select" @change="resetPage">
              <el-option label="启用" :value="true" />
              <el-option label="禁用" :value="false" />
            </el-select>
          </div>

          <el-table v-loading="loading" :data="pagedUsers" style="width: 100%" empty-text="暂无用户">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="username" label="用户名" min-width="130" />
            <el-table-column prop="name" label="姓名" min-width="120">
              <template #default="{ row }">{{ row.name || '未填写' }}</template>
            </el-table-column>
            <el-table-column prop="email" label="邮箱" min-width="190">
              <template #default="{ row }">{{ row.email || '未填写' }}</template>
            </el-table-column>
            <el-table-column prop="phone" label="手机号" min-width="130">
              <template #default="{ row }">{{ row.phone || '未填写' }}</template>
            </el-table-column>
            <el-table-column prop="role" label="角色" width="110">
              <template #default="{ row }">
                <el-tag :type="getRoleTagType(row.role)">{{ getRoleText(row.role) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="enabled" label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="row.enabled ? 'success' : 'danger'">
                  {{ row.enabled ? '启用' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="创建时间" min-width="170">
              <template #default="{ row }">{{ formatDateTime(row.createdAt) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="250" fixed="right">
              <template #default="{ row }">
                <div class="table-actions">
                  <el-tooltip content="编辑用户" placement="top">
                    <el-button :icon="Edit" size="small" @click="openEditDialog(row)" />
                  </el-tooltip>
                  <el-tooltip :content="row.enabled ? '禁用用户' : '启用用户'" placement="top">
                    <el-button
                      :type="row.enabled ? 'warning' : 'success'"
                      :icon="row.enabled ? CircleClose : CircleCheck"
                      size="small"
                      :disabled="isProtectedAdmin(row)"
                      @click="toggleUserStatus(row)"
                    />
                  </el-tooltip>
                  <el-tooltip content="删除用户" placement="top">
                    <el-button
                      type="danger"
                      :icon="Delete"
                      size="small"
                      :disabled="isProtectedAdmin(row)"
                      @click="confirmDeleteUser(row)"
                    />
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
              layout="total, sizes, prev, pager, next, jumper"
              :total="filteredUsers.length"
            />
          </div>
        </div>
      </el-main>
    </el-container>

    <el-dialog v-model="dialogVisible" :title="editingUserId ? '编辑用户' : '添加用户'" width="520px">
      <el-form :model="userForm" label-width="90px">
        <el-form-item label="用户名" required>
          <el-input v-model="userForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item :label="editingUserId ? '新密码' : '密码'" :required="!editingUserId">
          <el-input
            v-model="userForm.password"
            type="password"
            show-password
            :placeholder="editingUserId ? '不修改请留空' : '请输入密码'"
          />
        </el-form-item>
        <el-form-item label="角色" required>
          <el-select v-model="userForm.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="教师" value="TEACHER" />
            <el-option label="学生" value="STUDENT" />
          </el-select>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="userForm.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="userForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch
            v-model="userForm.enabled"
            active-text="启用"
            inactive-text="禁用"
            :disabled="userForm.role === 'ADMIN'"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="saving" @click="submitUser">保存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { adminApi } from '../../api/api';
import {
  Calendar,
  CircleCheck,
  CircleClose,
  Comment,
  DataLine,
  Delete,
  Document,
  Edit,
  Plus,
  Refresh,
  Search,
  SwitchButton,
  User
} from '@element-plus/icons-vue';

export default {
  name: 'AdminUsersView',
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
      activeIndex: '2',
      CircleCheck,
      CircleClose,
      Delete,
      Edit,
      Plus,
      Refresh,
      loading: false,
      saving: false,
      dialogVisible: false,
      editingUserId: null,
      searchQuery: '',
      roleFilter: '',
      statusFilter: '',
      usersList: [],
      currentPage: 1,
      pageSize: 10,
      userForm: this.createEmptyForm()
    };
  },
  computed: {
    filteredUsers() {
      const query = this.searchQuery.trim().toLowerCase();
      return this.usersList.filter(user => {
        const matchesQuery = !query || [user.username, user.name, user.email, user.phone]
          .some(value => String(value || '').toLowerCase().includes(query));
        const matchesRole = !this.roleFilter || user.role === this.roleFilter;
        const matchesStatus = this.statusFilter === '' || user.enabled === this.statusFilter;
        return matchesQuery && matchesRole && matchesStatus;
      });
    },
    pagedUsers() {
      const start = (this.currentPage - 1) * this.pageSize;
      return this.filteredUsers.slice(start, start + this.pageSize);
    }
  },
  watch: {
    filteredUsers() {
      const maxPage = Math.max(1, Math.ceil(this.filteredUsers.length / this.pageSize));
      if (this.currentPage > maxPage) {
        this.currentPage = maxPage;
      }
    }
  },
  mounted() {
    this.loadUsers();
  },
  methods: {
    createEmptyForm() {
      return {
        username: '',
        password: '',
        role: 'STUDENT',
        name: '',
        email: '',
        phone: '',
        enabled: true
      };
    },
    async loadUsers() {
      this.loading = true;
      try {
        const response = await adminApi.getUsers();
        this.usersList = (response || []).map(user => ({
          ...user,
          enabled: user.enabled !== false
        }));
      } catch (error) {
        console.error('加载用户列表失败:', error);
        this.$message.error('加载用户列表失败');
      } finally {
        this.loading = false;
      }
    },
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
    resetPage() {
      this.currentPage = 1;
    },
    openCreateDialog() {
      this.editingUserId = null;
      this.userForm = this.createEmptyForm();
      this.dialogVisible = true;
    },
    openEditDialog(user) {
      this.editingUserId = user.id;
      this.userForm = {
        username: user.username || '',
        password: '',
        role: user.role || 'STUDENT',
        name: user.name || '',
        email: user.email || '',
        phone: user.phone || '',
        enabled: user.enabled !== false
      };
      this.dialogVisible = true;
    },
    async submitUser() {
      if (!this.userForm.username.trim() || !this.userForm.role) {
        this.$message.warning('请填写用户名和角色');
        return;
      }
      if (!this.editingUserId && !this.userForm.password.trim()) {
        this.$message.warning('请填写密码');
        return;
      }

      this.saving = true;
      try {
        const payload = { ...this.userForm };
        if (this.editingUserId && !payload.password) {
          delete payload.password;
        }

        const response = this.editingUserId
          ? await adminApi.updateUser(this.editingUserId, payload)
          : await adminApi.createUser(payload);

        if (response?.success === false) {
          this.$message.error(response.error || '保存失败');
          return;
        }

        this.$message.success('保存成功');
        this.dialogVisible = false;
        await this.loadUsers();
      } catch (error) {
        console.error('保存用户失败:', error);
        this.$message.error('保存用户失败');
      } finally {
        this.saving = false;
      }
    },
    async toggleUserStatus(user) {
      try {
        const response = user.enabled
          ? await adminApi.disableUser(user.id)
          : await adminApi.enableUser(user.id);

        if (response?.success === false) {
          this.$message.error(response.error || '操作失败');
          return;
        }

        this.$message.success('操作成功');
        await this.loadUsers();
      } catch (error) {
        console.error('更新用户状态失败:', error);
        this.$message.error('更新用户状态失败');
      }
    },
    async confirmDeleteUser(user) {
      try {
        await this.$confirm(`确认删除用户 ${user.username} 吗？`, '删除确认', {
          confirmButtonText: '删除',
          cancelButtonText: '取消',
          type: 'warning'
        });

        const response = await adminApi.deleteUser(user.id);
        if (response?.success === false) {
          this.$message.error(response.error || '删除失败');
          return;
        }

        this.$message.success('删除成功');
        await this.loadUsers();
      } catch (error) {
        if (error !== 'cancel' && error !== 'close') {
          console.error('删除用户失败:', error);
          this.$message.error('删除用户失败');
        }
      }
    },
    isProtectedAdmin(user) {
      return user.role === 'ADMIN';
    },
    getRoleText(role) {
      const roleMap = {
        ADMIN: '管理员',
        TEACHER: '教师',
        STUDENT: '学生'
      };
      return roleMap[role] || role || '未知';
    },
    getRoleTagType(role) {
      if (role === 'ADMIN') {
        return 'danger';
      }
      if (role === 'TEACHER') {
        return 'success';
      }
      return 'info';
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

.users-container {
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  min-height: calc(100vh - 40px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  gap: 12px;
  flex-wrap: wrap;
}

.page-header h2 {
  margin: 0;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.header-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.search-input {
  width: 320px;
  max-width: 100%;
}

.filter-select {
  width: 180px;
}

.table-actions {
  display: flex;
  gap: 8px;
}

.pagination-row {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
