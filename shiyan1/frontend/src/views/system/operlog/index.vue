<template>
  <div class="oper-log-container">
    <!-- 搜索表单 -->
    <el-card class="search-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="操作模块">
          <el-input v-model="queryParams.title" placeholder="请输入模块名称" clearable />
        </el-form-item>
        <el-form-item label="操作人员">
          <el-input v-model="queryParams.operName" placeholder="请输入操作人员" clearable />
        </el-form-item>
        <el-form-item label="操作状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable>
            <el-option label="成功" value="0" />
            <el-option label="失败" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作时间">
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
          <span>操作日志列表</span>
          <div class="header-buttons">
            <el-button type="danger" @click="handleClean">清空</el-button>
            <el-button type="warning" @click="handleExport">导出</el-button>
          </div>
        </div>
      </template>
      
      <el-table v-loading="loading" :data="tableData" stripe>
        <el-table-column type="selection" width="55" />
        <el-table-column prop="title" label="操作模块" width="100" />
        <el-table-column prop="businessType" label="业务类型" width="80">
          <template #default="{ row }">
            <el-tag :type="getBusinessTypeTag(row.businessType)">
              {{ getBusinessTypeLabel(row.businessType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="requestMethod" label="请求方式" width="80">
          <template #default="{ row }">
            <el-tag :type="row.requestMethod === 'GET' ? '' : row.requestMethod === 'POST' ? 'success' : row.requestMethod === 'PUT' ? 'warning' : 'danger'">
              {{ row.requestMethod }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operName" label="操作人员" width="100" />
        <el-table-column prop="operIp" label="主机地址" width="130" />
        <el-table-column prop="operUrl" label="请求URL" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="操作状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">
              {{ row.status === '0' ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="costTime" label="耗时(ms)" width="80" />
        <el-table-column prop="operTime" label="操作时间" width="180" />
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleView(row)">详情</el-button>
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

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="操作日志详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="操作模块">{{ detailData.title }}</el-descriptions-item>
        <el-descriptions-item label="业务类型">{{ getBusinessTypeLabel(detailData.businessType) }}</el-descriptions-item>
        <el-descriptions-item label="请求方式">{{ detailData.requestMethod }}</el-descriptions-item>
        <el-descriptions-item label="操作人员">{{ detailData.operName }}</el-descriptions-item>
        <el-descriptions-item label="主机地址">{{ detailData.operIp }}</el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ detailData.operTime }}</el-descriptions-item>
        <el-descriptions-item label="请求URL" :span="2">{{ detailData.operUrl }}</el-descriptions-item>
        <el-descriptions-item label="请求参数" :span="2">
          <el-input type="textarea" :rows="4" :model-value="detailData.operParam" readonly />
        </el-descriptions-item>
        <el-descriptions-item label="响应结果" :span="2">
          <el-input type="textarea" :rows="4" :model-value="detailData.jsonResult" readonly />
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { operLogList, deleteOperLog, cleanOperLog, exportOperLog } from '@/api/log'

const loading = ref(false)
const total = ref(0)
const tableData = ref([])
const detailVisible = ref(false)
const detailData = ref({})

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  title: '',
  operName: '',
  status: '',
  dateRange: []
})

// 业务类型映射
const businessTypeMap = {
  '0': { label: '其它', tag: 'info' },
  '1': { label: '新增', tag: 'success' },
  '2': { label: '修改', tag: 'warning' },
  '3': { label: '删除', tag: 'danger' },
  '4': { label: '授权', tag: '' },
  '5': { label: '导出', tag: 'warning' },
  '6': { label: '导入', tag: 'success' },
  '7': { label: '强退', tag: 'danger' },
  '8': { label: '清空', tag: 'danger' }
}

const getBusinessTypeLabel = (code) => businessTypeMap[code]?.label || '未知'
const getBusinessTypeTag = (code) => businessTypeMap[code]?.tag || 'info'

// 获取列表
const getList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: queryParams.pageNum,
      pageSize: queryParams.pageSize,
      title: queryParams.title,
      operName: queryParams.operName,
      status: queryParams.status,
      beginTime: queryParams.dateRange?.[0],
      endTime: queryParams.dateRange?.[1]
    }
    const res = await operLogList(params)
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
  queryParams.title = ''
  queryParams.operName = ''
  queryParams.status = ''
  queryParams.dateRange = []
  handleQuery()
}

// 查看详情
const handleView = (row) => {
  detailData.value = row
  detailVisible.value = true
}

// 清空
const handleClean = async () => {
  await ElMessageBox.confirm('确认清空所有操作日志?', '警告', { type: 'warning' })
  await cleanOperLog()
  ElMessage.success('清空成功')
  getList()
}

// 导出
const handleExport = async () => {
  await ElMessageBox.confirm('确认导出操作日志?', '提示', { type: 'info' })
  const res = await exportOperLog()
  if (res.code === 200) {
    ElMessage.success('导出成功，文件名：' + res.data)
  }
}

// 初始化
getList()
</script>

<style scoped>
.oper-log-container {
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