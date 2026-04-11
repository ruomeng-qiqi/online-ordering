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

