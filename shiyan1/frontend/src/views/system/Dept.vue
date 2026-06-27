/**
 * 部门管理页面
 * 文件路径: frontend/src/views/system/Dept.vue
 */
<template>
  <div class="app-container">
    <el-form :model="queryParams" :inline="true">
      <el-form-item label="部门名称" prop="deptName">
        <el-input v-model="queryParams.deptName" placeholder="请输入部门名称" clearable />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="启用" value="1" />
          <el-option label="禁用" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery">搜索</el-button>
        <el-button type="primary" @click="handleAdd">新增</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="deptList" row-key="deptId" :tree-props="{ children: 'children' }" default-expand-all>
      <el-table-column label="部门名称" prop="deptName" />
      <el-table-column label="负责人" prop="leader" width="100" />
      <el-table-column label="联系电话" prop="phone" width="120" />
      <el-table-column label="排序" prop="orderNum" width="80" />
      <el-table-column label="状态" prop="status" width="80">
        <template #default="scope">
          <el-tag v-if="scope.row.status === '1'" type="success">启用</el-tag>
          <el-tag v-else type="danger">禁用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="scope">
          <el-button link type="primary" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="title" v-model="open" width="500px">
      <el-form ref="deptForm" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="上级部门">
          <el-tree-select v-model="form.parentId" :data="deptOptions" :props="{ value: 'deptId', label: 'deptName' }"
            check-strictly placeholder="请选择上级部门" />
        </el-form-item>
        <el-form-item label="部门名称" prop="deptName">
          <el-input v-model="form.deptName" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="form.leader" placeholder="请输入负责人" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="排序号">
          <el-input-number v-model="form.orderNum" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option label="启用" value="1" />
            <el-option label="禁用" value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="open = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, toRefs } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDeptTree, getDept, addDept, updateDept, deleteDept } from '@/api/dept'

const state = reactive({
  loading: false,
  deptList: [],
  deptOptions: [],
  title: '',
  open: false,
  queryParams: { deptName: undefined, status: undefined },
  form: {},
  rules: { deptName: [{ required: true, message: '部门名称不能为空', trigger: 'blur' }] }
})
const { loading, deptList, deptOptions, title, open, queryParams, form, rules } = toRefs(state)

const getList = async () => {
  state.loading = true
  const res = await getDeptTree(state.queryParams)
  state.deptList = res.data
  state.deptOptions = [{ deptId: 0, deptName: '顶级部门', children: res.data }]
  state.loading = false
}
const handleQuery = () => getList()
const handleAdd = () => { state.form = { parentId: 0, orderNum: 0, status: '1' }; state.open = true; state.title = '新增部门' }
const handleEdit = async (row) => { const res = await getDept(row.deptId); state.form = res.data; state.open = true; state.title = '编辑部门' }
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除?', '警告', { type: 'warning' }).then(async () => {
    await deleteDept(row.deptId)
    getList()
    ElMessage.success('删除成功')
  }).catch(() => {})
}
const submitForm = async () => {
  if (state.form.deptId) { await updateDept(state.form); ElMessage.success('修改成功') }
  else { await addDept(state.form); ElMessage.success('新增成功') }
  state.open = false; getList()
}
getList()
</script>

<style scoped>.app-container { padding: 20px; }</style>