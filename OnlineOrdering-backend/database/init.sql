-- 创建数据库
CREATE DATABASE IF NOT EXISTS online_ordering DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE online_ordering;

-- 管理员表
CREATE TABLE IF NOT EXISTS admin (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '管理员ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- 菜品分类表
CREATE TABLE IF NOT EXISTS category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
    sort_order INT DEFAULT 0 COMMENT '排序顺序，数字越小越靠前',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜品分类表';

-- 菜品信息表
CREATE TABLE IF NOT EXISTS dish (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '菜品ID',
    dish_name VARCHAR(100) NOT NULL COMMENT '菜品名称',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    price DECIMAL(10, 2) NOT NULL COMMENT '价格，单位：元',
    image_url VARCHAR(500) COMMENT '菜品图片URL',
    description VARCHAR(500) COMMENT '菜品描述',
    status VARCHAR(20) DEFAULT 'AVAILABLE' COMMENT '状态：AVAILABLE(可用), UNAVAILABLE(不可用), DELETED(已删除)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    FOREIGN KEY (category_id) REFERENCES category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜品信息表';

-- 顾客信息表
CREATE TABLE IF NOT EXISTS customer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '顾客ID',
    open_id VARCHAR(100) NOT NULL UNIQUE COMMENT '微信用户OpenID',
    nickname VARCHAR(100) COMMENT '微信昵称',
    avatar_url VARCHAR(500) COMMENT '微信头像URL',
    phone VARCHAR(20) COMMENT '手机号',
    points INT DEFAULT 0 COMMENT '积分余额',
    is_member TINYINT DEFAULT 0 COMMENT '是否会员：0(非会员), 1(会员)',
    member_since DATETIME COMMENT '成为会员时间',
    member_expire_time DATETIME COMMENT '会员到期时间',
    total_spent DECIMAL(10, 2) DEFAULT 0.00 COMMENT '累计消费金额',
    total_orders INT DEFAULT 0 COMMENT '累计订单数',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE(活跃), INACTIVE(不活跃), BLOCKED(已封禁)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_open_id (open_id),
    INDEX idx_phone (phone),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='顾客信息表';

-- 用餐会话表
CREATE TABLE IF NOT EXISTS dining_session (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '会话ID',
    table_number VARCHAR(20) NOT NULL COMMENT '桌号',
    customer_id BIGINT COMMENT '顾客ID',
    open_id VARCHAR(100) COMMENT '微信用户OpenID',
    scan_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '扫码时间',
    close_time DATETIME COMMENT '会话关闭时间',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE(活跃), CLOSED(已关闭)',
    INDEX idx_table_number (table_number),
    INDEX idx_customer_id (customer_id),
    INDEX idx_status (status),
    FOREIGN KEY (customer_id) REFERENCES customer(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用餐会话表';

-- 订单表
CREATE TABLE IF NOT EXISTS `order` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    order_number VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    table_number VARCHAR(20) NOT NULL COMMENT '桌号',
    customer_id BIGINT COMMENT '顾客ID',
    open_id VARCHAR(100) COMMENT '微信用户OpenID',
    session_id BIGINT COMMENT '会话ID',
    total_amount DECIMAL(10, 2) NOT NULL COMMENT '订单总金额',
    discount_amount DECIMAL(10, 2) DEFAULT 0.00 COMMENT '折扣金额',
    points_earned INT DEFAULT 0 COMMENT '本单获得积分',
    points_used INT DEFAULT 0 COMMENT '本单使用积分',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '订单状态：PENDING(待处理), PROCESSING(制作中), COMPLETED(已完成), CANCELLED(已取消), PAID(已支付)',
    payment_method VARCHAR(20) COMMENT '支付方式：WECHAT(微信支付), CASH(现金), CARD(银行卡)',
    pay_time DATETIME COMMENT '支付时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_number (order_number),
    INDEX idx_customer_id (customer_id),
    INDEX idx_session_id (session_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time),
    FOREIGN KEY (customer_id) REFERENCES customer(id),
    FOREIGN KEY (session_id) REFERENCES dining_session(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 订单明细表
CREATE TABLE IF NOT EXISTS order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单明细ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    dish_id BIGINT NOT NULL COMMENT '菜品ID',
    quantity INT NOT NULL COMMENT '数量',
    unit_price DECIMAL(10, 2) NOT NULL COMMENT '单价',
    subtotal DECIMAL(10, 2) NOT NULL COMMENT '小计金额',
    INDEX idx_order_id (order_id),
    INDEX idx_dish_id (dish_id),
    FOREIGN KEY (order_id) REFERENCES `order`(id),
    FOREIGN KEY (dish_id) REFERENCES dish(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单明细表';

-- 积分记录表
CREATE TABLE IF NOT EXISTS points_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    customer_id BIGINT NOT NULL COMMENT '顾客ID',
    order_id BIGINT COMMENT '关联订单ID',
    points_change INT NOT NULL COMMENT '积分变动（正数为增加，负数为减少）',
    points_balance INT NOT NULL COMMENT '变动后积分余额',
    change_type VARCHAR(20) NOT NULL COMMENT '变动类型：EARN(消费获得), REDEEM(兑换使用), EXPIRE(过期), ADMIN_ADJUST(管理员调整)',
    description VARCHAR(200) COMMENT '变动说明',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_customer_id (customer_id),
    INDEX idx_order_id (order_id),
    INDEX idx_create_time (create_time),
    FOREIGN KEY (customer_id) REFERENCES customer(id),
    FOREIGN KEY (order_id) REFERENCES `order`(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分记录表';

-- 插入默认管理员账号（用户名：admin，密码：admin123）
-- BCrypt 加密后的密码
INSERT INTO admin (username, password) VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi');
