<template>
  <div class="teacher-home">
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
            <el-icon><Comment /></el-icon>
            <span>我的评价</span>
          </el-menu-item>
          <el-menu-item index="7">
            <el-icon><SwitchButton /></el-icon>
            <span>退出登录</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-main class="main-content">
        <div class="search-container">
          <el-select
              v-model="searchSubject"
              placeholder="选择学科"
              clearable
              style="width: 200px; margin-right: 10px;"
          >
            <el-option label="语文" value="语文"></el-option>
            <el-option label="数学" value="数学"></el-option>
            <el-option label="英语" value="英语"></el-option>
            <el-option label="政治" value="政治"></el-option>
            <el-option label="历史" value="历史"></el-option>
            <el-option label="地理" value="地理"></el-option>
            <el-option label="化学" value="化学"></el-option>
            <el-option label="生物" value="生物"></el-option>
            <el-option label="物理" value="物理"></el-option>
            <el-option label="信息技术" value="信息技术"></el-option>
            <el-option label="美术" value="美术"></el-option>
            <el-option label="音乐" value="音乐"></el-option>
          </el-select>
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
                    <span>{{ getLocationText(request) }}</span>
                  </div>
                  <div class="info-row">
                    <el-icon><Clock /></el-icon>
                    <span>{{ request.preferredTime }}</span>
                  </div>
                  <div class="budget-row">
                    <span class="budget">预算: ¥{{ request.budgetPerHour }}/小时</span>
                    <div class="action-buttons">
                      <el-button type="info" size="small" plain @click="openDetailDialog(request)">详情</el-button>
                      <el-button
                        :type="isFavorited(request.id) ? 'warning' : 'default'"
                        size="small"
                        :loading="favoriteLoadingIds.includes(request.id)"
                        :disabled="favoriteLoadingIds.includes(request.id)"
                        @click="toggleFavorite(request)"
                      >
                        {{ isFavorited(request.id) ? '取消收藏' : '收藏' }}
                      </el-button>
                      <el-button type="primary" size="small" @click="openContactDialog(request)">联系学生</el-button>
                    </div>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
          <el-empty v-if="requestList.length === 0" :description="emptyDescription"></el-empty>
        </div>

        <el-dialog v-model="contactDialogVisible" title="预约学生" width="600px">
          <el-form :model="contactForm" label-width="100px">
            <el-form-item label="学生姓名">
              <el-input :value="selectedRequest?.student?.user?.name" disabled></el-input>
            </el-form-item>
            <el-form-item label="学科">
              <el-input :value="selectedRequest?.subject" disabled></el-input>
            </el-form-item>
            <el-form-item label="预约时间" required>
              <el-date-picker
                v-model="contactForm.appointmentTime"
                type="datetime"
                placeholder="选择预约时间"
                style="width: 100%"
              ></el-date-picker>
            </el-form-item>
            <el-form-item label="上课地点" required>
              <el-input v-model="contactForm.location" placeholder="请输入上课地点"></el-input>
            </el-form-item>
            <el-form-item label="价格/小时" required>
              <el-input-number v-model="contactForm.pricePerHour" :min="0" :precision="2" :step="10" style="width: 100%" placeholder="请输入每小时价格"></el-input-number>
            </el-form-item>
            <el-form-item label="留言">
              <el-input v-model="contactForm.message" type="textarea" rows="3" placeholder="请输入留言内容"></el-input>
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="contactDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitAppointment">确认预约</el-button>
          </template>
        </el-dialog>

        <el-dialog v-model="detailDialogVisible" title="需求详情" width="720px">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="需求标题" :span="2">
              {{ selectedRequest?.title || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="学生姓名">
              {{ selectedRequest?.student?.user?.name || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="学科">
              {{ selectedRequest?.subject || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="学校">
              {{ selectedRequest?.student?.school || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="年级">
              {{ selectedRequest?.student?.grade || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="电话">
              {{ selectedRequest?.student?.user?.phone || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="邮箱">
              {{ selectedRequest?.student?.user?.email || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="专业">
              {{ selectedRequest?.student?.major || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="预算/小时">
              ¥{{ selectedRequest?.budgetPerHour || 0 }}
            </el-descriptions-item>
            <el-descriptions-item label="地点" :span="2">
              {{ getLocationText(selectedRequest) || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="期望时间" :span="2">
              {{ selectedRequest?.preferredTime || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="地址" :span="2">
              {{ getAddressText(selectedRequest?.student) || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="需求描述" :span="2">
              <div class="detail-text">{{ selectedRequest?.description || '暂无描述' }}</div>
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
import { recommendationApi, tutoringRequestApi, appointmentApi, favoriteApi, userApi } from '../../api/api';
import { getDisplayLocation } from '../../utils/location';
import { User, School, Location, Clock, HomeFilled, EditPen, Calendar, Star, SwitchButton, Comment } from '@element-plus/icons-vue';

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
    SwitchButton,
    Comment
  },
  data() {
    return {
      activeIndex: '1',
      searchSubject: '',
      requestList: [],
      favoriteIds: [],
      favoriteLoadingIds: [],
      contactDialogVisible: false,
      detailDialogVisible: false,
      selectedRequest: null,
      emptyDescription: '暂无学生推荐',
      contactForm: {
        appointmentTime: '',
        location: '',
        pricePerHour: null,
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
          this.$router.push('/teacher/evaluations');
          break;
        case '7':
          this.logout();
          break;
      }
    },
    getLocationText(item) {
      return getDisplayLocation(item, 'location');
    },
    getAddressText(item) {
      return getDisplayLocation(item, 'address');
    },
    async loadRequests() {
      try {
        const [requests, favorites, profile] = await Promise.all([
          recommendationApi.getStudentsForTeacher(),
          favoriteApi.getList('STUDENT_TUTORING_REQUEST'),
          userApi.getTeacherProfile()
        ]);
        this.requestList = requests || [];
        this.favoriteIds = (favorites || []).map(favorite => favorite.resourceId).filter(id => id != null);
        this.emptyDescription = profile?.addressCity ? '暂无学生推荐' : '请先在个人资料中填写所在城市，以优先获取同市学生推荐';
      } catch (error) {
        console.error('加载学生推荐信息失败:', error);
        this.$message.error('加载学生推荐信息失败');
      }
    },
    async searchRequests() {
      if (!this.searchSubject) {
        this.loadRequests();
        return;
      }
      try {
        const response = await tutoringRequestApi.getList();
        this.requestList = (response || []).filter(req =>
            req.subject === this.searchSubject
        );
        this.emptyDescription = '暂无学生需求信息';
      } catch (error) {
        console.error('搜索失败:', error);
        this.$message.error('搜索失败');
      }
    },
    resetSearch() {
      this.searchSubject = '';
      this.loadRequests();
    },
    isFavorited(resourceId) {
      return this.favoriteIds.includes(resourceId);
    },
    async toggleFavorite(request) {
      const resourceId = request?.id;
      if (!resourceId || this.favoriteLoadingIds.includes(resourceId)) {
        return;
      }

      this.favoriteLoadingIds = [...this.favoriteLoadingIds, resourceId];

      try {
        if (this.isFavorited(resourceId)) {
          await favoriteApi.remove({
            resourceType: 'STUDENT_TUTORING_REQUEST',
            resourceId
          });
          this.favoriteIds = this.favoriteIds.filter(id => id !== resourceId);
          this.$message.success('已取消收藏');
        } else {
          await favoriteApi.add({
            resourceType: 'STUDENT_TUTORING_REQUEST',
            resourceId
          });
          this.favoriteIds = [...this.favoriteIds, resourceId];
          this.$message.success('收藏成功');
        }
      } catch (error) {
        console.error('收藏操作失败:', error);
        this.$message.error('收藏操作失败');
      } finally {
        this.favoriteLoadingIds = this.favoriteLoadingIds.filter(id => id !== resourceId);
      }
    },
    openContactDialog(request) {
      this.selectedRequest = request;
      this.contactForm = {
        appointmentTime: '',
        location: request?.locationFormatted || request?.location || '',
        pricePerHour: request?.budgetPerHour || null,
        message: ''
      };
      this.contactDialogVisible = true;
    },
    openDetailDialog(request) {
      this.selectedRequest = request;
      this.detailDialogVisible = true;
    },
    contactFromDetail() {
      this.detailDialogVisible = false;
      this.openContactDialog(this.selectedRequest);
    },
    async submitAppointment() {
      if (!this.contactForm.appointmentTime) {
        this.$message.warning('请选择预约时间');
        return;
      }
      if (!this.contactForm.location) {
        this.$message.warning('请输入上课地点');
        return;
      }
      if (this.contactForm.pricePerHour == null) {
        this.$message.warning('请输入价格/小时');
        return;
      }
      try {
        await appointmentApi.createForTeacher({
          studentId: this.selectedRequest?.student?.id,
          appointmentTime: this.contactForm.appointmentTime,
          subject: this.selectedRequest?.subject,
          location: this.contactForm.location,
          pricePerHour: this.contactForm.pricePerHour,
          remark: this.contactForm.message
        });
        this.$message.success('预约成功');
        this.contactDialogVisible = false;
        this.contactForm = {
          appointmentTime: '',
          location: '',
          pricePerHour: null,
          message: ''
        };
      } catch (error) {
        console.error('预约失败:', error);
        this.$message.error('预约失败: ' + (error.response?.data?.error || error.message));
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
.teacher-home {
  height: 100vh;
  overflow: hidden;
  background-color: #f5f7fa;
}

.sidebar {
  height: 100%;
  background-color: #304156;
  color: white;
  display: flex;
  flex-direction: column;
}

.logo {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid #3a4a5b;
  flex-shrink: 0;
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

.main-content {
  padding: 20px;
  margin-left: 0;
}

.search-container {
  margin-bottom: 30px;
  display: flex;
  align-items: center;
}

.requests-container {
  background-color: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.requests-container h2 {
  margin-bottom: 25px;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.request-card {
  margin-bottom: 20px;
  transition: all 0.3s;
}

.request-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header .title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.card-content {
  font-size: 14px;
}

.card-content .description {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 15px;
  min-height: 45px;
}

.info-row {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  color: #606266;
  font-size: 14px;
}

.info-row .el-icon {
  margin-right: 8px;
  color: #909399;
}

.budget-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
}

.budget-row .budget {
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
}

.action-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.detail-text {
  line-height: 1.6;
  white-space: pre-wrap;
}
</style>
