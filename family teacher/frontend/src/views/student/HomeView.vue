<template>
  <div class="student-home">
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
        <div class="main-content">
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
            <el-button type="primary" @click="searchJobPosts">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </div>

          <div class="job-posts-container">
            <h2>教师求职信息</h2>
            <el-row :gutter="20">
              <el-col :span="8" v-for="jobPost in jobPostList" :key="jobPost.id">
                <el-card class="job-post-card" shadow="hover">
                  <template #header>
                    <div class="card-header">
                      <span class="title">{{ jobPost.title }}</span>
                      <el-tag type="success">{{ jobPost.subject }}</el-tag>
                    </div>
                  </template>
                  <div class="card-content">
                    <p class="description">{{ jobPost.description }}</p>
                    <div class="info-row">
                      <el-icon><User /></el-icon>
                      <span>{{ jobPost.teacher?.user?.name || '未知' }}</span>
                    </div>
                    <div class="info-row">
                      <el-icon><School /></el-icon>
                      <span>{{ jobPost.teacher?.school || '未知学校' }}</span>
                    </div>
                    <div class="info-row">
                      <el-icon><Location /></el-icon>
                      <span>{{ getLocationText(jobPost) }}</span>
                    </div>
                    <div class="info-row">
                      <el-icon><Clock /></el-icon>
                      <span>{{ jobPost.availability }}</span>
                    </div>
                    <div class="price-row">
                      <span class="price">¥{{ jobPost.pricePerHour }}/小时</span>
                      <div class="action-buttons">
                        <el-button type="info" size="small" plain @click="openDetailDialog(jobPost)">详情</el-button>
                        <el-button
                          :type="isFavorited(jobPost.id) ? 'warning' : 'default'"
                          size="small"
                          :loading="favoriteLoadingIds.includes(jobPost.id)"
                          :disabled="favoriteLoadingIds.includes(jobPost.id)"
                          @click="toggleFavorite(jobPost)"
                        >
                          {{ isFavorited(jobPost.id) ? '取消收藏' : '收藏' }}
                        </el-button>
                        <el-button type="primary" size="small" @click="openAppointmentDialog(jobPost)">预约</el-button>
                      </div>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
            <el-empty v-if="jobPostList.length === 0" :description="emptyDescription"></el-empty>
          </div>
        </div>

        <el-dialog v-model="appointmentDialogVisible" title="预约教师" width="600px">
          <el-form :model="appointmentForm" label-width="100px">
            <el-form-item label="老师姓名">
              <el-input :value="selectedJobPost?.teacher?.user?.name" disabled></el-input>
            </el-form-item>
            <el-form-item label="学科">
              <el-input :value="selectedJobPost?.subject" disabled></el-input>
            </el-form-item>
            <el-form-item label="预约时间" required>
              <el-date-picker
                v-model="appointmentForm.appointmentTime"
                type="datetime"
                placeholder="选择预约时间"
                style="width: 100%"
              ></el-date-picker>
            </el-form-item>
            <el-form-item label="上课地点" required>
              <el-input v-model="appointmentForm.location" placeholder="请输入上课地点"></el-input>
            </el-form-item>
            <el-form-item label="价格/小时" required>
              <el-input-number v-model="appointmentForm.pricePerHour" :min="0" :precision="2" :step="10" style="width: 100%" placeholder="请输入每小时价格"></el-input-number>
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="appointmentForm.remark" type="textarea" rows="3" placeholder="请输入备注信息"></el-input>
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="appointmentDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitAppointment">确认预约</el-button>
          </template>
        </el-dialog>

        <el-dialog v-model="detailDialogVisible" title="求职详情" width="720px">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="求职标题" :span="2">
              {{ selectedDetailJobPost?.title || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="教师姓名">
              {{ selectedDetailJobPost?.teacher?.user?.name || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="学科">
              {{ selectedDetailJobPost?.subject || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="学校">
              {{ selectedDetailJobPost?.teacher?.school || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="专业">
              {{ selectedDetailJobPost?.teacher?.major || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="电话">
              {{ selectedDetailJobPost?.teacher?.user?.phone || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="邮箱">
              {{ selectedDetailJobPost?.teacher?.user?.email || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="授课地点" :span="2">
              {{ getLocationText(selectedDetailJobPost) || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="可授课时间" :span="2">
              {{ selectedDetailJobPost?.availability || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="价格/小时">
              ¥{{ selectedDetailJobPost?.pricePerHour || 0 }}
            </el-descriptions-item>
            <el-descriptions-item label="教师简介">
              {{ selectedDetailJobPost?.teacher?.bio || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="求职描述" :span="2">
              <div class="detail-text">{{ selectedDetailJobPost?.description || '暂无描述' }}</div>
            </el-descriptions-item>
          </el-descriptions>

          <template #footer>
            <el-button @click="detailDialogVisible = false">关闭</el-button>
            <el-button type="primary" @click="openAppointmentFromDetail">立即预约</el-button>
          </template>
        </el-dialog>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { recommendationApi, jobPostApi, appointmentApi, favoriteApi, userApi } from '../../api/api';
import { getDisplayLocation } from '../../utils/location';
import { User, School, Location, Clock, HomeFilled, EditPen, Calendar, Star, SwitchButton, Comment } from '@element-plus/icons-vue';

export default {
  name: 'StudentHomeView',
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
      jobPostList: [],
      favoriteIds: [],
      favoriteLoadingIds: [],
      appointmentDialogVisible: false,
      detailDialogVisible: false,
      selectedJobPost: null,
      selectedDetailJobPost: null,
      emptyDescription: '暂无教师推荐',
      appointmentForm: {
        appointmentTime: '',
        location: '',
        pricePerHour: null,
        remark: ''
      }
    }
  },
  mounted() {
    this.loadJobPosts();
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
          this.$router.push('/student/evaluations');
          break;
        case '7':
          this.logout();
          break;
      }
    },
    getLocationText(item) {
      return getDisplayLocation(item, 'location');
    },
    async loadJobPosts() {
      try {
        const [jobPosts, favorites, profile] = await Promise.all([
          recommendationApi.getTeachersForStudent(),
          favoriteApi.getList('TEACHER_JOB_POST'),
          userApi.getStudentProfile()
        ]);
        this.jobPostList = jobPosts || [];
        this.favoriteIds = (favorites || []).map(favorite => favorite.resourceId).filter(id => id != null);
        this.emptyDescription = profile?.addressCity ? '暂无教师推荐' : '请先在个人资料中填写所在城市，以优先获取同市教师推荐';
      } catch (error) {
        console.error('加载教师推荐信息失败:', error);
        this.$message.error('加载教师推荐信息失败');
      }
    },
    async searchJobPosts() {
      if (!this.searchSubject) {
        this.loadJobPosts();
        return;
      }
      try {
        const response = await jobPostApi.getList();
        this.jobPostList = (response || []).filter(post =>
          post.subject === this.searchSubject
        );
        this.emptyDescription = '暂无教师求职信息';
      } catch (error) {
        console.error('搜索失败:', error);
        this.$message.error('搜索失败');
      }
    },
    resetSearch() {
      this.searchSubject = '';
      this.loadJobPosts();
    },
    isFavorited(resourceId) {
      return this.favoriteIds.includes(resourceId);
    },
    async toggleFavorite(jobPost) {
      const resourceId = jobPost?.id;
      if (!resourceId || this.favoriteLoadingIds.includes(resourceId)) {
        return;
      }

      this.favoriteLoadingIds = [...this.favoriteLoadingIds, resourceId];

      try {
        if (this.isFavorited(resourceId)) {
          await favoriteApi.remove({
            resourceType: 'TEACHER_JOB_POST',
            resourceId
          });
          this.favoriteIds = this.favoriteIds.filter(id => id !== resourceId);
          this.$message.success('已取消收藏');
        } else {
          await favoriteApi.add({
            resourceType: 'TEACHER_JOB_POST',
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
    openAppointmentDialog(jobPost) {
      this.selectedJobPost = jobPost;
      this.appointmentDialogVisible = true;
    },
    openDetailDialog(jobPost) {
      this.selectedDetailJobPost = jobPost;
      this.detailDialogVisible = true;
    },
    openAppointmentFromDetail() {
      if (!this.selectedDetailJobPost) {
        return;
      }
      this.detailDialogVisible = false;
      this.openAppointmentDialog(this.selectedDetailJobPost);
    },
    async submitAppointment() {
      if (!this.appointmentForm.appointmentTime) {
        this.$message.warning('请选择预约时间');
        return;
      }
      if (!this.appointmentForm.location) {
        this.$message.warning('请输入上课地点');
        return;
      }
      if (!this.appointmentForm.pricePerHour || this.appointmentForm.pricePerHour <= 0) {
        this.$message.warning('请输入有效的价格');
        return;
      }
      try {
        await appointmentApi.create({
          teacherId: this.selectedJobPost.teacher?.id,
          subject: this.selectedJobPost.subject,
          appointmentTime: this.appointmentForm.appointmentTime,
          location: this.appointmentForm.location,
          pricePerHour: this.appointmentForm.pricePerHour,
          remark: this.appointmentForm.remark
        });
        this.$message.success('预约成功');
        this.appointmentDialogVisible = false;
        this.appointmentForm = {
          appointmentTime: '',
          location: '',
          pricePerHour: null,
          remark: ''
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
.student-home {
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

.main-content {
  padding: 20px;
}

.search-container {
  margin-bottom: 30px;
  display: flex;
  align-items: center;
}

.job-posts-container {
  background-color: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.job-posts-container h2 {
  margin-bottom: 25px;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.job-post-card {
  margin-bottom: 20px;
  transition: all 0.3s;
}

.job-post-card:hover {
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

.price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
}

.price-row .price {
  font-size: 20px;
  font-weight: bold;
  color: #f56c6c;
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
