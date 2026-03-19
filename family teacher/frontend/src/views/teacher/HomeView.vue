<template>
  <div class="teacher-home">
    <!-- 设置容器高度为 100vh，启用 Flex 布局 -->
    <el-container style="height: 100vh;">

      <!-- 侧边栏：不再需要 position: fixed，由 el-container 自动排列 -->
      <el-aside width="200px" class="sidebar">
        <div class="logo">
          <h2>家教平台</h2>
          <p>教师端</p>
        </div>
        <el-menu
            :default-active="activeIndex"
            class="sidebar-menu"
            @select="handleMenuSelect"
            background-color="#304156"
            text-color="#bfcbd9"
            active-text-color="#409EFF"
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
            <el-icon><SwitchButton /></el-icon>
            <span>退出登录</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主内容区：自动占满剩余宽度，无需 margin-left -->
      <el-main class="main-content">
        <div class="search-container">
          <el-input
              v-model="searchSubject"
              placeholder="搜索学科"
              style="width: 300px; margin-right: 10px;"
              @keyup.enter="searchRequests"
          ></el-input>
          <el-button type="primary" @click="searchRequests">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </div>

        <div class="requests-container">
          <h2>学生需求信息</h2>
          <el-row :gutter="20">
            <el-col :span="8" v-for="request in requestList" :key="request.id">
              <el-card class="request-card" shadow="hover">
                <template #header>
                  <div class="card-header">
                    <span class="title">{{ request.title }}</span>
                    <el-tag type="primary">{{ request.subject }}</el-tag>
                  </div>
                </template>
                <div class="card-content">
                  <p class="description">{{ request.description }}</p>
                  <div class="info-row">
                    <el-icon><User /></el-icon>
                    <span>{{ request.student?.user?.name || '未知' }}</span>
                  </div>
                  <div class="info-row">
                    <el-icon><School /></el-icon>
                    <span>{{ request.student?.school || '未知学校' }}</span>
                  </div>
                  <div class="info-row">
                    <el-icon><Location /></el-icon>
                    <span>{{ request.location }}</span>
                  </div>
                  <div class="info-row">
                    <el-icon><Clock /></el-icon>
                    <span>{{ request.preferredTime }}</span>
                  </div>
                  <div class="budget-row">
                    <span class="budget">预算: ¥{{ request.budgetPerHour }}/小时</span>
                    <div>
                      <el-button type="info" size="small" @click="openDetailDialog(request)">查看详情</el-button>
                      <el-button type="primary" size="small" @click="openContactDialog(request)">联系学生</el-button>
                    </div>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
          <el-empty v-if="requestList.length === 0" description="暂无学生需求信息"></el-empty>
        </div>

        <el-dialog v-model="contactDialogVisible" title="联系学生" width="500px">
          <el-form :model="contactForm" label-width="100px">
            <el-form-item label="学生姓名">
              <el-input :value="selectedRequest?.student?.user?.name" disabled></el-input>
            </el-form-item>
            <el-form-item label="学科">
              <el-input :value="selectedRequest?.subject" disabled></el-input>
            </el-form-item>
            <el-form-item label="联系方式">
              <el-input :value="selectedRequest?.student?.user?.phone || '未填写'" disabled></el-input>
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input :value="selectedRequest?.student?.user?.email || '未填写'" disabled></el-input>
            </el-form-item>
            <el-form-item label="留言">
              <el-input v-model="contactForm.message" type="textarea" rows="3" placeholder="请输入留言内容"></el-input>
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="contactDialogVisible = false">关闭</el-button>
          </template>
        </el-dialog>

        <el-dialog v-model="detailDialogVisible" title="需求详情" width="700px">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="需求标题" :span="2">
              {{ selectedRequest?.title || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="学科">
              {{ selectedRequest?.subject || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="预算/小时">
              ¥{{ selectedRequest?.budgetPerHour || 0 }}
            </el-descriptions-item>
            <el-descriptions-item label="地点" :span="2">
              {{ selectedRequest?.location || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="期望时间" :span="2">
              {{ selectedRequest?.preferredTime || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="需求描述" :span="2">
              {{ selectedRequest?.description || '暂无描述' }}
            </el-descriptions-item>
            <el-descriptions-item label="发布时间" :span="2">
              {{ selectedRequest?.createdAt || '未知' }}
            </el-descriptions-item>
          </el-descriptions>
          
          <el-divider content-position="left">学生信息</el-divider>
          
          <el-descriptions :column="2" border>
            <el-descriptions-item label="学生姓名">
              {{ selectedRequest?.student?.user?.name || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="学校">
              {{ selectedRequest?.student?.school || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="年级">
              {{ selectedRequest?.student?.grade || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="专业">
              {{ selectedRequest?.student?.major || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="联系电话">
              {{ selectedRequest?.student?.user?.phone || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="邮箱">
              {{ selectedRequest?.student?.user?.email || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="地址" :span="2">
              {{ selectedRequest?.student?.address || '未填写' }}
            </el-descriptions-item>
          </el-descriptions>
          
          <template #footer>
            <el-button @click="detailDialogVisible = false">关闭</el-button>
            <el-button type="primary" @click="contactFromDetail">联系学生</el-button>
          </template>
        </el-dialog>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { tutoringRequestApi } from '../../api/api';
import { User, School, Location, Clock, HomeFilled, EditPen, Calendar, Star, SwitchButton } from '@element-plus/icons-vue';

export default {
  name: 'TeacherHomeView',
  components: {
    User,
    School,
    Location,
    Clock,
    HomeFilled,
    EditPen,
    Calendar,
    Star,
    SwitchButton
  },
  data() {
    return {
      activeIndex: '1',
      searchSubject: '',
      requestList: [],
      contactDialogVisible: false,
      detailDialogVisible: false,
      selectedRequest: null,
      contactForm: {
        message: ''
      }
    }
  },
  mounted() {
    this.loadRequests();
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
          this.logout();
          break;
      }
    },
    async loadRequests() {
      try {
        const response = await tutoringRequestApi.getList();
        this.requestList = response || [];
      } catch (error) {
        console.error('加载学生需求信息失败:', error);
        this.$message.error('加载学生需求信息失败');
      }
    },
    async searchRequests() {
      if (!this.searchSubject.trim()) {
        this.loadRequests();
        return;
      }
      try {
        // 注意：实际项目中建议后端支持搜索参数，这里沿用前端过滤逻辑
        const response = await tutoringRequestApi.getList();
        this.requestList = (response || []).filter(req =>
            req.subject && req.subject.toLowerCase().includes(this.searchSubject.toLowerCase())
        );
      } catch (error) {
        console.error('搜索失败:', error);
        this.$message.error('搜索失败');
      }
    },
    resetSearch() {
      this.searchSubject = '';
      this.loadRequests();
    },
    openContactDialog(request) {
      this.selectedRequest = request;
      this.contactDialogVisible = true;
    },
    openDetailDialog(request) {
      this.selectedRequest = request;
      this.detailDialogVisible = true;
    },
    contactFromDetail() {
      this.detailDialogVisible = false;
      this.contactDialogVisible = true;
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
/* 最外层容器占满视口高度，防止出现双重滚动条 */
.teacher-home {
  height: 100vh;
  overflow: hidden;
  background-color: #f5f7fa;
}

/* 侧边栏样式：移除 fixed 定位，让它自然参与 Flex 布局 */
.sidebar {
  height: 100%;
  background-color: #304156;
  color: white;
  display: flex;
  flex-direction: column;
  /* 不再需要 z-index, left, top 等 */
}

.logo {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid #3a4a5b;
  flex-shrink: 0; /* 防止 logo 区域被压缩 */
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
  flex: 1; /* 菜单占据剩余垂直空间 */
  overflow-y: auto; /* 如果菜单项过多，允许侧边栏内部滚动 */
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

/* 主内容区样式 */
.el-main {
  padding: 0; /* 移除默认 padding，由内部容器控制 */
  background-color: #f5f7fa;
  height: 100%;
  overflow-y: auto; /* 只有主内容区滚动 */
  display: flex;
  flex-direction: column;
}

/* 内部包装器，用于控制实际内容的 padding 和布局 */
.main-content-wrapper {
  padding: 20px;
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

.search-container {
  margin: 20px 20px 0 20px; /* 上右下左 */
  padding: 20px;
  background-color: white;
  border-radius: 8px;
  flex-shrink: 0;
}

.requests-container {
  margin: 20px;
  padding: 20px;
  background-color: white;
  border-radius: 8px;
  flex: 1; /* 占据剩余垂直空间 */
  overflow-y: auto; /* 内容过多时在此区域滚动，或者依赖父级 .el-main 滚动 */
}

/* 如果希望整个页面一起滚动而不是局部滚动，可以将 .requests-container 的 overflow 改为 visible */
/* 但通常后台管理系统推荐局部滚动以保持头部搜索可见，或者像下面这样让 el-main 统一滚动 */

/* 修正：为了让搜索框和内容作为一个整体在 el-main 中滚动，我们调整一下结构逻辑 */
/* 上面的 .search-container 和 .requests-container 都在 el-main 内，el-main 设置了 overflow-y: auto */
/* 所以不需要给 .requests-container 单独设置 overflow，除非你想让搜索框固定 */

/* 下面的样式是针对当前模板结构的优化 */
.requests-container h2 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #303133;
}

.request-card {
  margin-bottom: 20px;
  height: 100%; /* 让卡片在列中撑开 */
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header .title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.card-content .description {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 15px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.info-row {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  color: #606266;
  font-size: 14px;
}

.info-row .el-icon {
  margin-right: 8px;
  color: #909399;
}

.budget-row {
  margin-top: 15px;
  padding-top: 10px;
  border-top: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.budget {
  font-size: 16px;
  font-weight: bold;
  color: #67c23a;
}

/* 响应式调整：小屏幕下卡片占满一行 */
@media (max-width: 768px) {
  .el-col {
    width: 100%;
    flex: 100%;
    max-width: 100%;
  }
}
</style>