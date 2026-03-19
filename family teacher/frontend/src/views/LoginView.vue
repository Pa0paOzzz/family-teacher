<template>
  <div class="login">
    <el-container>
      <el-main>
        <div class="login-form-container">
          <h2>用户登录</h2>
          <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-width="80px">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="loginForm.username" placeholder="请输入用户名"></el-input>
            </el-form-item>
            <el-form-item label="密码" prop="password">
              <el-input v-model="loginForm.password" type="password" placeholder="请输入密码"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="login">登录</el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
            <el-form-item>
              <span>还没有账号？</span>
              <el-link type="primary" @click="goToRegister">立即注册</el-link>
            </el-form-item>
          </el-form>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { userApi } from '../api/api';

export default {
  name: 'LoginView',
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    async login() {
      this.$refs.loginFormRef.validate(async (valid) => {
        if (valid) {
          try {
            const loginData = {
              username: this.loginForm.username,
              password: this.loginForm.password
            };
            const response = await userApi.login(loginData);
            
            if (response && response.token) {
              localStorage.setItem('token', response.token);
              localStorage.setItem('username', response.username);
              localStorage.setItem('role', response.role);
              this.$message.success('登录成功');
              
              if (response.role === 'STUDENT') {
                this.$router.push('/student/home');
              } else if (response.role === 'TEACHER') {
                this.$router.push('/teacher/home');
              } else if (response.role === 'ADMIN') {
                this.$router.push('/admin/dashboard');
              } else {
                this.$router.push('/');
              }
            } else {
              this.$message.error('登录失败，请检查用户名和密码');
            }
          } catch (error) {
            console.error('登录失败:', error);
            this.$message.error('登录失败，请检查网络连接或稍后再试');
          }
        } else {
          console.log('登录表单验证失败');
          return false;
        }
      });
    },
    resetForm() {
      this.$refs.loginFormRef.resetFields();
    },
    goToRegister() {
      this.$router.push('/register');
    }
  }
}
</script>

<style scoped>
.login {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.login-form-container {
  width: 400px;
  margin: 100px auto;
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.login-form-container h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #409EFF;
}

.el-form-item {
  margin-bottom: 20px;
}
</style>
