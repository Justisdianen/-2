/**
 * 用户管理页面
 * 文件路径: frontend/src/views/system/User.vue
 */
<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch">
      <el-form-item label="用户名称" prop="userName">
        <el-input v-model="queryParams.userName" placeholder="请输入用户名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="用户昵称" prop="nickName">
        <el-input v-model="queryParams.nickName" placeholder="请输入用户昵称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="启用" value="1" />
          <el-option label="禁用" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery">搜索</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" @click="handleImport">导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" @click="handleExport">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="用户编号" prop="userId" width="100" />
      <el-table-column label="用户名称" prop="userName" :show-overflow-tooltip="true" />
      <el-table-column label="用户昵称" prop="nickName" :show-overflow-tooltip="true" />
      <el-table-column label="手机号码" prop="phonenumber" width="120" />
      <el-table-column label="状态" prop="status" width="80">
        <template #default="scope">
          <el-switch v-model="scope.row.status" active-value="1" inactive-value="0"
            @change="handleStatusChange(scope.row)" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="160">
        <template #default="scope">
          {{ scope.row.createTime ? scope.row.createTime.replace('T', ' ') : '' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center">
        <template #default="scope">
          <el-button link type="primary" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button link type="primary" @click="handleResetPwd(scope.row)">重置密码</el-button>
          <el-button link type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination v-show="total > 0" :total="total" :page="queryParams.pageNum" :page-size="queryParams.pageSize"
      :page-sizes="[10, 20, 30, 50]" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
      @current-change="handleCurrentChange" />

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="userForm" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="用户名称" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入用户名称" :disabled="form.userId !== undefined" />
        </el-form-item>
        <el-form-item label="用户昵称" prop="nickName">
          <el-input v-model="form.nickName" placeholder="请输入用户昵称" />
        </el-form-item>
        <el-form-item label="手机号码" prop="phonenumber">
          <el-input v-model="form.phonenumber" placeholder="请输入手机号码" maxlength="11" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="50" />
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-select v-model="form.sex" placeholder="请选择性别">
            <el-option label="男" value="0" />
            <el-option label="女" value="1" />
            <el-option label="未知" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option label="启用" value="1" />
            <el-option label="禁用" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.userId === undefined" label="密码" prop="password">
          <el-input v-model="form.password" placeholder="请输入密码" type="password" maxlength="20" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="open = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 重置密码对话框 -->
    <el-dialog title="重置密码" v-model="resetPwdOpen" width="400px" append-to-body>
      <el-form ref="resetPwdForm" :model="resetPwdForm" :rules="resetPwdRules" label-width="100px">
        <el-form-item label="用户名称">
          <el-input v-model="resetPwdForm.userName" disabled />
        </el-form-item>
        <el-form-item label="新密码" prop="password">
          <el-input v-model="resetPwdForm.password" placeholder="请输入新密码" type="password" maxlength="20"
            show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resetPwdOpen = false">取消</el-button>
        <el-button type="primary" @click="submitResetPwd">确定</el-button>
      </template>
    </el-dialog>

    <!-- 用户导入对话框 -->
    <el-dialog title="用户导入" v-model="importOpen" width="400px" append-to-body>
      <el-upload
        ref="uploadRef"
        :limit="1"
        accept=".xlsx, .xls"
        :headers="uploadHeaders"
        :action="uploadUrl"
        :disabled="uploadDisabled"
        :on-progress="handleUploadProgress"
        :on-success="handleUploadSuccess"
        :on-error="handleUploadError"
        :auto-upload="false"
        drag
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip text-center">
            <div class="el-upload__tip">
              <el-checkbox v-model="updateSupport" />是否更新已经存在的用户数据
            </div>
            <span>仅允许导入xls、xlsx格式文件。</span>
            <el-link type="primary" :underline="false" style="font-size:12px;vertical-align: baseline;" @click="importTemplate">下载模板</el-link>
          </div>
        </template>
      </el-upload>
      <template #footer>
        <el-button @click="importOpen = false">取消</el-button>
        <el-button type="primary" @click="submitUpload">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, toRefs, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { getUserPage, getUser, addUser, updateUser, deleteUser, resetUserPwd, changeUserStatus, exportUser, importUser, importUserTemplate } from '@/api/user'

// 数据状态
const state = reactive({
  loading: false,
  showSearch: true,
  multiple: true,
  total: 0,
  userList: [],
  title: '',
  open: false,
  resetPwdOpen: false,
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    userName: undefined,
    nickName: undefined,
    status: undefined
  },
  form: {},
  rules: {
    userName: [{ required: true, message: '用户名称不能为空', trigger: 'blur' }],
    nickName: [{ required: true, message: '用户昵称不能为空', trigger: 'blur' }],
    password: [{ required: true, message: '密码不能为空', trigger: 'blur' }]
  },
  resetPwdForm: {},
  resetPwdRules: {
    password: [{ required: true, message: '新密码不能为空', trigger: 'blur' }]
  },
  ids: [],
  importOpen: false,
  updateSupport: 0,
  uploadDisabled: false
})

const { loading, showSearch, multiple, total, userList, title, open, resetPwdOpen, queryParams, form, rules, resetPwdForm, resetPwdRules } = toRefs(state)

// 导入相关
const uploadRef = ref()
const uploadUrl = computed(() => '/system/user/import?updateSupport=' + state.updateSupport)
const uploadHeaders = { Authorization: 'Bearer ' + localStorage.getItem('token') }

// 查询用户列表
const getList = async () => {
  state.loading = true
  try {
    const res = await getUserPage(state.queryParams)
    state.userList = res.data.records
    state.total = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    state.loading = false
  }
}

// 搜索按钮操作
const handleQuery = () => {
  state.queryParams.pageNum = 1
  getList()
}

// 重置按钮操作
const resetQuery = () => {
  state.queryParams = {
    pageNum: 1,
    pageSize: 10,
    userName: undefined,
    nickName: undefined,
    status: undefined
  }
  handleQuery()
}

// 多选框选中数据
const handleSelectionChange = (selection) => {
  state.ids = selection.map(item => item.userId)
  state.multiple = !selection.length
}

// 新增按钮操作
const handleAdd = () => {
  reset()
  state.open = true
  state.title = '新增用户'
}

// 编辑按钮操作
const handleEdit = async (row) => {
  reset()
  try {
    const res = await getUser(row.userId)
    state.form = res.data
    state.open = true
    state.title = '编辑用户'
  } catch (error) {
    console.error(error)
  }
}

// 删除按钮操作
const handleDelete = (row) => {
  const userIds = row.userId ? [row.userId] : state.ids
  ElMessageBox.confirm('是否确认删除用户编号为"' + userIds.join(',') + '"的数据项?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteUser(userIds.join(','))
    getList()
    ElMessage.success('删除成功')
  }).catch(() => {})
}

