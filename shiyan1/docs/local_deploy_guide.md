### 本地开发部署手册

#### 一、环境依赖

##### 1.1 基础环境

| 依赖 | 版本要求 | 说明 |
| :--- | :--- | :--- |
| JDK | 21+ | Java开发环境 |
| Node.js | 18+ | 前端开发环境 |
| MySQL | 8.0+ | 数据库 |
| Maven | 3.8+ | 后端构建工具 |
| Git | 2.0+ | 版本控制 |

##### 1.2 环境配置检查

**检查JDK版本**:
```bash
java -version
# 输出示例: openjdk version "21"
```

**检查Node.js版本**:
```bash
node -v
# 输出示例: v18.17.0
```

**检查MySQL服务**:
```bash
# Windows
net start MySQL80

# Linux
systemctl status mysql
```

#### 二、项目结构

```
ruoyi-admin/
├── backend/                    # 后端代码
│   ├── src/main/java/         # Java源代码
│   ├── src/main/resources/    # 配置文件
│   ├── src/main/resources/mapper/ # MyBatis映射文件
│   ├── sql/                   # SQL脚本
│   └── pom.xml               # Maven配置
├── frontend/                  # 前端代码
│   ├── src/                   # 前端源代码
│   ├── vite.config.js        # Vite配置
│   ├── package.json          # npm配置
│   └── nginx.conf            # Nginx配置
└── docs/                      # 文档目录
```

#### 三、数据库配置

##### 3.1 创建数据库

```sql
CREATE DATABASE IF NOT EXISTS example_db 
DEFAULT CHARACTER SET utf8mb4 
DEFAULT COLLATE utf8mb4_unicode_ci;
```

##### 3.2 导入初始化数据

```bash
mysql -u root -p example_db < backend/sql/init_table.sql
```

##### 3.3 导入索引

```bash
mysql -u root -p example_db < backend/sql/init_index.sql
```

#### 四、后端启动

##### 4.1 配置数据库连接

编辑 `backend/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/example_db?...
    username: root
    password: your_password
```

##### 4.2 启动后端服务

**开发模式**:
```bash
cd backend
mvn spring-boot:run
```

**打包构建**:
```bash
cd backend
mvn clean package
java -jar target/ruoyi-admin-1.0.0.jar
```

**验证启动**:
- 访问 `http://localhost:8080/swagger-ui/index.html`
- 看到Swagger文档页面说明启动成功

#### 五、前端启动

##### 5.1 安装依赖

```bash
cd frontend
npm install
```

##### 5.2 启动前端服务

```bash
npm run dev
```

**验证启动**:
- 访问 `http://localhost:5173`
- 看到登录页面说明启动成功

#### 六、开发流程

##### 6.1 代码提交规范

```bash
# 开发流程
git pull origin main
git checkout -b feature/xxx
# 修改代码
git add .
git commit -m "feat: 添加xxx功能"
git push origin feature/xxx
```

##### 6.2 分支管理策略

| 分支类型 | 命名规范 | 说明 |
| :--- | :--- | :--- |
| 主分支 | main | 生产环境代码 |
| 开发分支 | develop | 开发集成分支 |
| 特性分支 | feature/xxx | 新功能开发 |
| 修复分支 | fix/xxx | Bug修复 |

#### 七、常见报错及解决方案

##### 7.1 数据库连接失败

**错误信息**:
```
Cannot get connection for pool
```

**解决方案**:
1. 检查MySQL服务是否启动
2. 检查数据库用户名密码是否正确
3. 检查数据库端口是否为3306
4. 检查防火墙是否允许访问

##### 7.2 Maven依赖下载失败

**错误信息**:
```
Could not resolve dependencies
```

**解决方案**:
```bash
# 清理缓存
mvn clean install -U

# 或手动下载依赖到本地仓库
```

##### 7.3 前端依赖安装失败

**错误信息**:
```
npm ERR! code EINTEGRITY
```

**解决方案**:
```bash
# 清理缓存
npm cache clean --force

# 删除node_modules重新安装
rm -rf node_modules package-lock.json
npm install
```

##### 7.4 端口占用

**错误信息**:
```
Port 8080 is already in use
```

**解决方案**:
```bash
# 查找占用进程 (Windows)
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux
lsof -i :8080
kill -9 <PID>
```

##### 7.5 Swagger访问404

**错误信息**:
```
404 Not Found
```

**解决方案**:
1. 检查后端是否启动成功
2. 检查 `springdoc` 配置是否正确
3. 访问地址应为 `/swagger-ui/index.html`

##### 7.6 前端代理配置错误

**错误信息**:
```
503 Service Unavailable
```

**解决方案**:
检查 `vite.config.js` 代理配置:
```javascript
proxy: {
    '/prod-api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/prod-api/, '')
    }
}
```

##### 7.7 跨域问题

**错误信息**:
```
Access-Control-Allow-Origin
```

**解决方案**:
检查后端跨域配置，确保已配置 `WebMvcConfig`:
```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
```

#### 八、开发工具推荐

| 工具 | 用途 | 推荐版本 |
| :--- | :--- | :--- |
| IntelliJ IDEA | Java开发 | 2024+ |
| VS Code | 前端开发 | 最新 |
| Navicat | 数据库管理 | 16+ |
| Postman | API测试 | 最新 |

#### 九、启动验证清单

- [ ] MySQL服务已启动
- [ ] 数据库已创建并导入数据
- [ ] 后端服务启动成功 (端口8080)
- [ ] Swagger文档可访问
- [ ] 前端服务启动成功 (端口5173)
- [ ] 登录页面正常显示
- [ ] 四大模块接口测试通过