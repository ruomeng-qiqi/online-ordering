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

-- 插入默认管理员账号（用户名：admin，密码：admin123）
-- BCrypt 加密后的密码
INSERT INTO admin (username, password) VALUES ('admin', '$2a$10$zoD8JRrhsOEoZhJjtoeyXuk/LqBjwc8IFmYI8BJ5QUC37IMFcubEO');

-- 分类表
CREATE TABLE IF NOT EXISTS category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    type INT NOT NULL COMMENT '分类类型：1-菜品分类，2-套餐分类',
    sort INT DEFAULT 0 COMMENT '排序',
    status INT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- 插入默认分类数据
INSERT INTO category (name, type, sort, status) VALUES 
('腊味拼盘', 1, 4, 1),
('腊味牛蛙', 1, 5, 1),
('特色菜蛋', 1, 6, 1),
('新鲜时蔬', 1, 7, 1),
('水煮鱼', 1, 8, 1),
('佛跳土豆', 1, 9, 1),
('酒水饮料', 1, 10, 1),
('汤类', 1, 11, 1),
('人气套餐', 2, 12, 1);

-- 菜品表
CREATE TABLE IF NOT EXISTS dish (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '菜品ID',
    name VARCHAR(100) NOT NULL COMMENT '菜品名称',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    image VARCHAR(255) COMMENT '图片路径',
    description TEXT COMMENT '描述',
    status INT DEFAULT 1 COMMENT '状态：0-停售，1-起售',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜品表';

-- 菜品口味表
CREATE TABLE IF NOT EXISTS dish_flavor (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '口味ID',
    dish_id BIGINT NOT NULL COMMENT '菜品ID',
    name VARCHAR(50) NOT NULL COMMENT '口味名称（如：甜味、辣度、温度、忌口）',
    value TEXT NOT NULL COMMENT '口味选项（JSON数组格式，如：["无糖","少糖","半糖","多糖","全糖"]）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (dish_id) REFERENCES dish(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜品口味表';

-- 套餐表
CREATE TABLE IF NOT EXISTS setmeal (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '套餐ID',
    name VARCHAR(100) NOT NULL COMMENT '套餐名称',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    price DECIMAL(10,2) NOT NULL COMMENT '套餐价格',
    image VARCHAR(255) COMMENT '图片路径',
    description TEXT COMMENT '描述',
    status INT DEFAULT 1 COMMENT '状态：0-停售，1-起售',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='套餐表';

-- 套餐菜品关系表
CREATE TABLE IF NOT EXISTS setmeal_dish (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关系ID',
    setmeal_id BIGINT NOT NULL COMMENT '套餐ID',
    dish_id BIGINT NOT NULL COMMENT '菜品ID',
    copies INT NOT NULL DEFAULT 1 COMMENT '份数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (setmeal_id) REFERENCES setmeal(id) ON DELETE CASCADE,
    FOREIGN KEY (dish_id) REFERENCES dish(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='套餐菜品关系表';

-- 插入示例菜品数据
INSERT INTO dish (name, category_id, price, image, description, status) VALUES 
('测试菜品', 1, 58.00, 'https://via.placeholder.com/100', '美味可口', 1),
('平菇豆腐汤', 8, 6.00, 'https://via.placeholder.com/100', '营养健康', 1),
('肉茄子', 8, 4.00, 'https://via.placeholder.com/100', '家常美味', 1),
('鲍鱼2斤', 1, 72.00, 'https://via.placeholder.com/100', '新鲜鲍鱼', 1);

-- 插入示例菜品口味数据
INSERT INTO dish_flavor (dish_id, name, value) VALUES 
(1, '甜味', '["无糖","少糖","半糖","多糖","全糖"]'),
(1, '忌口', '["不要葱","不要蒜","不要香菜","不要辣"]'),
(2, '辣度', '["不辣","微辣","中辣"]');

-- 插入示例套餐数据
INSERT INTO setmeal (name, category_id, price, image, description, status) VALUES 
('人气套餐A计划', 9, 45.00, 'https://via.placeholder.com/100', '包含主食、饮料、甜点，营养均衡', 1);

-- 餐台表
CREATE TABLE IF NOT EXISTS dining_table (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '餐台ID',
    table_number VARCHAR(20) NOT NULL COMMENT '餐台号',
    table_name VARCHAR(50) NOT NULL COMMENT '餐台名称',
    seats INT NOT NULL COMMENT '座位数',
    status INT DEFAULT 0 COMMENT '状态：0-空闲，1-占用',
    qr_code VARCHAR(255) COMMENT '二维码URL',
    sort INT DEFAULT 0 COMMENT '排序',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-正常，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE INDEX uk_table_number_deleted (table_number, deleted),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='餐台表';

-- 插入示例餐台数据
INSERT INTO dining_table (table_number, table_name, seats, status, sort) VALUES 
('A01', '大厅A01', 4, 0, 1),
('A02', '大厅A02', 4, 0, 2),
('A03', '大厅A03', 2, 0, 3),
('B01', '包厢B01', 6, 0, 4),
('B02', '包厢B02', 8, 0, 5);

-- 顾客表
CREATE TABLE IF NOT EXISTS customer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '顾客ID',
    openid VARCHAR(100) NOT NULL UNIQUE COMMENT '微信openid',
    nickname VARCHAR(100) COMMENT '昵称',
    avatar VARCHAR(255) COMMENT '头像URL',
    gender TINYINT DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    is_member TINYINT DEFAULT 0 COMMENT '是否会员：0-否，1-是',
    points INT DEFAULT 0 COMMENT '当前积分',
    total_points INT DEFAULT 0 COMMENT '累计积分',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_openid (openid),
    INDEX idx_is_member (is_member),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='顾客表';

-- 积分记录表
CREATE TABLE IF NOT EXISTS points_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '积分记录ID',
    customer_id BIGINT NOT NULL COMMENT '顾客ID',
    type TINYINT NOT NULL COMMENT '类型：1-订单获得，2-积分抵扣，3-手动调整',
    points INT NOT NULL COMMENT '积分变动（正数增加，负数减少）',
    order_id BIGINT COMMENT '关联订单ID',
    remark VARCHAR(200) DEFAULT '管理员手动调整' COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_customer_id (customer_id),
    INDEX idx_type (type),
    FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分记录表';

-- 插入示例顾客数据
INSERT INTO customer (openid, nickname, avatar, gender, is_member, points, total_points, status, create_time, update_time) VALUES 
('wx_test_001', '张三', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 1, 1, 1580, 3200, 1, '2026-04-01 10:30:00', '2026-04-10 15:20:00'),
('wx_test_002', '李四', 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png', 2, 0, 0, 0, 1, '2026-04-02 14:20:00', '2026-04-12 09:15:00'),
('wx_test_003', '王五', '', 1, 1, 520, 1850, 0, '2026-04-03 09:15:00', '2026-04-11 16:30:00'),
('wx_test_004', '赵六', 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png', 0, 0, 0, 0, 1, '2026-04-05 16:45:00', '2026-04-13 08:10:00'),
('wx_test_005', '钱七', '', 2, 1, 2350, 5600, 1, '2026-04-10 11:30:00', '2026-04-13 10:25:00'),
('wx_test_006', '孙八', 'https://cube.elemecdn.com/6/94/4d3ea53c084bad6931a56d5158a48jpeg.jpeg', 1, 0, 0, 0, 1, '2026-04-13 08:20:00', '2026-04-13 08:20:00');

-- 插入示例积分记录数据
INSERT INTO points_record (customer_id, type, points, order_id, remark, create_time) VALUES 
(1, 1, 100, 1001, '订单消费获得', '2026-04-10 15:20:00'),
(1, 2, -50, 1002, '积分抵扣', '2026-04-09 12:30:00'),
(1, 3, 200, NULL, '管理员手动调整', '2026-04-08 10:15:00'),
(1, 1, 80, 1003, '订单消费获得', '2026-04-05 18:45:00'),
(3, 1, 150, 1004, '订单消费获得', '2026-04-11 16:30:00'),
(5, 1, 200, 1005, '订单消费获得', '2026-04-13 10:25:00'),
(5, 3, 500, NULL, '会员充值赠送', '2026-04-10 11:30:00');

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    order_number VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    customer_id BIGINT NOT NULL COMMENT '顾客ID',
    table_id BIGINT NOT NULL COMMENT '餐台ID',
    checkout_time DATETIME COMMENT '结账时间',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    actual_amount DECIMAL(10,2) COMMENT '实际支付金额',
    discount_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '优惠金额',
    points_deduction DECIMAL(10,2) DEFAULT 0.00 COMMENT '积分抵扣金额',
    points_used INT DEFAULT 0 COMMENT '使用的积分数',
    points_earned INT DEFAULT 0 COMMENT '获得的积分数',
    payment_method TINYINT COMMENT '支付方式：1-在线支付，2-线下支付',
    order_status TINYINT DEFAULT 1 COMMENT '订单状态：1-待支付，2-已完成，3-已取消',
    remark VARCHAR(500) COMMENT '备注',
    cancel_reason VARCHAR(200) COMMENT '取消原因',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_number (order_number),
    INDEX idx_customer_id (customer_id),
    INDEX idx_table_id (table_id),
    INDEX idx_order_status (order_status),
    INDEX idx_create_time (create_time),
    FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE RESTRICT,
    FOREIGN KEY (table_id) REFERENCES dining_table(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 订单明细表
CREATE TABLE IF NOT EXISTS order_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '明细ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    dish_id BIGINT COMMENT '菜品ID',
    setmeal_id BIGINT COMMENT '套餐ID',
    name VARCHAR(100) NOT NULL COMMENT '菜品/套餐名称',
    image VARCHAR(255) COMMENT '图片URL',
    quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
    price DECIMAL(10,2) NOT NULL COMMENT '单价',
    amount DECIMAL(10,2) NOT NULL COMMENT '小计金额',
    flavor TEXT COMMENT '口味信息（JSON格式）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_order_id (order_id),
    INDEX idx_dish_id (dish_id),
    INDEX idx_setmeal_id (setmeal_id),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (dish_id) REFERENCES dish(id) ON DELETE SET NULL,
    FOREIGN KEY (setmeal_id) REFERENCES setmeal(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单明细表';

-- 插入示例订单数据
INSERT INTO orders (order_number, customer_id, table_id, checkout_time, total_amount, actual_amount, discount_amount, points_deduction, points_used, points_earned, payment_method, order_status, remark, create_time, update_time) VALUES 
('20260410152000123456', 1, 1, '2026-04-10 16:30:00', 120.00, 100.00, 0.00, 20.00, 2000, 100, 1, 2, '少放辣椒', '2026-04-10 15:20:00', '2026-04-10 16:30:00'),
('20260409123000234567', 1, 2, '2026-04-09 13:45:00', 180.00, 130.00, 0.00, 50.00, 5000, 0, 1, 2, '', '2026-04-09 12:30:00', '2026-04-09 13:45:00'),
('20260405184500345678', 2, 3, '2026-04-05 20:00:00', 80.00, 80.00, 0.00, 0.00, 0, 80, 2, 2, '', '2026-04-05 18:45:00', '2026-04-05 20:00:00'),
('20260411163000456789', 3, 1, '2026-04-11 17:45:00', 250.00, 250.00, 0.00, 0.00, 0, 150, 1, 2, '不要香菜', '2026-04-11 16:30:00', '2026-04-11 17:45:00'),
('20260413102500567890', 4, 4, '2026-04-13 11:30:00', 200.00, 200.00, 0.00, 0.00, 0, 200, 2, 2, '', '2026-04-13 10:25:00', '2026-04-13 11:30:00'),
('20260416093000678901', 2, 2, NULL, 150.00, NULL, 0.00, 0.00, 0, 0, NULL, 1, '', '2026-04-16 09:30:00', '2026-04-16 09:35:00'),
('20260416100000789012', 4, 5, NULL, 220.00, NULL, 0.00, 0.00, 0, 0, NULL, 1, '多加点肉', '2026-04-16 10:00:00', '2026-04-16 10:05:00'),
('20260416103000890123', 3, 3, NULL, 68.00, NULL, 0.00, 0.00, 0, 0, NULL, 1, '', '2026-04-16 10:30:00', '2026-04-16 10:30:00');

-- 插入示例订单明细数据
INSERT INTO order_detail (order_id, dish_id, setmeal_id, name, image, quantity, price, amount, flavor) VALUES 
-- 订单1的明细
(1, 1, NULL, '测试菜品', 'https://via.placeholder.com/100', 2, 58.00, 116.00, '{"甜味":"少糖","忌口":"不要葱"}'),
(1, 2, NULL, '平菇豆腐汤', 'https://via.placeholder.com/100', 1, 6.00, 6.00, '{"辣度":"不辣"}'),
-- 订单2的明细
(2, 4, NULL, '鲍鱼2斤', 'https://via.placeholder.com/100', 2, 72.00, 144.00, NULL),
(2, 3, NULL, '肉茄子', 'https://via.placeholder.com/100', 3, 4.00, 12.00, NULL),
(2, 2, NULL, '平菇豆腐汤', 'https://via.placeholder.com/100', 4, 6.00, 24.00, NULL),
-- 订单3的明细
(3, 1, NULL, '测试菜品', 'https://via.placeholder.com/100', 1, 58.00, 58.00, '{"甜味":"半糖"}'),
(3, 3, NULL, '肉茄子', 'https://via.placeholder.com/100', 2, 4.00, 8.00, NULL),
(3, 2, NULL, '平菇豆腐汤', 'https://via.placeholder.com/100', 2, 6.00, 12.00, NULL),
-- 订单4的明细
(4, NULL, 1, '人气套餐A计划', 'https://via.placeholder.com/100', 2, 45.00, 90.00, NULL),
(4, 4, NULL, '鲍鱼2斤', 'https://via.placeholder.com/100', 2, 72.00, 144.00, NULL),
(4, 3, NULL, '肉茄子', 'https://via.placeholder.com/100', 4, 4.00, 16.00, NULL),
-- 订单5的明细
(5, 1, NULL, '测试菜品', 'https://via.placeholder.com/100', 3, 58.00, 174.00, '{"甜味":"全糖","忌口":"不要蒜"}'),
(5, 2, NULL, '平菇豆腐汤', 'https://via.placeholder.com/100', 4, 6.00, 24.00, '{"辣度":"微辣"}'),
-- 订单6的明细
(6, 4, NULL, '鲍鱼2斤', 'https://via.placeholder.com/100', 2, 72.00, 144.00, NULL),
(6, 2, NULL, '平菇豆腐汤', 'https://via.placeholder.com/100', 1, 6.00, 6.00, NULL),
-- 订单7的明细
(7, 1, NULL, '测试菜品', 'https://via.placeholder.com/100', 3, 58.00, 174.00, '{"甜味":"多糖"}'),
(7, NULL, 1, '人气套餐A计划', 'https://via.placeholder.com/100', 1, 45.00, 45.00, NULL),
-- 订单8的明细
(8, 4, NULL, '鲍鱼2斤', 'https://via.placeholder.com/100', 1, 72.00, 72.00, NULL);