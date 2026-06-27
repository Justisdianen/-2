<template>
  <el-container class="layout-container">
    <el-aside width="200px">
      <div class="logo">
        <h2>若依管理系统</h2>
      </div>
      <el-menu :default-active="activeMenu" router>
        <el-menu-item index="/home">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-sub-menu index="/system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/system/user">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/system/role">
            <el-icon><UserFilled /></el-icon>
            <span>角色管理</span>
          </el-menu-item>
          <el-menu-item index="/system/menu">
            <el-icon><Menu /></el-icon>
            <span>菜单管理</span>
          </el-menu-item>
          <el-menu-item index="/system/dept">
            <el-icon><OfficeBuilding /></el-icon>
            <span>部门管理</span>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header>
        <div class="header-title">
          <span>{{ currentRouteTitle }}</span>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar size="medium" icon="UserFilled" />
              <span>{{ userStore.user?.nickName || userStore.user?.userName || '用户' }}</span>
              <el-icon class="arrow-icon"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><Setting /></el-icon>
                  <span>个人设置</span>
                </el-dropdown-item>
                <el-dropdown-divider />
                <el-dropdown-item command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  <span>退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  HomeFilled, Setting, User, UserFilled, Menu, OfficeBuilding, 
  ArrowDown, SwitchButton 
} from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => {
  const route = router.currentRoute.value
  return route.path
})

const currentRouteTitle = computed(() => {
  const route = router.currentRoute.value
  return route.meta?.title || '首页'
})

const handleCommand = async (command) => {
  if (command === 'profile') {
    ElMessage.info('个人设置功能开发中')
  } else if (command === 'logout') {
    try {
      await ElMessageBox.confirm(
        '确定要退出当前账号吗？',
        '退出确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
      
      await userStore.handleLogout()
      ElMessage.success('退出成功')
    } catch (error) {
      if (error !== 'cancel') {
        ElMessage.error('退出失败')
      }
    }
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.el-aside {
  background-color: #304156;
  color: #fff;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b3a4d;
}

.logo h2 {
  color: #fff;
  font-size: 18px;
  margin: 0;
}

.el-menu {
  border-right: none;
  background-color: #304156;
}

.el-menu-item,
.el-sub-menu__title {
  color: #bfcbd9;
}

.el-menu-item:hover,
.el-sub-menu__title:hover {
  background-color: #263445;
}

.el-menu-item.is-active {
  color: #409EFF;
  background-color: #263445;
}

.el-header {
  background-color: #fff;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-title {
  font-size: 18px;
  font-weight: bold;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.user-info span {
  margin-left: 8px;
  font-size: 14px;
  color: #303133;
}

.arrow-icon {
  margin-left: 4px;
  font-size: 12px;
  color: #909399;
}

.el-main {
  background-color: #f0f2f5;
  padding: 0;
}
</style>