<template>
  <div class="teacher-job-post">
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
      <el-main>
        <div class="job-post-container">
          <h2>{{ editingId ? '编辑求职信息' : '发布求职信息' }}</h2>
          <el-form :model="jobPostForm" :rules="rules" ref="jobPostFormRef" label-width="100px">
            <el-form-item label="标题" prop="title">
              <el-input v-model="jobPostForm.title" placeholder="请输入求职标题" />
            </el-form-item>
            <el-form-item label="描述" prop="description">
              <el-input v-model="jobPostForm.description" type="textarea" rows="4" placeholder="请详细描述求职信息" />
            </el-form-item>
            <el-form-item label="学科" prop="subject">
              <el-select v-model="jobPostForm.subject" placeholder="请选择学科" style="width: 100%">
                <el-option
                  v-for="subject in subjectOptions"
                  :key="subject"
                  :label="subject"
                  :value="subject"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="价格/小时" prop="pricePerHour">
              <el-input-number v-model="jobPostForm.pricePerHour" :min="0" :precision="0" style="width: 100%" />
            </el-form-item>
            <el-form-item label="地区" prop="locationDistrict">
              <LocationSelector
                :province="jobPostForm.locationProvince"
                :city="jobPostForm.locationCity"
                :district="jobPostForm.locationDistrict"
                @update="updateLocation"
              />
            </el-form-item>
            <el-form-item v-if="jobPostForm.locationFormatted" label="已选地区">
              <el-input :model-value="jobPostForm.locationFormatted" disabled />
            </el-form-item>
            <el-form-item label="可用时间" prop="availabilityRange">
              <el-date-picker
                v-model="jobPostForm.availabilityRange"
                type="datetimerange"
                start-placeholder="开始时间"
                end-placeholder="结束时间"
                :value-format="datetimeValueFormat"
                :format="datetimeDisplayFormat"
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="submitJobPost">{{ editingId ? '更新' : '发布求职' }}</el-button>
              <el-button v-if="editingId" @click="cancelEdit">取消</el-button>
            </el-form-item>
          </el-form>

          <h3>我的求职列表</h3>
          <el-table :data="jobPostList" style="width: 100%">
            <el-table-column prop="title" label="标题" width="200" />
            <el-table-column prop="subject" label="学科" />
            <el-table-column prop="pricePerHour" label="价格/小时" />
            <el-table-column label="地区">
              <template #default="{ row }">
                {{ getLocationText(row) }}
              </template>
            </el-table-column>
            <el-table-column prop="availability" label="可用时间" min-width="200" />
            <el-table-column prop="createdAt" label="发布时间" />
            <el-table-column prop="active" label="状态">
              <template #default="{ row }">
                <el-tag :type="row.active ? 'success' : 'danger'">
                  {{ row.active ? '活跃' : '已关闭' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="editJobPost(row)">编辑</el-button>
                <el-button type="danger" size="small" @click="closeJobPost(row.id)" :disabled="!row.active">关闭</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { jobPostApi } from '../../api/api';
import LocationSelector from '../../components/LocationSelector.vue';
import { buildLocationPayload, getDisplayLocation, normalizeLocationFields } from '../../utils/location';
import {
  DATETIME_DISPLAY_FORMAT,
  DATETIME_VALUE_FORMAT,
  SUBJECT_OPTIONS,
  formatDateRangeText,
  parseDateRangeText
} from '../../utils/formOptions';
import { User, HomeFilled, EditPen, Calendar, Star, SwitchButton, Comment, ChatDotRound } from '@element-plus/icons-vue';

function createEmptyJobPostForm() {
  return {
    title: '',
    description: '',
    subject: '',
    pricePerHour: 0,
    location: '',
    locationProvince: '',
    locationCity: '',
    locationDistrict: '',
    locationFormatted: '',
    availability: '',
    availabilityRange: []
  };
}

export default {
  name: 'TeacherJobPostView',
  components: {
    User,
    HomeFilled,
    EditPen,
    Calendar,
    ChatDotRound,
    Star,
    SwitchButton,
    Comment,
    LocationSelector
  },
  data() {
    return {
      activeIndex: '3',
      jobPostForm: createEmptyJobPostForm(),
      subjectOptions: SUBJECT_OPTIONS,
      datetimeValueFormat: DATETIME_VALUE_FORMAT,
      datetimeDisplayFormat: DATETIME_DISPLAY_FORMAT,
      rules: {
        title: [{ required: true, message: '请输入求职标题', trigger: 'blur' }],
        description: [{ required: true, message: '请详细描述求职信息', trigger: 'blur' }],
        subject: [{ required: true, message: '请选择学科', trigger: 'change' }],
        pricePerHour: [{ required: true, message: '请输入每小时价格', trigger: 'change' }],
        locationDistrict: [{ required: true, message: '请选择区县', trigger: 'change' }],
        availabilityRange: [{ required: true, message: '请选择可用时间', trigger: 'change' }]
      },
      jobPostList: [],
      editingId: null
    };
  },
  mounted() {
    this.getJobPostList();
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
    updateLocation(location) {
      Object.assign(this.jobPostForm, {
        location: location.formatted,
        locationProvince: location.province,
        locationCity: location.city,
        locationDistrict: location.district,
        locationFormatted: location.formatted
      });
    },
    getLocationText(item) {
      return getDisplayLocation(item, 'location');
    },
    async getJobPostList() {
      try {
        const response = await jobPostApi.getMyList();
        this.jobPostList = response || [];
      } catch (error) {
        console.error('获取求职列表失败:', error);
        this.$message.error('获取求职列表失败');
      }
    },
    async submitJobPost() {
      this.$refs.jobPostFormRef.validate(async valid => {
        if (!valid) {
          return;
        }

        try {
          const payload = {
            title: this.jobPostForm.title,
            description: this.jobPostForm.description,
            subject: this.jobPostForm.subject,
            pricePerHour: this.jobPostForm.pricePerHour,
            availability: formatDateRangeText(this.jobPostForm.availabilityRange),
            ...buildLocationPayload('location', this.jobPostForm)
          };

          if (this.editingId) {
            await jobPostApi.update({
              id: this.editingId,
              ...payload
            });
            this.$message.success('更新成功');
            this.editingId = null;
          } else {
            await jobPostApi.create(payload);
            this.$message.success('发布成功');
          }

          this.jobPostForm = createEmptyJobPostForm();
          this.$refs.jobPostFormRef.resetFields();
          this.getJobPostList();
        } catch (error) {
          console.error('提交求职失败:', error);
          this.$message.error('操作失败: ' + (error.response?.data?.error || error.message));
        }
      });
    },
    editJobPost(jobPost) {
      this.editingId = jobPost.id;
      this.jobPostForm = {
        ...createEmptyJobPostForm(),
        title: jobPost.title,
        description: jobPost.description,
        subject: jobPost.subject,
        pricePerHour: jobPost.pricePerHour || 0,
        availability: jobPost.availability || '',
        availabilityRange: parseDateRangeText(jobPost.availability),
        ...normalizeLocationFields('location', jobPost)
      };
    },
    cancelEdit() {
      this.editingId = null;
      this.jobPostForm = createEmptyJobPostForm();
      this.$refs.jobPostFormRef.resetFields();
    },
    async closeJobPost(id) {
      try {
        await jobPostApi.deactivate(id);
        this.$message.success('关闭成功');
        this.getJobPostList();
      } catch (error) {
        console.error('关闭求职失败:', error);
        this.$message.error('关闭求职失败');
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
.teacher-job-post {
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

.job-post-container {
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  min-height: calc(100vh - 40px);
  width: 100%;
  box-sizing: border-box;
}

.el-form {
  width: 100%;
}

.el-form-item {
  margin-bottom: 20px;
}

.job-post-container h2 {
  margin-bottom: 20px;
  color: #409EFF;
}

.job-post-container h3 {
  margin: 30px 0 20px 0;
  color: #303133;
}
</style>
