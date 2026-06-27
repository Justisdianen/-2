-- Part6 Security Tables
-- Login Log Table
CREATE TABLE IF NOT EXISTS sys_login_log (
    log_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_name VARCHAR(50),
    ipaddr VARCHAR(128),
    login_location VARCHAR(255),
    browser VARCHAR(50),
    os VARCHAR(50),
    status VARCHAR(1) DEFAULT '0',
    msg VARCHAR(255),
    login_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Operation Log Table
CREATE TABLE IF NOT EXISTS sys_oper_log (
    oper_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(50),
    business_type VARCHAR(20),
    method VARCHAR(200),
    request_method VARCHAR(10),
    operator_name VARCHAR(50),
    oper_name VARCHAR(50),
    dept_name VARCHAR(50),
    oper_url VARCHAR(255),
    oper_ip VARCHAR(128),
    oper_location VARCHAR(255),
    oper_param TEXT,
    json_result TEXT,
    status VARCHAR(1) DEFAULT '0',
    error_msg TEXT,
    oper_time DATETIME,
    cost_time BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Exception Log Table
CREATE TABLE IF NOT EXISTS sys_exception_log (
    log_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    exception_type VARCHAR(100),
    exception_msg TEXT,
    exception_stack TEXT,
    request_url VARCHAR(255),
    request_method VARCHAR(10),
    request_param TEXT,
    operator_name VARCHAR(50),
    oper_ip VARCHAR(128),
    oper_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- User Lock Table
CREATE TABLE IF NOT EXISTS sys_user_lock (
    lock_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_name VARCHAR(50),
    fail_count INT DEFAULT 0,
    lock_time DATETIME,
    unlock_time DATETIME,
    status VARCHAR(1) DEFAULT '0',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- User Session Table
CREATE TABLE IF NOT EXISTS sys_user_session (
    session_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    user_name VARCHAR(50),
    token VARCHAR(500),
    ipaddr VARCHAR(128),
    browser VARCHAR(50),
    os VARCHAR(50),
    login_time DATETIME,
    last_access_time DATETIME,
    expire_time DATETIME,
    status VARCHAR(1) DEFAULT '0',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- System Config Table
CREATE TABLE IF NOT EXISTS sys_config (
    config_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_name VARCHAR(100),
    config_key VARCHAR(100),
    config_value VARCHAR(500),
    config_type VARCHAR(1) DEFAULT 'N',
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(64),
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Init Config Data
INSERT INTO sys_config (config_name, config_key, config_value, config_type, remark, create_by) VALUES
('System Name', 'sys.system.name', 'RuoYi Admin', 'Y', 'System Name', 'admin'),
('Token Expire Seconds', 'sys.token.expire', '7200', 'Y', 'Token expire 2 hours', 'admin'),
('Token Refresh Threshold', 'sys.token.refresh.threshold', '1800', 'Y', 'Refresh before 30 min', 'admin'),
('Login Fail Count', 'sys.login.fail.count', '5', 'Y', 'Lock after 5 fails', 'admin'),
('Lock Time Minutes', 'sys.login.lock.time', '10', 'Y', 'Lock 10 minutes', 'admin'),
('Session Timeout Minutes', 'sys.session.timeout', '30', 'Y', 'Kick after 30 min idle', 'admin'),
('Password Min Length', 'sys.password.min.length', '6', 'Y', 'Password min length', 'admin'),
('Password Max Length', 'sys.password.max.length', '20', 'Y', 'Password max length', 'admin'),
('Captcha Enable', 'sys.captcha.enable', 'true', 'Y', 'Captcha switch', 'admin'),
('Swagger Enable', 'sys.swagger.enable', 'true', 'N', 'Disable in prod', 'admin'),
('Log Retention Days', 'sys.log.retention.days', '30', 'Y', 'Auto clean 30 days', 'admin');

-- Menu Data - Log Management folder under System Management
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
('Log Management', 1, 9, 'log', NULL, NULL, '1', '0', 'M', '1', '1', '', 'log', 'admin', NOW(), 'Log Management');

-- Login Log and Operation Log under Log Management (menu_id will be assigned)
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
('Login Log', 100, 1, 'loginlog', 'system/loginlog/index', NULL, '1', '0', 'C', '1', '1', 'system:loginlog:list', 'logininfor', 'admin', NOW(), 'Login Log'),
('Operation Log', 100, 2, 'operlog', 'system/operlog/index', NULL, '1', '0', 'C', '1', '1', 'system:operlog:list', 'form', 'admin', NOW(), 'Operation Log');

-- System Config under System Management
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
('System Config', 1, 10, 'config', 'system/config/index', NULL, '1', '0', 'C', '1', '1', 'system:config:list', 'edit', 'admin', NOW(), 'System Config');

-- Profile menu (hidden top level)
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
('Profile', 0, 99, 'profile', 'system/user/profile/index', NULL, '1', '0', 'C', '0', '1', 'system:user:profile', 'user', 'admin', NOW(), 'Profile');