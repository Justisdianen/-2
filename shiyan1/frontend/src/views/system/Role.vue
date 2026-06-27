/**
 * 角色管理页面
 * 文件路径: frontend/src/views/system/Role.vue
 */
<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true">
      <el-form-item label="角色名称" prop="roleName">
        <el-input v-model="queryParams.roleName" placeholder="请输入角色名称" clearable />
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
        <el-button type="primary" @click="handleAdd">新增</el-button>
        <el-button type="danger" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="roleList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column label="角色编号" prop="roleId" width="100" />
      <el-table-column label="角色名称" prop="roleName" />
      <el-table-column label="权限标识" prop="roleKey" />
      <el-table-column label="排序" prop="roleSort" width="80" />
      <el-table-column label="状态" prop="status" width="80">
        <template #default="scope">
          <el-switch v-model="scope.row.status" active-value="1" inactive-value="0" @change="handleStatusChange(scope.row)" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="scope">
          <el-button link type="primary" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination v-show="total > 0" :total="total" :page="queryParams.pageNum" :page-size="queryParams.pageSize"
      layout="total, sizes, prev, pager, next" @size-change="getList" @current-change="getList" />

    <el-dialog :title="title" v-model="open" width="500px">
      <el-form ref="roleForm" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="权限标识" prop="roleKey">
          <el-input v-model="form.roleKey" placeholder="请输入权限标识" />
        </el-form-item>
        <el-form-item label="排序号" prop="roleSort">
          <el-input-number v-model="form.roleSort" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
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
import { getRolePage, getRole, addRole, updateRole, deleteRole, changeRoleStatus } from '@/api/role'

const state = reactive({
  loading: false,
  multiple: true,
  total: 0,
  roleList: [],
  title: '',
  open: false,
  queryParams: { pageNum: 1, pageSize: 10, roleName: undefined, status: undefined },
  form: {},
  rules: {
    roleName: [{ required: true, message: '角色名称不能为空', trigger: 'blur' }],
    roleKey: [{ required: true, message: '权限标识不能为空', trigger: 'blur' }]
  },
  ids: []
})
const { loading, multiple, total, roleList, title, open, queryParams, form, rules } = toRefs(state)

const getList = async () => {
  state.loading = true
  const res = await getRolePage(state.queryParams)
  state.roleList = res.data.records
  state.total = res.data.total
  state.loading = false
}

const handleQuery = () => { state.queryParams.pageNum = 1; getList() }
const resetQuery = () => { state.queryParams = { pageNum: 1, pageSize: 10 }; getList() }
const handleSelectionChange = (selection) => { state.ids = selection.map(item => item.roleId); state.multiple = !selection.length }
const handleAdd = () => { state.form = { roleSort: 0, status: '1' }; state.open = true; state.title = '新增角色' }
const handleEdit = async (row) => { const res = await getRole(row.roleId); state.form = res.data; state.open = true; state.title = '编辑角色' }
const handleDelete = (row) => {
  const roleIds = row.roleId ? [row.roleId] : state.ids
  ElMessageBox.confirm('确认删除?', '警告', { type: 'warning' }).then(async () => {
    await deleteRole(roleIds.join(','))
    getList()
    ElMessage.success('删除成功')
  }).catch(() => {})
}
const handleStatusChange = async (row) => {
  await changeRoleStatus({ roleId: row.roleId, status: row.status })
  ElMessage.success('状态修改成功')
}
const submitForm = async () => {
  if (state.form.roleId) { await updateRole(state.form); ElMessage.success('修改成功') }
  else { await addRole(state.form); ElMessage.success('新增成功') }
  state.open = false; getList()
}
getList()
</script>

<style scoped>.app-container { padding: 20px; }</style>