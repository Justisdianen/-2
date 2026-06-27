<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <!-- 左侧用户信息卡片 -->
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>个人信息</span>
          </template>
          <div class="user-info">
            <div class="avatar-wrapper">
              <el-avatar :size="100" :src="avatarUrl">
                <el-icon :size="50"><User /></el-icon>
              </el-avatar>
              <el-upload
                class="avatar-upload"
                action="/system/user/profile/avatar"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload"
              >
                <el-button type="primary" size="small">更换头像</el-button>
              </el-upload>
            </div>
            <el-divider />
            <el-descriptions :column="1" border>
              <el-descriptions-item label="用户名">{{ userStore.user?.userName }}</el-descriptions-item>
              <el-descriptions-item label="昵称">{{ userStore.user?.nickName }}</el-descriptions-item>
              <el-descriptions-item label="手机号">{{ userStore.user?.phonenumber || '未设置' }}</el-descriptions-item>
              <el-descriptions-item label="邮箱">{{ userStore.user?.email || '未设置' }}</el-descriptions-item>
              <el-descriptions-item label="部门">{{ userStore.user?.dept?.deptName || '未分配' }}</el-descriptions-item>
              <el-descriptions-item label="角色">{{ userStore.user?.roles?.map(r => r.roleName).join(', ') || '未分配' }}</el-descriptions-item>
              <el-descriptions-item label="创建时间">{{ userStore.user?.createTime }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧操作区域 -->
      <el-col :span="16">
        <!-- 修改密码 -->
        <el-card>
          <template #header>
            <span>修改密码</span>
          </template>
          <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="100px">
            <el-form-item label="旧密码" prop="oldPassword">
              <el-input v-model="pwdForm.oldPassword" type="password" placeholder="请输入旧密码" show-password />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="pwdForm.newPassword" type="password" placeholder="请输入新密码" show-password />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="pwdForm.confirmPassword" type="password" placeholder="请确认新密码" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdatePwd">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 基本信息 -->
        <el-card style="margin-top: 20px;">
          <template #header>
            <span>基本信息</span>
          </template>
          <el-form ref="infoFormRef" :model="infoForm" :rules="infoRules" label-width="100px">
            <el-form-item label="昵称" prop="nickName">
              <el-input v-model="infoForm.nickName" placeholder="请输入昵称" />
            </el-form-item>
            <el-form-item label="手机号" prop="phonenumber">
              <el-input v-model="infoForm.phonenumber" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="infoForm.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdateInfo">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { User } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { updateUserInfo, updateUserPwd } from '@/api/user'

const userStore = useUserStore()

const avatarUrl = computed(() => userStore.user?.avatar || '')

// 修改密码表单
const pwdFormRef = ref(null)
const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPwd = (rule, value, callback) => {
  if (value !== pwdForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度6-20位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPwd, trigger: 'blur' }
  ]
}

// 基本信息表单
const infoFormRef = ref(null)
const infoForm = reactive({
  nickName: userStore.user?.nickName || '',
  phonenumber: userStore.user?.phonenumber || '',
  email: userStore.user?.email || ''
})

const infoRules = {
  nickName: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  phonenumber: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱', trigger: 'blur' }
  ]
}

// 修改密码
const handleUpdatePwd = async () => {
  await pwdFormRef.value.validate()
  await updateUserPwd(pwdForm)
  ElMessage.success('密码修改成功，请重新登录')
  userStore.handleLogout()
}

// 修改基本信息
const handleUpdateInfo = async () => {
  await infoFormRef.value.validate()
  await updateUserInfo(infoForm)
  ElMessage.success('修改成功')
  // 更新用户信息
  userStore.user.nickName = infoForm.nickName
  userStore.user.phonenumber = infoForm.phonenumber
  userStore.user.email = infoForm.email
}

// 上传头像成功
const handleAvatarSuccess = (res) => {
  if (res.code === 200) {
    userStore.user.avatar = res.data
    ElMessage.success('头像上传成功')
  }
}

// 上传前校验
const beforeAvatarUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isJPG) {
    ElMessage.error('上传头像图片只能是 JPG/PNG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
  }
  return isJPG && isLt2M
}
</script>

<style scoped>
.profile-container {
  padding: 20px;
}
.user-info {
  text-align: center;
}
.avatar-wrapper {
  margin-bottom: 20px;
}
.avatar-upload {
  margin-top: 10px;
}
</style>