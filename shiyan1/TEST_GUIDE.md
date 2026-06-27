# 若依管理系统 - 测试说明文档

## 一、Swagger接口测试步骤

### 1. 访问Swagger文档
- 后端启动后访问: `http://localhost:8080/swagger-ui/index.html`
- 文档标题: 若依管理系统API文档
- 版本: 1.0.0

### 2. 用户管理接口测试

#### 2.1 分页查询用户列表
- 接口: `GET /system/user/page`
- 参数:
  - `pageNum`: 页码（默认1）
  - `pageSize`: 每页数量（默认10）
  - `userName`: 用户名称（可选）
  - `nickName`: 用户昵称（可选）
  - `status`: 状态（可选，0禁用/1启用）
- 测试步骤:
  1. 在Swagger页面找到"用户管理"分组
  2. 点击"分页查询用户"接口
  3. 点击"Try it out"按钮
  4. 输入参数（如pageNum=1, pageSize=10）
  5. 点击"Execute"执行请求
  6. 查看响应结果，确认返回data包含records和total字段

#### 2.2 新增用户
- 接口: `POST /system/user`
- 请求体示例:
```json
{
  "userName": "testuser",
  "nickName": "测试用户",
  "password": "123456",
  "phonenumber": "13800138000",
  "email": "test@example.com",
  "sex": "0",
  "status": "1"
}
```
- 测试步骤:
  1. 点击"新增用户"接口
  2. 输入上述JSON请求体
  3. 执行请求，确认返回code=200

#### 2.3 参数校验测试
- 测试必填字段校验:
  1. 尝试新增用户时不传userName字段
  2. 确认返回错误信息包含"用户名称不能为空"

### 3. 角色管理接口测试

#### 3.1 分页查询角色列表
- 接口: `GET /system/role/page`
- 参数同用户分页查询

#### 3.2 新增角色
- 接口: `POST /system/role`
- 请求体示例:
```json
{
  "roleName": "测试角色",
  "roleKey": "test_role",
  "roleSort": 1,
  "status": "1"
}
```
- 测试唯一性校验:
  1. 新增相同角色名称的角色
  2. 确认返回"角色名称已存在"错误

#### 3.3 查询角色选项列表
- 接口: `GET /system/role/optionselect`
- 用于下拉选择框，返回所有启用状态的角色

### 4. 菜单管理接口测试

#### 4.1 查询菜单树
- 接口: `GET /system/menu/tree`
- 返回树形结构的菜单列表，包含children字段

#### 4.2 新增菜单
- 接口: `POST /system/menu`
- 请求体示例:
```json
{
  "parentId": 0,
  "menuName": "测试菜单",
  "menuType": "2",
  "path": "/test",
  "orderNum": 1,
  "status": "1"
}
```

#### 4.3 测试树形结构
- 查询菜单树后，确认返回数据中children数组包含子菜单

### 5. 部门管理接口测试

#### 5.1 查询部门树
- 接口: `GET /system/dept/tree`
- 返回树形结构的部门列表

#### 5.2 删除部门校验
- 接口: `DELETE /system/dept/{deptIds}`
- 测试步骤:
  1. 尝试删除存在子部门的部门
  2. 确认返回"存在子部门,不允许删除"
  3. 尝试删除存在用户的部门
  4. 确认返回"部门存在用户,不允许删除"

## 二、前端功能验收操作说明

### 1. 启动项目
- 后端: 进入backend目录，执行`mvn spring-boot:run`
- 前端: 进入frontend目录，执行`npm run dev`
- 访问: `http://localhost:5173`

### 2. 用户管理功能验收

#### 2.1 用户列表查询
- 点击左侧菜单"系统管理 > 用户管理"
- 确认页面加载用户列表数据
- 测试分页功能：点击下一页、修改每页数量

#### 2.2 用户搜索
- 输入用户名称搜索
- 选择状态筛选
- 点击重置按钮清空搜索条件

#### 2.3 新增用户
- 点击"新增"按钮
- 填写用户信息（用户名称、昵称、密码等）
- 点击"确定"保存
- 确认列表中显示新增用户

#### 2.4 编辑用户
- 点击用户行的"编辑"按钮
- 修改用户信息
- 保存后确认数据已更新

#### 2.5 删除用户
- 单条删除：点击用户行的"删除"按钮
- 批量删除：勾选多个用户，点击顶部"删除"按钮
- 确认删除提示框显示，点击确定后数据被删除

