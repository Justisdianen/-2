/**
 * 首页
 * 文件路径: frontend/src/views/Home.vue
 */
<template>
  <div class="home-container">
    <el-card class="welcome-card">
      <h1>欢迎使用若依管理系统</h1>
      <p class="description">基于 SpringBoot3 + Vue3 + Element Plus 构建的企业级后台管理框架</p>
      <div class="features">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card shadow="hover" class="feature-card" @click="handleNavigate('/system/user')">
              <div class="feature-item">
                <el-icon :size="48" color="#409eff"><User /></el-icon>
                <span>用户管理</span>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="feature-card" @click="handleNavigate('/system/role')">
              <div class="feature-item">
                <el-icon :size="48" color="#409eff"><UserFilled /></el-icon>
                <span>角色管理</span>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="feature-card" @click="handleNavigate('/system/menu')">
              <div class="feature-item">
                <el-icon :size="48" color="#409eff"><Menu /></el-icon>
                <span>菜单管理</span>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="feature-card" @click="handleNavigate('/system/dept')">
              <div class="feature-item">
                <el-icon :size="48" color="#409eff"><OfficeBuilding /></el-icon>
                <span>部门管理</span>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { User, UserFilled, Menu, OfficeBuilding } from '@element-plus/icons-vue'

const router = useRouter()

const handleNavigate = (path) => {
  router.push(path)
}
</script>

<style scoped>
.home-container {
  padding: 20px;
}

.welcome-card {
  text-align: center;
}

.welcome-card h1 {
  font-size: 32px;
  margin-bottom: 16px;
  color: #303133;
}

.description {
  font-size: 16px;
  color: #606266;
  margin-bottom: 30px;
}

.feature-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
}

.feature-item span {
  font-size: 16px;
  color: #303133;
  margin-top: 12px;
}
</style>