### 线上服务器部署手册

#### 一、服务器环境要求

##### 1.1 服务器配置建议

| 配置项 | 最低配置 | 推荐配置 |
| :--- | :--- | :--- |
| CPU | 2核 | 4核+ |
| 内存 | 4GB | 8GB+ |
| 硬盘 | 40GB | 100GB+ |
| 带宽 | 1Mbps | 5Mbps+ |

##### 1.2 操作系统

- **推荐**: CentOS 7 / 8
- **备选**: Ubuntu 20.04+

#### 二、服务器初始化

##### 2.1 更新系统

```bash
# CentOS
yum update -y
yum install -y wget curl vim

# Ubuntu
apt update -y
apt upgrade -y
apt install -y wget curl vim
```

##### 2.2 关闭防火墙（或配置规则）

```bash
# CentOS 7
systemctl stop firewalld
systemctl disable firewalld

# 或配置规则
firewall-cmd --zone=public --add-port=80/tcp --permanent
firewall-cmd --zone=public --add-port=8080/tcp --permanent
firewall-cmd --reload
```

##### 2.3 禁用SELinux

```bash
setenforce 0
sed -i 's/^SELINUX=.*/SELINUX=disabled/' /etc/selinux/config
```

#### 三、安装JDK 21

##### 3.1 下载并安装

```bash
cd /opt
wget https://download.oracle.com/java/21/latest/jdk-21_linux-x64_bin.rpm
rpm -ivh jdk-21_linux-x64_bin.rpm
```

##### 3.2 配置环境变量

```bash
cat >> /etc/profile << 'EOF'
export JAVA_HOME=/usr/java/jdk-21
export PATH=$JAVA_HOME/bin:$PATH
EOF
source /etc/profile
```

##### 3.3 验证安装

```bash
java -version
# 输出示例: java version "21"
```

#### 四、安装MySQL 8.0

##### 4.1 安装MySQL

```bash
# CentOS
yum install -y https://dev.mysql.com/get/mysql80-community-release-el8-3.noarch.rpm
yum install -y mysql-community-server

systemctl start mysqld
systemctl enable mysqld
```

##### 4.2 初始化配置

```bash
# 获取初始密码
grep 'temporary password' /var/log/mysqld.log

# 登录并修改密码
mysql -u root -p
ALTER USER 'root'@'localhost' IDENTIFIED BY 'YourPassword@123';
```

##### 4.3 创建数据库

```sql
CREATE DATABASE IF NOT EXISTS example_db 
DEFAULT CHARACTER SET utf8mb4 
DEFAULT COLLATE utf8mb4_unicode_ci;

CREATE USER 'ruoyi'@'localhost' IDENTIFIED BY 'YourPassword@123';
GRANT ALL PRIVILEGES ON example_db.* TO 'ruoyi'@'localhost';
FLUSH PRIVILEGES;
```

##### 4.4 导入数据

```bash
mysql -u ruoyi -p example_db < /opt/ruoyi-admin/sql/init_table.sql
mysql -u ruoyi -p example_db < /opt/ruoyi-admin/sql/init_index.sql
```

#### 五、安装Nginx

##### 5.1 安装Nginx

```bash
# CentOS
yum install -y nginx

systemctl start nginx
systemctl enable nginx
```

##### 5.2 配置Nginx

```bash
mkdir -p /var/www/ruoyi-admin
chown -R nginx:nginx /var/www/ruoyi-admin
```

将 `frontend/nginx.conf` 复制到 `/etc/nginx/nginx.conf`

##### 5.3 验证配置

```bash
nginx -t
systemctl reload nginx
```

#### 六、部署后端服务

##### 6.1 创建部署目录

```bash
mkdir -p /opt/ruoyi-admin
mkdir -p /var/log/ruoyi-admin
```

##### 6.2 上传Jar包

```bash
# 将本地打包的jar包上传到服务器
scp target/ruoyi-admin-1.0.0.jar root@server-ip:/opt/ruoyi-admin/
```

##### 6.3 配置systemd服务

将 `backend/ruoyi-admin.service` 复制到 `/etc/systemd/system/`