#### 2.6 重置密码
- 点击用户行的"重置密码"按钮
- 输入新密码
- 确认重置成功提示

#### 2.7 状态切换
- 点击用户行的状态开关
- 确认状态切换成功（启用/禁用）

### 3. 角色管理功能验收

#### 3.1 角色列表
- 点击"角色管理"菜单
- 确认角色列表数据加载

#### 3.2 新增角色
- 点击新增，填写角色名称、权限标识
- 保存后确认列表更新

#### 3.3 编辑角色
- 编辑角色信息，保存确认

#### 3.4 状态切换
- 点击状态开关，确认角色启用/禁用状态变更

### 4. 菜单管理功能验收

#### 4.1 菜单树展示
- 点击"菜单管理"菜单
- 确认树形表格正确展示菜单层级关系

#### 4.2 新增菜单
- 新增顶级菜单（parentId=0）
- 新增子菜单（选择上级菜单）
- 确认树形结构正确更新

#### 4.3 菜单类型
- 测试三种菜单类型：目录、菜单、按钮
- 确认显示正确

### 5. 部门管理功能验收

#### 5.1 部门树展示
- 点击"部门管理"菜单
- 确认树形表格展示部门层级

#### 5.2 新增部门
- 新增顶级部门
- 新增子部门
- 确认树形结构更新

#### 5.3 删除部门
- 尝试删除有子部门的部门，确认提示"存在子部门"
- 先删除子部门，再删除父部门，确认成功

## 三、文件路径汇总

### 后端文件
| 模块 | 文件路径 |
|------|----------|
| Swagger配置 | `backend/src/main/java/com/example/ruoyi/config/SwaggerConfig.java` |
| 用户Controller | `backend/src/main/java/com/example/ruoyi/controller/SysUserController.java` |
| 角色Controller | `backend/src/main/java/com/example/ruoyi/controller/SysRoleController.java` |
| 菜单Controller | `backend/src/main/java/com/example/ruoyi/controller/SysMenuController.java` |
| 部门Controller | `backend/src/main/java/com/example/ruoyi/controller/SysDeptController.java` |
| 用户Entity | `backend/src/main/java/com/example/ruoyi/entity/SysUser.java` |
| 角色Entity | `backend/src/main/java/com/example/ruoyi/entity/SysRole.java` |
| 菜单Entity | `backend/src/main/java/com/example/ruoyi/entity/SysMenu.java` |
| 部门Entity | `backend/src/main/java/com/example/ruoyi/entity/SysDept.java` |

### 前端文件
| 模块 | 文件路径 |
|------|----------|
| 路由配置 | `frontend/src/router/index.js` |
| 布局组件 | `frontend/src/layout/index.vue` |
| 用户API | `frontend/src/api/user.js` |
| 角色API | `frontend/src/api/role.js` |
| 菜单API | `frontend/src/api/menu.js` |
| 部门API | `frontend/src/api/dept.js` |
| 用户页面 | `frontend/src/views/system/User.vue` |
| 角色页面 | `frontend/src/views/system/Role.vue` |
| 菜单页面 | `frontend/src/views/system/Menu.vue` |
| 部门页面 | `frontend/src/views/system/Dept.vue` |

## 四、部署说明

### 后端部署
1. 确保MySQL数据库已启动，并执行init_table.sql初始化表结构
2. 修改application.yml中的数据库连接配置
3. 执行命令:
```bash
cd backend
mvn clean install
mvn spring-boot:run
```
4. 验证: 访问 `http://localhost:8080/swagger-ui/index.html`

### 前端部署
1. 确保后端服务已启动
2. 执行命令:
```bash
cd frontend
npm install
npm run dev
```
3. 验证: 访问 `http://localhost:5173`

### 生产环境部署
1. 后端打包: `mvn clean package`
2. 前端打包: `npm run build`
3. 将backend.jar和frontend/dist部署到服务器

## 五、验收标准

### 后端验收标准
- Swagger文档可正常访问并显示所有接口
- 所有接口返回统一格式Result对象
- 参数校验生效，必填字段不传时返回错误信息
- 分页查询返回records和total字段
- 树形接口返回children数组
- 业务校验生效（用户名唯一、角色名唯一、删除校验等）

### 前端验收标准
- 页面正常加载，无空白或错误
- 列表数据正确展示
- 分页功能正常
- 搜索筛选功能正常
- 新增/编辑/删除功能正常
- 状态切换功能正常
- 表单校验生效
- 树形表格正确展示层级关系