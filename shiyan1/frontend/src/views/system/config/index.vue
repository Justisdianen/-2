<template>
  <div class="config-container">
    <!-- 搜索表单 -->
    <el-card class="search-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="参数名称">
          <el-input v-model="queryParams.configName" placeholder="请输入参数名称" clearable />
        </el-form-item>
        <el-form-item label="参数键名">
          <el-input v-model="queryParams.configKey" placeholder="请输入参数键名" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="primary" @click="handleAdd">新增</el-button>
          <el-button type="info" @click="handleRefreshCache">刷新缓存</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <el-table v-loading="loading" :data="tableData" stripe>
        <el-table-column type="selection" width="55" />
        <el-table-column prop="configId" label="参数主键" width="80" />
        <el-table-column prop="configName" label="参数名称" width="150" />
        <el-table-column prop="configKey" label="参数键名" width="200" />
        <el-table-column prop="configValue" label="参数键值" min-width="150" show-overflow-tooltip />
        <el-table-column prop="configType" label="系统内置" width="80">
          <template #default="{ row }">
            <el-tag :type="row.configType === 'Y' ? 'danger' : 'success'">
              {{ row.configType === 'Y' ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" width="150" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleEdit(row)">修改</el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row)" :disabled="row.configType === 'Y'">删除</el-button>
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

    <!-- 新增/修改弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="参数名称" prop="configName">
          <el-input v-model="formData.configName" placeholder="请输入参数名称" />
        </el-form-item>
        <el-form-item label="参数键名" prop="configKey">
          <el-input v-model="formData.configKey" placeholder="请输入参数键名" />
        </el-form-item>
        <el-form-item label="参数键值" prop="configValue">
          <el-input v-model="formData.configValue" placeholder="请输入参数键值" />
        </el-form-item>
        <el-form-item label="系统内置" prop="configType">
          <el-radio-group v-model="formData.configType">
            <el-radio value="Y">是</el-radio>
            <el-radio value="N">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { configList, addConfig, updateConfig, deleteConfig, refreshConfigCache } from '@/api/log'

const loading = ref(false)
const total = ref(0)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  configName: '',
  configKey: ''
})

const formData = reactive({
  configId: null,
  configName: '',
  configKey: '',
  configValue: '',
  configType: 'N',
  remark: ''
})

const rules = {
  configName: [{ required: true, message: '请输入参数名称', trigger: 'blur' }],
  configKey: [{ required: true, message: '请输入参数键名', trigger: 'blur' }],
  configValue: [{ required: true, message: '请输入参数键值', trigger: 'blur' }]
}

// 获取列表
const getList = async () => {
  loading.value = true
  try {
    const res = await configList(queryParams)
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
  queryParams.configName = ''
  queryParams.configKey = ''
  handleQuery()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增参数'
  Object.assign(formData, {
    configId: null,
    configName: '',
    configKey: '',
    configValue: '',
    configType: 'N',
    remark: ''
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '修改参数'
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 提交
const handleSubmit = async () => {
  await formRef.value.validate()
  if (formData.configId) {
    await updateConfig(formData)
    ElMessage.success('修改成功')
  } else {
    await addConfig(formData)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  getList()
}

// 删除
const handleDelete = async (row) => {
  if (row.configType === 'Y') {
    ElMessage.warning('系统内置参数不能删除')
    return
  }
  await ElMessageBox.confirm('确认删除该参数?', '警告', { type: 'warning' })
  await deleteConfig(row.configId)
  ElMessage.success('删除成功')
  getList()
}

// 刷新缓存
const handleRefreshCache = async () => {
  await refreshConfigCache()
  ElMessage.success('刷新缓存成功')
}

// 初始化
getList()
</script>

<style scoped>
.config-container {
  padding: 20px;
}
.search-card {
  margin-bottom: 20px;
}
</style>