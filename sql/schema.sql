-- =====================================================================
--  校园二手交易与失物招领管理系统  数据库脚本
--  数据库：MySQL 8.x   字符集：utf8mb4
--  说明：直接执行本脚本即可创建数据库、数据表并写入初始测试数据。
--  初始登录密码统一为 123456（库中存储其 MD5 值）。
-- =====================================================================

DROP DATABASE IF EXISTS campus_system;
CREATE DATABASE campus_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE campus_system;

-- ---------------------------------------------------------------------
-- 1. 用户表 user
--    role:   0=普通用户  1=管理员
--    status: 1=正常      0=禁用
-- ---------------------------------------------------------------------
CREATE TABLE `user` (
    id          INT          NOT NULL AUTO_INCREMENT COMMENT '用户编号',
    account     VARCHAR(50)  NOT NULL COMMENT '登录账号',
    password    VARCHAR(64)  NOT NULL COMMENT '密码(MD5)',
    nickname    VARCHAR(50)  NOT NULL COMMENT '昵称',
    phone       VARCHAR(30)           COMMENT '联系方式',
    role        INT          NOT NULL DEFAULT 0 COMMENT '角色:0普通用户 1管理员',
    status      INT          NOT NULL DEFAULT 1 COMMENT '状态:1正常 0禁用',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_account (account)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ---------------------------------------------------------------------
-- 2. 分类表 category
--    type:   1=二手商品分类  2=失物招领分类
--    status: 1=启用          0=停用
-- ---------------------------------------------------------------------
CREATE TABLE `category` (
    id     INT         NOT NULL AUTO_INCREMENT COMMENT '分类编号',
    name   VARCHAR(50) NOT NULL COMMENT '分类名称',
    type   INT         NOT NULL COMMENT '分类类型:1商品 2失物招领',
    status INT         NOT NULL DEFAULT 1 COMMENT '状态:1启用 0停用',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- ---------------------------------------------------------------------
-- 3. 二手商品表 goods
--    status: 1=在售  2=已售  0=下架
-- ---------------------------------------------------------------------
CREATE TABLE `goods` (
    id          INT            NOT NULL AUTO_INCREMENT COMMENT '商品编号',
    title       VARCHAR(100)   NOT NULL COMMENT '标题',
    category_id INT            NOT NULL COMMENT '分类编号',
    price       DECIMAL(10,2)  NOT NULL DEFAULT 0 COMMENT '价格',
    quality     VARCHAR(20)             COMMENT '成色',
    description TEXT                    COMMENT '描述',
    image       VARCHAR(255)            COMMENT '图片路径',
    trade_place VARCHAR(100)            COMMENT '交易地点',
    contact     VARCHAR(50)             COMMENT '联系方式',
    status      INT            NOT NULL DEFAULT 1 COMMENT '状态:1在售 2已售 0下架',
    user_id     INT            NOT NULL COMMENT '发布者编号',
    view_count  INT            NOT NULL DEFAULT 0 COMMENT '浏览量',
    create_time DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    PRIMARY KEY (id),
    KEY idx_goods_category (category_id),
    KEY idx_goods_user (user_id),
    CONSTRAINT fk_goods_category FOREIGN KEY (category_id) REFERENCES category (id),
    CONSTRAINT fk_goods_user     FOREIGN KEY (user_id)     REFERENCES `user` (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='二手商品表';

-- ---------------------------------------------------------------------
-- 4. 失物招领表 lost_found
--    type:   1=失物(寻物)  2=拾物(招领)
--    status: 1=寻找中 2=已找回 3=待认领 4=已认领 0=已关闭
-- ---------------------------------------------------------------------
CREATE TABLE `lost_found` (
    id          INT          NOT NULL AUTO_INCREMENT COMMENT '信息编号',
    type        INT          NOT NULL COMMENT '类型:1失物 2拾物',
    name        VARCHAR(100) NOT NULL COMMENT '物品名称',
    category_id INT          NOT NULL COMMENT '分类编号',
    place       VARCHAR(100)          COMMENT '地点',
    happen_time DATETIME              COMMENT '丢失/拾到时间',
    feature     VARCHAR(500)          COMMENT '物品特征/保管说明',
    contact     VARCHAR(50)           COMMENT '联系方式',
    image       VARCHAR(255)          COMMENT '图片路径',
    status      INT          NOT NULL DEFAULT 1 COMMENT '状态:1寻找中 2已找回 3待认领 4已认领 0已关闭',
    user_id     INT          NOT NULL COMMENT '发布者编号',
    view_count  INT          NOT NULL DEFAULT 0 COMMENT '浏览量',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    PRIMARY KEY (id),
    KEY idx_lf_category (category_id),
    KEY idx_lf_user (user_id),
    CONSTRAINT fk_lf_category FOREIGN KEY (category_id) REFERENCES category (id),
    CONSTRAINT fk_lf_user     FOREIGN KEY (user_id)     REFERENCES `user` (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='失物招领表';

-- ---------------------------------------------------------------------
-- 5. 留言表 message
--    target_type: 1=商品  2=失物招领
-- ---------------------------------------------------------------------
CREATE TABLE `message` (
    id          INT          NOT NULL AUTO_INCREMENT COMMENT '留言编号',
    target_type INT          NOT NULL COMMENT '目标类型:1商品 2失物招领',
    target_id   INT          NOT NULL COMMENT '目标信息编号',
    user_id     INT          NOT NULL COMMENT '留言人编号',
    content     VARCHAR(500) NOT NULL COMMENT '留言内容',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '留言时间',
    PRIMARY KEY (id),
    KEY idx_msg_target (target_type, target_id),
    KEY idx_msg_user (user_id),
    CONSTRAINT fk_msg_user FOREIGN KEY (user_id) REFERENCES `user` (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='留言表';

-- =====================================================================
--  初始化测试数据
-- =====================================================================

-- 用户（密码均为 123456 的 MD5：e10adc3949ba59abbe56e057f20f883e）
INSERT INTO `user` (account, password, nickname, phone, role, status) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', '13800000000', 1, 1),
('zhangsan', 'e10adc3949ba59abbe56e057f20f883e', '张三', '13811112222', 0, 1),
('lisi',    'e10adc3949ba59abbe56e057f20f883e', '李四', '13833334444', 0, 1),
('wangwu',  'e10adc3949ba59abbe56e057f20f883e', '王五', '13855556666', 0, 1);

-- 商品分类(type=1)
INSERT INTO `category` (name, type, status) VALUES
('教材书籍', 1, 1),
('数码电子', 1, 1),
('生活用品', 1, 1),
('运动器材', 1, 1),
('其他商品', 1, 1);

-- 失物招领分类(type=2)
INSERT INTO `category` (name, type, status) VALUES
('证件卡类', 2, 1),
('电子设备', 2, 1),
('钥匙挂件', 2, 1),
('书籍文具', 2, 1),
('其他物品', 2, 1);

-- 二手商品
INSERT INTO `goods` (title, category_id, price, quality, description, image, trade_place, contact, status, user_id, view_count) VALUES
('高等数学教材（同济第七版）', 1, 15.00, '9成新', '上学期用过，几乎没有笔记，干净整洁，适合大一新生。', NULL, '一号教学楼', '13811112222', 1, 2, 12),
('二手机械键盘 黑轴', 2, 120.00, '95新', '自用一年，手感很好，无任何故障，配送原装数据线。', NULL, '学生宿舍A栋', '13811112222', 1, 2, 30),
('小米台灯 护眼款', 3, 45.00, '8成新', '宿舍换床位闲置，亮度三档可调，无瑕疵。', NULL, '菜鸟驿站门口', '13833334444', 1, 3, 8),
('篮球 斯伯丁 7号', 4, 60.00, '9成新', '打球少，气足，送打气筒。', NULL, '体育馆篮球场', '13855556666', 1, 4, 20),
('考研英语真题（已售）', 1, 30.00, '8成新', '黄皮书一套，含答案解析。', NULL, '图书馆一楼', '13833334444', 2, 3, 45);

-- 失物招领（type=1失物 / 2拾物）
INSERT INTO `lost_found` (type, name, category_id, place, happen_time, feature, contact, image, status, user_id, view_count) VALUES
(1, '校园一卡通', 6, '第一食堂二楼', '2026-06-10 12:30:00', '卡面有蓝色卡套，姓名张三，丢失后请联系。', '13811112222', NULL, 1, 2, 18),
(1, '黑色雨伞', 10, '图书馆门口', '2026-06-11 18:00:00', '全自动黑色长柄伞，伞柄有划痕。', '13855556666', NULL, 1, 4, 5),
(2, '一串钥匙', 8, '操场看台', '2026-06-12 17:20:00', '捡到三把钥匙，带一个卡通熊挂件，现保管在宿管处。', '13833334444', NULL, 3, 3, 9),
(2, '白色蓝牙耳机', 7, '三号教学楼301', '2026-06-13 10:15:00', '捡到一只白色无线耳机充电盒，失主请描述特征认领。', '13811112222', NULL, 3, 2, 14),
(1, '英语四级准考证', 9, '考试中心', '2026-06-09 09:00:00', '准考证及身份证一份，急用，麻烦联系。', '13855556666', NULL, 2, 4, 25);

-- 留言
INSERT INTO `message` (target_type, target_id, user_id, content) VALUES
(1, 1, 3, '请问这本书还在吗？可以便宜一点吗？'),
(1, 2, 4, '键盘还在的话我想要，怎么联系？'),
(2, 1, 3, '我好像在食堂见过类似的卡，已经私信你了。');

SELECT '数据库初始化完成！' AS message;
