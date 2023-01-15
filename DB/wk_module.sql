SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for undo_log
-- ----------------------------
DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE `undo_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `branch_id` bigint NOT NULL,
  `xid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `context` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `rollback_info` longblob NOT NULL,
  `log_status` int NOT NULL,
  `log_created` datetime NOT NULL,
  `log_modified` datetime NOT NULL,
  `ext` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `ux_undo_log`(`xid` ASC, `branch_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of undo_log
-- ----------------------------

-- ----------------------------
-- Table structure for wk_app_category
-- ----------------------------
DROP TABLE IF EXISTS `wk_app_category`;
CREATE TABLE `wk_app_category`  (
  `id` bigint NOT NULL COMMENT '主键',
  `application_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '应用ID',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `wk_app_category_id_uindex`(`id` ASC) USING BTREE,
  INDEX `wk_app_category_category_id_index`(`category_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '应用分类关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_app_category
-- ----------------------------

-- ----------------------------
-- Table structure for wk_app_install_record
-- ----------------------------
DROP TABLE IF EXISTS `wk_app_install_record`;
CREATE TABLE `wk_app_install_record`  (
  `id` bigint NOT NULL COMMENT '主键 ID',
  `application_id` bigint NOT NULL COMMENT '安装后的应用 ID',
  `version` int NOT NULL COMMENT '安装的版本',
  `source_id` bigint NOT NULL COMMENT '源应用 ID',
  `create_time` datetime NOT NULL COMMENT '安装时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '应用安装记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_app_install_record
-- ----------------------------

-- ----------------------------
-- Table structure for wk_app_published
-- ----------------------------
DROP TABLE IF EXISTS `wk_app_published`;
CREATE TABLE `wk_app_published`  (
  `id` bigint NOT NULL COMMENT '主键 ID',
  `source_id` bigint NOT NULL COMMENT '源 appId',
  `app_data` json NOT NULL COMMENT '应用数据',
  `version` int NOT NULL DEFAULT 0 COMMENT '版本号',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user_id` bigint NOT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_app_published_source_id_index`(`source_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '应用发布' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_app_published
-- ----------------------------

-- ----------------------------
-- Table structure for wk_app_template_manage
-- ----------------------------
DROP TABLE IF EXISTS `wk_app_template_manage`;
CREATE TABLE `wk_app_template_manage`  (
  `id` int NOT NULL,
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user_id` bigint NOT NULL COMMENT '创建人',
  `type` int NOT NULL DEFAULT 0 COMMENT '0 其他\n1 应用模板管理员\n2 演示数据管理员',
  `status` int NOT NULL DEFAULT 0 COMMENT '0 禁用 1 正常',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '应用模板管理' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_app_template_manage
-- ----------------------------

-- ----------------------------
-- Table structure for wk_bi_dashboard
-- ----------------------------
DROP TABLE IF EXISTS `wk_bi_dashboard`;
CREATE TABLE `wk_bi_dashboard`  (
  `module_id` bigint NOT NULL COMMENT '自增ID',
  `application_id` bigint NOT NULL COMMENT '应用ID',
  `name` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '工作空间名称',
  `icon` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '图标',
  `icon_color` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '图标颜色',
  `type` int NULL DEFAULT NULL COMMENT '类型 1为仪表盘，暂时没其他选项',
  `status` int NULL DEFAULT 0 COMMENT '状态 0 草稿 1 对内发布 2 对外发布',
  `share_pass` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '对外分享密码',
  `refresh_time` int NULL DEFAULT 0 COMMENT '自动刷新时间，单位为分钟 0代表不自动刷新',
  `ws_style` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '仪表盘样式',
  `order_num` int NULL DEFAULT NULL COMMENT '排序字段',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`module_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = 'bi应用表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_bi_dashboard
-- ----------------------------

-- ----------------------------
-- Table structure for wk_bi_dashboard_user
-- ----------------------------
DROP TABLE IF EXISTS `wk_bi_dashboard_user`;
CREATE TABLE `wk_bi_dashboard_user`  (
  `id` bigint NOT NULL,
  `module_id` bigint NOT NULL COMMENT '仪表盘ID',
  `type` int NULL DEFAULT NULL COMMENT '类型 1 关联用户 2 关联部门',
  `type_id` bigint NULL DEFAULT NULL COMMENT '对应类型ID',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '仪表盘和用户关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_bi_dashboard_user
-- ----------------------------

-- ----------------------------
-- Table structure for wk_bi_element
-- ----------------------------
DROP TABLE IF EXISTS `wk_bi_element`;
CREATE TABLE `wk_bi_element`  (
  `element_id` bigint NOT NULL COMMENT '主键ID',
  `module_id` bigint NOT NULL COMMENT '应用id',
  `name` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '组件名称',
  `type` int NULL DEFAULT NULL COMMENT '组件类型，详见枚举',
  `coordinate` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'json类型 x 横轴坐标 y 纵轴坐标 w 组件宽度 h 组件高度 ',
  `ws_style` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '自定义其他样式',
  `target_id` bigint NULL DEFAULT NULL COMMENT '数据来源ID',
  `target_type` int NULL DEFAULT NULL COMMENT '数据来源类型1 crm 2 低代码 3',
  `target_category_id` bigint NULL DEFAULT NULL COMMENT '数据来源分类ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`element_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = 'bi组件表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_bi_element
-- ----------------------------

-- ----------------------------
-- Table structure for wk_bi_element_field
-- ----------------------------
DROP TABLE IF EXISTS `wk_bi_element_field`;
CREATE TABLE `wk_bi_element_field`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `element_id` bigint NULL DEFAULT NULL COMMENT '组件ID',
  `module_id` bigint NULL DEFAULT NULL COMMENT '模块ID',
  `field_id` bigint NULL DEFAULT NULL COMMENT '自定义字段ID，可能为空',
  `form_type` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '字段类型',
  `field_name` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '自定义字段名称',
  `name` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '自定义字段展示名称',
  `data_type` int NULL DEFAULT NULL COMMENT '字段数据类型，1、左维度字段 2 右维度字段 3 左指标字段 4 右指标字段 5 过滤条件',
  `order_type` int NULL DEFAULT NULL COMMENT '字段排序方式 0 默认 1 升序 2 降序',
  `field_text` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '维度字段和指标字段为汇总方式，过滤条件为搜索条件',
  `extra_text` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '其余额外信息 json类型',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '组件字段信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_bi_element_field
-- ----------------------------

-- ----------------------------
-- Table structure for wk_category
-- ----------------------------
DROP TABLE IF EXISTS `wk_category`;
CREATE TABLE `wk_category`  (
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '分类名称',
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父级分类',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `status` int NOT NULL DEFAULT 1 COMMENT '0 关闭\n1 开启',
  `type` int NOT NULL DEFAULT 0 COMMENT '分类类型\n0 自定义\n1 收藏',
  `is_system` tinyint(1) NULL DEFAULT 0 COMMENT '系统分类',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`category_id`) USING BTREE,
  UNIQUE INDEX `wk_app_category_category_id_uindex`(`category_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '应用分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_category
-- ----------------------------

-- ----------------------------
-- Table structure for wk_custom_button
-- ----------------------------
DROP TABLE IF EXISTS `wk_custom_button`;
CREATE TABLE `wk_custom_button`  (
  `id` bigint NOT NULL COMMENT '主键',
  `button_id` bigint NOT NULL COMMENT '按钮ID',
  `button_name` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '按钮名称',
  `status` int NOT NULL DEFAULT 1 COMMENT '0 禁用 1 启用',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '应用图标',
  `icon_color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标颜色',
  `effect_type` int NOT NULL DEFAULT 0 COMMENT '生效类型: 0  总是触发, 1 满足条件触发',
  `effect_config` json NULL COMMENT '触发条件',
  `execute_type` int NOT NULL DEFAULT 0 COMMENT '执行类型: 0  立即, 1 二次确认 2 填写内容',
  `recheck_config` json NULL COMMENT '二次确认配置',
  `fill_config` json NULL COMMENT '填写配置',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_custom_button_module_id_version_index`(`module_id` ASC, `version` ASC) USING BTREE,
  INDEX `wk_custom_button_button_id_version_index`(`button_id` ASC, `version` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '自定义按钮' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_custom_button
-- ----------------------------

-- ----------------------------
-- Table structure for wk_custom_category
-- ----------------------------
DROP TABLE IF EXISTS `wk_custom_category`;
CREATE TABLE `wk_custom_category`  (
  `id` bigint NOT NULL COMMENT '主键',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `category_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类名字',
  `type` int NOT NULL DEFAULT 1 COMMENT '类型 0 默认 1 自定义分类',
  `sort` int NOT NULL COMMENT '排序',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '自定义模块分类' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_custom_category
-- ----------------------------

-- ----------------------------
-- Table structure for wk_custom_category_field
-- ----------------------------
DROP TABLE IF EXISTS `wk_custom_category_field`;
CREATE TABLE `wk_custom_category_field`  (
  `id` bigint NOT NULL COMMENT '主键',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `field_id` bigint NOT NULL COMMENT '字段ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字段名称',
  `is_hide` int NOT NULL DEFAULT 1 COMMENT '是否隐藏 0、不隐藏 1、隐藏',
  `is_null` int NULL DEFAULT 0 COMMENT '是否必填 1 是 0 否',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '自定义模块分类字段' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_custom_category_field
-- ----------------------------

-- ----------------------------
-- Table structure for wk_custom_category_rule
-- ----------------------------
DROP TABLE IF EXISTS `wk_custom_category_rule`;
CREATE TABLE `wk_custom_category_rule`  (
  `id` bigint NOT NULL COMMENT '主键',
  `rule_id` bigint NOT NULL COMMENT '规则ID',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `from` bigint NOT NULL COMMENT '数据来源分组ID',
  `to` bigint NOT NULL COMMENT '数据去向分组ID',
  `formula` json NOT NULL COMMENT '公式',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '自定义模块分类规则' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_custom_category_rule
-- ----------------------------

-- ----------------------------
-- Table structure for wk_custom_component
-- ----------------------------
DROP TABLE IF EXISTS `wk_custom_component`;
CREATE TABLE `wk_custom_component`  (
  `id` bigint NOT NULL COMMENT '主键',
  `component_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '组件名称',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `module_id` bigint NOT NULL COMMENT '模块 ID',
  `layout` json NULL COMMENT '组件布局',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user_id` bigint NOT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '自定义组件' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_custom_component
-- ----------------------------

-- ----------------------------
-- Table structure for wk_custom_notice
-- ----------------------------
DROP TABLE IF EXISTS `wk_custom_notice`;
CREATE TABLE `wk_custom_notice`  (
  `id` bigint NOT NULL COMMENT '主键',
  `notice_id` bigint NOT NULL COMMENT '提醒ID',
  `notice_name` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '提醒名称',
  `status` int NOT NULL DEFAULT 1 COMMENT '0 禁用 1 启用',
  `effect_type` int NOT NULL DEFAULT 0 COMMENT '生效类型: 0  新增数据, 1 更新数据 2 更新指定字段 3 根据模块时间字段 4 自定义时间',
  `update_fields` json NULL COMMENT '指定更新字段',
  `time_field_config` json NULL COMMENT '模块时间字段配置',
  `effect_time` datetime NULL DEFAULT NULL COMMENT '生效时间',
  `repeat_period` json NULL COMMENT '重复周期',
  `effect_config` json NULL COMMENT '生效条件',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_custom_notice_module_id_version_index`(`module_id` ASC, `version` ASC) USING BTREE,
  INDEX `wk_custom_notice_notice_id_version_index`(`notice_id` ASC, `version` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '自定义提醒' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_custom_notice
-- ----------------------------

-- ----------------------------
-- Table structure for wk_custom_notice_receiver
-- ----------------------------
DROP TABLE IF EXISTS `wk_custom_notice_receiver`;
CREATE TABLE `wk_custom_notice_receiver`  (
  `id` bigint NOT NULL COMMENT '主键',
  `notice_id` bigint NOT NULL COMMENT '提醒ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '提醒内容',
  `notice_creator` tinyint(1) NOT NULL DEFAULT 0 COMMENT '通知创建人',
  `notice_owner` tinyint(1) NOT NULL DEFAULT 0 COMMENT '通知负责人',
  `notice_user` json NULL COMMENT '指定成员',
  `user_field` json NULL COMMENT '人员字段',
  `dept_field` json NULL COMMENT '部门字段',
  `notice_role` json NULL COMMENT '指定角色',
  `parent_level` json NULL COMMENT '负责人上级',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_custom_notice_receiver_module_id_version_index`(`module_id` ASC, `version` ASC) USING BTREE,
  INDEX `wk_custom_notice_receiver_notice_id_version_index`(`notice_id` ASC, `version` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '自定义提醒接收配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_custom_notice_receiver
-- ----------------------------

-- ----------------------------
-- Table structure for wk_custom_notice_record
-- ----------------------------
DROP TABLE IF EXISTS `wk_custom_notice_record`;
CREATE TABLE `wk_custom_notice_record`  (
  `id` bigint NOT NULL COMMENT '主键',
  `data_id` bigint NOT NULL COMMENT '数据ID',
  `notice_id` bigint NOT NULL COMMENT '提醒ID',
  `status` int NOT NULL DEFAULT 0 COMMENT '0 未处理 1 已发送 2 废弃',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '批次ID',
  `repeat_count` int NOT NULL DEFAULT 0 COMMENT '重复次数',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `last_deal_time` datetime NULL DEFAULT NULL COMMENT '上次处理时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '自定义提醒记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_custom_notice_record
-- ----------------------------

-- ----------------------------
-- Table structure for wk_external_application
-- ----------------------------
DROP TABLE IF EXISTS `wk_external_application`;
CREATE TABLE `wk_external_application`  (
  `application_id` bigint NOT NULL COMMENT '应用ID',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '应用名称',
  `key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '应用标识',
  `status` int NOT NULL DEFAULT 1 COMMENT '0 停用 1 开启',
  `type` int NOT NULL DEFAULT 1 COMMENT '1 自用系统 2 其他',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '应用描述',
  `detail` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '应用详情描述',
  `sort` int UNSIGNED NOT NULL DEFAULT 999 COMMENT '排序 从小到大',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`application_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '外部应用' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_external_application
-- ----------------------------

-- ----------------------------
-- Table structure for wk_external_module
-- ----------------------------
DROP TABLE IF EXISTS `wk_external_module`;
CREATE TABLE `wk_external_module`  (
  `id` bigint NOT NULL COMMENT '主键',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模块名称',
  `key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模块标识',
  `status` int NOT NULL DEFAULT 1 COMMENT '0 停用 1 开启',
  `type` int NOT NULL DEFAULT 1 COMMENT '1 自用系统 2 其他',
  `application_id` bigint NOT NULL COMMENT '应用ID',
  `sort` int UNSIGNED NOT NULL DEFAULT 999 COMMENT '排序 从小到大',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '外部应用模块' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_external_module
-- ----------------------------

-- ----------------------------
-- Table structure for wk_external_module_field_mapping
-- ----------------------------
DROP TABLE IF EXISTS `wk_external_module_field_mapping`;
CREATE TABLE `wk_external_module_field_mapping`  (
  `id` bigint NOT NULL,
  `field_id` bigint NULL DEFAULT NULL COMMENT '当前字段ID',
  `module_id` bigint NOT NULL COMMENT '当前模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `target_field_id` bigint NULL DEFAULT NULL COMMENT '目标字段ID',
  `target_field_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '目标字段名',
  `target_module_id` bigint NOT NULL COMMENT '目标模块ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user_id` bigint NOT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '外部模块字段映射' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_external_module_field_mapping
-- ----------------------------

-- ----------------------------
-- Table structure for wk_file
-- ----------------------------
DROP TABLE IF EXISTS `wk_file`;
CREATE TABLE `wk_file`  (
  `file_id` bigint NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '附件名称',
  `size` bigint NOT NULL COMMENT '附件大小（字节）',
  `create_user_id` bigint NOT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件真实路径',
  `file_type` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'file' COMMENT '文件类型,file,img',
  `type` int NULL DEFAULT NULL COMMENT '1 本地 2 阿里云oss',
  `is_public` tinyint(1) NULL DEFAULT 0 COMMENT '1 公有访问 0 私有访问',
  `batch_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次id',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改人ID',
  PRIMARY KEY (`file_id`) USING BTREE,
  INDEX `batch_id`(`batch_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '附件表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_file
-- ----------------------------

-- ----------------------------
-- Table structure for wk_flow
-- ----------------------------
DROP TABLE IF EXISTS `wk_flow`;
CREATE TABLE `wk_flow`  (
  `id` bigint NOT NULL COMMENT '主键',
  `flow_id` bigint NOT NULL COMMENT '流程ID',
  `flow_metadata_id` bigint NOT NULL COMMENT '流程元数据ID',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `condition_id` bigint NULL DEFAULT 0 COMMENT '条件ID',
  `flow_name` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '流程名称',
  `flow_type` int NOT NULL COMMENT '流程类型  0 条件节点 1 审批节点 2 填写节点 3 抄送节点 4 添加数据 5 更新数据',
  `examine_error_handling` int NOT NULL DEFAULT 1 COMMENT '审批找不到用户或者条件均不满足时怎么处理 1 自动通过 2 管理员审批',
  `field_id` json NULL COMMENT '填写节点配置的字段ID',
  `content` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述文本',
  `type` int NULL DEFAULT NULL COMMENT '流程下属类型 如审批节点类型',
  `priority` int NULL DEFAULT NULL COMMENT '优先级 数字越低优先级越高',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_flow_flow_id_index`(`flow_id` ASC) USING BTREE,
  INDEX `wk_flow_module_id_flow_id_index`(`module_id` ASC, `flow_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '模块流程表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_flow
-- ----------------------------

-- ----------------------------
-- Table structure for wk_flow_condition
-- ----------------------------
DROP TABLE IF EXISTS `wk_flow_condition`;
CREATE TABLE `wk_flow_condition`  (
  `id` bigint NOT NULL COMMENT '主键',
  `condition_id` bigint NOT NULL COMMENT '条件ID',
  `condition_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '条件名称',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `flow_id` bigint NOT NULL COMMENT '审批流程ID',
  `priority` int NOT NULL COMMENT '优先级 数字越低优先级越高',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次ID',
  `flow_metadata_id` bigint NOT NULL COMMENT '流程元数据ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '流程条件表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_flow_condition
-- ----------------------------

-- ----------------------------
-- Table structure for wk_flow_condition_data
-- ----------------------------
DROP TABLE IF EXISTS `wk_flow_condition_data`;
CREATE TABLE `wk_flow_condition_data`  (
  `id` bigint NOT NULL,
  `rule_type` int NOT NULL COMMENT '筛选类型 1 条件筛选 2 更新节点筛选 3 更新节点更新 4 更新节点添加 5 添加节点添加',
  `type_id` bigint NOT NULL COMMENT '对应类型ID',
  `model` int NOT NULL COMMENT '模式：0 简单 1 高级',
  `type` int NULL DEFAULT NULL COMMENT '类型：0 自定义 1 匹配字段',
  `target_module_id` bigint NULL DEFAULT NULL COMMENT '目标模块ID',
  `search` json NULL COMMENT '筛选/更新规则',
  `group_id` int NOT NULL COMMENT '分组ID',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次ID',
  `flow_metadata_id` bigint NOT NULL COMMENT '流程元数据ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user_id` bigint NOT NULL COMMENT '创建人',
  `module_id` bigint NOT NULL COMMENT '当前模块ID',
  `version` int NOT NULL COMMENT '版本号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '审批条件扩展字段表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_flow_condition_data
-- ----------------------------

-- ----------------------------
-- Table structure for wk_flow_copy
-- ----------------------------
DROP TABLE IF EXISTS `wk_flow_copy`;
CREATE TABLE `wk_flow_copy`  (
  `id` bigint NOT NULL,
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `flow_id` bigint NULL DEFAULT NULL COMMENT '流程ID',
  `user_ids` json NULL COMMENT '用户列表',
  `parent_levels` json NULL COMMENT '上级级别list',
  `role_ids` json NULL COMMENT '角色list',
  `is_self` int NULL DEFAULT NULL COMMENT '1是0否，是否给自己发送',
  `is_add` int NULL DEFAULT NULL COMMENT '是否允许发起人添加抄送人',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次ID',
  `flow_metadata_id` bigint NOT NULL COMMENT '流程元数据ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '流程抄送节点配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_flow_copy
-- ----------------------------

-- ----------------------------
-- Table structure for wk_flow_data_deal_record
-- ----------------------------
DROP TABLE IF EXISTS `wk_flow_data_deal_record`;
CREATE TABLE `wk_flow_data_deal_record`  (
  `id` bigint NOT NULL COMMENT '主键',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '原处理记录ID',
  `is_main` tinyint(1) NULL DEFAULT 1 COMMENT '主记录标识',
  `user_id` bigint NULL DEFAULT NULL COMMENT '负责人',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '负责人',
  `record_id` bigint NOT NULL COMMENT '审核记录ID',
  `flow_id` bigint NOT NULL COMMENT '流程Id',
  `condition_id` bigint NULL DEFAULT NULL COMMENT '条件ID',
  `data_id` bigint NOT NULL COMMENT '数据ID',
  `flow_type` int NULL DEFAULT NULL COMMENT '节点类型\n条件节点 0\n审批节点 1\n填写节点 2\n抄送节点 3\n添加数据 4\n更新数据 5',
  `source_data` blob NULL COMMENT '原始数据',
  `current_data` blob NULL COMMENT '当前数据',
  `flow_status` int NOT NULL COMMENT '流程状态 0 待处理 1 已处理 3 处理中',
  `type` int NULL DEFAULT NULL COMMENT '1 依次审批 2 会签 3 或签',
  `invalid_type` int NULL DEFAULT NULL COMMENT '失效类型 1 转交失效 2 撤回失效',
  `role_id` bigint NULL DEFAULT NULL COMMENT '角色ID',
  `sort` int NULL DEFAULT NULL COMMENT '排序',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '批次ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `overtime_type` int NULL DEFAULT NULL COMMENT '超时类型 1 自动提醒 2 自动转交 3 自动同意',
  `time_value` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '超时时间',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `remark` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '备注',
  `ext_data` json NULL COMMENT '扩展字段',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '节点数据处理记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_flow_data_deal_record
-- ----------------------------

-- ----------------------------
-- Table structure for wk_flow_examine_continuous_superior
-- ----------------------------
DROP TABLE IF EXISTS `wk_flow_examine_continuous_superior`;
CREATE TABLE `wk_flow_examine_continuous_superior`  (
  `id` bigint NOT NULL,
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `flow_id` bigint NOT NULL COMMENT '审批流程ID',
  `role_id` bigint NULL DEFAULT NULL COMMENT '角色ID',
  `max_level` int NULL DEFAULT NULL COMMENT '角色审批的最高级别或者组织架构的第N级',
  `type` int NULL DEFAULT NULL COMMENT '1 指定角色 2 组织架构的最上级',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次ID',
  `flow_metadata_id` bigint NOT NULL COMMENT '流程元数据ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '审批流程连续多级主管审批记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_flow_examine_continuous_superior
-- ----------------------------

-- ----------------------------
-- Table structure for wk_flow_examine_member
-- ----------------------------
DROP TABLE IF EXISTS `wk_flow_examine_member`;
CREATE TABLE `wk_flow_examine_member`  (
  `id` bigint NOT NULL,
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `flow_id` bigint NOT NULL COMMENT '审批流程ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '审批人ID',
  `type` int NULL DEFAULT NULL COMMENT '1 依次审批 2 会签 3 或签',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序规则',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次ID',
  `flow_metadata_id` bigint NOT NULL COMMENT '流程元数据ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '审批流程指定成员记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_flow_examine_member
-- ----------------------------

-- ----------------------------
-- Table structure for wk_flow_examine_optional
-- ----------------------------
DROP TABLE IF EXISTS `wk_flow_examine_optional`;
CREATE TABLE `wk_flow_examine_optional`  (
  `id` bigint NOT NULL,
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `flow_id` bigint NOT NULL COMMENT '审核流程ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '审批人ID',
  `role_id` int NULL DEFAULT NULL COMMENT '角色ID',
  `choose_type` int NULL DEFAULT NULL COMMENT '选择类型 1 自选一人 2 自选多人',
  `type` int NULL DEFAULT NULL COMMENT '1 依次审批 2 会签 3 或签',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序规则',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次ID',
  `flow_metadata_id` bigint NOT NULL COMMENT '流程元数据ID',
  `range_type` int NULL DEFAULT NULL COMMENT '选择范围 1 全公司 2 指定成员 3 指定角色 ',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '审批流程自选成员记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_flow_examine_optional
-- ----------------------------

-- ----------------------------
-- Table structure for wk_flow_examine_record
-- ----------------------------
DROP TABLE IF EXISTS `wk_flow_examine_record`;
CREATE TABLE `wk_flow_examine_record`  (
  `record_id` bigint NOT NULL COMMENT '审核记录ID',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `flow_metadata_id` bigint NOT NULL COMMENT '流程元数据ID',
  `flow_id` bigint NOT NULL COMMENT '流程ID',
  `data_id` bigint NULL DEFAULT NULL COMMENT '关联模块数据Id',
  `examine_status` int NULL DEFAULT NULL COMMENT '审核状态\r\n0 未审核 1 审核通过 2 审核拒绝\r\n3 审核中 4 已撤回   5 未提交\r\n7 失败   8 作废     9 忽略',
  `type_id` bigint NOT NULL DEFAULT 0 COMMENT '类型ID',
  `flow_metadata_type` int NOT NULL DEFAULT 0 COMMENT '流程类型 0 系统 1 自定义按钮',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user_id` bigint NOT NULL COMMENT '创建人',
  `update_user_id` bigint NOT NULL COMMENT '修改人',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`record_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '审核记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_flow_examine_record
-- ----------------------------

-- ----------------------------
-- Table structure for wk_flow_examine_record_optional
-- ----------------------------
DROP TABLE IF EXISTS `wk_flow_examine_record_optional`;
CREATE TABLE `wk_flow_examine_record_optional`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `module_id` bigint NOT NULL COMMENT '模块Id',
  `version` int NOT NULL COMMENT '版本号',
  `flow_id` bigint NOT NULL COMMENT '流程ID',
  `record_id` bigint NOT NULL COMMENT '审核记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `sort` int NOT NULL DEFAULT 1 COMMENT '排序。从小到大',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次ID',
  `flow_metadata_id` bigint NOT NULL COMMENT '流程元数据ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '审核自选成员选择成员表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_flow_examine_record_optional
-- ----------------------------

-- ----------------------------
-- Table structure for wk_flow_examine_role
-- ----------------------------
DROP TABLE IF EXISTS `wk_flow_examine_role`;
CREATE TABLE `wk_flow_examine_role`  (
  `id` bigint NOT NULL,
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `flow_id` bigint NOT NULL COMMENT '审核流程ID',
  `role_id` bigint NULL DEFAULT NULL COMMENT '角色ID',
  `type` int NULL DEFAULT NULL COMMENT '2 会签 3 或签',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次ID',
  `flow_metadata_id` bigint NOT NULL COMMENT '流程元数据ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '审批流程角色审批记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_flow_examine_role
-- ----------------------------

-- ----------------------------
-- Table structure for wk_flow_examine_superior
-- ----------------------------
DROP TABLE IF EXISTS `wk_flow_examine_superior`;
CREATE TABLE `wk_flow_examine_superior`  (
  `id` bigint NOT NULL,
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `flow_id` bigint NOT NULL COMMENT '审核流程ID',
  `parent_level` int NULL DEFAULT NULL COMMENT '直属上级级别 1 代表直属上级 2 代表 直属上级的上级',
  `type` int NULL DEFAULT NULL COMMENT '找不到上级时，是否由上一级上级代审批 0 否 1 是',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次ID',
  `flow_metadata_id` bigint NOT NULL COMMENT '流程元数据ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '审批流程主管审批记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_flow_examine_superior
-- ----------------------------

-- ----------------------------
-- Table structure for wk_flow_field_auth
-- ----------------------------
DROP TABLE IF EXISTS `wk_flow_field_auth`;
CREATE TABLE `wk_flow_field_auth`  (
  `id` bigint NOT NULL,
  `module_id` bigint NULL DEFAULT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `flow_id` bigint NULL DEFAULT NULL COMMENT '流程ID',
  `auth` json NOT NULL COMMENT '授权',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次ID',
  `flow_metadata_id` bigint NOT NULL COMMENT '流程元数据ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '节点字段授权' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_flow_field_auth
-- ----------------------------

-- ----------------------------
-- Table structure for wk_flow_metadata
-- ----------------------------
DROP TABLE IF EXISTS `wk_flow_metadata`;
CREATE TABLE `wk_flow_metadata`  (
  `metadata_id` bigint NOT NULL COMMENT '主键ID',
  `type` int NOT NULL DEFAULT 0 COMMENT '流程类型 0 系统 1 自定义按钮 2 阶段',
  `type_id` bigint NOT NULL DEFAULT 0 COMMENT '对应类型ID',
  `status` int NULL DEFAULT NULL COMMENT '状态 1 正常 2 停用 3 删除',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次ID',
  `module_id` bigint NULL DEFAULT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`metadata_id`) USING BTREE,
  INDEX `wk_flow_metadata_module_id_version_index`(`module_id` ASC, `version` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '模块自定义流程元数据表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_flow_metadata
-- ----------------------------

-- ----------------------------
-- Table structure for wk_flow_save
-- ----------------------------
DROP TABLE IF EXISTS `wk_flow_save`;
CREATE TABLE `wk_flow_save`  (
  `id` bigint NOT NULL,
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `flow_id` bigint NULL DEFAULT NULL COMMENT '流程ID',
  `target_module_id` bigint NULL DEFAULT NULL COMMENT '目标模块',
  `owner_user_id` bigint NULL DEFAULT NULL COMMENT '负责人 0 代表模块创建人',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次ID',
  `flow_metadata_id` bigint NOT NULL COMMENT '流程元数据ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '流程更新数据节点配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_flow_save
-- ----------------------------

-- ----------------------------
-- Table structure for wk_flow_time_limit
-- ----------------------------
DROP TABLE IF EXISTS `wk_flow_time_limit`;
CREATE TABLE `wk_flow_time_limit`  (
  `id` bigint NOT NULL,
  `module_id` bigint NULL DEFAULT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `flow_id` bigint NULL DEFAULT NULL COMMENT '流程ID',
  `is_send_message` int NULL DEFAULT NULL COMMENT '是否进入此节点时给处理人发消息通知 1 是 0 否',
  `allow_transfer` int NULL DEFAULT NULL COMMENT '是否允许转交 1 是 0 否',
  `transfer_user_ids` json NULL COMMENT '允许转交的用户ID，空数组代表全部',
  `open_feedback` int NULL DEFAULT NULL COMMENT '反馈是否必填 1 是 0 否',
  `recheck_type` int NULL DEFAULT NULL COMMENT ' 审批被拒后重新提交 1 从第一层开始 2 从拒绝的层级开始',
  `open_time_limit` int NULL DEFAULT NULL COMMENT '是否开启限时处理 1 是 0 否',
  `time_value` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '限时处理的值 d代表天，m代表分钟 h代表小时 例 10d代表10天',
  `overtime_type` int NULL DEFAULT NULL COMMENT '超时类型 1 自动提醒 2 自动转交 3 自动同意',
  `user_ids` json NULL COMMENT '自动提醒和自动转交的人员列表',
  `examine_type` int NULL DEFAULT NULL COMMENT '自动转交的审批类型 1 依次审批 2 会签 3 或签',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次ID',
  `flow_metadata_id` bigint NOT NULL COMMENT '流程元数据ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '流程限时处理设置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_flow_time_limit
-- ----------------------------

-- ----------------------------
-- Table structure for wk_flow_update
-- ----------------------------
DROP TABLE IF EXISTS `wk_flow_update`;
CREATE TABLE `wk_flow_update`  (
  `id` bigint NOT NULL,
  `module_id` bigint NULL DEFAULT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `flow_id` bigint NULL DEFAULT NULL COMMENT '流程ID',
  `target_module_id` bigint NULL DEFAULT NULL COMMENT '目标模块ID',
  `is_insert` int NULL DEFAULT NULL COMMENT '查找不到数据是否添加',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次ID',
  `flow_metadata_id` bigint NOT NULL COMMENT '流程元数据ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '流程更新数据节点配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_flow_update
-- ----------------------------

-- ----------------------------
-- Table structure for wk_message
-- ----------------------------
DROP TABLE IF EXISTS `wk_message`;
CREATE TABLE `wk_message`  (
  `message_id` bigint NOT NULL COMMENT '消息ID',
  `value` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '内容',
  `data_id` bigint NULL DEFAULT NULL COMMENT '数据ID',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `module_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模块名称',
  `type_id` bigint NULL DEFAULT NULL COMMENT '类型ID',
  `type_name` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '对应类型名称',
  `status` int NULL DEFAULT NULL COMMENT '审批状态',
  `type` int NOT NULL DEFAULT 0 COMMENT '类型 0 系统 1 节点 2 自定义提醒 3 自定义按钮 4团队成员添加移除',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次ID',
  `time_value` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '限时处理的值 d代表天，m代表分钟 h代表小时 例 10d代表10天',
  `create_user_id` bigint NOT NULL COMMENT '消息创建者 0为系统',
  `receiver` bigint NOT NULL COMMENT '接收人',
  `read_time` datetime NULL DEFAULT NULL COMMENT '已读时间',
  `is_read` int NULL DEFAULT 0 COMMENT '是否已读 0 未读 1 已读',
  `ext_data` json NULL COMMENT '扩展字段',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`message_id`) USING BTREE,
  INDEX `wk_message_receiver_index`(`receiver` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统消息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_message
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module
-- ----------------------------
DROP TABLE IF EXISTS `wk_module`;
CREATE TABLE `wk_module`  (
  `id` bigint NOT NULL COMMENT '主键',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `application_id` bigint NOT NULL COMMENT '应用ID',
  `main_field_id` bigint NULL DEFAULT NULL COMMENT '主字段ID',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '应用图标',
  `icon_color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标颜色',
  `index_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '索引名称',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模块名称',
  `type` int NOT NULL DEFAULT 0 COMMENT '0 自定义模块 1 自用系统 2 其他',
  `relate_module_id` bigint NULL DEFAULT NULL COMMENT '关联的模块',
  `manage_user_id` json NULL COMMENT '流程管理员',
  `sort` int UNSIGNED NOT NULL DEFAULT 999 COMMENT '排序 从小到大',
  `is_active` tinyint(1) NULL DEFAULT 0 COMMENT '已激活',
  `status` int NULL DEFAULT 0 COMMENT '0 停用 1 正常 2 草稿',
  `module_type` int NULL DEFAULT 1 COMMENT '模块类型 1 无代码模块 2 自定义bi模块',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_module_module_id_version_index`(`module_id` ASC, `version` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '应用模块' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_data_check_rule
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_data_check_rule`;
CREATE TABLE `wk_module_data_check_rule`  (
  `id` bigint NOT NULL COMMENT '主键',
  `rule_id` bigint NOT NULL COMMENT '规则ID',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `formula` json NOT NULL COMMENT '公式',
  `tip` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '提示',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_module_data_check_module_id_version_index`(`module_id` ASC, `version` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据校验表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_data_check_rule
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_data_operation_record
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_data_operation_record`;
CREATE TABLE `wk_module_data_operation_record`  (
  `id` bigint NOT NULL COMMENT '主键',
  `module_id` bigint NULL DEFAULT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `data_id` bigint NOT NULL COMMENT '数据ID',
  `value` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '值',
  `action_type` int NOT NULL COMMENT '操作类型: 0 新建,1 编辑,2 删除',
  `from_user_id` bigint NULL DEFAULT NULL COMMENT '原负责人ID',
  `to_user_id` bigint NULL DEFAULT NULL COMMENT '现负责人ID',
  `team_user_id` bigint NULL DEFAULT NULL COMMENT '团队成员',
  `flow_id` bigint NULL DEFAULT NULL COMMENT '节点ID',
  `examine_record_id` bigint NULL DEFAULT NULL COMMENT '审核记录ID',
  `ext_data` json NULL COMMENT '扩展数据',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `remarks` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `data_id`(`data_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字段值操作记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_data_operation_record
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_field
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_field`;
CREATE TABLE `wk_module_field`  (
  `id` bigint NOT NULL COMMENT '主键',
  `field_id` bigint NOT NULL COMMENT '主键ID',
  `group_id` int NULL DEFAULT NULL COMMENT '分组ID',
  `field_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '自定义字段英文标识',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '字段名称',
  `type` int NOT NULL DEFAULT 1 COMMENT '字段类型',
  `field_type` int NOT NULL DEFAULT 1 COMMENT '0 系统 1 自定义',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `version` int NULL DEFAULT NULL COMMENT '版本号',
  `remark` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字段说明',
  `input_tips` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '输入提示',
  `max_length` int NULL DEFAULT NULL COMMENT '最大长度',
  `is_unique` int NULL DEFAULT 0 COMMENT '是否唯一 1 是 0 否',
  `is_null` int NULL DEFAULT 0 COMMENT '是否必填 1 是 0 否',
  `sorting` int NULL DEFAULT 1 COMMENT '排序 从小到大',
  `operating` int NULL DEFAULT 255 COMMENT '操作指令',
  `is_hidden` int NOT NULL DEFAULT 0 COMMENT '是否隐藏  0不隐藏 1隐藏',
  `style_percent` int NULL DEFAULT 50 COMMENT '样式百分比',
  `precisions` int NULL DEFAULT NULL COMMENT '精度，允许的最大小数位',
  `form_position` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表单定位 坐标格式',
  `max_num_restrict` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '限制的最大数',
  `min_num_restrict` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '限制的最小数',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `moduleId_fieldId_version`(`module_id` ASC, `field_id` ASC, `version` ASC) USING BTREE,
  INDEX `label`(`module_id` ASC) USING BTREE,
  INDEX `update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '自定义字段表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_field
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_field_config
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_field_config`;
CREATE TABLE `wk_module_field_config`  (
  `id` bigint NOT NULL COMMENT 'id',
  `field_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字段名称',
  `field_type` int NOT NULL DEFAULT 1 COMMENT '字段类型 1 keyword 2 date 3 number 4 nested 5 datetime',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `field_name`(`field_name` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字段配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_field_config
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_field_data
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_field_data`;
CREATE TABLE `wk_module_field_data`  (
  `id` bigint NOT NULL COMMENT 'id',
  `data_id` bigint NOT NULL COMMENT '数据ID',
  `module_id` bigint NULL DEFAULT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `field_id` bigint NOT NULL COMMENT '字段ID',
  `field_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '自定义字段英文标识',
  `value` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '值',
  `is_main` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否是主字段',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `module_field_data_index_module_id_version`(`module_id` ASC, `version` ASC) USING BTREE,
  INDEX `module_field_data_index_data_id`(`data_id` ASC) USING BTREE,
  INDEX `wk_module_field_data_module_id_field_id_index`(`module_id` ASC, `field_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '模块字段值表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_field_data
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_field_data_common
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_field_data_common`;
CREATE TABLE `wk_module_field_data_common`  (
  `id` bigint NOT NULL COMMENT '主键',
  `data_id` bigint NOT NULL COMMENT '数据ID',
  `create_user_id` bigint NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `owner_user_id` bigint NOT NULL COMMENT '负责人ID',
  `team_member` json NULL COMMENT '团队成员',
  `type` int NULL DEFAULT 0 COMMENT '类型 0 审批 1 其他',
  `current_flow_id` bigint NULL DEFAULT NULL COMMENT '当前节点',
  `flow_type` int NULL DEFAULT NULL COMMENT '节点类型 0 条件 1 审批节点 2 填写节点 3 抄送节点 4 添加数据 5 更新节点',
  `flow_status` int NULL DEFAULT NULL COMMENT '节点状态 0 待处理 1 已处理 2 拒绝 3 处理中 4 撤回 5 未提交 8 作废 9 忽略',
  `category_id` bigint NULL DEFAULT NULL COMMENT '分类ID',
  `stage_id` bigint NULL DEFAULT NULL COMMENT '阶段ID',
  `stage_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '阶段名称',
  `stage_status` int NULL DEFAULT NULL COMMENT '阶段状态 0 未开始 1 完成 2 草稿 3 成功 4 失败',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次id，导入使用',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `data_id`(`data_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通用模块字段值表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_field_data_common
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_field_default
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_field_default`;
CREATE TABLE `wk_module_field_default`  (
  `id` bigint NOT NULL COMMENT '主键',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `target_module_id` bigint NULL DEFAULT NULL COMMENT '目标模块ID',
  `target_field_id` bigint NULL DEFAULT NULL COMMENT '目标字段ID',
  `field_id` bigint NULL DEFAULT NULL COMMENT '字段ID',
  `key` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '选项ID',
  `value` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '默认值',
  `type` int NOT NULL DEFAULT 1 COMMENT '默认值类型 1 固定值2 自定义筛选 3 公式',
  `search` json NULL COMMENT '筛选条件',
  `formula` json NULL COMMENT '公式',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字段默认值配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_field_default
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_field_formula
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_field_formula`;
CREATE TABLE `wk_module_field_formula`  (
  `id` bigint NOT NULL COMMENT '主键',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `field_id` bigint NULL DEFAULT NULL COMMENT '字段ID',
  `type` int NOT NULL COMMENT '数值类型 1 数字 2 金额 3 百分比 4 日期 5 日期时间 6 文本 7 布尔值',
  `formula` json NOT NULL COMMENT '公式',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_module_field_formula_field_id_version_index`(`field_id` ASC, `version` ASC) USING BTREE,
  INDEX `wk_module_field_formula_module_id_version_index`(`module_id` ASC, `version` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字段公式' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_field_formula
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_field_options
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_field_options`;
CREATE TABLE `wk_module_field_options`  (
  `id` bigint NOT NULL COMMENT '主键',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `field_id` bigint NULL DEFAULT NULL COMMENT '字段ID',
  `key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '选项ID',
  `value` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '选项值',
  `type` int NOT NULL DEFAULT 1 COMMENT '选项类型：0 普通 1 其他',
  `sorting` int NOT NULL DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_module_field_options_module_id_version_index`(`module_id` ASC, `version` ASC) USING BTREE,
  INDEX `wk_module_field_options_field_id_version_index`(`field_id` ASC, `version` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字段选项表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_field_options
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_field_serial_number
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_field_serial_number`;
CREATE TABLE `wk_module_field_serial_number`  (
  `id` bigint NOT NULL COMMENT '主键',
  `module_id` bigint NOT NULL COMMENT '模块id',
  `version` int NOT NULL COMMENT '版本号',
  `field_id` bigint NOT NULL COMMENT '自定义字段id',
  `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字段值',
  `field_number` int NULL DEFAULT 0 COMMENT '自动计数类型数据值',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user_id` bigint NOT NULL COMMENT '创建人id',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_module_field_serial_number_field_id_version_index`(`field_id` ASC, `version` ASC) USING BTREE,
  INDEX `wk_module_field_serial_number_module_id_version_index`(`module_id` ASC, `version` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '自定义编号字段值表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_field_serial_number
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_field_serial_number_rules
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_field_serial_number_rules`;
CREATE TABLE `wk_module_field_serial_number_rules`  (
  `id` bigint NOT NULL COMMENT '主键',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `field_id` bigint NULL DEFAULT NULL COMMENT '字段ID',
  `text_format` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'type为3，并且为日期和日期时间类型时需要，为需要格式化的日期格式',
  `start_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '对应type的值，1起始编号，2固定值，3字段id',
  `step_number` int NULL DEFAULT NULL COMMENT 'stepNumber  当type为1时需要，递增数;',
  `reset_type` int NULL DEFAULT NULL COMMENT ' 当type为1时需要，1 每天 2 每月 3 每年 4 从不;',
  `type` int NULL DEFAULT NULL COMMENT '类型 1 自动计数 2 固定值 3 表单内字段',
  `sorting` int NULL DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_module_field_serial_number_rules_module_id_version_index`(`module_id` ASC, `version` ASC) USING BTREE,
  INDEX `wk_module_field_serial_number_rules_field_id_version_index`(`field_id` ASC, `version` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '自定义编码规则表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_field_serial_number_rules
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_field_sort
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_field_sort`;
CREATE TABLE `wk_module_field_sort`  (
  `id` bigint NOT NULL COMMENT 'id',
  `field_id` bigint NULL DEFAULT NULL COMMENT '字段ID',
  `field_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字段名称',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `category_id` bigint NULL DEFAULT NULL COMMENT '分类ID',
  `type` int NULL DEFAULT NULL COMMENT '字段类型',
  `style` int NULL DEFAULT NULL COMMENT '字段宽度',
  `sort` int NOT NULL DEFAULT 0 COMMENT '字段排序',
  `user_id` bigint NOT NULL DEFAULT 0 COMMENT '用户id',
  `is_hide` int NOT NULL DEFAULT 1 COMMENT '是否隐藏 0、不隐藏 1、隐藏',
  `is_lock` tinyint(1) NULL DEFAULT 0 COMMENT '字段锁定',
  `is_null` tinyint NULL DEFAULT 0 COMMENT '是否必填 1 是 0 否',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字段排序表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_field_sort
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_field_tags
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_field_tags`;
CREATE TABLE `wk_module_field_tags`  (
  `id` bigint NOT NULL COMMENT '主键',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `field_id` bigint NULL DEFAULT NULL COMMENT '字段ID',
  `key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '选项ID',
  `value` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '选项值',
  `color` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '颜色',
  `sorting` int NOT NULL DEFAULT 0 COMMENT '排序',
  `group_id` int NOT NULL COMMENT '分组ID',
  `group_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分组名字',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_module_field_tags_field_id_version_index`(`field_id` ASC, `version` ASC) USING BTREE,
  INDEX `wk_module_field_tags_module_id_version_index`(`module_id` ASC, `version` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字段标签选项表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_field_tags
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_field_tree
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_field_tree`;
CREATE TABLE `wk_module_field_tree`  (
  `id` bigint NOT NULL COMMENT '主键',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `field_id` bigint NULL DEFAULT NULL COMMENT '字段ID',
  `show_field` bigint NOT NULL COMMENT '展示字段',
  `sorting` int NOT NULL DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '树字段' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_field_tree
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_field_union
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_field_union`;
CREATE TABLE `wk_module_field_union`  (
  `id` bigint NOT NULL,
  `type` int NOT NULL DEFAULT 0 COMMENT '0 字段关联 1 模块关联',
  `relate_field_id` bigint NOT NULL COMMENT '数据关联字段ID',
  `field_id` bigint NULL DEFAULT NULL COMMENT '当前字段ID',
  `module_id` bigint NOT NULL COMMENT '当前模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `target_field_id` bigint NULL DEFAULT NULL COMMENT '目标字段ID',
  `target_module_id` bigint NOT NULL COMMENT '目标模块ID',
  `target_category_ids` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '目标分类ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user_id` bigint NOT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `union_field_id`(`relate_field_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字段关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_field_union
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_field_union_condition
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_field_union_condition`;
CREATE TABLE `wk_module_field_union_condition`  (
  `id` bigint NOT NULL,
  `model` int NOT NULL COMMENT '模式：0 简单 1 高级',
  `type` int NULL DEFAULT NULL COMMENT '类型：0 自定义 1 匹配字段',
  `search` json NULL COMMENT '筛选条件',
  `group_id` int NOT NULL COMMENT '分组ID',
  `target_module_id` bigint NOT NULL COMMENT '目标模块ID',
  `relate_field_id` bigint NOT NULL COMMENT '数据关联字段ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user_id` bigint NOT NULL COMMENT '创建人',
  `module_id` bigint NOT NULL COMMENT '当前模块ID',
  `version` int NOT NULL COMMENT '版本号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据关联筛选条件表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_field_union_condition
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_file
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_file`;
CREATE TABLE `wk_module_file`  (
  `id` bigint NOT NULL,
  `data_id` bigint NOT NULL COMMENT '数据ID',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `batch_id` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件批次ID',
  `create_user_id` bigint NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '模块文件' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_file
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_group
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_group`;
CREATE TABLE `wk_module_group`  (
  `id` bigint NOT NULL COMMENT '主键-模块分组的标题的id',
  `application_id` bigint NOT NULL COMMENT '应用id',
  `group_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分组标题的名称',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
  `icon_color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标颜色',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_module_group_application_id_index`(`application_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '模块分组表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_group
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_group_sort
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_group_sort`;
CREATE TABLE `wk_module_group_sort`  (
  `id` bigint NOT NULL COMMENT '主键',
  `group_id` bigint NULL DEFAULT NULL COMMENT '分组id',
  `module_id` bigint NULL DEFAULT NULL COMMENT '模块id',
  `sort` int NULL DEFAULT NULL COMMENT '分组、模块全部在一起的排序',
  `application_id` bigint NULL DEFAULT NULL COMMENT '应用id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '分组与模块一起的排序表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_group_sort
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_layout
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_layout`;
CREATE TABLE `wk_module_layout`  (
  `id` bigint NOT NULL COMMENT '主键',
  `module_id` bigint NOT NULL COMMENT '模块Id',
  `version` int NOT NULL COMMENT '版本号',
  `data` json NOT NULL COMMENT '布局数据',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `create_user_id` bigint NOT NULL COMMENT '创建人',
  `update_user_id` bigint NOT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '模块页面布局' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_layout
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_metadata
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_metadata`;
CREATE TABLE `wk_module_metadata`  (
  `application_id` bigint NOT NULL COMMENT '应用ID',
  `source_id` bigint NULL DEFAULT NULL COMMENT '源应用 ID',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '应用名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '应用简介',
  `detail` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '应用详情描述',
  `big_picture` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '应用大图',
  `key_point` json NULL COMMENT '要点',
  `preview` json NULL COMMENT '预览',
  `is_featured` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否精选',
  `main_picture` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '主图',
  `detail_picture` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '详情图',
  `type` int NOT NULL DEFAULT 0 COMMENT '0 自定义\r\n1 安装\r\n2 导入\r\n3 自用系统\r\n4 其他',
  `relate_application_id` bigint NULL DEFAULT NULL COMMENT '关联的应用 ID',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
  `icon_color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标颜色',
  `status` int UNSIGNED NOT NULL COMMENT '状态 1 正常 2 禁用',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`application_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '应用表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_metadata
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_print_record
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_print_record`;
CREATE TABLE `wk_module_print_record`  (
  `id` bigint NOT NULL COMMENT '主键',
  `template_id` bigint NOT NULL COMMENT '模板id',
  `record_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '打印内容',
  `data_id` bigint NOT NULL COMMENT '数据ID',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user_id` bigint NOT NULL COMMENT '创建人id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '打印记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_print_record
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_print_template
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_print_template`;
CREATE TABLE `wk_module_print_template`  (
  `id` bigint NOT NULL COMMENT '主键',
  `template_id` bigint NULL DEFAULT NULL COMMENT '模板id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板名称',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '模板内容',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `create_user_id` bigint NOT NULL COMMENT '创建人id',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改人id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_module_print_template_module_id_version_index`(`module_id` ASC, `version` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '打印模板' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_print_template
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_publish_record
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_publish_record`;
CREATE TABLE `wk_module_publish_record`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `application_id` bigint NOT NULL COMMENT '应用ID',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '模块版本号',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user_id` bigint NOT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_module_publish_record_application_id_index`(`application_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '模块发布记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_publish_record
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_role
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_role`;
CREATE TABLE `wk_module_role`  (
  `role_id` bigint NOT NULL,
  `role_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `application_id` bigint NOT NULL COMMENT '应用ID',
  `range_type` int NOT NULL DEFAULT 1 COMMENT '1-本人 2-本人及下属 3-本部门 4-本部门及下属部门 5-全部',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '1 启用 0 禁用',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_user_id` bigint NOT NULL COMMENT '创建人',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_role
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_role_field
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_role_field`;
CREATE TABLE `wk_module_role_field`  (
  `id` bigint NOT NULL,
  `role_id` bigint NOT NULL COMMENT '角色id',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `field_id` bigint NULL DEFAULT NULL COMMENT '字段id',
  `auth_level` int NOT NULL COMMENT '权限 1不可编辑不可查看 2可查看不可编辑 3可编辑可查看',
  `operate_type` int NOT NULL COMMENT '操作权限 1都可以设置 2只有查看权限可设置 3只有编辑权限可设置 4都不能设置',
  `mask_type` int NULL DEFAULT 0 COMMENT '掩码类型 0 都不隐藏 1 列表隐藏详情不隐藏 2 都隐藏',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色字段授权表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_role_field
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_role_module
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_role_module`;
CREATE TABLE `wk_module_role_module`  (
  `id` bigint NOT NULL,
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `category_id` bigint NULL DEFAULT NULL COMMENT '分类ID',
  `auth` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '权限\n1: \'index\', // 查看列表\n2: \'read\', // 查看详情\n3: \'save\', // 新建\n4: \'edit\', // 编辑\n5: \'transfer\', // 转移\n6: \'delete\', // 删除\n7: \'import\', // 导入\n8: \'export\', // 导出\n9: \'print\', // 打印\n10: \'moveCategory\', // 转移到分类\n11: \'teamSave\' // 编辑团队成员',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_user_id` bigint NOT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色模块关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_role_module
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_role_user
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_role_user`;
CREATE TABLE `wk_module_role_user`  (
  `id` bigint NOT NULL,
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `user_id` bigint NOT NULL COMMENT '用户',
  `application_id` bigint NOT NULL COMMENT '应用ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_role_user
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_scene
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_scene`;
CREATE TABLE `wk_module_scene`  (
  `scene_id` bigint NOT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '场景名称',
  `module_id` bigint NULL DEFAULT NULL,
  `data` json NULL,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `sort` int NOT NULL DEFAULT 999 COMMENT '排序ID',
  `is_hide` int NOT NULL DEFAULT 0 COMMENT '1隐藏',
  `is_system` int NOT NULL DEFAULT 0 COMMENT '1全部 2 我负责的 3 我下属负责的 0 自定义',
  `is_default` int NULL DEFAULT 0 COMMENT '是否默认 0 否 1是',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`scene_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '模块场景表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_scene
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_statistic_field_union
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_statistic_field_union`;
CREATE TABLE `wk_module_statistic_field_union`  (
  `id` bigint NOT NULL,
  `relate_field_id` bigint NOT NULL COMMENT '统计字段ID',
  `module_id` bigint NOT NULL COMMENT '当前模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `target_field_id` bigint NULL DEFAULT NULL COMMENT '目标字段ID',
  `target_module_id` bigint NOT NULL COMMENT '目标模块ID',
  `statistic_type` int NOT NULL COMMENT '统计类型',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user_id` bigint NOT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `relate_field_id`(`relate_field_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '统计字段关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_statistic_field_union
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_status
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_status`;
CREATE TABLE `wk_module_status`  (
  `id` bigint NOT NULL COMMENT '主键',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `is_enable` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0 停用 1 启用',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_module_status_module_id_index`(`module_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '模块状态表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_status
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_team_member
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_team_member`;
CREATE TABLE `wk_module_team_member`  (
  `id` bigint NOT NULL,
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `data_id` bigint NOT NULL COMMENT '数据ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `power` int NULL DEFAULT NULL COMMENT '1 只读 2 读写 3 负责人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `expires_time` datetime NULL DEFAULT NULL COMMENT '过期时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_module_team_member_module_id_data_id_index`(`module_id` ASC, `data_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '团队成员表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_team_member
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_tree_data
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_tree_data`;
CREATE TABLE `wk_module_tree_data`  (
  `id` bigint NOT NULL COMMENT '主键',
  `data_id` bigint NOT NULL COMMENT '数据 ID',
  `parent_id` json NULL COMMENT '父级数据 ID',
  `child_id` json NULL COMMENT '子集数据 ID',
  `module_id` bigint NOT NULL COMMENT '模块 ID',
  `field_id` bigint NOT NULL COMMENT '字段 ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '树字段数据' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_tree_data
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_user_search_config
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_user_search_config`;
CREATE TABLE `wk_module_user_search_config`  (
  `id` bigint NOT NULL,
  `data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '配置数据',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `create_user_id` bigint NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_module_user_search_config_module_id_create_user_id_index`(`module_id` ASC, `create_user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用戶搜索配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_user_search_config
-- ----------------------------

-- ----------------------------
-- Table structure for wk_router
-- ----------------------------
DROP TABLE IF EXISTS `wk_router`;
CREATE TABLE `wk_router`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `router_id` bigint NOT NULL COMMENT '路由 ID',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '上级路由 ID',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '别名',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
  `path` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '路径',
  `permissions` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组件',
  `redirect` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '重定向',
  `hidden` tinyint(1) NULL DEFAULT 0 COMMENT '隐藏',
  `type` int NULL DEFAULT NULL COMMENT '路由类型 0 模块 1 分组',
  `is_system` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是系统级别',
  `sort` int UNSIGNED NULL DEFAULT 0 COMMENT '排序（同级有效）',
  `status` int NULL DEFAULT 1 COMMENT '状态 1 启用 0 禁用',
  `ext_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '扩展数据',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `source_module_id` bigint NULL DEFAULT NULL COMMENT '源模块 ID',
  `source_application_id` bigint NOT NULL COMMENT '源应用 ID',
  `application_id` bigint NOT NULL COMMENT '应用 ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '全局路由配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_router
-- ----------------------------

-- ----------------------------
-- Table structure for wk_stage
-- ----------------------------
DROP TABLE IF EXISTS `wk_stage`;
CREATE TABLE `wk_stage`  (
  `id` bigint NOT NULL COMMENT '主键',
  `stage_setting_id` bigint NOT NULL COMMENT '阶段流程ID',
  `stage_id` bigint NOT NULL COMMENT '阶段ID',
  `stage_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '阶段名称',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_stage_module_id_version_index`(`module_id` ASC, `version` ASC) USING BTREE,
  INDEX `wk_stage_module_id_stage_id_version_index`(`module_id` ASC, `stage_id` ASC, `version` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '阶段表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_stage
-- ----------------------------

-- ----------------------------
-- Table structure for wk_stage_comment
-- ----------------------------
DROP TABLE IF EXISTS `wk_stage_comment`;
CREATE TABLE `wk_stage_comment`  (
  `id` bigint NOT NULL COMMENT '主键',
  `comment_id` bigint NOT NULL COMMENT '评论ID',
  `stage_setting_id` bigint NOT NULL COMMENT '阶段流程ID',
  `stage_id` bigint NOT NULL COMMENT '阶段ID',
  `content` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '内容',
  `main_id` bigint NULL DEFAULT 0 COMMENT '主评论id',
  `reply_user_id` bigint NULL DEFAULT NULL COMMENT '回复评论用户ID',
  `is_delete` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  `data_id` bigint NULL DEFAULT NULL COMMENT '关联模块数据Id',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_stage_comment_data_id_index`(`data_id` ASC) USING BTREE,
  INDEX `wk_stage_comment_module_id_version_index`(`module_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '阶段评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_stage_comment
-- ----------------------------

-- ----------------------------
-- Table structure for wk_stage_data
-- ----------------------------
DROP TABLE IF EXISTS `wk_stage_data`;
CREATE TABLE `wk_stage_data`  (
  `id` bigint NOT NULL COMMENT '主键',
  `stage_setting_id` bigint NOT NULL COMMENT '阶段流程ID',
  `is_main` tinyint(1) NOT NULL DEFAULT 0 COMMENT '阶段流程主体',
  `stage_id` bigint NOT NULL COMMENT '阶段ID',
  `stage_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '阶段名称',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `data_id` bigint NULL DEFAULT NULL COMMENT '关联模块数据Id',
  `field_data` json NULL COMMENT '表单数据',
  `task_data` json NULL COMMENT '阶段工作数据',
  `status` int NULL DEFAULT 0 COMMENT '0 未开始 1 完成 2 草稿 3 成功 4 失败',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_stage_comment_module_id_version_index`(`module_id` ASC, `version` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '阶段数据表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_stage_data
-- ----------------------------

-- ----------------------------
-- Table structure for wk_stage_field
-- ----------------------------
DROP TABLE IF EXISTS `wk_stage_field`;
CREATE TABLE `wk_stage_field`  (
  `id` bigint NOT NULL COMMENT '主键',
  `stage_setting_id` bigint NOT NULL COMMENT '阶段流程ID',
  `stage_id` bigint NOT NULL COMMENT '阶段ID',
  `field_id` bigint NOT NULL COMMENT '主键ID',
  `field_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '自定义字段英文标识',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '字段名称',
  `type` int NOT NULL DEFAULT 1 COMMENT '字段类型',
  `field_type` int NOT NULL DEFAULT 1 COMMENT '0 系统 1 自定义',
  `remark` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字段说明',
  `input_tips` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '输入提示',
  `max_length` int NULL DEFAULT NULL COMMENT '最大长度',
  `is_unique` int NULL DEFAULT 0 COMMENT '是否唯一 1 是 0 否',
  `is_null` int NULL DEFAULT 0 COMMENT '是否必填 1 是 0 否',
  `sorting` int NULL DEFAULT 1 COMMENT '排序 从小到大',
  `is_hidden` int NOT NULL DEFAULT 0 COMMENT '是否隐藏  0不隐藏 1隐藏',
  `style_percent` int NULL DEFAULT 50 COMMENT '样式百分比',
  `precisions` int NULL DEFAULT NULL COMMENT '精度，允许的最大小数位',
  `form_position` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表单定位 坐标格式',
  `max_num_restrict` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '限制的最大数',
  `min_num_restrict` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '限制的最小数',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_stage_field_module_id_version_index`(`module_id` ASC, `version` ASC) USING BTREE,
  INDEX `wk_stage_field_stage_setting_id_version_index`(`stage_setting_id` ASC, `version` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '阶段字段表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_stage_field
-- ----------------------------

-- ----------------------------
-- Table structure for wk_stage_setting
-- ----------------------------
DROP TABLE IF EXISTS `wk_stage_setting`;
CREATE TABLE `wk_stage_setting`  (
  `id` bigint NOT NULL COMMENT '主键',
  `stage_setting_id` bigint NOT NULL COMMENT '阶段流程ID',
  `stage_setting_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '阶段流程名称',
  `success_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '成功阶段的名称',
  `failed_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '失败阶段的名称',
  `dept_ids` json NULL COMMENT '适用部门',
  `user_ids` json NULL COMMENT '适用员工',
  `status` int NULL DEFAULT 1 COMMENT '状态 1 正常 0 停用',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_stage_flow_module_id_version_index`(`module_id` ASC, `version` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '阶段配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_stage_setting
-- ----------------------------

-- ----------------------------
-- Table structure for wk_stage_task
-- ----------------------------
DROP TABLE IF EXISTS `wk_stage_task`;
CREATE TABLE `wk_stage_task`  (
  `id` bigint NOT NULL COMMENT '主键',
  `stage_setting_id` bigint NOT NULL COMMENT '阶段流程ID',
  `stage_id` bigint NOT NULL COMMENT '阶段ID',
  `task_id` bigint NOT NULL COMMENT '任务ID',
  `task_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务名称',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `is_must` tinyint(1) NULL DEFAULT 1 COMMENT '是否必做',
  `module_id` bigint NOT NULL COMMENT '模块ID',
  `version` int NOT NULL COMMENT '版本号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '阶段任务表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_stage_task
-- ----------------------------

-- ----------------------------
-- Table structure for wk_to_do
-- ----------------------------
DROP TABLE IF EXISTS `wk_to_do`;
CREATE TABLE `wk_to_do`  (
  `id` bigint NOT NULL COMMENT '主键',
  `application_id` bigint NOT NULL COMMENT '应用 ID',
  `module_id` bigint NOT NULL COMMENT '模块 ID',
  `module_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '模块名称',
  `object_type` int NOT NULL DEFAULT 0 COMMENT '对象类型 0 自定义流程 1 自定义按钮',
  `object_id` bigint NULL DEFAULT NULL COMMENT '对象 ID',
  `record_id` bigint NULL DEFAULT NULL COMMENT '记录 ID',
  `version` int NOT NULL COMMENT '版本号',
  `data_id` bigint NOT NULL COMMENT '数据 ID',
  `field_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '字段值',
  `field_auth` json NULL COMMENT '字段授权',
  `type` int NOT NULL DEFAULT 0 COMMENT '待办类型 0 节点通知',
  `type_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '类型名称',
  `type_id` bigint NULL DEFAULT NULL COMMENT '类型 ID',
  `flow_type` int NULL DEFAULT NULL COMMENT '节点类型 0 条件节点 1 审批节点 2 填写节点 3 抄送节点 4 添加数据 5 更新数据 6 发起人节点',
  `create_user_id` bigint NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `owner_user_id` bigint NULL DEFAULT NULL COMMENT '负责人',
  `viewed` tinyint(1) NOT NULL DEFAULT 0 COMMENT '已读',
  `view_time` datetime NULL DEFAULT NULL COMMENT '查看时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '变更时间',
  `status` int NOT NULL DEFAULT 0 COMMENT '待办状态 0 待处理 1 已处理',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '待办' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_to_do
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
