<template>
  <div class="teacher-favorites">
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
                    <span>{{ favorite.resource?.student?.name || '未知' }}</span>
                  </div>
                  <div class="info-row">
                    <el-icon><School /></el-icon>
                    <span>{{ favorite.resource?.student?.school || '未知学校' }}</span>
                  </div>
                  <div class="info-row">
                    <el-icon><Location /></el-icon>
                    <span>{{ favorite.resource?.location || '未知地点' }}</span>
                  </div>
                  <div class="info-row">
                    <el-icon><Clock /></el-icon>
                    <span>{{ favorite.resource?.preferredTime || '未知时间' }}</span>
                  </div>
                  <div class="price-row">
                    <span class="price">¥{{ favorite.resource?.budgetPerHour || 0 }}/小时</span>
                    <div>
                      <el-button type="primary" size="small" @click="openContactDialog(favorite)">联系学生</el-button>
                      <el-button type="danger" size="small" @click="removeFavorite(favorite)">取消收藏</el-button>
                    </div>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
          <el-empty v-if="favoriteList.length === 0" description="暂无收藏"></el-empty>
        </div>
        
        <el-dialog v-model="contactDialogVisible" title="联系学生" width="500px">
          <el-form :model="contactForm" label-width="100px">
            <el-form-item label="学生姓名">
              <el-input :value="selectedFavorite?.resource?.student?.name" disabled></el-input>
            </el-form-item>
            <el-form-item label="学科">
              <el-input :value="selectedFavorite?.resource?.subject" disabled></el-input>
            </el-form-item>
            <el-form-item label="联系方式">
              <el-input v-model="contactForm.contact" placeholder="请输入您的联系方式"></el-input>
            </el-form-item>
            <el-form-item label="留言">
              <el-input v-model="contactForm.message" type="textarea" rows="3" placeholder="请输入留言内容"></el-input>
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="contactDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitContact">发送</el-button>
          </template>
        </el-dialog>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { favoriteApi } from '../../api/api';
import { User, School, Location, Clock, HomeFilled, EditPen, Calendar, Star, SwitchButton } from '@element-plus/icons-vue';

export default {
  name: 'TeacherFavoritesView',
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
      activeIndex: '5',
      favoriteList: [],
      contactDialogVisible: false,
      selectedFavorite: null,
      contactForm: {
        contact: '',
        message: ''
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
    async loadFavorites() {
      try {
        const response = await favoriteApi.getList('STUDENT_TUTORING_REQUEST');
        this.favoriteList = response || [];
      } catch (error) {
        console.error('加载收藏失败:', error);
        this.$message.error('加载收藏失败');
      }
    },
    async removeFavorite(favorite) {
      try {
        await favoriteApi.remove({
          resourceType: 'STUDENT_TUTORING_REQUEST',
          resourceId: favorite.resourceId
        });
        this.$message.success('已取消收藏');
        this.loadFavorites();
      } catch (error) {
        console.error('取消收藏失败:', error);
        this.$message.error('取消收藏失败');
      }
    },
    openContactDialog(favorite) {
      this.selectedFavorite = favorite;
      this.contactDialogVisible = true;
    },
    async submitContact() {
      if (!this.contactForm.contact) {
        this.$message.warning('请输入联系方式');
        return;
      }
      this.$message.success('已发送联系信息');
      this.contactDialogVisible = false;
      this.contactForm = {
        contact: '',
        message: ''
      };
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
.teacher-favorites {
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
}

.favorite-card {
  margin-bottom: 20px;
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
}

.price-row {
  margin-top: 15px;
  padding-top: 10px;
  border-top: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  font-size: 20px;
  font-weight: bold;
  color: #67c23a;
}
</style>
