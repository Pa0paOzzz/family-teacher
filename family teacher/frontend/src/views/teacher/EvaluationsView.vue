<template>
  <div class="teacher-evaluations">
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
        <div class="evaluations-container">
          <h2>我的评价</h2>
          <el-tabs type="border-card">
            <el-tab-pane label="我发出的评价">
              <el-empty v-if="myEvaluations.length === 0" description="暂未发出评价"></el-empty>
              <el-card v-for="evaluation in myEvaluations" :key="evaluation.id" class="evaluation-card" shadow="hover">
                <div class="evaluation-header">
                  <span class="evaluated-name">评价对象：{{ evaluation.evaluatedName }}</span>
                  <el-tag type="success" size="small">{{ evaluation.subject }}</el-tag>
                </div>
                <div class="evaluation-ratings">
                  <div class="rating-row">
                    <span class="rating-label">教学质量：</span>
                    <el-rate v-model="evaluation.teachingQuality" disabled></el-rate>
                  </div>
                  <div class="rating-row">
                    <span class="rating-label">服务态度：</span>
                    <el-rate v-model="evaluation.attitude" disabled></el-rate>
                  </div>
                  <div class="rating-row">
                    <span class="rating-label">满意度：</span>
                    <el-rate v-model="evaluation.satisfaction" disabled></el-rate>
                  </div>
                </div>
                <div class="evaluation-comment" v-if="evaluation.comment">
                  <span class="comment-label">评价内容：</span>
                  <span>{{ evaluation.comment }}</span>
                </div>
                <div class="evaluation-footer">
                  <span class="evaluation-date">{{ evaluation.createdAt }}</span>
                </div>
              </el-card>
            </el-tab-pane>
            <el-tab-pane label="我收到的评价">
              <el-empty v-if="receivedEvaluations.length === 0" description="暂未收到评价"></el-empty>
              <el-card v-for="evaluation in receivedEvaluations" :key="evaluation.id" class="evaluation-card" shadow="hover">
                <div class="evaluation-header">
                  <span class="evaluator-name">评价人：{{ evaluation.evaluatorName }}</span>
                  <el-tag type="success" size="small">{{ evaluation.subject }}</el-tag>
                </div>
                <div class="evaluation-ratings">
                  <div class="rating-row">
                    <span class="rating-label">教学质量：</span>
                    <el-rate v-model="evaluation.teachingQuality" disabled></el-rate>
                  </div>
                  <div class="rating-row">
                    <span class="rating-label">服务态度：</span>
                    <el-rate v-model="evaluation.attitude" disabled></el-rate>
                  </div>
                  <div class="rating-row">
                    <span class="rating-label">满意度：</span>
                    <el-rate v-model="evaluation.satisfaction" disabled></el-rate>
                  </div>
                </div>
                <div class="evaluation-comment" v-if="evaluation.comment">
                  <span class="comment-label">评价内容：</span>
                  <span>{{ evaluation.comment }}</span>
                </div>
                <div class="evaluation-footer">
                  <span class="evaluation-date">{{ evaluation.createdAt }}</span>
                </div>
              </el-card>
            </el-tab-pane>
          </el-tabs>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { evaluationApi } from '../../api/api';
import { User, HomeFilled, EditPen, Calendar, Star, SwitchButton, Comment, ChatDotRound } from '@element-plus/icons-vue';

export default {
  name: 'TeacherEvaluationsView',
  components: {
    User,
    HomeFilled,
    EditPen,
    Calendar,
    ChatDotRound,
    Star,
    SwitchButton,
    Comment
  },
  data() {
    return {
      activeIndex: '7',
      myEvaluations: [],
      receivedEvaluations: []
    }
  },
  mounted() {
    this.loadEvaluations();
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
    async loadEvaluations() {
      try {
        const [myEvals, receivedEvals] = await Promise.all([
          evaluationApi.myEvaluations(),
          evaluationApi.receivedEvaluations()
        ]);
        this.myEvaluations = myEvals || [];
        this.receivedEvaluations = receivedEvals || [];
      } catch (error) {
        console.error('加载评价列表失败:', error);
        this.$message.error('加载评价列表失败');
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
.teacher-evaluations {
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

.evaluations-container {
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  min-height: calc(100vh - 40px);
}

.evaluations-container h2 {
  margin-bottom: 30px;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.evaluation-card {
  margin-bottom: 20px;
}

.evaluation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.evaluated-name,
.evaluator-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.evaluation-ratings {
  margin-bottom: 15px;
}

.rating-row {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.rating-label {
  width: 80px;
  color: #606266;
  font-size: 14px;
}

.evaluation-comment {
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 15px;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.comment-label {
  font-weight: 600;
  color: #303133;
  margin-right: 8px;
}

.evaluation-footer {
  text-align: right;
}

.evaluation-date {
  font-size: 12px;
  color: #909399;
}
</style>
