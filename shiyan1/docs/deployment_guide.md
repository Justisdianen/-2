# 若依管理系统部署运维手册

## 一、环境准备

### 1.1 服务器要求
- 操作系统：Linux (CentOS 7+/Ubuntu 18+) 或 Windows Server 2012+
- CPU：至少 2核
- 内存：至少 4GB
- 磁盘：至少 20GB

### 1.2 软件依赖
- JDK 17+
- MySQL 8.0+
- Redis 6.0+ (可选，用于分布式会话)
- Nginx 1.18+

## 二、后端部署

### 2.1 数据库初始化
```bash
# 导入数据库脚本
mysql -u root -p example_db < db/schema.sql
mysql -u root -p example_db < db/security_tables.sql
```

### 2.2 配置文件
根据环境选择配置文件：
- 开发环境：application-dev.yml
- 测试环境：application-test.yml
- 生产环境：application-prod.yml

修改关键配置：
```yaml
# 数据库连接（生产环境必须修改）
spring.datasource.url: jdbc:mysql://your-mysql-server:3306/ruoyi_prod
spring.datasource.username: your_username
spring.datasource.password: your_password

# Redis连接（生产环境推荐）
spring.data.redis.host: your-redis-server
spring.data.redis.password: your_redis_password

# JWT密钥（生产环境必须修改）
jwt.secret: your-very-long-secret-key-here
```

### 2.3 构建打包
```bash
cd backend
mvn clean package -Dmaven.test.skip=true

# 输出文件：target/ruoyi-admin-1.0.0.jar
```

### 2.4 启动服务
```bash
# 开发环境启动
java -jar ruoyi-admin-1.0.0.jar --spring.profiles.active=dev

# 生产环境启动
nohup java -jar ruoyi-admin-1.0.0.jar --spring.profiles.active=prod > logs/app.log 2>&1 &
```

## 三、前端部署

### 3.1 配置修改
修改 vite.config.js 中的代理配置：
```javascript
proxy: {
    '/prod-api': {
        target: 'http://your-backend-server:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/prod-api/, '')
    }
}
```

### 3.2 构建打包
```bash
cd frontend
npm install
npm run build

# 输出目录：dist/
```

### 3.3 部署静态资源
```bash
# 将dist目录复制到Nginx静态资源目录
scp -r dist/* /var/www/ruoyi/frontend/
```

## 四、Nginx配置

### 4.1 安装Nginx
```bash
# CentOS
yum install nginx

# Ubuntu
apt install nginx
```

### 4.2 配置反向代理
复制 nginx.conf.example 到 Nginx配置目录：
```bash
cp nginx.conf.example /etc/nginx/sites-available/ruoyi.conf
ln -s /etc/nginx/sites-available/ruoyi.conf /etc/nginx/sites-enabled/
nginx -t  # 测试配置
nginx -s reload  # 重新加载
```

### 4.3 HTTPS配置（生产环境必须）
1. 获取SSL证书（Let's Encrypt或购买）
2. 配置证书路径
3. 启用HTTP自动跳转HTTPS

## 五、安全加固清单

### 5.1 必须配置
- [ ] 修改默认管理员密码
- [ ] 修改JWT密钥
- [ ] 生产环境关闭Swagger（已配置）
- [ ] 配置HTTPS
- [ ] 配置防火墙（只开放80/443端口）

### 5.2 推荐配置
- [ ] 启用Redis分布式会话
- [ ] 配置数据库访问白名单
- [ ] 配置请求限流
- [ ] 配置日志自动清理

## 六、运维监控

### 6.1 日志查看
```bash
# 应用日志
tail -f logs/prod/ruoyi-prod.log

# Nginx日志
tail -f /var/log/nginx/access.log
tail -f /var/log/nginx/error.log
```

### 6.2 性能监控
- 使用 Spring Boot Actuator：/actuator/health
- 使用 APM工具：SkyWalking/Prometheus

### 6.3 定时任务
- 日志清理：每天凌晨1点自动执行
- 会话清理：每小时自动执行

## 七、故障排查

### 7.1 常见问题

**问题1：登录失败显示"账号已被锁定"**
- 解决：等待10分钟或查看sys_user_lock表清除锁定记录

**问题2：接口返回401未授权**
- 解决：检查Token是否过期，重新登录获取新Token

**问题3：跨域请求失败**
- 解决：检查Nginx代理配置和后端CORS配置

**问题4：数据库连接失败**
- 解决：检查数据库连接配置和网络连通性

### 7.2 数据备份
```bash
# 数据库备份
mysqldump -u root -p example_db > backup_$(date +%Y%m%d).sql

# 应用备份
tar -czf ruoyi_backup_$(date +%Y%m%d).tar.gz backend/frontend/
```

## 八、更新升级

### 8.1 后端更新
```bash
# 停止服务
kill -15 $(cat app.pid)

# 更新jar包
cp ruoyi-admin-1.0.0.jar /opt/ruoyi/

# 启动服务
nohup java -jar ruoyi-admin-1.0.0.jar --spring.profiles.active=prod &
```

### 8.2 前端更新
```bash
# 重新构建
npm run build

# 更新静态资源
rm -rf /var/www/ruoyi/frontend/*
cp -r dist/* /var/www/ruoyi/frontend/
```