<template>
  <div class="teacher-profile">
    <el-container style="height: 100vh;">
      <el-aside width="200px" class="sidebar">
        <div class="logo">
          <h2>家教平台</h2>
          <p>教师端</p>
        </div>
        <el-menu :default-active="activeIndex" class="sidebar-menu" @select="handleMenuSelect">
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

      <el-main>
        <div class="profile-container">
          <h2>个人资料</h2>

          <div v-if="!isEditing" class="profile-display">
            <el-descriptions :column="1" border>
              <el-descriptions-item label="用户名">{{ teacherForm.username }}</el-descriptions-item>
              <el-descriptions-item label="姓名">{{ teacherForm.name }}</el-descriptions-item>
              <el-descriptions-item label="邮箱">{{ teacherForm.email }}</el-descriptions-item>
              <el-descriptions-item label="手机号">{{ teacherForm.phone }}</el-descriptions-item>
              <el-descriptions-item label="学校">{{ teacherForm.school }}</el-descriptions-item>
              <el-descriptions-item label="专业">{{ teacherForm.major }}</el-descriptions-item>
              <el-descriptions-item label="学历">{{ teacherForm.education }}</el-descriptions-item>
              <el-descriptions-item label="教学经验">{{ teacherForm.teachingExperience }}</el-descriptions-item>
              <el-descriptions-item label="擅长学科">{{ teacherForm.subject }}</el-descriptions-item>
              <el-descriptions-item label="个人简介">{{ teacherForm.bio }}</el-descriptions-item>
              <el-descriptions-item label="价格/小时">¥{{ teacherForm.pricePerHour }}</el-descriptions-item>
              <el-descriptions-item label="地区">
                {{ teacherForm.addressFormatted || teacherForm.address }}
              </el-descriptions-item>
            </el-descriptions>
            <div class="button-group">
              <el-button type="primary" @click="startEditing">修改个人信息</el-button>
            </div>
          </div>

          <el-form
            v-else
            ref="teacherFormRef"
            :model="teacherForm"
            :rules="rules"
            label-width="100px"
          >
            <el-form-item label="用户名">
              <el-input v-model="teacherForm.username" disabled />
            </el-form-item>
            <el-form-item label="姓名" prop="name">
              <el-input v-model="teacherForm.name" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="teacherForm.email" />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="teacherForm.phone" />
            </el-form-item>
            <el-form-item label="学校" prop="school">
              <el-input v-model="teacherForm.school" />
            </el-form-item>
            <el-form-item label="专业" prop="major">
              <el-input v-model="teacherForm.major" />
            </el-form-item>
            <el-form-item label="学历" prop="education">
              <el-input v-model="teacherForm.education" />
            </el-form-item>
            <el-form-item label="教学经验" prop="teachingExperience">
              <el-input v-model="teacherForm.teachingExperience" type="textarea" rows="3" />
            </el-form-item>
            <el-form-item label="擅长学科" prop="subject">
              <el-select v-model="teacherForm.subject" placeholder="请选择学科" style="width: 100%">
                <el-option
                  v-for="subject in subjectOptions"
                  :key="subject"
                  :label="subject"
                  :value="subject"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="个人简介" prop="bio">
              <el-input v-model="teacherForm.bio" type="textarea" rows="4" />
            </el-form-item>
            <el-form-item label="价格/小时" prop="pricePerHour">
              <el-input-number v-model="teacherForm.pricePerHour" :min="0" :precision="0" style="width: 100%" />
            </el-form-item>
            <el-form-item label="地区" prop="addressDistrict">
              <LocationSelector
                :province="teacherForm.addressProvince"
                :city="teacherForm.addressCity"
                :district="teacherForm.addressDistrict"
                @update="updateAddress"
              />
            </el-form-item>
            <el-form-item v-if="teacherForm.addressFormatted" label="已选地区">
              <el-input :model-value="teacherForm.addressFormatted" disabled />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveProfile">保存修改</el-button>
              <el-button @click="cancelEdit">取消</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { userApi } from '../../api/api';
