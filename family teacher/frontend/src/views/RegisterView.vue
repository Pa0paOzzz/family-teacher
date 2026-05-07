<template>
  <div class="register">
    <el-container>
      <el-main>
        <div class="register-form-container">
          <h2>用户注册</h2>
          <el-form :model="registerForm" :rules="rules" ref="registerFormRef" label-width="80px">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="registerForm.username" placeholder="请输入用户名"></el-input>
            </el-form-item>
            <el-form-item label="密码" prop="password">
              <el-input v-model="registerForm.password" type="password" placeholder="请输入密码"></el-input>
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请确认密码"></el-input>
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="registerForm.email" placeholder="请输入邮箱"></el-input>
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="registerForm.phone" placeholder="请输入手机号"></el-input>
            </el-form-item>
            <el-form-item label="姓名" prop="name">
              <el-input v-model="registerForm.name" placeholder="请输入姓名"></el-input>
            </el-form-item>
            <el-form-item label="角色">
              <el-radio-group v-model="registerForm.role">
                <el-radio label="STUDENT">学生</el-radio>
                <el-radio label="TEACHER">家教老师</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="register">注册</el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
            <el-form-item>
              <span>已有账号？</span>
              <el-link type="primary" @click="goToLogin">立即登录</el-link>
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
  name: 'RegisterView',
  data() {
    return {
      registerForm: {
        username: '',
        password: '',
        confirmPassword: '',
        email: '',
        phone: '',
        name: '',
        role: 'STUDENT'
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              if (value !== this.registerForm.password) {
                callback(new Error('两次输入密码不一致'));
              } else {
                callback();
              }
            },
            trigger: 'blur'
          }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    async register() {
      this.$refs.registerFormRef.validate(async (valid) => {
        if (valid) {
          try {
            // 准备注册数据
            const registerData = {
              username: this.registerForm.username,
              password: this.registerForm.password,
              email: this.registerForm.email,
              phone: this.registerForm.phone,
              name: this.registerForm.name,
              role: this.registerForm.role
            };
            
            const response = await userApi.register(registerData);
            
            if (response && response.success) {
              this.$message.success('注册成功，请登录');
              this.$router.push('/login');
            } else {
              // 显示后端返回的具体错误信息
              this.$message.error(response.error || '注册失败，请稍后再试');
            }
          } catch (error) {
            console.error('注册失败:', error);
            if (error.response && error.response.data) {
              this.$message.error(error.response.data.error || '注册失败，请检查网络连接');
            } else {
              this.$message.error('注册失败，请检查网络连接');
            }
          }
        } else {
          console.log('注册表单验证失败');
          return false;
        }
      });
    },
    resetForm() {
      this.$refs.registerFormRef.resetFields();
    },
    goToLogin() {
      this.$router.push('/login');
    }
  }
}
</script>

<style scoped>
.register {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.register-form-container {
  width: 500px;
  margin: 50px auto;
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.register-form-container h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #409EFF;
}

.el-form-item {
  margin-bottom: 20px;
}
</style>