```bash
systemctl daemon-reload
systemctl enable ruoyi-admin
systemctl start ruoyi-admin
```

##### 6.4 验证后端服务

```bash
systemctl status ruoyi-admin
curl http://localhost:8080/api/user/list?pageNum=1&pageSize=10
```

#### 七、部署前端静态资源

##### 7.1 前端打包

```bash
# 本地打包
cd frontend
npm run build

# 上传dist目录到服务器
scp -r dist/ root@server-ip:/var/www/ruoyi-admin/
```

##### 7.2 配置Nginx静态资源

确保Nginx配置中包含：
```nginx
location / {
    root /var/www/ruoyi-admin;
    try_files $uri $uri/ /index.html;
}
```

##### 7.3 验证前端

访问服务器IP地址，应看到登录页面

#### 八、安全配置

##### 8.1 配置HTTPS（推荐）

```bash
# 安装certbot
yum install -y certbot python3-certbot-nginx

# 申请证书
certbot --nginx -d your-domain.com

# 自动续期
certbot renew --dry-run
```

##### 8.2 限制访问权限

```bash
# 设置目录权限
chmod 755 /opt/ruoyi-admin
chmod 644 /opt/ruoyi-admin/ruoyi-admin-1.0.0.jar

# 创建专用用户
useradd -r -s /sbin/nologin ruoyi
chown -R ruoyi:ruoyi /opt/ruoyi-admin
chown -R ruoyi:ruoyi /var/log/ruoyi-admin
```

##### 8.3 修改systemd服务用户

```ini
[Service]
User=ruoyi
Group=ruoyi
```

#### 九、监控与日志

##### 9.1 查看应用日志

```bash
# 查看实时日志
tail -f /var/log/ruoyi-admin/ruoyi-admin.log

# 查看启动日志
cat /var/log/ruoyi-admin/nohup.out
```

##### 9.2 查看Nginx日志

```bash
tail -f /var/log/nginx/access.log
tail -f /var/log/nginx/error.log
```

##### 9.3 服务状态检查

```bash
# 后端服务
systemctl status ruoyi-admin

# Nginx服务
systemctl status nginx

# MySQL服务
systemctl status mysqld
```

#### 十、备份策略

##### 10.1 数据库备份

```bash
# 手动备份
mysqldump -u ruoyi -p example_db > /backup/example_db_$(date +%Y%m%d).sql

# 自动备份脚本
cat > /backup/backup.sh << 'EOF'
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/backup"
mysqldump -u ruoyi -p'YourPassword@123' example_db > ${BACKUP_DIR}/example_db_${DATE}.sql
find ${BACKUP_DIR} -name "*.sql" -mtime +7 -delete
EOF

chmod +x /backup/backup.sh
```

##### 10.2 配置定时任务

```bash
crontab -e

# 添加定时任务（每天凌晨2点备份）
0 2 * * * /backup/backup.sh
```

#### 十一、故障排查

##### 11.1 后端服务无法启动

```bash
# 检查端口占用
netstat -tlnp | grep 8080

# 检查日志
cat /var/log/ruoyi-admin/ruoyi-admin.log

# 检查配置文件
java -jar /opt/ruoyi-admin/ruoyi-admin-1.0.0.jar --help
```

##### 11.2 前端页面无法访问

```bash
# 检查Nginx配置
nginx -t

# 检查端口
netstat -tlnp | grep 80

# 检查防火墙
firewall-cmd --list-ports
```

##### 11.3 数据库连接失败

```bash
# 检查MySQL服务
systemctl status mysqld

# 检查数据库连接
mysql -u ruoyi -p

# 检查网络
telnet localhost 3306
```

#### 十二、部署验证清单

- [ ] JDK 21安装成功
- [ ] MySQL 8.0安装成功并导入数据
- [ ] Nginx安装成功并配置完成
- [ ] 后端Jar包部署成功
- [ ] systemd服务配置完成并启动
- [ ] 前端静态资源部署成功
- [ ] HTTPS证书配置完成（可选）
- [ ] 安全配置完成
- [ ] 备份策略配置完成
- [ ] 登录页面正常显示
- [ ] 四大模块功能正常