import LocationSelector from '../../components/LocationSelector.vue';
import { SUBJECT_OPTIONS } from '../../utils/formOptions';
import { buildLocationPayload, normalizeLocationFields } from '../../utils/location';
import { User, HomeFilled, EditPen, Calendar, Star, SwitchButton, Comment } from '@element-plus/icons-vue';

function createEmptyTeacherForm() {
  return {
    username: '',
    name: '',
    email: '',
    phone: '',
    school: '',
    major: '',
    education: '',
    teachingExperience: '',
    subject: '',
    bio: '',
    pricePerHour: 0,
    address: '',
    addressProvince: '',
    addressCity: '',
    addressDistrict: '',
    addressFormatted: ''
  };
}

export default {
  name: 'TeacherProfileView',
  components: {
    User,
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
      activeIndex: '2',
      isEditing: false,
      originalForm: null,
      subjectOptions: SUBJECT_OPTIONS,
      teacherForm: createEmptyTeacherForm(),
      rules: {
        name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ],
        phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
        school: [{ required: true, message: '请输入学校', trigger: 'blur' }],
        major: [{ required: true, message: '请输入专业', trigger: 'blur' }],
        education: [{ required: true, message: '请输入学历', trigger: 'blur' }],
        teachingExperience: [{ required: true, message: '请输入教学经验', trigger: 'blur' }],
        subject: [{ required: true, message: '请选择擅长学科', trigger: 'change' }],
        bio: [{ required: true, message: '请输入个人简介', trigger: 'blur' }],
        pricePerHour: [{ required: true, message: '请输入价格/小时', trigger: 'change' }],
        addressDistrict: [{ required: true, message: '请选择地区', trigger: 'change' }]
      }
    };
  },
  mounted() {
    this.loadProfile();
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
    updateAddress(location) {
      Object.assign(this.teacherForm, {
        address: location.formatted,
        addressProvince: location.province,
        addressCity: location.city,
        addressDistrict: location.district,
        addressFormatted: location.formatted
      });
    },
    startEditing() {
      this.originalForm = { ...this.teacherForm };
      this.isEditing = true;
    },
    cancelEdit() {
      if (this.originalForm) {
        this.teacherForm = { ...this.originalForm };
      }
      this.isEditing = false;
    },
    async loadProfile() {
      try {
        const response = await userApi.getTeacherProfile();
        this.teacherForm = {
          ...createEmptyTeacherForm(),
          username: response.username || '',
          name: response.name || '',
          email: response.email || '',
          phone: response.phone || '',
          school: response.school || '',
          major: response.major || '',
          education: response.education || '',
          teachingExperience: response.teachingExperience || '',
          subject: response.subject || '',
          bio: response.bio || '',
          pricePerHour: response.pricePerHour || 0,
          ...normalizeLocationFields('address', response)
        };
      } catch (error) {
        console.error('加载个人资料失败:', error);
        this.$message.error('加载个人资料失败');
      }
    },
    async saveProfile() {
      this.$refs.teacherFormRef.validate(async valid => {
        if (!valid) {
          return;
        }

        try {
          await userApi.updateTeacherProfile({
            name: this.teacherForm.name,
            email: this.teacherForm.email,
            phone: this.teacherForm.phone,
            school: this.teacherForm.school,
            major: this.teacherForm.major,
            education: this.teacherForm.education,
            teachingExperience: this.teacherForm.teachingExperience,
            subject: this.teacherForm.subject,
            bio: this.teacherForm.bio,
            pricePerHour: this.teacherForm.pricePerHour,
            ...buildLocationPayload('address', this.teacherForm)
          });
          this.$message.success('保存成功');
          this.isEditing = false;
          await this.loadProfile();
        } catch (error) {
          console.error('保存个人资料失败:', error);
          this.$message.error('保存失败，请稍后重试');
        }
      });
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
.teacher-profile {
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

.profile-container {
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  min-height: calc(100vh - 40px);
}

.profile-display {
  max-width: 720px;
  margin: 0 auto;
}

.button-group {
  margin-top: 20px;
  text-align: center;
}
</style>
