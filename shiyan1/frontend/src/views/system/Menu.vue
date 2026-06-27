/**
 * 菜单管理页面
 * 文件路径: frontend/src/views/system/Menu.vue
 */
<template>
  <div class="app-container">
    <el-form :model="queryParams" :inline="true">
      <el-form-item label="菜单名称" prop="menuName">
        <el-input v-model="queryParams.menuName" placeholder="请输入菜单名称" clearable />
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

    <el-table v-loading="loading" :data="menuList" row-key="menuId" :tree-props="{ children: 'children' }">
      <el-table-column label="菜单名称" prop="menuName" />
      <el-table-column label="图标" prop="icon" width="80" />
      <el-table-column label="排序" prop="orderNum" width="80" />
      <el-table-column label="路由地址" prop="path" />
      <el-table-column label="菜单类型" prop="menuType" width="80">
        <template #default="scope">
          <span v-if="scope.row.menuType === '1'">目录</span>
          <span v-else-if="scope.row.menuType === '2'">菜单</span>
          <span v-else>按钮</span>
        </template>
      </el-table-column>
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

    <el-dialog :title="title" v-model="open" width="600px">
      <el-form ref="menuForm" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="上级菜单">
          <el-tree-select v-model="form.parentId" :data="menuOptions" :props="{ value: 'menuId', label: 'menuName' }"
            check-strictly placeholder="请选择上级菜单" />
        </el-form-item>
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="菜单类型" prop="menuType">
          <el-radio-group v-model="form.menuType">
            <el-radio value="1">目录</el-radio>
            <el-radio value="2">菜单</el-radio>
            <el-radio value="3">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="路由地址" prop="path">
          <el-input v-model="form.path" placeholder="请输入路由地址" />
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
import { getMenuTree, getMenu, addMenu, updateMenu, deleteMenu } from '@/api/menu'

const state = reactive({
  loading: false,
  menuList: [],
  menuOptions: [],
  title: '',
  open: false,
  queryParams: { menuName: undefined, status: undefined },
  form: {},
  rules: { menuName: [{ required: true, message: '菜单名称不能为空', trigger: 'blur' }] }
})
const { loading, menuList, menuOptions, title, open, queryParams, form, rules } = toRefs(state)

const getList = async () => {
  state.loading = true
  const res = await getMenuTree(state.queryParams)
  state.menuList = res.data
  state.menuOptions = [{ menuId: 0, menuName: '主类目', children: res.data }]
  state.loading = false
}
const handleQuery = () => getList()
const handleAdd = () => { state.form = { parentId: 0, menuType: '1', orderNum: 0, status: '1' }; state.open = true; state.title = '新增菜单' }
const handleEdit = async (row) => { const res = await getMenu(row.menuId); state.form = res.data; state.open = true; state.title = '编辑菜单' }
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除?', '警告', { type: 'warning' }).then(async () => {
    await deleteMenu(row.menuId)
    getList()
    ElMessage.success('删除成功')
  }).catch(() => {})
}
const submitForm = async () => {
  if (state.form.menuId) { await updateMenu(state.form); ElMessage.success('修改成功') }
  else { await addMenu(state.form); ElMessage.success('新增成功') }
  state.open = false; getList()
}
getList()
</script>

<style scoped>.app-container { padding: 20px; }</style>