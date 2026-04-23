<template>
  <div class="student-favorites">
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
        <div class="favorites-container">
          <h2>我的收藏</h2>
          <el-row :gutter="20">
            <el-col :span="8" v-for="favorite in favoriteList" :key="favorite.id">
              <el-card class="favorite-card" shadow="hover">
                <template #header>
                  <div class="card-header">
                    <span class="title">{{ favorite.resource?.title || '未知标题' }}</span>
                    <el-tag type="success">{{ favorite.resource?.subject || '未知学科' }}</el-tag>
                  </div>
                </template>
                <div class="card-content">
                  <p class="description">{{ favorite.resource?.description || '暂无描述' }}</p>
                  <div class="info-row">
                    <el-icon><User /></el-icon>
                    <span>{{ favorite.resource?.teacher?.name || '未知' }}</span>
                  </div>
                  <div class="info-row">
                    <el-icon><School /></el-icon>
                    <span>{{ favorite.resource?.teacher?.school || '未知学校' }}</span>
                  </div>
                  <div class="info-row">
                    <el-icon><Location /></el-icon>
                    <span>{{ getLocationText(favorite.resource) || '未知地点' }}</span>
                  </div>
                  <div class="info-row">
                    <el-icon><Clock /></el-icon>
                    <span>{{ favorite.resource?.availability || '未知时间' }}</span>
                  </div>
                  <div class="price-row">
                    <span class="price">¥{{ favorite.resource?.pricePerHour || 0 }}/小时</span>
                    <div>
                      <el-button type="primary" size="small" @click="openAppointmentDialog(favorite)">预约</el-button>
                      <el-button type="danger" size="small" @click="removeFavorite(favorite)">取消收藏</el-button>
                    </div>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
          <el-empty v-if="favoriteList.length === 0" description="暂无收藏"></el-empty>
        </div>

        <el-dialog v-model="appointmentDialogVisible" title="预约教师" width="600px">
          <el-form :model="appointmentForm" label-width="100px">
            <el-form-item label="老师姓名">
              <el-input :value="selectedFavorite?.resource?.teacher?.name" disabled></el-input>
            </el-form-item>
            <el-form-item label="学科">
              <el-input :value="selectedFavorite?.resource?.subject" disabled></el-input>
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
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { favoriteApi, appointmentApi } from '../../api/api';
import { getDisplayLocation } from '../../utils/location';
import { User, School, Location, Clock, HomeFilled, EditPen, Calendar, Star, SwitchButton, Comment } from '@element-plus/icons-vue';

export default {
  name: 'StudentFavoritesView',
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
      activeIndex: '5',
      favoriteList: [],
      appointmentDialogVisible: false,
      selectedFavorite: null,
      appointmentForm: {
        appointmentTime: '',
        location: '',
        pricePerHour: null,
        remark: ''
      }
    }
  },
  mounted() {
    this.loadFavorites();
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
    async loadFavorites() {
      try {
        const response = await favoriteApi.getList('TEACHER_JOB_POST');
        this.favoriteList = response || [];
      } catch (error) {
        console.error('加载收藏失败:', error);
        this.$message.error('加载收藏失败');
      }
    },
    async removeFavorite(favorite) {
      try {
        await favoriteApi.remove({
          resourceType: 'TEACHER_JOB_POST',
          resourceId: favorite.resourceId
        });
        this.$message.success('已取消收藏');
        this.loadFavorites();
      } catch (error) {
        console.error('取消收藏失败:', error);
        this.$message.error('取消收藏失败');
      }
    },
    openAppointmentDialog(favorite) {
      this.selectedFavorite = favorite;
      this.appointmentDialogVisible = true;
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
          teacherId: this.selectedFavorite.resource?.teacher?.id,
          subject: this.selectedFavorite.resource?.subject,
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
.student-favorites {
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

.sidebar-menu .el-menu-item.is-active {
  color: #409EFF;
  background-color: #263445;
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

.favorites-container {
  padding: 20px;
  background-color: white;
  border-radius: 8px;
  min-height: calc(100vh - 40px);
  width: 100%;
  box-sizing: border-box;
}

.favorites-container h2 {
  margin-bottom: 20px;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.favorite-card {
  margin-bottom: 20px;
  transition: all 0.3s;
}

.favorite-card:hover {
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
</style>
