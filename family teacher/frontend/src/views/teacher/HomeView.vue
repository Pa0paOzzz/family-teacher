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

      <el-main class="main-content">
        <div class="search-container">
          <el-select
            v-model="searchSubject"
            placeholder="选择学科"
            clearable
            class="subject-select"
          >
            <el-option
              v-for="subject in subjectOptions"
              :key="subject"
              :label="subject"
              :value="subject"
            />
          </el-select>
          <LocationSelector
            :province="searchLocation.province"
            :city="searchLocation.city"
            :district="searchLocation.district"
            @update="updateSearchLocation"
          />
          <el-button type="primary" @click="searchRequests">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </div>

        <div class="requests-container">
          <h2>学生需求信息</h2>
          <el-row :gutter="20">
            <el-col v-for="request in requestList" :key="request.id" :span="8">
              <el-card class="request-card" shadow="hover">
                <template #header>
                  <div class="card-header">
                    <span class="title">{{ request.title }}</span>
                    <el-tag type="primary">{{ request.subject }}</el-tag>
                  </div>
                </template>

                <div class="card-content">
                  <p class="description">{{ request.description }}</p>
                  <div v-if="request.recommendationReason" class="recommendation-reason">
                    推荐理由：{{ request.recommendationReason }}
                  </div>
                  <div class="info-row">
                    <el-icon><User /></el-icon>
                    <span>{{ getStudentName(request) }}</span>
                  </div>
                  <div class="info-row">
                    <el-icon><School /></el-icon>
                    <span>{{ request.student?.school || '未知学校' }}</span>
                  </div>
                  <div class="info-row">
                    <el-icon><Location /></el-icon>
                    <span>{{ getLocationText(request) || '未填写地区' }}</span>
                  </div>
                  <div class="info-row">
                    <el-icon><Clock /></el-icon>
                    <span>{{ request.preferredTime || '未填写时间' }}</span>
                  </div>
                  <div class="budget-row">
                    <span class="budget">预算: ¥{{ request.budgetPerHour || 0 }}/小时</span>
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
                      <el-button type="primary" size="small" @click="openContactDialog(request)">预约学生</el-button>
                    </div>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <el-empty v-if="requestList.length === 0" :description="emptyDescription" />
        </div>

        <el-dialog v-model="contactDialogVisible" title="预约学生" width="600px">
          <el-form :model="contactForm" label-width="100px">
            <el-form-item label="学生姓名">
              <el-input :value="getStudentName(selectedRequest)" disabled />
            </el-form-item>
            <el-form-item label="学科">
              <el-input :value="selectedRequest?.subject" disabled />
            </el-form-item>
            <el-form-item label="预约时间" required>
              <el-date-picker
                v-model="contactForm.appointmentTime"
                type="datetime"
                placeholder="选择预约时间"
                :value-format="datetimeValueFormat"
                :format="datetimeDisplayFormat"
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item label="上课地区" required>
              <LocationSelector
                :province="contactForm.locationProvince"
                :city="contactForm.locationCity"
                :district="contactForm.locationDistrict"
                @update="updateAppointmentLocation"
              />
            </el-form-item>
            <el-form-item v-if="contactForm.locationFormatted" label="已选地区">
              <el-input :model-value="contactForm.locationFormatted" disabled />
            </el-form-item>
            <el-form-item label="价格/小时" required>
              <el-input-number
                v-model="contactForm.pricePerHour"
                :min="0"
                :precision="2"
                :step="10"
                style="width: 100%"
                placeholder="请输入每小时价格"
              />
            </el-form-item>
            <el-form-item label="留言">
              <el-input
                v-model="contactForm.message"
                type="textarea"
                rows="3"
                placeholder="请输入留言内容"
              />
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
              {{ getStudentName(selectedRequest) }}
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
            <el-descriptions-item label="地区" :span="2">
              {{ getLocationText(selectedRequest) || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="推荐理由" :span="2">
              {{ selectedRequest?.recommendationReason || '近期新发布' }}
            </el-descriptions-item>
            <el-descriptions-item label="期望时间" :span="2">
              {{ selectedRequest?.preferredTime || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="详细地址" :span="2">
              {{ getAddressText(selectedRequest?.student) || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="需求描述" :span="2">
              <div class="detail-text">{{ selectedRequest?.description || '暂无描述' }}</div>
            </el-descriptions-item>
          </el-descriptions>

          <template #footer>
            <el-button @click="detailDialogVisible = false">关闭</el-button>
            <el-button type="primary" @click="contactFromDetail">立即预约</el-button>
          </template>
        </el-dialog>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { recommendationApi, tutoringRequestApi, appointmentApi, favoriteApi, userApi } from '../../api/api';
import LocationSelector from '../../components/LocationSelector.vue';
import { getDisplayLocation, normalizeLocationFields } from '../../utils/location';
import { DATETIME_DISPLAY_FORMAT, DATETIME_VALUE_FORMAT, SUBJECT_OPTIONS } from '../../utils/formOptions';
import {
  User,
  School,
  Location,
  Clock,
  ChatDotRound,
  HomeFilled,
  EditPen,
  Calendar,
  Star,
  SwitchButton,
  Comment
} from '@element-plus/icons-vue';

function createContactForm() {
  return {
    appointmentTime: '',
    location: '',
    locationProvince: '',
    locationCity: '',
    locationDistrict: '',
    locationFormatted: '',
    pricePerHour: null,
    message: ''
  };
}

export default {
  name: 'TeacherHomeView',
  components: {
    User,
    School,
    Location,
    Clock,
    ChatDotRound,
    HomeFilled,
    EditPen,
    Calendar,
    Star,
    SwitchButton,
    Comment,
    LocationSelector
  },
  data() {
    return {
      activeIndex: '1',
      searchSubject: '',
      subjectOptions: SUBJECT_OPTIONS,
      datetimeValueFormat: DATETIME_VALUE_FORMAT,
      datetimeDisplayFormat: DATETIME_DISPLAY_FORMAT,
      searchLocation: {
        province: '',
        city: '',
        district: ''
      },
      requestList: [],
      favoriteIds: [],
      favoriteLoadingIds: [],
      contactDialogVisible: false,
      detailDialogVisible: false,
      selectedRequest: null,
      emptyDescription: '暂无学生推荐',
      contactForm: createContactForm()
    };
  },
  mounted() {
    this.loadRequests();
  },
  methods: {
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
    getStudentName(item) {
      return item?.student?.user?.name || item?.student?.name || '未知';
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
        this.emptyDescription = profile?.addressCity
          ? '暂无学生推荐'
          : '请先在个人资料中填写所在城市，以便优先获取同城学生推荐';
      } catch (error) {
        console.error('加载学生推荐信息失败:', error);
        this.$message.error('加载学生推荐信息失败');
      }
    },
    async searchRequests() {
      if (!this.hasSearchFilters()) {
        this.loadRequests();
        return;
      }
      try {
        const response = await tutoringRequestApi.getList();
        this.requestList = (response || []).filter(request =>
          this.matchesSubject(request.subject) && this.matchesLocation(request)
        );
        this.emptyDescription = '暂无学生需求信息';
      } catch (error) {
        console.error('搜索失败:', error);
        this.$message.error('搜索失败');
      }
    },
    resetSearch() {
      this.searchSubject = '';
      this.searchLocation = {
        province: '',
        city: '',
        district: ''
      };
      this.loadRequests();
    },
    updateSearchLocation(location) {
      this.searchLocation = {
        province: location.province,
        city: location.city,
        district: location.district
      };
    },
    hasSearchFilters() {
      return Boolean(
        this.searchSubject ||
        this.searchLocation.province ||
        this.searchLocation.city ||
        this.searchLocation.district
      );
    },
    matchesSubject(subject) {
      return !this.searchSubject || subject === this.searchSubject;
    },
    matchesLocation(item) {
      const { province, city, district } = this.searchLocation;
      if (!province && !city && !district) {
        return true;
      }
      const normalized = normalizeLocationFields('location', item);
      return (!province || normalized.locationProvince === province)
        && (!city || normalized.locationCity === city)
        && (!district || normalized.locationDistrict === district);
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
      const location = normalizeLocationFields('location', request || {});
      this.contactForm = {
        appointmentTime: '',
        location: location.locationFormatted || location.location || '',
        locationProvince: location.locationProvince || '',
        locationCity: location.locationCity || '',
        locationDistrict: location.locationDistrict || '',
        locationFormatted: location.locationFormatted || '',
        pricePerHour: request?.budgetPerHour || null,
        message: ''
      };
      this.contactDialogVisible = true;
    },
    updateAppointmentLocation(location) {
      Object.assign(this.contactForm, {
        location: location.formatted,
        locationProvince: location.province,
        locationCity: location.city,
        locationDistrict: location.district,
        locationFormatted: location.formatted
      });
    },
    openDetailDialog(request) {
      this.selectedRequest = request;
      this.detailDialogVisible = true;
    },
    contactFromDetail() {
      if (!this.selectedRequest) {
        return;
      }
      this.detailDialogVisible = false;
      this.openContactDialog(this.selectedRequest);
    },
    async submitAppointment() {
      if (!this.contactForm.appointmentTime) {
        this.$message.warning('请选择预约时间');
        return;
      }
      if (!this.contactForm.locationDistrict) {
        this.$message.warning('请选择上课地区');
        return;
      }
      if (this.contactForm.pricePerHour == null || this.contactForm.pricePerHour <= 0) {
        this.$message.warning('请输入有效的价格');
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
        this.contactForm = createContactForm();
      } catch (error) {
        console.error('预约失败:', error);
        this.$message.error('预约失败，请稍后重试');
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

.main-content {
  padding: 20px;
  margin-left: 0;
  overflow-y: auto;
}

.search-container {
  margin-bottom: 30px;
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.subject-select {
  width: 200px;
}

.requests-container {
  background-color: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgb(0 0 0 / 10%);
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
  box-shadow: 0 4px 20px rgb(0 0 0 / 15%);
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

.description {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 15px;
  min-height: 45px;
}

.recommendation-reason {
  margin-bottom: 12px;
  padding: 8px 10px;
  border-radius: 6px;
  background: #f0f9eb;
  color: #67c23a;
  font-size: 13px;
  line-height: 1.5;
}

.info-row {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  color: #606266;
}

.info-row .el-icon {
  margin-right: 8px;
  color: #909399;
}

.budget-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
}

.budget {
  font-size: 18px;
  font-weight: bold;
  color: #67c23a;
}

.action-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.detail-text {
  white-space: pre-wrap;
  line-height: 1.7;
}
</style>