// 重置密码按钮操作
const handleResetPwd = (row) => {
  state.resetPwdForm = {
    userId: row.userId,
    userName: row.userName,
    password: ''
  }
  state.resetPwdOpen = true
}

// 提交重置密码
const submitResetPwd = async () => {
  await resetUserPwd(state.resetPwdForm)
  ElMessage.success('重置密码成功')
  state.resetPwdOpen = false
}

// 用户状态修改
const handleStatusChange = async (row) => {
  const text = row.status === '1' ? '启用' : '禁用'
  try {
    await changeUserStatus({ userId: row.userId, status: row.status })
    ElMessage.success('已成功"' + text + '"用户"' + row.userName + '"')
  } catch (error) {
    row.status = row.status === '1' ? '0' : '1'
  }
}

// 提交按钮
const submitForm = async () => {
  try {
    if (state.form.userId !== undefined) {
      await updateUser(state.form)
      ElMessage.success('修改成功')
    } else {
      await addUser(state.form)
      ElMessage.success('新增成功')
    }
    state.open = false
    getList()
  } catch (error) {
    console.error(error)
  }
}

// 分页相关
const handleSizeChange = (val) => {
  state.queryParams.pageSize = val
  getList()
}

const handleCurrentChange = (val) => {
  state.queryParams.pageNum = val
  getList()
}

// 表单重置
const reset = () => {
  state.form = {
    userId: undefined,
    userName: undefined,
    nickName: undefined,
    password: undefined,
    phonenumber: undefined,
    email: undefined,
    sex: '0',
    status: '1'
  }
}

// 导入按钮操作
const handleImport = () => {
  state.importOpen = true
}

// 导出按钮操作
const handleExport = () => {
  exportUser(state.queryParams).then(res => {
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '用户数据.xlsx'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  })
}

// 下载模板
const importTemplate = () => {
  importUserTemplate().then(res => {
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '用户导入模板.xlsx'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  })
}

// 提交上传
const submitUpload = () => {
  uploadRef.value.submit()
}

// 上传进度
const handleUploadProgress = () => {
  state.uploadDisabled = true
}

// 上传成功
const handleUploadSuccess = (res) => {
  state.importOpen = false
  state.uploadDisabled = false
  ElMessage.success(res.msg)
  getList()
}

// 上传失败
const handleUploadError = () => {
  state.uploadDisabled = false
  ElMessage.error('导入失败')
}

// 初始化
getList()
</script>

<style scoped>
.app-container {
  padding: 20px;
}
</style>