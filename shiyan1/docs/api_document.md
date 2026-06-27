### 接口文档

#### 一、接口访问说明

**基础地址**:
- 开发环境: `http://localhost:8080`
- 生产环境: 通过Nginx反向代理访问

**Swagger文档地址**:
- `http://localhost:8080/swagger-ui/index.html`

**接口前缀**:
- 所有接口统一前缀: `/api`

**认证方式**:
- 使用Bearer Token认证
- 请求头: `Authorization: Bearer {token}`

#### 二、统一返回结果格式

```json
{
    "code": 200,
    "msg": "操作成功",
    "data": {}
}
```

**返回码说明**:

| 返回码 | 含义 | 说明 |
| :--- | :--- | :--- |
| 200 | 成功 | 操作成功 |
| 400 | 请求参数错误 | 参数校验失败或参数格式错误 |
| 401 | 未授权 | 未登录或Token已过期 |
| 403 | 禁止访问 | 无权限访问该资源 |
| 500 | 服务器错误 | 系统内部异常 |

#### 三、用户管理接口

##### 3.1 查询用户列表（分页）

- **URL**: `GET /api/user/list`
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| pageNum | INT | 是 | 页码 |
| pageSize | INT | 是 | 每页数量 |
| userName | STRING | 否 | 用户名（模糊查询） |
| nickName | STRING | 否 | 用户昵称（模糊查询） |
| status | STRING | 否 | 状态（0正常 1停用） |

- **响应示例**:

```json
{
    "code": 200,
    "msg": "操作成功",
    "data": {
        "records": [
            {
                "userId": 1,
                "deptId": 1,
                "userName": "admin",
                "nickName": "管理员",
                "status": "0",
                "createTime": "2024-01-01 10:00:00"
            }
        ],
        "total": 1,
        "size": 10,
        "current": 1,
        "pages": 1
    }
}
```

##### 3.2 查询用户详情

- **URL**: `GET /api/user/{userId}`
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| userId | LONG | 是 | 用户ID |

##### 3.3 新增用户

- **URL**: `POST /api/user`
- **请求体**:

```json
{
    "userName": "testuser",
    "nickName": "测试用户",
    "password": "123456",
    "deptId": 1,
    "status": "0"
}
```

| 参数名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| userName | STRING | 是 | 用户名（3-30字符） |
| nickName | STRING | 是 | 用户昵称 |
| password | STRING | 是 | 密码（6-20字符） |
| deptId | LONG | 否 | 部门ID |
| status | STRING | 否 | 状态，默认0正常 |

##### 3.4 编辑用户

- **URL**: `PUT /api/user`
- **请求体**:

```json
{
    "userId": 2,
    "userName": "testuser",
    "nickName": "测试用户",
    "deptId": 2,
    "status": "1"
}
```

##### 3.5 删除用户

- **URL**: `DELETE /api/user/{userId}`

##### 3.6 批量删除用户

- **URL**: `DELETE /api/user/batch`
- **请求体**:

```json
{
    "userIds": [1, 2, 3]
}
```

#### 四、角色管理接口

##### 4.1 查询角色列表

- **URL**: `GET /api/role/list`
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| pageNum | INT | 是 | 页码 |
| pageSize | INT | 是 | 每页数量 |
| roleName | STRING | 否 | 角色名称（模糊查询） |
| status | STRING | 否 | 状态 |

##### 4.2 查询角色详情

- **URL**: `GET /api/role/{roleId}`

##### 4.3 新增角色

- **URL**: `POST /api/role`
- **请求体**:

```json
{
    "roleName": "测试角色",
    "roleKey": "test_role",
    "sort": 1,
    "status": "0"
}
```

##### 4.4 编辑角色

- **URL**: `PUT /api/role`

##### 4.5 删除角色

- **URL**: `DELETE /api/role/{roleId}`

##### 4.6 批量删除角色

- **URL**: `DELETE /api/role/batch`

#### 五、菜单管理接口

##### 5.1 查询菜单列表

- **URL**: `GET /api/menu/list`

##### 5.2 查询菜单树形列表

- **URL**: `GET /api/menu/tree`

- **响应示例**:

```json
{
    "code": 200,
    "msg": "操作成功",
    "data": [
        {
            "menuId": 1,
            "menuName": "系统管理",
            "parentId": 0,
            "children": [
                {
                    "menuId": 2,
                    "menuName": "用户管理",
                    "parentId": 1,
                    "children": []
                }
            ]
        }
    ]
}
```

##### 5.3 查询菜单详情

- **URL**: `GET /api/menu/{menuId}`

##### 5.4 新增菜单

- **URL**: `POST /api/menu`
- **请求体**:

```json
{
    "menuName": "测试菜单",
    "parentId": 1,
    "orderNum": 1,
    "path": "/test",
    "component": "test/index",
    "menuType": "C",
    "visible": "0",
    "status": "0"
}
```

##### 5.5 编辑菜单

- **URL**: `PUT /api/menu`

##### 5.6 删除菜单

- **URL**: `DELETE /api/menu/{menuId}`

#### 六、部门管理接口

##### 6.1 查询部门列表

- **URL**: `GET /api/dept/list`

##### 6.2 查询部门树形列表

- **URL**: `GET /api/dept/tree`

##### 6.3 查询部门详情

- **URL**: `GET /api/dept/{deptId}`

##### 6.4 新增部门

- **URL**: `POST /api/dept`
- **请求体**:

```json
{
    "deptName": "测试部门",
    "parentId": 0,
    "orderNum": 1,
    "leader": "张三",
    "phone": "13800138000",
    "email": "zhangsan@example.com",
    "status": "0"
}
```

##### 6.5 编辑部门

- **URL**: `PUT /api/dept`

##### 6.6 删除部门

- **URL**: `DELETE /api/dept/{deptId}`

##### 6.7 批量删除部门

- **URL**: `DELETE /api/dept/batch`

#### 七、错误响应示例

##### 7.1 参数校验失败

```json
{
    "code": 400,
    "msg": "参数校验失败",
    "data": {
        "userName": "用户名长度必须在3-30之间",
        "password": "密码不能为空"
    }
}
```

##### 7.2 用户名重复

```json
{
    "code": 400,
    "msg": "用户名已存在",
    "data": null
}
```

##### 7.3 服务器错误

```json
{
    "code": 500,
    "msg": "系统内部错误",
    "data": null
}
```

#### 八、接口权限说明

| 接口 | 所需权限 | 说明 |
| :--- | :--- | :--- |
| /api/user/* | 用户管理权限 | 需要用户管理相关权限 |
| /api/role/* | 角色管理权限 | 需要角色管理相关权限 |
| /api/menu/* | 菜单管理权限 | 需要菜单管理相关权限 |
| /api/dept/* | 部门管理权限 | 需要部门管理相关权限 |

**权限控制**:
- 所有接口需要登录认证（Token）
- 特定接口需要对应角色权限
- 未授权访问返回401或403错误