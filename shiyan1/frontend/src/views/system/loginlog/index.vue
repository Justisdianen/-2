<template>
  <div class="login-log-container">
    <!-- 搜索表单 -->
    <el-card class="search-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="登录账号">
          <el-input v-model="queryParams.userName" placeholder="请输入登录账号" clearable />
        </el-form-item>
        <el-form-item label="登录状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable>
            <el-option label="成功" value="0" />
            <el-option label="失败" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="登录时间">
          <el-date-picker
            v-model="queryParams.dateRange"
            type="daterange"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>登录日志列表</span>
          <div class="header-buttons">
            <el-button type="danger" @click="handleClean">清空</el-button>
            <el-button type="warning" @click="handleExport">导出</el-button>
          </div>
        </div>
      </template>
      
      <el-table v-loading="loading" :data="tableData" stripe>
        <el-table-column type="selection" width="55" />
        <el-table-column prop="userName" label="登录账号" width="120" />
        <el-table-column prop="ipaddr" label="登录IP" width="130" />
        <el-table-column prop="loginLocation" label="登录地点" width="150" />
        <el-table-column prop="browser" label="浏览器" width="100" />
        <el-table-column prop="os" label="操作系统" width="100" />
        <el-table-column prop="status" label="登录状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">
              {{ row.status === '0' ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="msg" label="提示消息" min-width="150" />
        <el-table-column prop="loginTime" label="登录时间" width="180" />
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <el-button type="danger" size="small" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="getList"
        @current-change="getList"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { loginLogList, deleteLoginLog, cleanLoginLog, exportLoginLog } from '@/api/log'

const loading = ref(false)
const total = ref(0)
const tableData = ref([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  userName: '',
  status: '',
  dateRange: []
})

// 获取列表
const getList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: queryParams.pageNum,
      pageSize: queryParams.pageSize,
      userName: queryParams.userName,
      status: queryParams.status,
      beginTime: queryParams.dateRange?.[0],
      endTime: queryParams.dateRange?.[1]
    }
    const res = await loginLogList(params)
    if (res.code === 200) {
      tableData.value = res.data.records
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

// 搜索
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置
const resetQuery = () => {
  queryParams.userName = ''
  queryParams.status = ''
  queryParams.dateRange = []
  handleQuery()
}

// 删除
const handleDelete = async (row) => {
  await ElMessageBox.confirm('确认删除该日志?', '警告', { type: 'warning' })
  await deleteLoginLog(row.logId)
  ElMessage.success('删除成功')
  getList()
}

// 清空
const handleClean = async () => {
  await ElMessageBox.confirm('确认清空所有登录日志?', '警告', { type: 'warning' })
  await cleanLoginLog()
  ElMessage.success('清空成功')
  getList()
}

// 导出
const handleExport = async () => {
  await ElMessageBox.confirm('确认导出登录日志?', '提示', { type: 'info' })
  const res = await exportLoginLog()
  if (res.code === 200) {
    ElMessage.success('导出成功，文件名：' + res.data)
  }
}

// 初始化
getList()
</script>

<style scoped>
.login-log-container {
  padding: 20px;
}
.search-card {
  margin-bottom: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-buttons {
  display: flex;
  gap: 10px;
}
</style>