SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for undo_log
-- ----------------------------
DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE `undo_log`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `branch_id` bigint(20) NOT NULL,
  `xid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `context` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `rollback_info` longblob NOT NULL,
  `log_status` int(11) NOT NULL,
  `log_created` datetime NOT NULL,
  `log_modified` datetime NOT NULL,
  `ext` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `ux_undo_log`(`xid`, `branch_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of undo_log
-- ----------------------------

-- ----------------------------
-- Table structure for wk_app_category
-- ----------------------------
DROP TABLE IF EXISTS `wk_app_category`;
CREATE TABLE `wk_app_category`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `application_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '应用ID',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `sort` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `wk_app_category_id_uindex`(`id`) USING BTREE,
  INDEX `wk_app_category_category_id_index`(`category_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '应用分类关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_app_category
-- ----------------------------

-- ----------------------------
-- Table structure for wk_bi_dashboard
-- ----------------------------
DROP TABLE IF EXISTS `wk_bi_dashboard`;
CREATE TABLE `wk_bi_dashboard`  (
  `module_id` bigint(20) NOT NULL COMMENT '自增ID',
  `application_id` bigint(20) NOT NULL COMMENT '应用ID',
  `name` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '工作空间名称',
  `icon` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '图标',
  `icon_color` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '图标颜色',
  `type` int(11) NULL DEFAULT NULL COMMENT '类型 1为仪表盘，暂时没其他选项',
  `status` int(11) NULL DEFAULT 0 COMMENT '状态 0 草稿 1 对内发布 2 对外发布',
  `share_pass` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '对外分享密码',
  `refresh_time` int(11) NULL DEFAULT 0 COMMENT '自动刷新时间，单位为分钟 0代表不自动刷新',
  `ws_style` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '仪表盘样式',
  `order_num` int(11) NULL DEFAULT NULL COMMENT '排序字段',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint(20) NULL DEFAULT NULL COMMENT '修改人',
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
  `id` bigint(20) NOT NULL,
  `module_id` bigint(20) NOT NULL COMMENT '仪表盘ID',
  `type` int(11) NULL DEFAULT NULL COMMENT '类型 1 关联用户 2 关联部门',
  `type_id` bigint(20) NULL DEFAULT NULL COMMENT '对应类型ID',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint(20) NULL DEFAULT NULL COMMENT '修改人',
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
  `element_id` bigint(20) NOT NULL COMMENT '主键ID',
  `module_id` bigint(20) NOT NULL COMMENT '应用id',
  `name` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '组件名称',
  `type` int(11) NULL DEFAULT NULL COMMENT '组件类型，详见枚举',
  `coordinate` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'json类型 x 横轴坐标 y 纵轴坐标 w 组件宽度 h 组件高度 ',
  `ws_style` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '自定义其他样式',
  `target_id` bigint(20) NULL DEFAULT NULL COMMENT '数据来源ID',
  `target_type` int(11) NULL DEFAULT NULL COMMENT '数据来源类型1 crm 2 低代码 3',
  `target_category_id` bigint(20) NULL DEFAULT NULL COMMENT '数据来源分类ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `update_user_id` bigint(20) NULL DEFAULT NULL COMMENT '修改人',
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
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `element_id` bigint(20) NULL DEFAULT NULL COMMENT '组件ID',
  `module_id` bigint(20) NULL DEFAULT NULL COMMENT '模块ID',
  `field_id` bigint(20) NULL DEFAULT NULL COMMENT '自定义字段ID，可能为空',
  `form_type` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '字段类型',
  `field_name` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '自定义字段名称',
  `name` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '自定义字段展示名称',
  `data_type` int(11) NULL DEFAULT NULL COMMENT '字段数据类型，1、左维度字段 2 右维度字段 3 左指标字段 4 右指标字段 5 过滤条件',
  `order_type` int(11) NULL DEFAULT NULL COMMENT '字段排序方式 0 默认 1 升序 2 降序',
  `field_text` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '维度字段和指标字段为汇总方式，过滤条件为搜索条件',
  `extra_text` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '其余额外信息 json类型',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `update_user_id` bigint(20) NULL DEFAULT NULL COMMENT '修改人',
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
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '分类名称',
  `parent_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '父级分类',
  `sort` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '0 关闭\n1 开启',
  `type` int(11) NOT NULL DEFAULT 0 COMMENT '分类类型\n0 自定义\n1 收藏',
  `is_system` tinyint(1) NULL DEFAULT 0 COMMENT '系统分类',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`category_id`) USING BTREE,
  UNIQUE INDEX `wk_app_category_category_id_uindex`(`category_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '应用分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_category
-- ----------------------------
INSERT INTO `wk_category` VALUES (1608031394006388738, '我的收藏', 0, 0, 1, 1, 0, '2022-12-28 17:25:35');
INSERT INTO `wk_category` VALUES (1608031394006388739, '自定义应用', 0, 0, 1, 0, 0, '2022-12-28 17:25:35');

-- ----------------------------
-- Table structure for wk_custom_button
-- ----------------------------
DROP TABLE IF EXISTS `wk_custom_button`;
CREATE TABLE `wk_custom_button`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `button_id` bigint(20) NOT NULL COMMENT '按钮ID',
  `button_name` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '按钮名称',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '0 禁用 1 启用',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '应用图标',
  `icon_color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标颜色',
  `effect_type` int(11) NOT NULL DEFAULT 0 COMMENT '生效类型: 0  总是触发, 1 满足条件触发',
  `effect_config` json NULL COMMENT '触发条件',
  `execute_type` int(11) NOT NULL DEFAULT 0 COMMENT '执行类型: 0  立即, 1 二次确认 2 填写内容',
  `recheck_config` json NULL COMMENT '二次确认配置',
  `fill_config` json NULL COMMENT '填写配置',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_custom_button_module_id_version_index`(`module_id`, `version`) USING BTREE,
  INDEX `wk_custom_button_button_id_version_index`(`button_id`, `version`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '自定义按钮' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_custom_button
-- ----------------------------

-- ----------------------------
-- Table structure for wk_custom_category
-- ----------------------------
DROP TABLE IF EXISTS `wk_custom_category`;
CREATE TABLE `wk_custom_category`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `category_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类名字',
  `type` int(11) NOT NULL DEFAULT 1 COMMENT '类型 0 默认 1 自定义分类',
  `sort` int(11) NOT NULL COMMENT '排序',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
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
  `id` bigint(20) NOT NULL COMMENT '主键',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `field_id` bigint(20) NOT NULL COMMENT '字段ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字段名称',
  `is_hide` int(11) NOT NULL DEFAULT 1 COMMENT '是否隐藏 0、不隐藏 1、隐藏',
  `is_null` int(11) NULL DEFAULT 0 COMMENT '是否必填 1 是 0 否',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
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
  `id` bigint(20) NOT NULL COMMENT '主键',
  `rule_id` bigint(20) NOT NULL COMMENT '规则ID',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `from` bigint(20) NOT NULL COMMENT '数据来源分组ID',
  `to` bigint(20) NOT NULL COMMENT '数据去向分组ID',
  `formula` json NOT NULL COMMENT '公式',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
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
  `id` bigint(20) NOT NULL COMMENT '主键',
  `component_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '组件名称',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `module_id` bigint(20) NOT NULL COMMENT '模块 ID',
  `layout` json NULL COMMENT '组件布局',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建人',
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
  `id` bigint(20) NOT NULL COMMENT '主键',
  `notice_id` bigint(20) NOT NULL COMMENT '提醒ID',
  `notice_name` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '提醒名称',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '0 禁用 1 启用',
  `effect_type` int(11) NOT NULL DEFAULT 0 COMMENT '生效类型: 0  新增数据, 1 更新数据 2 更新指定字段 3 根据模块时间字段 4 自定义时间',
  `update_fields` json NULL COMMENT '指定更新字段',
  `time_field_config` json NULL COMMENT '模块时间字段配置',
  `effect_time` datetime NULL DEFAULT NULL COMMENT '生效时间',
  `repeat_period` json NULL COMMENT '重复周期',
  `effect_config` json NULL COMMENT '生效条件',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_custom_notice_module_id_version_index`(`module_id`, `version`) USING BTREE,
  INDEX `wk_custom_notice_notice_id_version_index`(`notice_id`, `version`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '自定义提醒' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_custom_notice
-- ----------------------------
INSERT INTO `wk_custom_notice` VALUES (1608392516421029889, 1608392516400058368, '库存数量不足提醒', 1, 1, NULL, NULL, NULL, NULL, '[{\"name\": \"数量\", \"type\": 9, \"values\": [\"10\"], \"fieldId\": 1522614130873303040, \"formType\": \"number\", \"fieldName\": \"fieldOjkodf\", \"searchEnum\": \"LT\", \"conditionType\": \"LT\"}]', 1608392444123811840, 0, '2022-12-29 17:20:33', 0, '2022-12-29 17:20:33');

-- ----------------------------
-- Table structure for wk_custom_notice_receiver
-- ----------------------------
DROP TABLE IF EXISTS `wk_custom_notice_receiver`;
CREATE TABLE `wk_custom_notice_receiver`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `notice_id` bigint(20) NOT NULL COMMENT '提醒ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '提醒内容',
  `notice_creator` tinyint(1) NOT NULL DEFAULT 0 COMMENT '通知创建人',
  `notice_owner` tinyint(1) NOT NULL DEFAULT 0 COMMENT '通知负责人',
  `notice_user` json NULL COMMENT '指定成员',
  `user_field` json NULL COMMENT '人员字段',
  `dept_field` json NULL COMMENT '部门字段',
  `notice_role` json NULL COMMENT '指定角色',
  `parent_level` json NULL COMMENT '负责人上级',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_custom_notice_receiver_module_id_version_index`(`module_id`, `version`) USING BTREE,
  INDEX `wk_custom_notice_receiver_notice_id_version_index`(`notice_id`, `version`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '自定义提醒接收配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_custom_notice_receiver
-- ----------------------------
INSERT INTO `wk_custom_notice_receiver` VALUES (1608392516433612805, 1608392516400058368, '#{1522614130734891008}库存不足，请及时采购', 1, 1, NULL, NULL, NULL, NULL, NULL, 1608392444123811840, 0, '2022-12-29 17:20:33', 0, '2022-12-29 17:20:33');

-- ----------------------------
-- Table structure for wk_custom_notice_record
-- ----------------------------
DROP TABLE IF EXISTS `wk_custom_notice_record`;
CREATE TABLE `wk_custom_notice_record`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `data_id` bigint(20) NOT NULL COMMENT '数据ID',
  `notice_id` bigint(20) NOT NULL COMMENT '提醒ID',
  `status` int(11) NOT NULL DEFAULT 0 COMMENT '0 未处理 1 已发送 2 废弃',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '批次ID',
  `repeat_count` int(11) NOT NULL DEFAULT 0 COMMENT '重复次数',
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
  `application_id` bigint(20) NOT NULL COMMENT '应用ID',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '应用名称',
  `key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '应用标识',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '0 停用 1 开启',
  `type` int(11) NOT NULL DEFAULT 1 COMMENT '1 自用系统 2 其他',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '应用描述',
  `detail` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '应用详情描述',
  `sort` int(10) UNSIGNED NOT NULL DEFAULT 999 COMMENT '排序 从小到大',
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
  `id` bigint(20) NOT NULL COMMENT '主键',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模块名称',
  `key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模块标识',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '0 停用 1 开启',
  `type` int(11) NOT NULL DEFAULT 1 COMMENT '1 自用系统 2 其他',
  `application_id` bigint(20) NOT NULL COMMENT '应用ID',
  `sort` int(10) UNSIGNED NOT NULL DEFAULT 999 COMMENT '排序 从小到大',
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
  `id` bigint(20) NOT NULL,
  `field_id` bigint(20) NULL DEFAULT NULL COMMENT '当前字段ID',
  `module_id` bigint(20) NOT NULL COMMENT '当前模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `target_field_id` bigint(20) NULL DEFAULT NULL COMMENT '目标字段ID',
  `target_field_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '目标字段名',
  `target_module_id` bigint(20) NOT NULL COMMENT '目标模块ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '外部模块字段映射' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_external_module_field_mapping
-- ----------------------------

-- ----------------------------
-- Table structure for wk_flow
-- ----------------------------
DROP TABLE IF EXISTS `wk_flow`;
CREATE TABLE `wk_flow`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `flow_id` bigint(20) NOT NULL COMMENT '流程ID',
  `flow_metadata_id` bigint(20) NOT NULL COMMENT '流程元数据ID',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `condition_id` bigint(20) NULL DEFAULT 0 COMMENT '条件ID',
  `flow_name` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '流程名称',
  `flow_type` int(11) NOT NULL COMMENT '流程类型  0 条件节点 1 审批节点 2 填写节点 3 抄送节点 4 添加数据 5 更新数据',
  `examine_error_handling` int(11) NOT NULL DEFAULT 1 COMMENT '审批找不到用户或者条件均不满足时怎么处理 1 自动通过 2 管理员审批',
  `field_id` json NULL COMMENT '填写节点配置的字段ID',
  `content` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述文本',
  `type` int(11) NULL DEFAULT NULL COMMENT '流程下属类型 如审批节点类型',
  `priority` int(11) NULL DEFAULT NULL COMMENT '优先级 数字越低优先级越高',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_flow_flow_id_index`(`flow_id`) USING BTREE,
  INDEX `wk_flow_module_id_flow_id_index`(`module_id`, `flow_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '模块流程表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_flow
-- ----------------------------
INSERT INTO `wk_flow` VALUES (1608392516689465386, 1608392516999843840, 1608392516970483712, 1608392507768180736, 0, 0, '审批节点', 1, 2, NULL, '发起人自选', 4, 1, '2022-12-29 17:20:33', '58639d238c27445d9bc1be085c68c26c');
INSERT INTO `wk_flow` VALUES (1608392516689465390, 1608392516999843841, 1608392516970483712, 1608392507768180736, 0, 0, '添加库存流水', 4, 1, NULL, '', NULL, 2, '2022-12-29 17:20:33', '58639d238c27445d9bc1be085c68c26c');
INSERT INTO `wk_flow` VALUES (1608392517066952723, 1608392517146644480, 1608392517113090048, 1608392498486185984, 0, 0, '审批节点', 1, 2, NULL, '发起人自选', 4, 1, '2022-12-29 17:20:33', '10829cb9ba05477c8dd7611035e22b28');
INSERT INTO `wk_flow` VALUES (1608392518186831876, 1608392518211997696, 1608392518191026176, 1608392505129963520, 0, 0, '审批节点', 1, 2, NULL, '发起人自选', 4, 1, '2022-12-29 17:20:33', 'a08868bb1db647d1b7542994b3a47c17');
INSERT INTO `wk_flow` VALUES (1608392518186831878, 1608392518211997697, 1608392518191026176, 1608392505129963520, 0, 0, '添加库存流水', 4, 1, NULL, '', NULL, 2, '2022-12-29 17:20:33', 'a08868bb1db647d1b7542994b3a47c17');
INSERT INTO `wk_flow` VALUES (1608392518451073025, 1608392518463655936, 1608392518404935680, 1608392509865332736, 0, 0, '审批节点', 1, 2, NULL, '发起人自选', 4, 1, '2022-12-29 17:20:34', '98d8235b58c64c3286a75e0de3de6c6b');
INSERT INTO `wk_flow` VALUES (1608392518451073026, 1608392518463655937, 1608392518404935680, 1608392509865332736, 0, 0, '添加库存流水', 4, 1, NULL, '', NULL, 2, '2022-12-29 17:20:34', '98d8235b58c64c3286a75e0de3de6c6b');
INSERT INTO `wk_flow` VALUES (1608439789838499840, 1608439789297434625, 1608439788827672577, 1608439765649948672, 0, 0, '条件节点', 0, 1, NULL, '', NULL, 1, '2022-12-29 20:28:24', '261007cb63c44b9baf0ccf7ba5bfd1b4');
INSERT INTO `wk_flow` VALUES (1608439789838499841, 1608439789305823232, 1608439788823478273, 1608439767700963328, 0, 0, '审批节点', 1, 2, NULL, '发起人自选', 4, 1, '2022-12-29 20:28:24', 'b39254d7292a4cdd9aa87af4142b5729');
INSERT INTO `wk_flow` VALUES (1608439789838499842, 1608439789330989056, 1608439788844449792, 1608439746549088256, 0, 0, '更新油卡余额', 5, 1, NULL, '更新一条数据', NULL, 1, '2022-12-29 20:28:24', 'da48f281b0c4430fa0bd3beb182a7b75');
INSERT INTO `wk_flow` VALUES (1608439789855277056, 1608439789297434624, 1608439788823478272, 1608439761141071872, 0, 0, '更新油卡余额', 5, 1, NULL, '更新一条数据', NULL, 1, '2022-12-29 20:28:24', '6b9c8eb44f81471392176edfa00d169d');
INSERT INTO `wk_flow` VALUES (1608439789859471360, 1608439789305823233, 1608439788823478273, 1608439767700963328, 0, 0, '更新数据', 5, 1, NULL, '更新一条数据', NULL, 2, '2022-12-29 20:28:24', 'b39254d7292a4cdd9aa87af4142b5729');
INSERT INTO `wk_flow` VALUES (1608439789859471361, 1608439789297434627, 1608439788827672577, 1608439765649948672, 0, 1547502626008014848, '更新数据', 5, 1, NULL, NULL, NULL, 1, '2022-12-29 20:28:24', '261007cb63c44b9baf0ccf7ba5bfd1b4');
INSERT INTO `wk_flow` VALUES (1608439789867859968, 1608439789297434626, 1608439788836061184, 1608439433763061760, 0, 0, '审批节点', 1, 2, NULL, '发起人自选', 4, 1, '2022-12-29 20:28:24', 'c97e2238565b49a08c0c3c926c1e2daa');
INSERT INTO `wk_flow` VALUES (1608439789888831488, 1608439789297434628, 1608439788836061184, 1608439433763061760, 0, 0, '更新驾驶员状态', 5, 1, NULL, '更新一条数据', NULL, 2, '2022-12-29 20:28:24', 'c97e2238565b49a08c0c3c926c1e2daa');
INSERT INTO `wk_flow` VALUES (1608439789909803008, 1608439789297434629, 1608439788836061184, 1608439433763061760, 0, 0, '更新车辆状态', 5, 1, NULL, '更新一条数据', NULL, 3, '2022-12-29 20:28:24', 'c97e2238565b49a08c0c3c926c1e2daa');
INSERT INTO `wk_flow` VALUES (1608439792388636673, 1608439792367665152, 1608439792334110720, 1608439774743199744, 0, 0, '审批节点', 1, 2, NULL, '发起人自选', 4, 1, '2022-12-29 20:28:25', '790e265c994a47978603f0494b51f561');
INSERT INTO `wk_flow` VALUES (1608439792409608192, 1608439792367665153, 1608439792334110720, 1608439774743199744, 0, 0, '更新车辆状态', 5, 1, NULL, '更新一条数据', NULL, 2, '2022-12-29 20:28:25', '790e265c994a47978603f0494b51f561');
INSERT INTO `wk_flow` VALUES (1608439792430579712, 1608439792367665154, 1608439792334110720, 1608439774743199744, 0, 0, '更新驾驶员状态', 5, 1, NULL, '更新一条数据', NULL, 3, '2022-12-29 20:28:25', '790e265c994a47978603f0494b51f561');
INSERT INTO `wk_flow` VALUES (1608439794494177280, 1608439794469011456, 1608439794439651328, 1608439769529679872, 0, 0, '更新数据', 5, 1, NULL, '更新一条数据', NULL, 1, '2022-12-29 20:28:25', 'ee01bc484faa43378cdd48f974371a9e');

-- ----------------------------
-- Table structure for wk_flow_condition
-- ----------------------------
DROP TABLE IF EXISTS `wk_flow_condition`;
CREATE TABLE `wk_flow_condition`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `condition_id` bigint(20) NOT NULL COMMENT '条件ID',
  `condition_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '条件名称',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `flow_id` bigint(20) NOT NULL COMMENT '审批流程ID',
  `priority` int(11) NOT NULL COMMENT '优先级 数字越低优先级越高',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次ID',
  `flow_metadata_id` bigint(20) NOT NULL COMMENT '流程元数据ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '流程条件表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_flow_condition
-- ----------------------------
INSERT INTO `wk_flow_condition` VALUES (1608439790614446080, 1608439789880442880, '条件1', 1608439765649948672, 0, 1608439789297434625, 0, '261007cb63c44b9baf0ccf7ba5bfd1b4', 1608439788827672577, '2022-12-29 20:28:24', 1);
INSERT INTO `wk_flow_condition` VALUES (1608439790631223296, 1608439789880442881, '条件2', 1608439765649948672, 0, 1608439789297434625, 1, '261007cb63c44b9baf0ccf7ba5bfd1b4', 1608439788827672577, '2022-12-29 20:28:24', 1);

-- ----------------------------
-- Table structure for wk_flow_condition_data
-- ----------------------------
DROP TABLE IF EXISTS `wk_flow_condition_data`;
CREATE TABLE `wk_flow_condition_data`  (
  `id` bigint(20) NOT NULL,
  `rule_type` int(11) NOT NULL COMMENT '筛选类型 1 条件筛选 2 更新节点筛选 3 更新节点更新 4 更新节点添加 5 添加节点添加',
  `type_id` bigint(20) NOT NULL COMMENT '对应类型ID',
  `model` int(11) NOT NULL COMMENT '模式：0 简单 1 高级',
  `type` int(11) NULL DEFAULT NULL COMMENT '类型：0 自定义 1 匹配字段',
  `target_module_id` bigint(20) NULL DEFAULT NULL COMMENT '目标模块ID',
  `search` json NULL COMMENT '筛选/更新规则',
  `group_id` int(11) NOT NULL COMMENT '分组ID',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次ID',
  `flow_metadata_id` bigint(20) NOT NULL COMMENT '流程元数据ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建人',
  `module_id` bigint(20) NOT NULL COMMENT '当前模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '审批条件扩展字段表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_flow_condition_data
-- ----------------------------
INSERT INTO `wk_flow_condition_data` VALUES (1608392517066952709, 5, 1608392516999843841, 0, 1, 1608392444123811840, '{\"name\": \"用品名称\", \"type\": 1, \"values\": [], \"formType\": \"text\", \"fieldName\": \"fieldMhlqon\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\", \"currentFieldId\": 1527573799052034048}', 1, '58639d238c27445d9bc1be085c68c26c', 1608392516970483712, '2022-12-29 17:20:33', 0, 1608392507768180736, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608392517066952712, 5, 1608392516999843841, 0, 1, 1608392444123811840, '{\"name\": \"规格\", \"type\": 1, \"values\": [], \"formType\": \"text\", \"fieldName\": \"fieldFowfyg\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\", \"currentFieldId\": 1523704529989763072}', 1, '58639d238c27445d9bc1be085c68c26c', 1608392516970483712, '2022-12-29 17:20:33', 0, 1608392507768180736, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608392517066952715, 5, 1608392516999843841, 0, 1, 1608392444123811840, '{\"name\": \"单位\", \"type\": 1, \"values\": [], \"formType\": \"text\", \"fieldName\": \"fieldZoywvv\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\", \"currentFieldId\": 1523704530140758016}', 1, '58639d238c27445d9bc1be085c68c26c', 1608392516970483712, '2022-12-29 17:20:33', 0, 1608392507768180736, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608392517066952717, 5, 1608392516999843841, 0, 1, 1608392444123811840, '{\"name\": \"分类\", \"type\": 1, \"values\": [], \"formType\": \"select\", \"fieldName\": \"fieldWisysx\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\", \"currentFieldId\": 1523704530035900416}', 1, '58639d238c27445d9bc1be085c68c26c', 1608392516970483712, '2022-12-29 17:20:33', 0, 1608392507768180736, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608392517066952721, 5, 1608392516999843841, 0, 1, 1608392444123811840, '{\"name\": \"归还数量\", \"type\": 1, \"values\": [], \"formType\": \"number\", \"fieldName\": \"fieldVjmdue\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\", \"currentFieldId\": 1523704530237227008}', 1, '58639d238c27445d9bc1be085c68c26c', 1608392516970483712, '2022-12-29 17:20:33', 0, 1608392507768180736, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608392517066952724, 5, 1608392516999843841, 0, 1, 1608392444123811840, '{\"name\": \"仓库\", \"type\": 1, \"values\": [], \"formType\": \"data_union\", \"fieldName\": \"fieldYyoira\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\", \"currentFieldId\": 1547842611416449024}', 1, '58639d238c27445d9bc1be085c68c26c', 1608392516970483712, '2022-12-29 17:20:33', 0, 1608392507768180736, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608392518253940739, 5, 1608392518211997697, 0, 1, 1608392444123811840, '{\"name\": \"用品名称\", \"type\": 1, \"values\": [], \"formType\": \"text\", \"fieldName\": \"fieldMhlqon\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\", \"currentFieldId\": 1522635492744593408}', 1, 'a08868bb1db647d1b7542994b3a47c17', 1608392518191026176, '2022-12-29 17:20:33', 0, 1608392505129963520, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608392518253940741, 5, 1608392518211997697, 0, 1, 1608392444123811840, '{\"name\": \"仓库\", \"type\": 1, \"values\": [], \"formType\": \"data_union\", \"fieldName\": \"fieldYyoira\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\", \"currentFieldId\": 1528919053848096768}', 1, 'a08868bb1db647d1b7542994b3a47c17', 1608392518191026176, '2022-12-29 17:20:33', 0, 1608392505129963520, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608392518253940743, 5, 1608392518211997697, 0, 1, 1608392444123811840, '{\"name\": \"规格\", \"type\": 1, \"values\": [], \"formType\": \"text\", \"fieldName\": \"fieldFowfyg\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\", \"currentFieldId\": 1522635492790730752}', 1, 'a08868bb1db647d1b7542994b3a47c17', 1608392518191026176, '2022-12-29 17:20:33', 0, 1608392505129963520, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608392518253940744, 5, 1608392518211997697, 0, 1, 1608392444123811840, '{\"name\": \"单位\", \"type\": 1, \"values\": [], \"formType\": \"text\", \"fieldName\": \"fieldZoywvv\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\", \"currentFieldId\": 1522635492920754176}', 1, 'a08868bb1db647d1b7542994b3a47c17', 1608392518191026176, '2022-12-29 17:20:33', 0, 1608392505129963520, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608392518321049602, 5, 1608392518211997697, 0, 1, 1608392444123811840, '{\"name\": \"分类\", \"type\": 1, \"values\": [], \"formType\": \"select\", \"fieldName\": \"fieldWisysx\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\", \"currentFieldId\": 1522635492832673792}', 1, 'a08868bb1db647d1b7542994b3a47c17', 1608392518191026176, '2022-12-29 17:20:33', 0, 1608392505129963520, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608392518325243907, 5, 1608392518211997697, 0, 1, 1608392444123811840, '{\"name\": \"入库数量\", \"type\": 1, \"values\": [], \"formType\": \"number\", \"fieldName\": \"fieldPizhki\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\", \"currentFieldId\": 1522635492962697216}', 1, 'a08868bb1db647d1b7542994b3a47c17', 1608392518191026176, '2022-12-29 17:20:33', 0, 1608392505129963520, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608392518782423042, 5, 1608392518463655937, 0, 1, 1608392444123811840, '{\"name\": \"用品名称\", \"type\": 1, \"values\": [], \"formType\": \"text\", \"fieldName\": \"fieldMhlqon\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\", \"currentFieldId\": 1527551818508320768}', 1, '98d8235b58c64c3286a75e0de3de6c6b', 1608392518404935680, '2022-12-29 17:20:34', 0, 1608392509865332736, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608392518782423043, 5, 1608392518463655937, 0, 1, 1608392444123811840, '{\"name\": \"规格\", \"type\": 1, \"values\": [], \"formType\": \"text\", \"fieldName\": \"fieldFowfyg\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\", \"currentFieldId\": 1523696647688781824}', 1, '98d8235b58c64c3286a75e0de3de6c6b', 1608392518404935680, '2022-12-29 17:20:34', 0, 1608392509865332736, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608392518782423044, 5, 1608392518463655937, 0, 1, 1608392444123811840, '{\"name\": \"单位\", \"type\": 1, \"values\": [], \"formType\": \"text\", \"fieldName\": \"fieldZoywvv\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\", \"currentFieldId\": 1523696647835582464}', 1, '98d8235b58c64c3286a75e0de3de6c6b', 1608392518404935680, '2022-12-29 17:20:34', 0, 1608392509865332736, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608392518849531905, 5, 1608392518463655937, 0, 1, 1608392444123811840, '{\"name\": \"分类\", \"type\": 1, \"values\": [], \"formType\": \"select\", \"fieldName\": \"fieldWisysx\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\", \"currentFieldId\": 1523696647734919168}', 1, '98d8235b58c64c3286a75e0de3de6c6b', 1608392518404935680, '2022-12-29 17:20:34', 0, 1608392509865332736, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608392518849531906, 5, 1608392518463655937, 0, 1, 1608392444123811840, '{\"name\": \"领用数量\", \"type\": 1, \"values\": [], \"formType\": \"number\", \"fieldName\": \"fieldLnxruf\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\", \"currentFieldId\": 1523696647881719808}', 1, '98d8235b58c64c3286a75e0de3de6c6b', 1608392518404935680, '2022-12-29 17:20:34', 0, 1608392509865332736, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608392518849531907, 5, 1608392518463655937, 0, 1, 1608392444123811840, '{\"name\": \"仓库\", \"type\": 1, \"values\": [], \"formType\": \"data_union\", \"fieldName\": \"fieldYyoira\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\", \"currentFieldId\": 1547831583253274624}', 1, '98d8235b58c64c3286a75e0de3de6c6b', 1608392518404935680, '2022-12-29 17:20:34', 0, 1608392509865332736, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608439790429896704, 2, 1608439789297434628, 0, 1, 1608439771190624256, '{\"type\": 1, \"values\": [], \"formType\": \"text\", \"fieldName\": \"fieldMygrvj\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\", \"currentFieldId\": 1532228759043776512}', 1, 'c97e2238565b49a08c0c3c926c1e2daa', 1608439788836061184, '2022-12-29 20:28:24', 1, 1608439433763061760, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608439790429896705, 2, 1608439789305823233, 0, 1, 1608439777943453696, '{\"type\": 1, \"values\": [], \"formType\": \"text\", \"fieldName\": \"fieldZoywvv\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\", \"currentFieldId\": 1531221281732603904}', 1, 'b39254d7292a4cdd9aa87af4142b5729', 1608439788823478273, '2022-12-29 20:28:24', 1, 1608439767700963328, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608439790429896706, 2, 1608439789297434624, 0, 1, 1608439763607322624, '{\"type\": 1, \"values\": [], \"formType\": \"text\", \"fieldName\": \"fieldYlzjop\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\", \"currentFieldId\": 1531209525480173568}', 1, '6b9c8eb44f81471392176edfa00d169d', 1608439788823478272, '2022-12-29 20:28:24', 1, 1608439761141071872, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608439790429896707, 2, 1608439789330989056, 0, 1, 1608439763607322624, '{\"type\": 1, \"values\": [], \"formType\": \"text\", \"fieldName\": \"fieldYlzjop\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\", \"currentFieldId\": 1534102235916599296}', 1, 'da48f281b0c4430fa0bd3beb182a7b75', 1608439788844449792, '2022-12-29 20:28:24', 1, 1608439746549088256, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608439790446673920, 3, 1608439789297434628, 0, 0, 1608439771190624256, '{\"name\": \"驾驶员状态\", \"type\": 1, \"values\": [\"{\\\"key\\\":\\\"1531101362701586432-1\\\",\\\"value\\\":\\\"空闲\\\",\\\"type\\\":0}\"], \"formType\": \"select\", \"fieldName\": \"fieldXgxxqa\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\"}', 1, 'c97e2238565b49a08c0c3c926c1e2daa', 1608439788836061184, '2022-12-29 20:28:24', 1, 1608439433763061760, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608439790446673921, 3, 1608439789330989056, 0, 1, 1608439763607322624, '{\"name\": \"余额\", \"type\": 1, \"values\": [], \"formType\": \"number\", \"fieldName\": \"fieldWbogqz\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\", \"currentFieldId\": 1534102235975319552}', 1, 'da48f281b0c4430fa0bd3beb182a7b75', 1608439788844449792, '2022-12-29 20:28:24', 1, 1608439746549088256, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608439790446673922, 3, 1608439789297434624, 0, 1, 1608439763607322624, '{\"name\": \"余额\", \"type\": 1, \"values\": [], \"formType\": \"number\", \"fieldName\": \"fieldWbogqz\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\", \"currentFieldId\": 1532245858373935104}', 1, '6b9c8eb44f81471392176edfa00d169d', 1608439788823478272, '2022-12-29 20:28:24', 1, 1608439761141071872, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608439790450868224, 3, 1608439789305823233, 0, 0, 1608439777943453696, '{\"name\": \"车辆状态\", \"type\": 1, \"values\": [\"{\\\"key\\\":\\\"1531086705781755904-3\\\",\\\"value\\\":\\\"维保\\\",\\\"type\\\":0}\"], \"formType\": \"select\", \"fieldName\": \"fieldCvwjri\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\"}', 1, 'b39254d7292a4cdd9aa87af4142b5729', 1608439788823478273, '2022-12-29 20:28:24', 1, 1608439767700963328, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608439790463451136, 2, 1608439789297434629, 0, 1, 1608439777943453696, '{\"type\": 1, \"values\": [], \"formType\": \"text\", \"fieldName\": \"fieldZoywvv\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\", \"currentFieldId\": 1547094601317294080}', 1, 'c97e2238565b49a08c0c3c926c1e2daa', 1608439788836061184, '2022-12-29 20:28:24', 1, 1608439433763061760, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608439790480228352, 3, 1608439789297434629, 0, 0, 1608439777943453696, '{\"name\": \"车辆状态\", \"type\": 1, \"values\": [\"{\\\"key\\\":\\\"1531086705781755904-1\\\",\\\"value\\\":\\\"可用\\\",\\\"type\\\":0}\"], \"formType\": \"select\", \"fieldName\": \"fieldCvwjri\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\"}', 1, 'c97e2238565b49a08c0c3c926c1e2daa', 1608439788836061184, '2022-12-29 20:28:24', 1, 1608439433763061760, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608439790824161280, 1, 1608439789880442880, 0, 0, 0, '{\"name\": \"年审情况\", \"type\": 1, \"values\": [\"{\\\"key\\\":\\\"1532169825620713472-2\\\",\\\"value\\\":\\\"不合格\\\",\\\"type\\\":0}\"], \"formType\": \"select\", \"fieldName\": \"fieldWisysx\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\"}', 2, '261007cb63c44b9baf0ccf7ba5bfd1b4', 1608439788827672577, '2022-12-29 20:28:24', 1, 1608439765649948672, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608439790840938496, 2, 1608439789297434627, 0, 1, 1608439777943453696, '{\"type\": 1, \"values\": [], \"formType\": \"text\", \"fieldName\": \"fieldZoywvv\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\", \"currentFieldId\": 1532171630933041152}', 1, '261007cb63c44b9baf0ccf7ba5bfd1b4', 1608439788827672577, '2022-12-29 20:28:24', 1, 1608439765649948672, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608439790857715712, 3, 1608439789297434627, 0, 0, 1608439777943453696, '{\"name\": \"车辆状态\", \"type\": 1, \"values\": [\"{\\\"key\\\":\\\"1531086705781755904-5\\\",\\\"value\\\":\\\"已报废\\\",\\\"type\\\":0}\"], \"formType\": \"select\", \"fieldName\": \"fieldCvwjri\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\"}', 1, '261007cb63c44b9baf0ccf7ba5bfd1b4', 1608439788827672577, '2022-12-29 20:28:24', 1, 1608439765649948672, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608439790874492928, 3, 1608439789297434627, 0, 0, 1608439777943453696, '{\"name\": \"是否报废\", \"type\": 1, \"values\": [\"{\\\"key\\\":\\\"1531086705781755904-1\\\",\\\"value\\\":\\\"是\\\",\\\"type\\\":0}\"], \"formType\": \"select\", \"fieldName\": \"fieldWasmoq\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\"}', 1, '261007cb63c44b9baf0ccf7ba5bfd1b4', 1608439788827672577, '2022-12-29 20:28:24', 1, 1608439765649948672, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608439790895464448, 1, 1608439789880442881, 0, 0, 0, '{\"name\": \"年审情况\", \"type\": 1, \"values\": [\"{\\\"key\\\":\\\"1532169825620713472-1\\\",\\\"value\\\":\\\"合格\\\",\\\"type\\\":0}\"], \"formType\": \"select\", \"fieldName\": \"fieldWisysx\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\"}', 2, '261007cb63c44b9baf0ccf7ba5bfd1b4', 1608439788827672577, '2022-12-29 20:28:24', 1, 1608439765649948672, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608439792527048704, 2, 1608439792367665153, 0, 1, 1608439777943453696, '{\"type\": 1, \"values\": [], \"formType\": \"text\", \"fieldName\": \"fieldZoywvv\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\", \"currentFieldId\": 1532199205797437440}', 1, '790e265c994a47978603f0494b51f561', 1608439792334110720, '2022-12-29 20:28:25', 1, 1608439774743199744, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608439792552214528, 3, 1608439792367665153, 0, 0, 1608439777943453696, '{\"name\": \"车辆状态\", \"type\": 1, \"values\": [\"{\\\"key\\\":\\\"1531086705781755904-2\\\",\\\"value\\\":\\\"出差\\\",\\\"type\\\":0}\"], \"formType\": \"select\", \"fieldName\": \"fieldCvwjri\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\"}', 1, '790e265c994a47978603f0494b51f561', 1608439792334110720, '2022-12-29 20:28:25', 1, 1608439774743199744, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608439792568991745, 2, 1608439792367665154, 0, 1, 1608439771190624256, '{\"type\": 1, \"values\": [], \"formType\": \"text\", \"fieldName\": \"fieldMygrvj\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\", \"currentFieldId\": 1532228091981668352}', 1, '790e265c994a47978603f0494b51f561', 1608439792334110720, '2022-12-29 20:28:25', 1, 1608439774743199744, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608439792589963264, 3, 1608439792367665154, 0, 0, 1608439771190624256, '{\"name\": \"驾驶员状态\", \"type\": 1, \"values\": [\"{\\\"key\\\":\\\"1531101362701586432-2\\\",\\\"value\\\":\\\"出车\\\",\\\"type\\\":0}\"], \"formType\": \"select\", \"fieldName\": \"fieldXgxxqa\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\"}', 1, '790e265c994a47978603f0494b51f561', 1608439792334110720, '2022-12-29 20:28:25', 1, 1608439774743199744, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608439794527731712, 2, 1608439794469011456, 0, 1, 1608439777943453696, '{\"type\": 1, \"values\": [], \"formType\": \"text\", \"fieldName\": \"fieldZoywvv\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\", \"currentFieldId\": 1531929289878003712}', 1, 'ee01bc484faa43378cdd48f974371a9e', 1608439794439651328, '2022-12-29 20:28:25', 1, 1608439769529679872, 0);
INSERT INTO `wk_flow_condition_data` VALUES (1608439794540314624, 3, 1608439794469011456, 0, 0, 1608439777943453696, '{\"name\": \"车辆状态\", \"type\": 1, \"values\": [\"{\\\"key\\\":\\\"1531086705781755904-1\\\",\\\"value\\\":\\\"可用\\\",\\\"type\\\":0}\"], \"formType\": \"select\", \"fieldName\": \"fieldCvwjri\", \"searchEnum\": \"IS\", \"conditionType\": \"IS\"}', 1, 'ee01bc484faa43378cdd48f974371a9e', 1608439794439651328, '2022-12-29 20:28:25', 1, 1608439769529679872, 0);

-- ----------------------------
-- Table structure for wk_flow_copy
-- ----------------------------
DROP TABLE IF EXISTS `wk_flow_copy`;
CREATE TABLE `wk_flow_copy`  (
  `id` bigint(20) NOT NULL,
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `flow_id` bigint(20) NULL DEFAULT NULL COMMENT '流程ID',
  `user_ids` json NULL COMMENT '用户列表',
  `parent_levels` json NULL COMMENT '上级级别list',
  `role_ids` json NULL COMMENT '角色list',
  `is_self` int(11) NULL DEFAULT NULL COMMENT '1是0否，是否给自己发送',
  `is_add` int(11) NULL DEFAULT NULL COMMENT '是否允许发起人添加抄送人',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次ID',
  `flow_metadata_id` bigint(20) NOT NULL COMMENT '流程元数据ID',
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
  `id` bigint(20) NOT NULL COMMENT '主键',
  `parent_id` bigint(20) NULL DEFAULT NULL COMMENT '原处理记录ID',
  `is_main` tinyint(1) NULL DEFAULT 1 COMMENT '主记录标识',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '负责人',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '负责人',
  `record_id` bigint(20) NOT NULL COMMENT '审核记录ID',
  `flow_id` bigint(20) NOT NULL COMMENT '流程Id',
  `condition_id` bigint(20) NULL DEFAULT NULL COMMENT '条件ID',
  `data_id` bigint(20) NOT NULL COMMENT '数据ID',
  `flow_type` int(11) NULL DEFAULT NULL COMMENT '节点类型\n条件节点 0\n审批节点 1\n填写节点 2\n抄送节点 3\n添加数据 4\n更新数据 5',
  `source_data` blob NULL COMMENT '原始数据',
  `current_data` blob NULL COMMENT '当前数据',
  `flow_status` int(11) NOT NULL COMMENT '流程状态 0 待处理 1 已处理 3 处理中',
  `type` int(11) NULL DEFAULT NULL COMMENT '1 依次审批 2 会签 3 或签',
  `invalid_type` int(11) NULL DEFAULT NULL COMMENT '失效类型 1 转交失效 2 撤回失效',
  `role_id` bigint(20) NULL DEFAULT NULL COMMENT '角色ID',
  `sort` int(11) NULL DEFAULT NULL COMMENT '排序',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '批次ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `overtime_type` int(11) NULL DEFAULT NULL COMMENT '超时类型 1 自动提醒 2 自动转交 3 自动同意',
  `time_value` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '超时时间',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
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
  `id` bigint(20) NOT NULL,
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `flow_id` bigint(20) NOT NULL COMMENT '审批流程ID',
  `role_id` bigint(20) NULL DEFAULT NULL COMMENT '角色ID',
  `max_level` int(11) NULL DEFAULT NULL COMMENT '角色审批的最高级别或者组织架构的第N级',
  `type` int(11) NULL DEFAULT NULL COMMENT '1 指定角色 2 组织架构的最上级',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次ID',
  `flow_metadata_id` bigint(20) NOT NULL COMMENT '流程元数据ID',
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
  `id` bigint(20) NOT NULL,
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `flow_id` bigint(20) NOT NULL COMMENT '审批流程ID',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '审批人ID',
  `type` int(11) NULL DEFAULT NULL COMMENT '1 依次审批 2 会签 3 或签',
  `sort` int(11) NOT NULL DEFAULT 0 COMMENT '排序规则',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次ID',
  `flow_metadata_id` bigint(20) NOT NULL COMMENT '流程元数据ID',
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
  `id` bigint(20) NOT NULL,
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `flow_id` bigint(20) NOT NULL COMMENT '审核流程ID',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '审批人ID',
  `role_id` int(11) NULL DEFAULT NULL COMMENT '角色ID',
  `choose_type` int(11) NULL DEFAULT NULL COMMENT '选择类型 1 自选一人 2 自选多人',
  `type` int(11) NULL DEFAULT NULL COMMENT '1 依次审批 2 会签 3 或签',
  `sort` int(11) NOT NULL DEFAULT 0 COMMENT '排序规则',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次ID',
  `flow_metadata_id` bigint(20) NOT NULL COMMENT '流程元数据ID',
  `range_type` int(11) NULL DEFAULT NULL COMMENT '选择范围 1 全公司 2 指定成员 3 指定角色 ',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '审批流程自选成员记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_flow_examine_optional
-- ----------------------------
INSERT INTO `wk_flow_examine_optional` VALUES (1608392517251502082, 1608392507768180736, 0, 1608392516999843840, NULL, NULL, 1, 1, 1, '58639d238c27445d9bc1be085c68c26c', 1608392516970483712, 1);
INSERT INTO `wk_flow_examine_optional` VALUES (1608392517402497028, 1608392498486185984, 0, 1608392517146644480, NULL, NULL, 1, 1, 1, '10829cb9ba05477c8dd7611035e22b28', 1608392517113090048, 1);
INSERT INTO `wk_flow_examine_optional` VALUES (1608392518388158466, 1608392505129963520, 0, 1608392518211997696, NULL, NULL, 1, 1, 1, 'a08868bb1db647d1b7542994b3a47c17', 1608392518191026176, 1);
INSERT INTO `wk_flow_examine_optional` VALUES (1608392518912446466, 1608392509865332736, 0, 1608392518463655936, NULL, NULL, 1, 1, 1, '98d8235b58c64c3286a75e0de3de6c6b', 1608392518404935680, 1);
INSERT INTO `wk_flow_examine_optional` VALUES (1608439791927263232, 1608439767700963328, 0, 1608439789305823232, NULL, NULL, 1, 1, 1, 'b39254d7292a4cdd9aa87af4142b5729', 1608439788823478273, 1);
INSERT INTO `wk_flow_examine_optional` VALUES (1608439792397025280, 1608439433763061760, 0, 1608439789297434626, NULL, NULL, 1, 1, 1, 'c97e2238565b49a08c0c3c926c1e2daa', 1608439788836061184, 1);
INSERT INTO `wk_flow_examine_optional` VALUES (1608439792904536064, 1608439774743199744, 0, 1608439792367665152, NULL, NULL, 1, 1, 1, '790e265c994a47978603f0494b51f561', 1608439792334110720, 1);

-- ----------------------------
-- Table structure for wk_flow_examine_record
-- ----------------------------
DROP TABLE IF EXISTS `wk_flow_examine_record`;
CREATE TABLE `wk_flow_examine_record`  (
  `record_id` bigint(20) NOT NULL COMMENT '审核记录ID',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `flow_metadata_id` bigint(20) NOT NULL COMMENT '流程元数据ID',
  `flow_id` bigint(20) NOT NULL COMMENT '流程ID',
  `data_id` bigint(20) NULL DEFAULT NULL COMMENT '关联模块数据Id',
  `examine_status` int(11) NULL DEFAULT NULL COMMENT '审核状态\r\n0 未审核 1 审核通过 2 审核拒绝\r\n3 审核中 4 已撤回   5 未提交\r\n7 失败   8 作废     9 忽略',
  `type_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '类型ID',
  `flow_metadata_type` int(11) NOT NULL DEFAULT 0 COMMENT '流程类型 0 系统 1 自定义按钮',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建人',
  `update_user_id` bigint(20) NOT NULL COMMENT '修改人',
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
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `module_id` bigint(20) NOT NULL COMMENT '模块Id',
  `version` int(11) NOT NULL COMMENT '版本号',
  `flow_id` bigint(20) NOT NULL COMMENT '流程ID',
  `record_id` bigint(20) NOT NULL COMMENT '审核记录ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `sort` int(11) NOT NULL DEFAULT 1 COMMENT '排序。从小到大',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次ID',
  `flow_metadata_id` bigint(20) NOT NULL COMMENT '流程元数据ID',
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
  `id` bigint(20) NOT NULL,
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `flow_id` bigint(20) NOT NULL COMMENT '审核流程ID',
  `role_id` bigint(20) NULL DEFAULT NULL COMMENT '角色ID',
  `type` int(11) NULL DEFAULT NULL COMMENT '2 会签 3 或签',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次ID',
  `flow_metadata_id` bigint(20) NOT NULL COMMENT '流程元数据ID',
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
  `id` bigint(20) NOT NULL,
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `flow_id` bigint(20) NOT NULL COMMENT '审核流程ID',
  `parent_level` int(11) NULL DEFAULT NULL COMMENT '直属上级级别 1 代表直属上级 2 代表 直属上级的上级',
  `type` int(11) NULL DEFAULT NULL COMMENT '找不到上级时，是否由上一级上级代审批 0 否 1 是',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次ID',
  `flow_metadata_id` bigint(20) NOT NULL COMMENT '流程元数据ID',
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
  `id` bigint(20) NOT NULL,
  `module_id` bigint(20) NULL DEFAULT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `flow_id` bigint(20) NULL DEFAULT NULL COMMENT '流程ID',
  `auth` json NOT NULL COMMENT '授权',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次ID',
  `flow_metadata_id` bigint(20) NOT NULL COMMENT '流程元数据ID',
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
  `metadata_id` bigint(20) NOT NULL COMMENT '主键ID',
  `type` int(11) NOT NULL DEFAULT 0 COMMENT '流程类型 0 系统 1 自定义按钮 2 阶段',
  `type_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '对应类型ID',
  `status` int(11) NULL DEFAULT NULL COMMENT '状态 1 正常 2 停用 3 删除',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次ID',
  `module_id` bigint(20) NULL DEFAULT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`metadata_id`) USING BTREE,
  INDEX `wk_flow_metadata_module_id_version_index`(`module_id`, `version`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '模块自定义流程元数据表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_flow_metadata
-- ----------------------------
INSERT INTO `wk_flow_metadata` VALUES (1608392516362309632, 0, 0, 1, 'c0c8c5055e854d3a83b6c418cf4dab6e', 1608392444123811840, 0, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_flow_metadata` VALUES (1608392516970483712, 0, 0, 1, '58639d238c27445d9bc1be085c68c26c', 1608392507768180736, 0, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_flow_metadata` VALUES (1608392517012426752, 0, 0, 1, 'c9290f9e9b844c0dab41e611dc5498ae', 1608392472255008768, 0, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_flow_metadata` VALUES (1608392517113090048, 0, 0, 1, '10829cb9ba05477c8dd7611035e22b28', 1608392498486185984, 0, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_flow_metadata` VALUES (1608392517113090049, 0, 0, 1, 'f7cd14d4783f43599135387220103907', 1608392513568903168, 0, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_flow_metadata` VALUES (1608392517792567296, 0, 0, 1, 'e125ad86325a4e2abe0130af1eb7ba9e', 1608392204931043328, 0, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_flow_metadata` VALUES (1608392518191026176, 0, 0, 1, 'a08868bb1db647d1b7542994b3a47c17', 1608392505129963520, 0, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_flow_metadata` VALUES (1608392518404935680, 0, 0, 1, '98d8235b58c64c3286a75e0de3de6c6b', 1608392509865332736, 0, '2022-12-29 17:20:34', 0);
INSERT INTO `wk_flow_metadata` VALUES (1608439788802506752, 0, 0, 1, '3f8ea030160c47aebe46cae8e717ab78', 1608439761891852288, 0, '2022-12-29 20:28:24', 1);
INSERT INTO `wk_flow_metadata` VALUES (1608439788810895360, 0, 0, 1, 'd3d83bb6ee494bf5aa9114eb00150c67', 1608439764727201792, 0, '2022-12-29 20:28:24', 1);
INSERT INTO `wk_flow_metadata` VALUES (1608439788823478272, 0, 0, 1, '6b9c8eb44f81471392176edfa00d169d', 1608439761141071872, 0, '2022-12-29 20:28:24', 1);
INSERT INTO `wk_flow_metadata` VALUES (1608439788823478273, 0, 0, 1, 'b39254d7292a4cdd9aa87af4142b5729', 1608439767700963328, 0, '2022-12-29 20:28:24', 1);
INSERT INTO `wk_flow_metadata` VALUES (1608439788827672576, 0, 0, 1, 'fbc49c607ace441ab039009e822ded2c', 1608439768413995008, 0, '2022-12-29 20:28:24', 1);
INSERT INTO `wk_flow_metadata` VALUES (1608439788827672577, 0, 0, 1, '261007cb63c44b9baf0ccf7ba5bfd1b4', 1608439765649948672, 0, '2022-12-29 20:28:24', 1);
INSERT INTO `wk_flow_metadata` VALUES (1608439788836061184, 0, 0, 1, 'c97e2238565b49a08c0c3c926c1e2daa', 1608439433763061760, 0, '2022-12-29 20:28:24', 1);
INSERT INTO `wk_flow_metadata` VALUES (1608439788844449792, 0, 0, 1, 'da48f281b0c4430fa0bd3beb182a7b75', 1608439746549088256, 0, '2022-12-29 20:28:24', 1);
INSERT INTO `wk_flow_metadata` VALUES (1608439792334110720, 0, 0, 1, '790e265c994a47978603f0494b51f561', 1608439774743199744, 0, '2022-12-29 20:28:25', 1);
INSERT INTO `wk_flow_metadata` VALUES (1608439792950673408, 0, 0, 1, '78612537e6fa4f08bb4421c43de3b64d', 1608439763607322624, 0, '2022-12-29 20:28:25', 1);
INSERT INTO `wk_flow_metadata` VALUES (1608439792980033537, 0, 0, 1, '0ec4ee4f2fd4488fb810013bc09edae8', 1608439771190624256, 0, '2022-12-29 20:28:25', 1);
INSERT INTO `wk_flow_metadata` VALUES (1608439793361715200, 0, 0, 1, 'f3192ff869fa453ca5182370247ef5b5', 1608439767134732288, 0, '2022-12-29 20:28:25', 1);
INSERT INTO `wk_flow_metadata` VALUES (1608439794057969664, 0, 0, 1, 'ba01cefd234147d29b6d0dd037d705df', 1608439776207011840, 0, '2022-12-29 20:28:25', 1);
INSERT INTO `wk_flow_metadata` VALUES (1608439794297044992, 0, 0, 1, 'dc319dbff16b4350b902fc7c1bc348b5', 1608439776668385280, 0, '2022-12-29 20:28:25', 1);
INSERT INTO `wk_flow_metadata` VALUES (1608439794439651328, 0, 0, 1, 'ee01bc484faa43378cdd48f974371a9e', 1608439769529679872, 0, '2022-12-29 20:28:25', 1);
INSERT INTO `wk_flow_metadata` VALUES (1608439795400146944, 0, 0, 1, '92b6a077c9d14b1ea0ec57eadeb639cf', 1608439777943453696, 0, '2022-12-29 20:28:25', 1);

-- ----------------------------
-- Table structure for wk_flow_save
-- ----------------------------
DROP TABLE IF EXISTS `wk_flow_save`;
CREATE TABLE `wk_flow_save`  (
  `id` bigint(20) NOT NULL,
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `flow_id` bigint(20) NULL DEFAULT NULL COMMENT '流程ID',
  `target_module_id` bigint(20) NULL DEFAULT NULL COMMENT '目标模块',
  `owner_user_id` bigint(20) NULL DEFAULT NULL COMMENT '负责人 0 代表模块创建人',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次ID',
  `flow_metadata_id` bigint(20) NOT NULL COMMENT '流程元数据ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '流程更新数据节点配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_flow_save
-- ----------------------------
INSERT INTO `wk_flow_save` VALUES (1608392517066952728, 1608392507768180736, 0, 1608392516999843841, 1608392444123811840, 0, '58639d238c27445d9bc1be085c68c26c', 1608392516970483712, '2022-12-29 17:20:33');
INSERT INTO `wk_flow_save` VALUES (1608392518383964162, 1608392505129963520, 0, 1608392518211997697, 1608392444123811840, 0, 'a08868bb1db647d1b7542994b3a47c17', 1608392518191026176, '2022-12-29 17:20:34');
INSERT INTO `wk_flow_save` VALUES (1608392518912446465, 1608392509865332736, 0, 1608392518463655937, 1608392444123811840, 0, '98d8235b58c64c3286a75e0de3de6c6b', 1608392518404935680, '2022-12-29 17:20:34');

-- ----------------------------
-- Table structure for wk_flow_time_limit
-- ----------------------------
DROP TABLE IF EXISTS `wk_flow_time_limit`;
CREATE TABLE `wk_flow_time_limit`  (
  `id` bigint(20) NOT NULL,
  `module_id` bigint(20) NULL DEFAULT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `flow_id` bigint(20) NULL DEFAULT NULL COMMENT '流程ID',
  `is_send_message` int(11) NULL DEFAULT NULL COMMENT '是否进入此节点时给处理人发消息通知 1 是 0 否',
  `allow_transfer` int(11) NULL DEFAULT NULL COMMENT '是否允许转交 1 是 0 否',
  `transfer_user_ids` json NULL COMMENT '允许转交的用户ID，空数组代表全部',
  `open_feedback` int(11) NULL DEFAULT NULL COMMENT '反馈是否必填 1 是 0 否',
  `recheck_type` int(11) NULL DEFAULT NULL COMMENT ' 审批被拒后重新提交 1 从第一层开始 2 从拒绝的层级开始',
  `open_time_limit` int(11) NULL DEFAULT NULL COMMENT '是否开启限时处理 1 是 0 否',
  `time_value` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '限时处理的值 d代表天，m代表分钟 h代表小时 例 10d代表10天',
  `overtime_type` int(11) NULL DEFAULT NULL COMMENT '超时类型 1 自动提醒 2 自动转交 3 自动同意',
  `user_ids` json NULL COMMENT '自动提醒和自动转交的人员列表',
  `examine_type` int(11) NULL DEFAULT NULL COMMENT '自动转交的审批类型 1 依次审批 2 会签 3 或签',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次ID',
  `flow_metadata_id` bigint(20) NOT NULL COMMENT '流程元数据ID',
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
  `id` bigint(20) NOT NULL,
  `module_id` bigint(20) NULL DEFAULT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `flow_id` bigint(20) NULL DEFAULT NULL COMMENT '流程ID',
  `target_module_id` bigint(20) NULL DEFAULT NULL COMMENT '目标模块ID',
  `is_insert` int(11) NULL DEFAULT NULL COMMENT '查找不到数据是否添加',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次ID',
  `flow_metadata_id` bigint(20) NOT NULL COMMENT '流程元数据ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '流程更新数据节点配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_flow_update
-- ----------------------------
INSERT INTO `wk_flow_update` VALUES (1608439791210037248, 1608439767700963328, 0, 1608439789305823233, 1608439777943453696, 0, 'b39254d7292a4cdd9aa87af4142b5729', 1608439788823478273, '2022-12-29 20:28:24');
INSERT INTO `wk_flow_update` VALUES (1608439791210037249, 1608439433763061760, 0, 1608439789297434628, 1608439771190624256, 0, 'c97e2238565b49a08c0c3c926c1e2daa', 1608439788836061184, '2022-12-29 20:28:24');
INSERT INTO `wk_flow_update` VALUES (1608439791210037250, 1608439765649948672, 0, 1608439789297434627, 1608439777943453696, 0, '261007cb63c44b9baf0ccf7ba5bfd1b4', 1608439788827672577, '2022-12-29 20:28:24');
INSERT INTO `wk_flow_update` VALUES (1608439791222620160, 1608439433763061760, 0, 1608439789297434629, 1608439777943453696, 0, 'c97e2238565b49a08c0c3c926c1e2daa', 1608439788836061184, '2022-12-29 20:28:24');
INSERT INTO `wk_flow_update` VALUES (1608439791222620161, 1608439746549088256, 0, 1608439789330989056, 1608439763607322624, 0, 'da48f281b0c4430fa0bd3beb182a7b75', 1608439788844449792, '2022-12-29 20:28:24');
INSERT INTO `wk_flow_update` VALUES (1608439791239397377, 1608439761141071872, 0, 1608439789297434624, 1608439763607322624, 0, '6b9c8eb44f81471392176edfa00d169d', 1608439788823478272, '2022-12-29 20:28:24');
INSERT INTO `wk_flow_update` VALUES (1608439792640294912, 1608439774743199744, 0, 1608439792367665153, 1608439777943453696, 0, '790e265c994a47978603f0494b51f561', 1608439792334110720, '2022-12-29 20:28:25');
INSERT INTO `wk_flow_update` VALUES (1608439792719986689, 1608439774743199744, 0, 1608439792367665154, 1608439771190624256, 0, '790e265c994a47978603f0494b51f561', 1608439792334110720, '2022-12-29 20:28:25');
INSERT INTO `wk_flow_update` VALUES (1608439794573869056, 1608439769529679872, 0, 1608439794469011456, 1608439777943453696, 0, 'ee01bc484faa43378cdd48f974371a9e', 1608439794439651328, '2022-12-29 20:28:25');

-- ----------------------------
-- Table structure for wk_message
-- ----------------------------
DROP TABLE IF EXISTS `wk_message`;
CREATE TABLE `wk_message`  (
  `message_id` bigint(20) NOT NULL COMMENT '消息ID',
  `value` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '内容',
  `data_id` bigint(20) NULL DEFAULT NULL COMMENT '数据ID',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `module_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模块名称',
  `type_id` bigint(20) NULL DEFAULT NULL COMMENT '类型ID',
  `type_name` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '对应类型名称',
  `status` int(11) NULL DEFAULT NULL COMMENT '审批状态',
  `type` int(11) NOT NULL DEFAULT 0 COMMENT '类型 0 系统 1 节点 2 自定义提醒 3 自定义按钮 4团队成员添加移除',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次ID',
  `time_value` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '限时处理的值 d代表天，m代表分钟 h代表小时 例 10d代表10天',
  `create_user_id` bigint(20) NOT NULL COMMENT '消息创建者 0为系统',
  `receiver` bigint(20) NOT NULL COMMENT '接收人',
  `read_time` datetime NULL DEFAULT NULL COMMENT '已读时间',
  `is_read` int(1) NULL DEFAULT 0 COMMENT '是否已读 0 未读 1 已读',
  `ext_data` json NULL COMMENT '扩展字段',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`message_id`) USING BTREE,
  INDEX `wk_message_receiver_index`(`receiver`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统消息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_message
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module
-- ----------------------------
DROP TABLE IF EXISTS `wk_module`;
CREATE TABLE `wk_module`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `application_id` bigint(20) NOT NULL COMMENT '应用ID',
  `main_field_id` bigint(20) NULL DEFAULT NULL COMMENT '主字段ID',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '应用图标',
  `icon_color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标颜色',
  `index_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '索引名称',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模块名称',
  `type` int(11) NOT NULL DEFAULT 0 COMMENT '0 自定义模块 1 自用系统 2 其他',
  `relate_module_id` bigint(20) NULL DEFAULT NULL COMMENT '关联的模块',
  `manage_user_id` json NULL COMMENT '流程管理员',
  `sort` int(10) UNSIGNED NOT NULL DEFAULT 999 COMMENT '排序 从小到大',
  `is_active` tinyint(1) NULL DEFAULT 0 COMMENT '已激活',
  `status` int(11) NULL DEFAULT 0 COMMENT '0 停用 1 正常 2 草稿',
  `module_type` int(2) NULL DEFAULT 1 COMMENT '模块类型 1 无代码模块 2 自定义bi模块',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_module_module_id_version_index`(`module_id`, `version`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '应用模块' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module
-- ----------------------------
INSERT INTO `wk_module` VALUES (1608392204914266114, 1608392204931043328, 0, 1608392204842962944, 1547822901325012992, 'wk wk-icon-pay-solid', '#0052CC', 'module_2022', '产品库存', 0, NULL, '[1]', 6, 1, 1, 1, '2022-12-29 17:19:19', NULL, '2022-12-29 17:19:19');
INSERT INTO `wk_module` VALUES (1608392444174143490, 1608392444123811840, 0, 1608392204842962944, 1522614130734891008, 'wk wk-icon-business-opportunity', '#0052CC', 'module_2022', '库存流水', 0, NULL, '[1]', 0, 1, 1, 1, '2022-12-29 17:20:16', NULL, '2022-12-29 17:20:16');
INSERT INTO `wk_module` VALUES (1608392472225648642, 1608392472255008768, 0, 1608392204842962944, 1523690993846681600, 'wk wk-o-task', '#0052CC', 'module_2022', '仓库信息', 0, NULL, '[1]', 4, 1, 1, 1, '2022-12-29 17:20:23', NULL, '2022-12-29 17:20:23');
INSERT INTO `wk_module` VALUES (1608392498519740417, 1608392498486185984, 0, 1608392204842962944, 1522619338940579840, 'wk wk-approval-8', '#0052CC', 'module_2022', '采购申请', 0, NULL, '[1]', 1, 1, 1, 1, '2022-12-29 17:20:29', NULL, '2022-12-29 17:20:29');
INSERT INTO `wk_module` VALUES (1608392505150935042, 1608392505129963520, 0, 1608392204842962944, 1522635492488740864, 'wk wk-icon-briefing', '#0052CC', 'module_2022', '签收入库', 0, NULL, '[1]', 2, 1, 1, 1, '2022-12-29 17:20:30', NULL, '2022-12-29 17:20:30');
INSERT INTO `wk_module` VALUES (1608392507768180737, 1608392507768180736, 0, 1608392204842962944, 1534356260369063936, 'wk wk-contract', '#0052CC', 'module_2022', '物品归还', 0, NULL, '[1]', 5, 1, 1, 1, '2022-12-29 17:20:31', NULL, '2022-12-29 17:20:31');
INSERT INTO `wk_module` VALUES (1608392509882109954, 1608392509865332736, 0, 1608392204842962944, 1523690294253551616, 'wk wk-icon-label-solid', '#0052CC', 'module_2022', '物品领用', 0, NULL, '[1]', 3, 1, 1, 1, '2022-12-29 17:20:32', NULL, '2022-12-29 17:20:32');
INSERT INTO `wk_module` VALUES (1608392513627623425, 1608392513568903168, 0, 1608392204842962944, 1547821988942258176, 'wk wk-icon-catalog2', '#0052CC', 'module_2022', '产品信息', 0, NULL, '[1]', 7, 1, 1, 1, '2022-12-29 17:20:32', NULL, '2022-12-29 17:20:32');
INSERT INTO `wk_module` VALUES (1608439742413504512, 1608439433763061760, 0, 1608439432936783872, 1547094601317294080, 'wk wk-approval-13', '#0052CC', 'module_2022', '还车登记', 0, NULL, '[1]', 6, 1, 1, 1, '2022-12-29 20:28:13', 1, '2022-12-29 20:28:13');
INSERT INTO `wk_module` VALUES (1608439758377025536, 1608439746549088256, 0, 1608439432936783872, 1532252179148120064, 'wk wk-approval-7', '#0052CC', 'module_2022', '汽车加油登记', 0, NULL, '[1]', 9, 1, 1, 1, '2022-12-29 20:28:16', 1, '2022-12-29 20:28:16');
INSERT INTO `wk_module` VALUES (1608439761153654784, 1608439761141071872, 0, 1608439432936783872, 1532231649967808512, 'wk wk-icon-business-opportunity', '#0052CC', 'module_2022', '油卡充值', 0, NULL, '[1]', 8, 1, 1, 1, '2022-12-29 20:28:17', 1, '2022-12-29 20:28:17');
INSERT INTO `wk_module` VALUES (1608439761900240896, 1608439761891852288, 0, 1608439432936783872, 1532174514554789888, 'wk wk-icon-task', '#0052CC', 'module_2022', '车辆事故登记', 0, NULL, '[1]', 14, 1, 1, 1, '2022-12-29 20:28:17', 1, '2022-12-29 20:28:17');
INSERT INTO `wk_module` VALUES (1608439763615711232, 1608439763607322624, 0, 1608439432936783872, 1532189498936467456, 'wk wk-icon-select', '#0052CC', 'module_2022', '油卡信息', 0, NULL, '[1]', 1, 1, 1, 1, '2022-12-29 20:28:18', 1, '2022-12-29 20:28:18');
INSERT INTO `wk_module` VALUES (1608439764773339136, 1608439764727201792, 0, 1608439432936783872, 1531192798142836736, 'wk wk-icon-account-book', '#0052CC', 'module_2022', '车辆费用', 0, NULL, '[1]', 7, 1, 1, 1, '2022-12-29 20:28:18', 1, '2022-12-29 20:28:18');
INSERT INTO `wk_module` VALUES (1608439765658337280, 1608439765649948672, 0, 1608439432936783872, 1532171631398608896, 'wk wk-icon-shelves-line', '#0052CC', 'module_2022', '车辆年检登记', 0, NULL, '[1]', 12, 1, 1, 1, '2022-12-29 20:28:18', 1, '2022-12-29 20:28:18');
INSERT INTO `wk_module` VALUES (1608439767147315200, 1608439767134732288, 0, 1608439432936783872, 1531101048367861760, 'wk wk-social', '#0052CC', 'module_2022', '车辆配件信息', 0, NULL, '[1]', 3, 1, 1, 1, '2022-12-29 20:28:19', 1, '2022-12-29 20:28:19');
INSERT INTO `wk_module` VALUES (1608439767709351936, 1608439767700963328, 0, 1608439432936783872, 1531924677582249984, 'wk wk-icon-fm-line', '#0052CC', 'module_2022', '车辆维保申请', 0, NULL, '[1]', 10, 1, 1, 1, '2022-12-29 20:28:19', 1, '2022-12-29 20:28:19');
INSERT INTO `wk_module` VALUES (1608439768426577920, 1608439768413995008, 0, 1608439432936783872, 1532173423981223936, 'wk wk-icon-b-people', '#0052CC', 'module_2022', '车辆保险登记', 0, NULL, '[1]', 13, 1, 1, 1, '2022-12-29 20:28:19', 1, '2022-12-29 20:28:19');
INSERT INTO `wk_module` VALUES (1608439769538068480, 1608439769529679872, 0, 1608439432936783872, 1531951270681763840, 'wk wk-approval-6', '#0052CC', 'module_2022', '维保取车登记', 0, NULL, '[1]', 11, 1, 1, 1, '2022-12-29 20:28:19', 1, '2022-12-29 20:28:19');
INSERT INTO `wk_module` VALUES (1608439771203207168, 1608439771190624256, 0, 1608439432936783872, 1534014966169935872, 'wk wk-approval-17', '#0052CC', 'module_2022', '驾驶员', 0, NULL, '[1]', 4, 1, 1, 1, '2022-12-29 20:28:19', 1, '2022-12-29 20:28:19');
INSERT INTO `wk_module` VALUES (1608439774751588352, 1608439774743199744, 0, 1608439432936783872, 1531113641056784384, 'wk wk-approval-14', '#0052CC', 'module_2022', '用车申请', 0, NULL, '[1]', 5, 1, 1, 1, '2022-12-29 20:28:20', 1, '2022-12-29 20:28:20');
INSERT INTO `wk_module` VALUES (1608439776219594752, 1608439776207011840, 0, 1608439432936783872, 1529016059039920128, 'wk wk-icon-label-solid', '#0052CC', 'module_2022', '车队信息', 0, NULL, '[1]', 0, 1, 1, 1, '2022-12-29 20:28:21', 1, '2022-12-29 20:28:21');
INSERT INTO `wk_module` VALUES (1608439776680968192, 1608439776668385280, 0, 1608439432936783872, 1532178371464732672, 'wk wk-icon-category-note', '#0052CC', 'module_2022', '车辆违章登记', 0, NULL, '[1]', 15, 1, 1, 1, '2022-12-29 20:28:21', 1, '2022-12-29 20:28:21');
INSERT INTO `wk_module` VALUES (1608439777960230912, 1608439777943453696, 0, 1608439432936783872, 1531094470235377664, 'wk wk-approval-13', '#0052CC', 'module_2022', '车辆信息', 0, NULL, '[1]', 2, 1, 1, 1, '2022-12-29 20:28:21', 1, '2022-12-29 20:28:21');

-- ----------------------------
-- Table structure for wk_module_data_check_rule
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_data_check_rule`;
CREATE TABLE `wk_module_data_check_rule`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `rule_id` bigint(20) NOT NULL COMMENT '规则ID',
  `sort` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `formula` json NOT NULL COMMENT '公式',
  `tip` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '提示',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_module_data_check_module_id_version_index`(`module_id`, `version`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据校验表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_data_check_rule
-- ----------------------------
INSERT INTO `wk_module_data_check_rule` VALUES (1608392517280862211, 1547493096826150912, 5, 1608392507768180736, 0, '{\"expression\": \"IF(#{1608392507768180736-1523704530035900416}==\\\"耐用品\\\",true,false)\"}', '消耗品不需要归还', '2022-12-29 17:20:33');

-- ----------------------------
-- Table structure for wk_module_data_operation_record
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_data_operation_record`;
CREATE TABLE `wk_module_data_operation_record`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `module_id` bigint(20) NULL DEFAULT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `data_id` bigint(20) NOT NULL COMMENT '数据ID',
  `value` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '值',
  `action_type` int(11) NOT NULL COMMENT '操作类型: 0 新建,1 编辑,2 删除',
  `from_user_id` bigint(20) NULL DEFAULT NULL COMMENT '原负责人ID',
  `to_user_id` bigint(20) NULL DEFAULT NULL COMMENT '现负责人ID',
  `team_user_id` bigint(20) NULL DEFAULT NULL COMMENT '团队成员',
  `flow_id` bigint(20) NULL DEFAULT NULL COMMENT '节点ID',
  `examine_record_id` bigint(20) NULL DEFAULT NULL COMMENT '审核记录ID',
  `ext_data` json NULL COMMENT '扩展数据',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `remarks` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `data_id`(`data_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字段值操作记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_data_operation_record
-- ----------------------------
INSERT INTO `wk_module_data_operation_record` VALUES (1608410348143837184, 1608398882170810368, 0, 1608410293450113024, '安师大发发生的', 0, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2022-12-29 18:31:24', '新建');
INSERT INTO `wk_module_data_operation_record` VALUES (1608430753848045568, 1608398882170810368, 0, 1608430751088193536, '啊SD爱多分', 0, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2022-12-29 19:52:30', '新建');
INSERT INTO `wk_module_data_operation_record` VALUES (1608433181536403456, 1608398882170810368, 0, 1608430751088193536, '啊SD爱多分', 2, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2022-12-29 20:02:07', '删除');
INSERT INTO `wk_module_data_operation_record` VALUES (1608433224481882112, 1608398882170810368, 0, 1608410293450113024, '安师大发发生的', 1, NULL, NULL, NULL, NULL, NULL, '[{\"name\": \"产品描述\", \"type\": 2, \"value\": \"低调低调\", \"fieldId\": 1606112762074607616, \"formType\": \"textarea\", \"moduleId\": 1608398882170810368, \"oldValue\": \"\", \"fieldName\": \"fieldZoywvv\", \"fieldType\": 1}]', 1, '2022-12-29 20:02:19', '编辑');
INSERT INTO `wk_module_data_operation_record` VALUES (1608440062363402240, 1608398875837411328, 0, 1608440060631154688, '265655', 0, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2022-12-29 20:29:29', '新建');
INSERT INTO `wk_module_data_operation_record` VALUES (1608440281419317248, 1608398875837411328, 0, 1608440281163464704, '888', 0, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2022-12-29 20:30:21', '新建');

-- ----------------------------
-- Table structure for wk_module_field
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_field`;
CREATE TABLE `wk_module_field`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `field_id` bigint(20) NOT NULL COMMENT '主键ID',
  `group_id` int(11) NULL DEFAULT NULL COMMENT '分组ID',
  `field_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '自定义字段英文标识',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '字段名称',
  `type` int(11) NOT NULL DEFAULT 1 COMMENT '字段类型',
  `field_type` int(11) NOT NULL DEFAULT 1 COMMENT '0 系统 1 自定义',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `version` int(11) NULL DEFAULT NULL COMMENT '版本号',
  `remark` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字段说明',
  `input_tips` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '输入提示',
  `max_length` int(11) NULL DEFAULT NULL COMMENT '最大长度',
  `is_unique` int(11) NULL DEFAULT 0 COMMENT '是否唯一 1 是 0 否',
  `is_null` int(11) NULL DEFAULT 0 COMMENT '是否必填 1 是 0 否',
  `sorting` int(11) NULL DEFAULT 1 COMMENT '排序 从小到大',
  `operating` int(11) NULL DEFAULT 255 COMMENT '操作指令',
  `is_hidden` int(11) NOT NULL DEFAULT 0 COMMENT '是否隐藏  0不隐藏 1隐藏',
  `style_percent` int(11) NULL DEFAULT 50 COMMENT '样式百分比',
  `precisions` int(11) NULL DEFAULT NULL COMMENT '精度，允许的最大小数位',
  `form_position` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表单定位 坐标格式',
  `max_num_restrict` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '限制的最大数',
  `min_num_restrict` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '限制的最小数',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `moduleId_fieldId_version`(`module_id`, `field_id`, `version`) USING BTREE,
  INDEX `label`(`module_id`) USING BTREE,
  INDEX `update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '自定义字段表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_field
-- ----------------------------
INSERT INTO `wk_module_field` VALUES (1608392514869137409, 1547822901325012992, NULL, 'fieldMhlqon', '产品库存编号', 63, 1, 1608392204931043328, 0, NULL, '', NULL, 1, 0, 0, 245, 0, 50, NULL, '0,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137410, 1522614130734891008, NULL, 'fieldMhlqon', '用品名称', 1, 1, 1608392444123811840, 0, NULL, '', NULL, 0, 1, 0, 255, 0, 50, NULL, '0,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137411, 1523690993800544256, NULL, 'fieldMhlqon', '编号', 63, 1, 1608392472255008768, 0, NULL, '', NULL, 1, 0, 0, 245, 0, 50, NULL, '0,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137412, 1547822901371150336, NULL, 'fieldFowfyg', '产品名称', 52, 1, 1608392204931043328, 0, NULL, '', NULL, 0, 1, 1, 251, 0, 50, NULL, '0,1', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137413, 1522619338940579840, NULL, 'fieldMhlqon', '采购单号', 63, 1, 1608392498486185984, 0, NULL, '', NULL, 1, 0, 0, 245, 0, 50, NULL, '0,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137414, 1523706973259923456, NULL, 'fieldYyoira', '仓库', 52, 1, 1608392444123811840, 0, NULL, '', NULL, 0, 1, 1, 251, 0, 50, NULL, '0,1', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137415, 1522635492488740864, NULL, 'fieldMhlqon', '入库编号', 63, 1, 1608392505129963520, 0, NULL, '', NULL, 1, 0, 0, 245, 0, 50, NULL, '0,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137416, 1547822901417287680, NULL, 'fieldZoywvv', '所属仓库', 52, 1, 1608392204931043328, 0, NULL, '', NULL, 0, 0, 2, 251, 0, 50, NULL, '1,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137417, 1522619338986717184, NULL, 'fieldDqxojy', '申请时间', 13, 1, 1608392498486185984, 0, NULL, '', NULL, 0, 0, 1, 255, 0, 50, NULL, '0,1', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137418, 1534356260369063936, NULL, 'fieldHevxte', '物品归还编号', 63, 1, 1608392507768180736, 0, NULL, '', NULL, 1, 0, 0, 245, 0, 50, NULL, '0,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137419, 1522614130785222656, NULL, 'fieldFowfyg', '规格', 1, 1, 1608392444123811840, 0, NULL, '', NULL, 0, 0, 2, 255, 0, 50, NULL, '1,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137420, 1522622016580059136, NULL, 'fieldZoywvv', '申请人', 10, 1, 1608392498486185984, 0, NULL, '', NULL, 0, 1, 2, 255, 0, 50, NULL, '1,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137421, 1522635492660707328, NULL, 'fieldYlzjop', '关联采购申请单', 52, 1, 1608392505129963520, 0, NULL, '', NULL, 0, 1, 1, 251, 0, 50, NULL, '0,1', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137422, 1527563081086976000, NULL, 'fieldHsnfao', '关联领用单号', 52, 1, 1608392507768180736, 0, NULL, '', NULL, 0, 1, 1, 251, 0, 50, NULL, '0,1', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137423, 1522614130827165696, NULL, 'fieldZoywvv', '单位', 1, 1, 1608392444123811840, 0, NULL, '', NULL, 0, 0, 3, 255, 0, 50, NULL, '1,1', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137424, 1522622016626196480, NULL, 'fieldYlzjop', '部门', 12, 1, 1608392498486185984, 0, NULL, '', NULL, 0, 1, 3, 255, 0, 50, NULL, '1,1', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137425, 1547825385271533568, NULL, 'fieldYlzjop', '入库数量', 56, 1, 1608392204931043328, 0, NULL, '', NULL, 0, 0, 3, 255, 0, 50, NULL, '1,1', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137426, 1522635492534878208, NULL, 'fieldFowfyg', '签收人', 10, 1, 1608392505129963520, 0, NULL, '', NULL, 0, 1, 2, 255, 0, 50, NULL, '1,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137427, 1523704529641635840, NULL, 'fieldTwjaes', '归还日期', 4, 1, 1608392507768180736, 0, NULL, '', NULL, 0, 1, 2, 255, 0, 50, NULL, '1,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137428, 1522614679714758656, NULL, 'fieldWisysx', '分类', 3, 1, 1608392444123811840, 0, NULL, '', NULL, 0, 0, 4, 255, 0, 50, 2, '2,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137429, 1547825385321865216, NULL, 'fieldDomlvu', '领用数量', 56, 1, 1608392204931043328, 0, NULL, '', NULL, 0, 0, 4, 255, 0, 50, NULL, '2,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137430, 1522619339032854528, NULL, 'fieldFowfyg', '用途', 1, 1, 1608392498486185984, 0, NULL, '', NULL, 0, 1, 4, 255, 0, 100, NULL, '2,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137431, 1547484787855400960, NULL, 'fieldPizhki', '入库数量', 5, 1, 1608392444123811840, 0, NULL, '', NULL, 0, 0, 5, 255, 0, 50, 4, '2,1', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137432, 1523704529738104832, NULL, 'fieldFowfyg', '归还人员', 10, 1, 1608392507768180736, 0, NULL, '', NULL, 0, 1, 3, 255, 0, 50, NULL, '1,1', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137433, 1523690294253551616, NULL, 'fieldMhlqon', '领用单号', 63, 1, 1608392509865332736, 0, NULL, '', NULL, 1, 0, 0, 245, 0, 50, NULL, '0,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137434, 1522622016697499648, 1, 'fieldTghtjb', '采购信息', 45, 1, 1608392498486185984, 0, '添加明细表格', '', NULL, 0, 0, 5, 233, 0, 100, 2, '3,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137435, 1527570189740195840, NULL, 'fieldQyghpc', '归还部门', 12, 1, 1608392507768180736, 0, NULL, '', NULL, 0, 0, 4, 255, 0, 50, NULL, '2,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137436, 1547823290552229888, NULL, 'fieldLnxruf', '领用数量', 5, 1, 1608392444123811840, 0, NULL, '', NULL, 0, 0, 6, 255, 0, 50, 4, '3,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137437, 1523690294299688960, NULL, 'fieldTwjaes', '领用日期', 4, 1, 1608392509865332736, 0, NULL, '', NULL, 0, 1, 1, 255, 0, 50, NULL, '0,1', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137438, 1547821988896120832, NULL, 'fieldMhlqon', '产品编号', 63, 1, 1608392513568903168, 0, NULL, '', NULL, 1, 0, 0, 245, 0, 50, NULL, '0,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137439, 1522623630405632000, 1, 'fieldYyoira', '序号', 63, 1, 1608392498486185984, 0, NULL, '', NULL, 1, 0, 6, 171, 0, 50, NULL, '4,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137440, 1523704529893294080, 1, 'fieldTghtjb', '归还明细', 45, 1, 1608392507768180736, 0, '添加明细表格', '', NULL, 0, 0, 5, 233, 0, 100, 2, '3,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137441, 1547823290770333696, NULL, 'fieldVjmdue', '归还数量', 5, 1, 1608392444123811840, 0, NULL, '', NULL, 0, 0, 7, 255, 0, 50, 4, '3,1', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137442, 1523690294341632000, NULL, 'fieldFowfyg', '领用人', 10, 1, 1608392509865332736, 0, NULL, '', NULL, 0, 1, 2, 255, 0, 50, NULL, '1,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137443, 1547821988942258176, NULL, 'fieldFowfyg', '产品名称', 1, 1, 1608392513568903168, 0, NULL, '', NULL, 0, 1, 1, 255, 0, 50, NULL, '0,1', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137444, 1527573799052034048, 1, 'fieldHgzwjo', '归还产品', 1, 1, 1608392507768180736, 0, NULL, '', NULL, 0, 0, 6, 171, 0, 50, NULL, '4,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137445, 1547099620317929472, 1, 'fieldQyghpc', '用品名称', 52, 1, 1608392498486185984, 0, NULL, '', NULL, 0, 0, 7, 171, 0, 50, NULL, '4,1', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392514869137446, 1522601270784917504, NULL, 'dataId', '数据ID', 1, 0, 1608392444123811840, 0, NULL, NULL, NULL, 0, 0, 8, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515401814018, 1547827576711487488, NULL, 'fieldZoywvv', '规格', 1, 1, 1608392513568903168, 0, NULL, '', NULL, 0, 0, 2, 255, 0, 50, NULL, '1,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202625, 1522622016793968640, 1, 'fieldPavmpj', '规格', 1, 1, 1608392498486185984, 0, NULL, '', NULL, 0, 0, 8, 171, 0, 50, NULL, '4,2', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202626, 1522601270789111808, NULL, 'createUserName', '创建人', 10, 0, 1608392444123811840, 0, NULL, NULL, NULL, 0, 0, 9, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202627, 1523690993846681600, NULL, 'fieldFowfyg', '仓库名称', 1, 1, 1608392472255008768, 0, NULL, '', NULL, 0, 1, 1, 255, 0, 50, NULL, '0,1', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202628, 1547827576753430528, NULL, 'fieldYlzjop', '单位', 1, 1, 1608392513568903168, 0, NULL, '', NULL, 0, 0, 3, 255, 0, 50, NULL, '1,1', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202629, 1522622016840105984, 1, 'fieldWisysx', '分类', 3, 1, 1608392498486185984, 0, NULL, '', NULL, 0, 1, 9, 171, 0, 50, 2, '4,3', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202630, 1522601270789111809, NULL, 'createTime', '创建时间', 13, 0, 1608392444123811840, 0, NULL, NULL, NULL, 0, 0, 10, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202631, 1527533665225064448, NULL, 'fieldDomlvu', '仓库管理员', 10, 1, 1608392472255008768, 0, NULL, '', NULL, 0, 0, 2, 255, 0, 50, NULL, '1,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202632, 1547827576799567872, NULL, 'fieldOjkodf', '单价', 6, 1, 1608392513568903168, 0, NULL, '', NULL, 0, 0, 4, 255, 0, 50, 2, '2,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202633, 1522635492576821248, NULL, 'fieldZoywvv', '部门', 12, 1, 1608392505129963520, 0, NULL, '', NULL, 0, 0, 3, 255, 0, 50, NULL, '1,1', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202634, 1547842611416449024, 1, 'fieldYymhyq', '归还仓库', 52, 1, 1608392507768180736, 0, NULL, '', NULL, 0, 0, 7, 171, 0, 50, NULL, '4,1', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202635, 1522601270789111810, NULL, 'updateTime', '更新时间', 13, 0, 1608392444123811840, 0, NULL, NULL, NULL, 0, 0, 11, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202636, 1522622016944963584, 1, 'fieldRxrbad', '单位', 1, 1, 1608392498486185984, 0, NULL, '', NULL, 0, 0, 10, 171, 0, 50, NULL, '4,4', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202637, 1523690993938956288, NULL, 'fieldYlzjop', '联系电话', 7, 1, 1608392472255008768, 0, NULL, '', NULL, 0, 0, 3, 255, 0, 50, NULL, '1,1', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202638, 1522635492618764288, NULL, 'fieldTwjaes', '签收日期', 4, 1, 1608392505129963520, 0, NULL, '', NULL, 0, 0, 4, 255, 0, 50, NULL, '2,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202639, 1522601270789111811, NULL, 'ownerUserName', '负责人', 10, 0, 1608392444123811840, 0, NULL, NULL, NULL, 0, 0, 12, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202640, 1523704529989763072, 1, 'fieldYlzjop', '规格', 1, 1, 1608392507768180736, 0, NULL, '', NULL, 0, 0, 8, 171, 0, 50, NULL, '4,2', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202641, 1522622016991100928, 1, 'fieldOjkodf', '采购数量', 5, 1, 1608392498486185984, 0, NULL, '', NULL, 0, 0, 11, 171, 0, 50, 4, '4,5', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202642, 1523690993985093632, NULL, 'fieldWisysx', '地址', 43, 1, 1608392472255008768, 0, NULL, '', NULL, 0, 0, 4, 255, 0, 100, 1, '2,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202643, 1522635492702650368, 1, 'fieldTghtjb', '签收信息', 45, 1, 1608392505129963520, 0, '添加明细表格', '', NULL, 0, 0, 5, 233, 0, 100, 2, '3,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202644, 1522601270789111812, NULL, 'currentFlowId', '当前节点', 1, 0, 1608392444123811840, 0, NULL, NULL, NULL, 0, 0, 13, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202645, 1523704530035900416, 1, 'fieldCvwjri', '分类', 3, 1, 1608392507768180736, 0, NULL, '', NULL, 0, 0, 9, 171, 0, 50, 2, '4,3', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202646, 1523903289730580480, 1, 'fieldPizhki', '单价', 6, 1, 1608392498486185984, 0, NULL, '', NULL, 0, 0, 12, 171, 0, 50, 2, '4,6', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202647, 1547825385368002560, NULL, 'fieldPavmpj', '归还数量', 56, 1, 1608392204931043328, 0, NULL, '', NULL, 0, 0, 5, 255, 0, 50, NULL, '2,1', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202648, 1523704530140758016, 1, 'fieldDomlvu', '单位', 1, 1, 1608392507768180736, 0, NULL, '', NULL, 0, 0, 10, 171, 0, 50, NULL, '4,4', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202649, 1522635492744593408, 1, 'fieldDomlvu', '用品名称', 1, 1, 1608392505129963520, 0, NULL, '', NULL, 0, 1, 6, 171, 0, 50, NULL, '4,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202650, 1522601270789111813, NULL, 'flowType', '节点类型', 1, 0, 1608392444123811840, 0, NULL, NULL, NULL, 0, 0, 14, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202651, 1523903289806077952, 1, 'fieldZhjtju', '金额', 6, 1, 1608392498486185984, 0, NULL, '', NULL, 0, 0, 13, 171, 0, 50, 2, '4,7', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202652, 1547825640620761088, NULL, 'fieldRxrbad', '当前库存', 64, 1, 1608392204931043328, 0, NULL, '', NULL, 0, 0, 6, 242, 0, 50, 1, '3,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202653, 1523704530191089664, 1, 'fieldOjkodf', '领用数量', 5, 1, 1608392507768180736, 0, NULL, '', NULL, 0, 0, 11, 171, 0, 50, 4, '4,5', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202654, 1522601270789111814, NULL, 'flowStatus', '节点状态', 1, 0, 1608392444123811840, 0, NULL, NULL, NULL, 0, 0, 15, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202655, 1524053824723017728, NULL, 'fieldLnxruf', '预计金额', 6, 1, 1608392498486185984, 0, NULL, '', NULL, 0, 0, 14, 255, 0, 50, 2, '5,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202656, 1523690294387769344, NULL, 'fieldZoywvv', '领用部门', 12, 1, 1608392509865332736, 0, NULL, '', NULL, 0, 0, 3, 255, 0, 50, NULL, '1,1', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202657, 1547820594801745920, NULL, 'dataId', '数据ID', 1, 0, 1608392204931043328, 0, NULL, NULL, NULL, 0, 0, 7, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202658, 1522601270789111815, NULL, 'categoryId', '分类ID', 1, 0, 1608392444123811840, 0, NULL, NULL, NULL, 0, 0, 16, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202659, 1523704530237227008, 1, 'fieldPfmdbh', '归还数量', 5, 1, 1608392507768180736, 0, NULL, '', NULL, 0, 1, 12, 171, 0, 50, 4, '4,6', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202660, 1522618449823629312, NULL, 'dataId', '数据ID', 1, 0, 1608392498486185984, 0, NULL, NULL, NULL, 0, 0, 15, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202661, 1523690294433906688, NULL, 'fieldYlzjop', '领用用途', 1, 1, 1608392509865332736, 0, NULL, '', NULL, 0, 1, 4, 255, 0, 50, NULL, '2,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515410202662, 1547820594801745921, NULL, 'createUserName', '创建人', 10, 0, 1608392204931043328, 0, NULL, NULL, NULL, 0, 0, 8, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515737358338, 1522601270789111816, NULL, 'stageId', '阶段ID', 1, 0, 1608392444123811840, 0, NULL, NULL, NULL, 0, 0, 17, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515737358339, 1523704530329501696, NULL, 'fieldRxrbad', '备注', 2, 1, 1608392507768180736, 0, NULL, '', 800, 0, 0, 13, 255, 0, 100, NULL, '5,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515737358340, 1522618449823629313, NULL, 'createUserName', '创建人', 10, 0, 1608392498486185984, 0, NULL, NULL, NULL, 0, 0, 16, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515737358341, 1547827576845705216, NULL, 'fieldWisysx', '分类', 3, 1, 1608392513568903168, 0, NULL, '', NULL, 0, 0, 5, 255, 0, 50, 2, '2,1', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515737358342, 1523696647592312832, 1, 'fieldTghtjb', '领用信息', 45, 1, 1608392509865332736, 0, '添加明细表格', '', NULL, 0, 0, 5, 233, 0, 100, 2, '3,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515737358343, 1522601270789111817, NULL, 'stageName', '阶段名称', 1, 0, 1608392444123811840, 0, NULL, NULL, NULL, 0, 0, 18, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515737358344, 1523701952950665216, NULL, 'dataId', '数据ID', 1, 0, 1608392507768180736, 0, NULL, NULL, NULL, 0, 0, 14, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515737358345, 1522618449823629314, NULL, 'createTime', '创建时间', 13, 0, 1608392498486185984, 0, NULL, NULL, NULL, 0, 0, 17, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515737358346, 1547821744326254592, NULL, 'dataId', '数据ID', 1, 0, 1608392513568903168, 0, NULL, NULL, NULL, 0, 0, 6, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515737358347, 1527551818508320768, 1, 'fieldHofwzx', '领用产品', 52, 1, 1608392509865332736, 0, NULL, '', NULL, 0, 0, 6, 171, 0, 50, NULL, '4,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515737358348, 1522601270789111818, NULL, 'stageStatus', '阶段状态', 1, 0, 1608392444123811840, 0, NULL, NULL, NULL, 0, 0, 19, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515737358349, 1523701952950665217, NULL, 'createUserName', '创建人', 10, 0, 1608392507768180736, 0, NULL, NULL, NULL, 0, 0, 15, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515737358350, 1522618449823629315, NULL, 'updateTime', '更新时间', 13, 0, 1608392498486185984, 0, NULL, NULL, NULL, 0, 0, 18, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515737358351, 1523690674521735168, NULL, 'dataId', '数据ID', 1, 0, 1608392472255008768, 0, NULL, NULL, NULL, 0, 0, 5, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515737358352, 1547821744326254593, NULL, 'createUserName', '创建人', 10, 0, 1608392513568903168, 0, NULL, NULL, NULL, 0, 0, 7, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515737358353, 1522601270789111819, NULL, 'moduleId', '模块ID', 1, 0, 1608392444123811840, 0, NULL, NULL, NULL, 0, 0, 20, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515737358354, 1523701952950665218, NULL, 'createTime', '创建时间', 13, 0, 1608392507768180736, 0, NULL, NULL, NULL, 0, 0, 16, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515737358355, 1522618449823629316, NULL, 'ownerUserName', '负责人', 10, 0, 1608392498486185984, 0, NULL, NULL, NULL, 0, 0, 19, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515737358356, 1523690674521735169, NULL, 'createUserName', '创建人', 10, 0, 1608392472255008768, 0, NULL, NULL, NULL, 0, 0, 6, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515737358357, 1522635492790730752, 1, 'fieldPavmpj', '规格', 1, 1, 1608392505129963520, 0, NULL, '', NULL, 0, 0, 7, 171, 0, 50, NULL, '4,1', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515737358358, 1523701952950665219, NULL, 'updateTime', '更新时间', 13, 0, 1608392507768180736, 0, NULL, NULL, NULL, 0, 0, 17, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515737358359, 1522618449823629317, NULL, 'currentFlowId', '当前节点', 1, 0, 1608392498486185984, 0, NULL, NULL, NULL, 0, 0, 20, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515737358360, 1523690674521735170, NULL, 'createTime', '创建时间', 13, 0, 1608392472255008768, 0, NULL, NULL, NULL, 0, 0, 7, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515737358361, 1522635492832673792, 1, 'fieldWisysx', '分类', 3, 1, 1608392505129963520, 0, NULL, '', NULL, 0, 1, 8, 171, 0, 50, 2, '4,2', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515737358362, 1523701952950665220, NULL, 'ownerUserName', '负责人', 10, 0, 1608392507768180736, 0, NULL, NULL, NULL, 0, 0, 18, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515737358363, 1522618449823629318, NULL, 'flowType', '节点类型', 1, 0, 1608392498486185984, 0, NULL, NULL, NULL, 0, 0, 21, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515737358364, 1522635492920754176, 1, 'fieldRxrbad', '单位', 1, 1, 1608392505129963520, 0, NULL, '', NULL, 0, 0, 9, 171, 0, 50, NULL, '4,3', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515737358365, 1523690674521735171, NULL, 'updateTime', '更新时间', 13, 0, 1608392472255008768, 0, NULL, NULL, NULL, 0, 0, 8, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515737358366, 1523701952950665221, NULL, 'currentFlowId', '当前节点', 1, 0, 1608392507768180736, 0, NULL, NULL, NULL, 0, 0, 19, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515737358368, 1522618449823629319, NULL, 'flowStatus', '节点状态', 1, 0, 1608392498486185984, 0, NULL, NULL, NULL, 0, 0, 22, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515737358370, 1522635492962697216, 1, 'fieldOjkodf', '入库数量', 5, 1, 1608392505129963520, 0, NULL, '', NULL, 0, 0, 10, 171, 0, 50, 4, '4,4', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392515737358371, 1547820594801745922, NULL, 'createTime', '创建时间', 13, 0, 1608392204931043328, 0, NULL, NULL, NULL, 0, 0, 9, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516064514050, 1522618449823629320, NULL, 'categoryId', '分类ID', 1, 0, 1608392498486185984, 0, NULL, NULL, NULL, 0, 0, 23, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516068708353, 1547820594801745923, NULL, 'updateTime', '更新时间', 13, 0, 1608392204931043328, 0, NULL, NULL, NULL, 0, 0, 10, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516068708354, 1523701952950665222, NULL, 'flowType', '节点类型', 1, 0, 1608392507768180736, 0, NULL, NULL, NULL, 0, 0, 20, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516068708355, 1547831583253274624, 1, 'fieldAuwcxa', '所属仓库', 52, 1, 1608392509865332736, 0, NULL, '', NULL, 0, 0, 7, 171, 0, 50, NULL, '4,1', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516068708356, 1522618449823629321, NULL, 'stageId', '阶段ID', 1, 0, 1608392498486185984, 0, NULL, NULL, NULL, 0, 0, 24, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516068708357, 1547820594801745924, NULL, 'ownerUserName', '负责人', 10, 0, 1608392204931043328, 0, NULL, NULL, NULL, 0, 0, 11, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516068708358, 1523701952954859520, NULL, 'flowStatus', '节点状态', 1, 0, 1608392507768180736, 0, NULL, NULL, NULL, 0, 0, 21, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516068708359, 1547821744326254594, NULL, 'createTime', '创建时间', 13, 0, 1608392513568903168, 0, NULL, NULL, NULL, 0, 0, 8, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516068708360, 1523696647688781824, 1, 'fieldYyoira', '规格', 1, 1, 1608392509865332736, 0, NULL, '', NULL, 0, 0, 8, 171, 0, 50, NULL, '4,2', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516068708362, 1522618449823629322, NULL, 'stageName', '阶段名称', 1, 0, 1608392498486185984, 0, NULL, NULL, NULL, 0, 0, 25, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516068708363, 1523701952954859521, NULL, 'categoryId', '分类ID', 1, 0, 1608392507768180736, 0, NULL, NULL, NULL, 0, 0, 22, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516068708364, 1547821744326254595, NULL, 'updateTime', '更新时间', 13, 0, 1608392513568903168, 0, NULL, NULL, NULL, 0, 0, 9, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516068708366, 1523696647734919168, 1, 'fieldCvwjri', '分类', 3, 1, 1608392509865332736, 0, NULL, '', NULL, 0, 0, 9, 171, 0, 50, 2, '4,3', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516068708367, 1522618449823629323, NULL, 'stageStatus', '阶段状态', 1, 0, 1608392498486185984, 0, NULL, NULL, NULL, 0, 0, 26, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516068708369, 1523701952954859522, NULL, 'stageId', '阶段ID', 1, 0, 1608392507768180736, 0, NULL, NULL, NULL, 0, 0, 23, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516068708371, 1547821744326254596, NULL, 'ownerUserName', '负责人', 10, 0, 1608392513568903168, 0, NULL, NULL, NULL, 0, 0, 10, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516068708373, 1522618449823629324, NULL, 'moduleId', '模块ID', 1, 0, 1608392498486185984, 0, NULL, NULL, NULL, 0, 0, 27, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516068708374, 1523701952954859523, NULL, 'stageName', '阶段名称', 1, 0, 1608392507768180736, 0, NULL, NULL, NULL, 0, 0, 24, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516068708377, 1547821744326254597, NULL, 'teamMember', '团队成员', 1, 0, 1608392513568903168, 0, NULL, NULL, NULL, 0, 0, 11, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516068708379, 1523690674521735172, NULL, 'ownerUserName', '负责人', 10, 0, 1608392472255008768, 0, NULL, NULL, NULL, 0, 0, 9, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516068708380, 1523701952954859524, NULL, 'stageStatus', '阶段状态', 1, 0, 1608392507768180736, 0, NULL, NULL, NULL, 0, 0, 25, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516068708383, 1523903534505967616, 1, 'fieldRqwfhn', '采购单价', 6, 1, 1608392505129963520, 0, NULL, '', NULL, 0, 0, 11, 171, 0, 50, 2, '4,5', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516068708384, 1523690674521735173, NULL, 'currentFlowId', '当前节点', 1, 0, 1608392472255008768, 0, NULL, NULL, NULL, 0, 0, 10, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516068708385, 1523701952954859525, NULL, 'moduleId', '模块ID', 1, 0, 1608392507768180736, 0, NULL, NULL, NULL, 0, 0, 26, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516068708386, 1523903534577270784, 1, 'fieldPizhki', '采购金额小计', 6, 1, 1608392505129963520, 0, NULL, '', NULL, 0, 0, 12, 171, 0, 50, 2, '4,6', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516068708387, 1523690674521735174, NULL, 'flowType', '节点类型', 1, 0, 1608392472255008768, 0, NULL, NULL, NULL, 0, 0, 11, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516068708388, 1528919053848096768, 1, 'fieldYymhyq', '入库仓库', 52, 1, 1608392505129963520, 0, NULL, '', NULL, 0, 0, 13, 171, 0, 50, NULL, '4,7', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516433612802, 1547820594801745925, NULL, 'teamMember', '团队成员', 1, 0, 1608392204931043328, 0, NULL, NULL, NULL, 0, 0, 12, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516433612803, 1522635895833985024, NULL, 'fieldYyoira', '总数量', 64, 1, 1608392505129963520, 0, NULL, '', NULL, 0, 0, 14, 242, 0, 50, 2, '5,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516433612806, 1523696647835582464, 1, 'fieldEilney', '单位', 1, 1, 1608392509865332736, 0, NULL, '', NULL, 0, 0, 10, 171, 0, 50, NULL, '4,4', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516433612808, 1547820594801745926, NULL, 'currentFlowId', '当前节点', 1, 0, 1608392204931043328, 0, NULL, NULL, NULL, 0, 0, 13, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516433612810, 1547820594801745927, NULL, 'flowType', '节点类型', 1, 0, 1608392204931043328, 0, NULL, NULL, NULL, 0, 0, 14, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516433612811, 1523696647881719808, 1, 'fieldOjkodf', '领用数量', 5, 1, 1608392509865332736, 0, NULL, '', NULL, 0, 0, 11, 171, 0, 50, 4, '4,5', '', '', '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516433612812, 1547821744326254598, NULL, 'currentFlowId', '当前节点', 1, 0, 1608392513568903168, 0, NULL, NULL, NULL, 0, 0, 12, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516433612813, 1523699173540593664, 1, 'fieldCefggt', '物品用途', 1, 1, 1608392509865332736, 0, NULL, '', NULL, 0, 0, 12, 171, 0, 50, NULL, '4,6', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516433612814, 1547821744326254599, NULL, 'flowType', '节点类型', 1, 0, 1608392513568903168, 0, NULL, NULL, NULL, 0, 0, 13, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516433612815, 1523690674521735175, NULL, 'flowStatus', '节点状态', 1, 0, 1608392472255008768, 0, NULL, NULL, NULL, 0, 0, 12, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516576219138, 1547820594805940224, NULL, 'flowStatus', '节点状态', 1, 0, 1608392204931043328, 0, NULL, NULL, NULL, 0, 0, 15, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516580413441, 1523906876086992896, NULL, 'fieldZhjtju', '采购金额合计', 6, 1, 1608392505129963520, 0, NULL, '', NULL, 0, 0, 15, 255, 0, 50, 2, '5,1', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516580413443, 1547820594805940225, NULL, 'categoryId', '分类ID', 1, 0, 1608392204931043328, 0, NULL, NULL, NULL, 0, 0, 16, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516580413444, 1547833333272092672, 1, 'fieldRxehqc', '单价', 6, 1, 1608392509865332736, 0, NULL, '', NULL, 0, 0, 13, 171, 0, 50, 2, '4,7', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516580413445, 1547821744326254600, NULL, 'flowStatus', '节点状态', 1, 0, 1608392513568903168, 0, NULL, NULL, NULL, 0, 0, 14, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516622356481, 1523690674521735176, NULL, 'categoryId', '分类ID', 1, 0, 1608392472255008768, 0, NULL, NULL, NULL, 0, 0, 13, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516626550786, 1522636123425308672, NULL, 'fieldHsnfao', '备注', 2, 1, 1608392505129963520, 0, NULL, '', 800, 0, 0, 16, 255, 0, 100, NULL, '6,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516630745090, 1523701602042609664, NULL, 'fieldQyghpc', '备注', 2, 1, 1608392509865332736, 0, NULL, '', 800, 0, 0, 14, 255, 0, 100, NULL, '5,0', NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516630745091, 1547821744326254601, NULL, 'categoryId', '分类ID', 1, 0, 1608392513568903168, 0, NULL, NULL, NULL, 0, 0, 15, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516630745092, 1547820594805940226, NULL, 'stageId', '阶段ID', 1, 0, 1608392204931043328, 0, NULL, NULL, NULL, 0, 0, 17, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516672688130, 1522630040774344704, NULL, 'dataId', '数据ID', 1, 0, 1608392505129963520, 0, NULL, NULL, NULL, 0, 0, 17, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516672688131, 1523690674521735177, NULL, 'stageId', '阶段ID', 1, 0, 1608392472255008768, 0, NULL, NULL, NULL, 0, 0, 14, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516689465347, 1547820594805940227, NULL, 'stageName', '阶段名称', 1, 0, 1608392204931043328, 0, NULL, NULL, NULL, 0, 0, 18, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516689465348, 1547821744326254602, NULL, 'stageId', '阶段ID', 1, 0, 1608392513568903168, 0, NULL, NULL, NULL, 0, 0, 16, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516689465349, 1522640460302090240, NULL, 'dataId', '数据ID', 1, 0, 1608392509865332736, 0, NULL, NULL, NULL, 0, 0, 15, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516689465350, 1522630040778539008, NULL, 'createUserName', '创建人', 10, 0, 1608392505129963520, 0, NULL, NULL, NULL, 0, 0, 18, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516689465351, 1523690674521735178, NULL, 'stageName', '阶段名称', 1, 0, 1608392472255008768, 0, NULL, NULL, NULL, 0, 0, 15, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516689465353, 1522630040778539009, NULL, 'createTime', '创建时间', 13, 0, 1608392505129963520, 0, NULL, NULL, NULL, 0, 0, 19, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516689465354, 1547821744326254603, NULL, 'stageName', '阶段名称', 1, 0, 1608392513568903168, 0, NULL, NULL, NULL, 0, 0, 17, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516689465355, 1547820594805940228, NULL, 'stageStatus', '阶段状态', 1, 0, 1608392204931043328, 0, NULL, NULL, NULL, 0, 0, 19, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516689465357, 1522640460302090241, NULL, 'createUserName', '创建人', 10, 0, 1608392509865332736, 0, NULL, NULL, NULL, 0, 0, 16, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516689465359, 1523690674521735179, NULL, 'stageStatus', '阶段状态', 1, 0, 1608392472255008768, 0, NULL, NULL, NULL, 0, 0, 16, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516689465361, 1547821744326254604, NULL, 'stageStatus', '阶段状态', 1, 0, 1608392513568903168, 0, NULL, NULL, NULL, 0, 0, 18, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516689465362, 1547820594805940229, NULL, 'moduleId', '模块ID', 1, 0, 1608392204931043328, 0, NULL, NULL, NULL, 0, 0, 20, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516689465364, 1523690674521735180, NULL, 'moduleId', '模块ID', 1, 0, 1608392472255008768, 0, NULL, NULL, NULL, 0, 0, 17, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516689465366, 1522630040778539010, NULL, 'updateTime', '更新时间', 13, 0, 1608392505129963520, 0, NULL, NULL, NULL, 0, 0, 20, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516689465368, 1547821744326254605, NULL, 'moduleId', '模块ID', 1, 0, 1608392513568903168, 0, NULL, NULL, NULL, 0, 0, 19, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392516689465372, 1522630040778539011, NULL, 'ownerUserName', '负责人', 10, 0, 1608392505129963520, 0, NULL, NULL, NULL, 0, 0, 21, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392517066952710, 1522640460302090242, NULL, 'createTime', '创建时间', 13, 0, 1608392509865332736, 0, NULL, NULL, NULL, 0, 0, 17, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392517066952714, 1522630040778539012, NULL, 'currentFlowId', '当前节点', 1, 0, 1608392505129963520, 0, NULL, NULL, NULL, 0, 0, 22, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392517066952720, 1522640460302090243, NULL, 'updateTime', '更新时间', 13, 0, 1608392509865332736, 0, NULL, NULL, NULL, 0, 0, 18, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392517066952725, 1522630040778539013, NULL, 'flowType', '节点类型', 1, 0, 1608392505129963520, 0, NULL, NULL, NULL, 0, 0, 23, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392517066952726, 1522640460302090244, NULL, 'ownerUserName', '负责人', 10, 0, 1608392509865332736, 0, NULL, NULL, NULL, 0, 0, 19, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392517066952729, 1522630040778539014, NULL, 'flowStatus', '节点状态', 1, 0, 1608392505129963520, 0, NULL, NULL, NULL, 0, 0, 24, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392517247307778, 1522640460302090245, NULL, 'currentFlowId', '当前节点', 1, 0, 1608392509865332736, 0, NULL, NULL, NULL, 0, 0, 20, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392517276667906, 1522630040778539015, NULL, 'categoryId', '分类ID', 1, 0, 1608392505129963520, 0, NULL, NULL, NULL, 0, 0, 25, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392517280862210, 1522640460302090246, NULL, 'flowType', '节点类型', 1, 0, 1608392509865332736, 0, NULL, NULL, NULL, 0, 0, 21, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392517280862212, 1522630040778539016, NULL, 'stageId', '阶段ID', 1, 0, 1608392505129963520, 0, NULL, NULL, NULL, 0, 0, 26, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392517280862213, 1522640460302090247, NULL, 'flowStatus', '节点状态', 1, 0, 1608392509865332736, 0, NULL, NULL, NULL, 0, 0, 22, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392517339582466, 1522630040778539017, NULL, 'stageName', '阶段名称', 1, 0, 1608392505129963520, 0, NULL, NULL, NULL, 0, 0, 27, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392517339582467, 1522640460302090248, NULL, 'categoryId', '分类ID', 1, 0, 1608392509865332736, 0, NULL, NULL, NULL, 0, 0, 23, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392517339582468, 1522630040778539018, NULL, 'stageStatus', '阶段状态', 1, 0, 1608392505129963520, 0, NULL, NULL, NULL, 0, 0, 28, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392517402497026, 1522640460302090249, NULL, 'stageId', '阶段ID', 1, 0, 1608392509865332736, 0, NULL, NULL, NULL, 0, 0, 24, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392517402497027, 1522630040778539019, NULL, 'moduleId', '模块ID', 1, 0, 1608392505129963520, 0, NULL, NULL, NULL, 0, 0, 29, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392517469605890, 1522640460302090250, NULL, 'stageName', '阶段名称', 1, 0, 1608392509865332736, 0, NULL, NULL, NULL, 0, 0, 25, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392517469605891, 1522640460302090251, NULL, 'stageStatus', '阶段状态', 1, 0, 1608392509865332736, 0, NULL, NULL, NULL, 0, 0, 26, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608392517536714754, 1522640460302090252, NULL, 'moduleId', '模块ID', 1, 0, 1608392509865332736, 0, NULL, NULL, NULL, 0, 0, 27, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_field` VALUES (1608439784725643264, 1532171630933041152, NULL, 'fieldMhlqon', '车辆', 52, 1, 1608439765649948672, 0, NULL, '', NULL, 0, 1, 0, 251, 0, 50, NULL, '0,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439784725643265, 1532231649967808512, NULL, 'fieldZoywvv', '序号', 63, 1, 1608439761141071872, 0, NULL, '', NULL, 1, 0, 0, 245, 0, 50, NULL, '0,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439784725643266, 1531185823434715136, NULL, 'fieldMhlqon', '关联用车申请单', 52, 1, 1608439433763061760, 0, NULL, '', NULL, 0, 1, 0, 251, 0, 50, NULL, '0,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439784729837568, 1532174514554789888, NULL, 'fieldMhlqon', '文件号-处理号', 1, 1, 1608439761891852288, 0, NULL, '', NULL, 1, 0, 0, 255, 0, 50, NULL, '0,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439784729837569, 1532252179148120064, NULL, 'fieldYyoira', '序号', 63, 1, 1608439746549088256, 0, NULL, '', NULL, 1, 0, 0, 245, 0, 100, NULL, '0,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439784729837570, 1532173423880560640, NULL, 'fieldMhlqon', '车辆', 52, 1, 1608439768413995008, 0, NULL, '', NULL, 0, 1, 0, 251, 0, 50, NULL, '0,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439784767586304, 1532174514600927232, NULL, 'fieldDqxojy', '事故时间', 13, 1, 1608439761891852288, 0, NULL, '', NULL, 0, 1, 1, 255, 0, 50, NULL, '0,1', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439784767586305, 1531924677582249984, NULL, 'fieldYyoira', '维保申请单号', 63, 1, 1608439767700963328, 0, NULL, '', NULL, 1, 0, 0, 245, 0, 50, NULL, '0,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439784767586306, 1531192798000230400, NULL, 'fieldMhlqon', '关联用车申请单', 52, 1, 1608439764727201792, 0, NULL, '', NULL, 0, 0, 0, 251, 0, 100, NULL, '0,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439784767586307, 1532173423826034688, NULL, 'fieldTwjaes', '投保日期', 4, 1, 1608439768413995008, 0, NULL, '', NULL, 0, 1, 1, 255, 0, 50, NULL, '0,1', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439784767586308, 1531220096699117568, NULL, 'fieldMhlqon', '车辆', 52, 1, 1608439746549088256, 0, NULL, '', NULL, 0, 1, 1, 251, 0, 50, NULL, '1,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439784805335040, 1532176550532816896, NULL, 'fieldMygrvj', '关联用车申请', 52, 1, 1608439761891852288, 0, NULL, '', NULL, 0, 1, 2, 251, 0, 50, NULL, '1,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439784805335041, 1531221281732603904, NULL, 'fieldMhlqon', '车辆', 52, 1, 1608439767700963328, 0, NULL, '', NULL, 0, 1, 1, 251, 0, 50, NULL, '1,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439784805335042, 1531192798050562048, NULL, 'fieldFowfyg', '车辆', 52, 1, 1608439764727201792, 0, NULL, '', NULL, 0, 1, 1, 251, 0, 50, NULL, '1,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439784809529344, 1532173423930892288, NULL, 'fieldFowfyg', '车辆所属部门', 12, 1, 1608439768413995008, 0, NULL, '', NULL, 0, 0, 2, 255, 0, 100, NULL, '1,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439784880832512, 1531209525480173568, NULL, 'fieldMhlqon', '油卡号码', 52, 1, 1608439761141071872, 0, NULL, '', NULL, 0, 1, 1, 251, 0, 50, NULL, '1,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439784897609728, 1532174514651258880, NULL, 'fieldFowfyg', '车辆', 52, 1, 1608439761891852288, 0, NULL, '', NULL, 0, 0, 3, 251, 0, 50, NULL, '1,1', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439784910192640, 1531220096745254912, NULL, 'fieldTwjaes', '加油日期', 4, 1, 1608439746549088256, 0, NULL, '', NULL, 0, 1, 2, 255, 0, 50, NULL, '1,1', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439784918581248, 1532171630983372800, NULL, 'fieldTwjaes', '年检日期', 4, 1, 1608439765649948672, 0, NULL, '', NULL, 0, 1, 1, 255, 0, 50, NULL, '0,1', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439784931164160, 1531192798096699392, NULL, 'fieldTwjaes', '登记日期', 4, 1, 1608439764727201792, 0, NULL, '', NULL, 0, 1, 2, 255, 0, 50, NULL, '1,1', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439784935358464, 1532176549903671296, NULL, 'fieldDomlvu', '驾驶员', 52, 1, 1608439761891852288, 0, NULL, '', NULL, 0, 0, 4, 251, 0, 50, NULL, '2,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439784935358465, 1531185823489241088, NULL, 'fieldFowfyg', '用车人', 10, 1, 1608439433763061760, 0, NULL, '', NULL, 0, 0, 1, 255, 0, 50, NULL, '0,1', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439784952135680, 1531220096795586560, NULL, 'fieldFowfyg', '车辆所属部门', 12, 1, 1608439746549088256, 0, NULL, '', NULL, 0, 0, 3, 255, 0, 50, NULL, '2,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439784956329984, 1532171631033704448, NULL, 'fieldFowfyg', '车辆所属部门', 12, 1, 1608439765649948672, 0, NULL, '', NULL, 0, 0, 2, 255, 0, 50, NULL, '1,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439784973107200, 1531192798142836736, NULL, 'fieldOjkodf', '修理维护费', 6, 1, 1608439764727201792, 0, NULL, '', NULL, 0, 0, 3, 255, 0, 50, 2, '2,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439784973107201, 1532174514697396224, NULL, 'fieldZoywvv', '所属部门', 12, 1, 1608439761891852288, 0, NULL, '', NULL, 0, 0, 5, 255, 0, 50, NULL, '2,1', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439784977301504, 1547094601317294080, NULL, 'fieldHsnfao', '归还车辆', 1, 1, 1608439433763061760, 0, NULL, '', NULL, 0, 1, 2, 255, 0, 50, NULL, '1,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439784994078720, 1532171631088230400, 1, 'fieldTghtjb', '年检内容', 45, 1, 1608439765649948672, 0, '添加明细表格', '', NULL, 0, 0, 3, 233, 0, 100, 2, '2,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785015050240, 1532228759043776512, NULL, 'fieldEilney', '驾驶员', 1, 1, 1608439433763061760, 0, NULL, '', NULL, 0, 0, 3, 255, 0, 50, NULL, '1,1', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785027633152, 1532173423981223936, NULL, 'fieldZoywvv', '保单号', 1, 1, 1608439768413995008, 0, NULL, '', NULL, 0, 0, 3, 255, 0, 50, NULL, '2,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785036021760, 1531220096841723904, NULL, 'fieldWisysx', '付款方式', 3, 1, 1608439746549088256, 0, NULL, '', NULL, 0, 1, 4, 255, 0, 50, 2, '2,1', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785040216064, 1531192798188974080, NULL, 'fieldPfmdbh', '路桥费', 6, 1, 1608439764727201792, 0, NULL, '', NULL, 0, 0, 4, 255, 0, 50, 2, '2,1', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785052798976, 1534083811756802048, NULL, 'fieldDqxojy', '出车日期', 13, 1, 1608439433763061760, 0, NULL, '', NULL, 0, 1, 4, 255, 0, 50, NULL, '2,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785061187584, 1531221281791324160, NULL, 'fieldFowfyg', '车辆所属部门', 12, 1, 1608439767700963328, 0, NULL, '', NULL, 0, 0, 2, 255, 0, 50, NULL, '1,1', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785065381888, 1532173424035749888, NULL, 'fieldYlzjop', '保险公司', 1, 1, 1608439768413995008, 0, NULL, '', NULL, 0, 0, 4, 255, 0, 50, NULL, '2,1', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785073770496, 1531209525526310912, NULL, 'fieldTwjaes', '充值日期', 4, 1, 1608439761141071872, 0, NULL, '', NULL, 0, 0, 2, 255, 0, 50, NULL, '1,1', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785086353408, 1532171631138562048, 1, 'fieldZoywvv', '年审项目', 2, 1, 1608439765649948672, 0, NULL, '', 800, 0, 0, 4, 171, 0, 50, NULL, '3,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785090547712, 1532176549954002944, NULL, 'fieldWisysx', '事故地点', 43, 1, 1608439761891852288, 0, NULL, '', NULL, 0, 0, 6, 255, 0, 100, 1, '3,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785098936320, 1531922301118963712, NULL, 'fieldWisysx', '维保类型', 3, 1, 1608439767700963328, 0, NULL, '', NULL, 0, 1, 3, 255, 0, 50, 2, '2,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785103130624, 1532173424086081536, 1, 'fieldTghtjb', '参保明细', 45, 1, 1608439768413995008, 0, '添加明细表格', '', NULL, 0, 0, 5, 233, 0, 100, 2, '3,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785107324928, 1532252179328475136, NULL, 'fieldCvwjri', '油品类型', 3, 1, 1608439746549088256, 0, NULL, '', NULL, 0, 0, 5, 255, 0, 50, 2, '3,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785115713536, 1532248972401385472, NULL, 'fieldRqwfhn', '充值前余额', 5, 1, 1608439761141071872, 0, NULL, '', NULL, 0, 0, 3, 255, 0, 50, 4, '2,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785124102144, 1531187321879179264, NULL, 'fieldOjkodf', '出车里程', 5, 1, 1608439433763061760, 0, NULL, '', NULL, 0, 1, 5, 255, 0, 50, 4, '2,1', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785140879360, 1532176550008528896, NULL, 'fieldCvwjri', '事故种类', 3, 1, 1608439761891852288, 0, NULL, '', NULL, 0, 1, 7, 255, 0, 100, 2, '4,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785145073664, 1531922301232209920, NULL, 'fieldYlzjop', '修理厂', 1, 1, 1608439767700963328, 0, NULL, '', NULL, 0, 0, 4, 255, 0, 50, NULL, '2,1', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785153462272, 1532173424140607488, 1, 'fieldDomlvu', '保险名称', 1, 1, 1608439768413995008, 0, NULL, '', NULL, 0, 0, 6, 171, 0, 50, NULL, '4,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785157656576, 1532171631188893696, 1, 'fieldWisysx', '年审情况', 3, 1, 1608439765649948672, 0, NULL, '', NULL, 0, 0, 5, 171, 0, 50, 2, '3,1', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785157656577, 1531209525610196992, NULL, 'fieldPfmdbh', '充值金额', 6, 1, 1608439761141071872, 0, NULL, '', NULL, 0, 1, 4, 255, 0, 50, 2, '2,1', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785161850880, 1531192798235111424, NULL, 'fieldRmsiyy', '养路费', 6, 1, 1608439764727201792, 0, NULL, '', NULL, 0, 0, 5, 255, 0, 50, 2, '3,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785161850881, 1534083811840688128, NULL, 'fieldJbegdq', '回车日期', 13, 1, 1608439433763061760, 0, NULL, '', NULL, 0, 1, 6, 255, 0, 50, NULL, '3,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785178628096, 1532176550125969408, NULL, 'fieldPavmpj', '事故说明', 2, 1, 1608439761891852288, 0, NULL, '', 800, 0, 1, 8, 255, 0, 100, NULL, '5,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785182822400, 1531922301278347264, NULL, 'fieldTwjaes', '送修日期', 4, 1, 1608439767700963328, 0, NULL, '', NULL, 0, 0, 5, 255, 0, 50, NULL, '3,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785191211008, 1531220097043050496, NULL, 'fieldOjkodf', '油品单价（元/L）', 6, 1, 1608439746549088256, 0, NULL, '', NULL, 0, 1, 6, 255, 0, 50, 2, '3,1', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785199599616, 1532173424190939136, 1, 'fieldOjkodf', '保费', 6, 1, 1608439768413995008, 0, NULL, '', NULL, 0, 0, 7, 171, 0, 50, 2, '4,1', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785203793920, 1531187321963065344, NULL, 'fieldPfmdbh', '回车里程', 5, 1, 1608439433763061760, 0, NULL, '', NULL, 0, 1, 7, 255, 0, 50, 4, '3,1', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785212182528, 1532171631297945600, 1, 'fieldYlzjop', '不合格记录', 2, 1, 1608439765649948672, 0, NULL, '', 800, 0, 0, 6, 171, 0, 50, NULL, '3,2', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785220571136, 1532176550176301056, NULL, 'fieldRxrbad', '处理情况', 2, 1, 1608439761891852288, 0, NULL, '', 800, 0, 1, 9, 255, 0, 100, NULL, '6,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785228959744, 1531192798281248768, NULL, 'fieldWbogqz', '保险费', 6, 1, 1608439764727201792, 0, NULL, '', NULL, 0, 0, 6, 255, 0, 50, 2, '3,1', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785228959745, 1531220097093382144, NULL, 'fieldPfmdbh', '加油量（升）', 5, 1, 1608439746549088256, 0, NULL, '', NULL, 0, 0, 7, 255, 0, 50, 4, '4,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785233154048, 1531922301328678912, NULL, 'fieldDomlvu', '经手人', 10, 1, 1608439767700963328, 0, NULL, '', NULL, 0, 1, 6, 255, 0, 50, NULL, '3,1', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785233154049, 1532173424245465088, NULL, 'fieldPfmdbh', '投保金额', 6, 1, 1608439768413995008, 0, NULL, '', NULL, 0, 1, 8, 255, 0, 50, 2, '5,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785241542656, 1531187608413057024, NULL, 'fieldPavmpj', '行驶里程', 64, 1, 1608439433763061760, 0, NULL, '', NULL, 0, 0, 8, 242, 0, 100, 2, '4,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785249931264, 1532171631348277248, NULL, 'fieldOjkodf', '年检费用', 6, 1, 1608439765649948672, 0, NULL, '', NULL, 0, 0, 7, 255, 0, 50, 2, '4,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785262514176, 1532176550226632704, NULL, 'fieldYyoira', '处理结果', 2, 1, 1608439761891852288, 0, NULL, '', 800, 0, 1, 10, 255, 0, 100, NULL, '7,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785270902784, 1531922301425147904, NULL, 'fieldRxrbad', '送修原因', 2, 1, 1608439767700963328, 0, NULL, '', 800, 0, 0, 7, 255, 0, 100, NULL, '4,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785270902785, 1531192798327386112, NULL, 'fieldRxehqc', '加油费', 6, 1, 1608439764727201792, 0, NULL, '', NULL, 0, 0, 7, 255, 0, 50, 2, '4,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785270902786, 1532173424295796736, NULL, 'fieldWkshct', '到期日期', 4, 1, 1608439768413995008, 0, NULL, '', NULL, 0, 0, 9, 255, 0, 50, NULL, '5,1', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785275097088, 1532245858373935104, NULL, 'fieldRxehqc', '当前余额', 5, 1, 1608439761141071872, 0, NULL, '', NULL, 0, 0, 5, 255, 0, 100, 4, '3,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785275097089, 1531220265255612416, NULL, 'fieldYlzjop', '加油金额', 64, 1, 1608439746549088256, 0, NULL, '', NULL, 0, 0, 8, 242, 0, 50, 2, '4,1', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785279291392, 1531185233027706880, NULL, 'dataId', '数据ID', 1, 0, 1608439433763061760, 0, NULL, NULL, NULL, 0, 0, 9, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785296068608, 1532176550276964352, NULL, 'fieldEilney', '定损人', 10, 1, 1608439761891852288, 0, NULL, '', NULL, 0, 0, 11, 255, 0, 50, NULL, '8,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785308651520, 1531922301471285248, NULL, 'fieldWkshct', '预计取车日期', 4, 1, 1608439767700963328, 0, NULL, '', NULL, 0, 0, 8, 255, 0, 50, NULL, '5,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785308651521, 1531192798373523456, NULL, 'fieldRqwfhn', '其他费用', 6, 1, 1608439764727201792, 0, NULL, '', NULL, 0, 0, 8, 255, 0, 50, 2, '4,1', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785312845824, 1532173424346128384, NULL, 'fieldPavmpj', '经手人', 10, 1, 1608439768413995008, 0, NULL, '', NULL, 0, 1, 10, 255, 0, 100, NULL, '6,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785312845825, 1531217143745523712, NULL, 'fieldFowfyg', '备注', 2, 1, 1608439761141071872, 0, NULL, '', 800, 0, 0, 6, 255, 0, 100, NULL, '4,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785317040128, 1531185233027706881, NULL, 'createUserName', '创建人', 10, 0, 1608439433763061760, 0, NULL, NULL, NULL, 0, 0, 10, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785329623040, 1532171631398608896, NULL, 'fieldDomlvu', '车管所', 1, 1, 1608439765649948672, 0, NULL, '', NULL, 0, 0, 8, 255, 0, 50, NULL, '4,1', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785333817344, 1534102235916599296, NULL, 'fieldPzuvea', '油卡', 52, 1, 1608439746549088256, 0, NULL, '', NULL, 0, 0, 9, 251, 0, 50, NULL, '5,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785338011648, 1532176550327296000, NULL, 'fieldHsnfao', '修理地点', 1, 1, 1608439761891852288, 0, NULL, '', NULL, 0, 0, 12, 255, 0, 50, NULL, '8,1', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785346400256, 1531221060046860288, NULL, 'dataId', '数据ID', 1, 0, 1608439767700963328, 0, NULL, NULL, NULL, 0, 0, 9, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785350594560, 1532173424400654336, NULL, 'fieldRxrbad', '备注', 2, 1, 1608439768413995008, 0, NULL, '', 800, 0, 0, 11, 255, 0, 100, NULL, '7,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785354788864, 1531193456040390656, NULL, 'dataId', '数据ID', 1, 0, 1608439761141071872, 0, NULL, NULL, NULL, 0, 0, 7, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785363177472, 1531185233027706882, NULL, 'createTime', '创建时间', 13, 0, 1608439433763061760, 0, NULL, NULL, NULL, 0, 0, 11, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785375760384, 1532176550377627648, NULL, 'fieldOjkodf', '保险赔偿金额', 6, 1, 1608439761891852288, 0, NULL, '', NULL, 0, 0, 13, 255, 0, 100, 2, '9,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785375760385, 1534102235975319552, NULL, 'fieldRmsiyy', '油卡余额', 5, 1, 1608439746549088256, 0, NULL, '', NULL, 0, 0, 10, 255, 0, 50, 4, '5,1', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785379954688, 1531193080780206080, NULL, 'fieldZoywvv', '费用合计', 64, 1, 1608439764727201792, 0, NULL, '', NULL, 0, 0, 9, 242, 0, 100, 2, '5,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785388343296, 1531221060046860289, NULL, 'createUserName', '创建人', 10, 0, 1608439767700963328, 0, NULL, NULL, NULL, 0, 0, 10, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785388343297, 1532171956620746752, NULL, 'dataId', '数据ID', 1, 0, 1608439768413995008, 0, NULL, NULL, NULL, 0, 0, 12, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785388343298, 1532171631448940544, NULL, 'fieldPavmpj', '经手人', 10, 1, 1608439765649948672, 0, NULL, '', NULL, 0, 0, 9, 255, 0, 50, NULL, '5,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785400926208, 1531185233027706883, NULL, 'updateTime', '更新时间', 13, 0, 1608439433763061760, 0, NULL, NULL, NULL, 0, 0, 12, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785413509120, 1532176550427959296, NULL, 'fieldPzuvea', '照片', 8, 1, 1608439761891852288, 0, NULL, '', NULL, 0, 0, 14, 255, 0, 100, NULL, '10,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785417703424, 1531220442947301376, NULL, 'fieldDomlvu', '经手人', 10, 1, 1608439746549088256, 0, NULL, '', NULL, 0, 0, 11, 255, 0, 100, NULL, '6,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785421897728, 1531193080872480768, NULL, 'fieldYlzjop', '附件', 8, 1, 1608439764727201792, 0, NULL, '', NULL, 0, 0, 10, 255, 0, 100, NULL, '6,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785426092032, 1531193456040390657, NULL, 'createUserName', '创建人', 10, 0, 1608439761141071872, 0, NULL, NULL, NULL, 0, 0, 8, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785426092033, 1532171956620746753, NULL, 'createUserName', '创建人', 10, 0, 1608439768413995008, 0, NULL, NULL, NULL, 0, 0, 13, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785426092034, 1532171631503466496, NULL, 'fieldWkshct', '到期日期', 4, 1, 1608439765649948672, 0, NULL, '', NULL, 0, 1, 10, 255, 0, 50, NULL, '5,1', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785426092035, 1531221060046860290, NULL, 'createTime', '创建时间', 13, 0, 1608439767700963328, 0, NULL, NULL, NULL, 0, 0, 11, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785438674944, 1531185233027706884, NULL, 'ownerUserName', '负责人', 10, 0, 1608439433763061760, 0, NULL, NULL, NULL, 0, 0, 13, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785451257856, 1532176550482485248, NULL, 'fieldCefggt', '备注', 2, 1, 1608439761891852288, 0, NULL, '', 800, 0, 0, 15, 255, 0, 100, NULL, '11,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785468035072, 1532171956620746754, NULL, 'createTime', '创建时间', 13, 0, 1608439768413995008, 0, NULL, NULL, NULL, 0, 0, 14, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785468035073, 1531191383995494400, NULL, 'dataId', '数据ID', 1, 0, 1608439764727201792, 0, NULL, NULL, NULL, 0, 0, 11, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785468035074, 1532171631553798144, NULL, 'fieldKpcmww', '下次年检日期', 4, 1, 1608439765649948672, 0, NULL, '', NULL, 0, 1, 11, 255, 0, 100, NULL, '6,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785472229376, 1531193456040390658, NULL, 'createTime', '创建时间', 13, 0, 1608439761141071872, 0, NULL, NULL, NULL, 0, 0, 9, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785472229377, 1531220442993438720, NULL, 'fieldPavmpj', '备注', 2, 1, 1608439746549088256, 0, NULL, '', 800, 0, 0, 12, 255, 0, 100, NULL, '7,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785476423680, 1531221060046860291, NULL, 'updateTime', '更新时间', 13, 0, 1608439767700963328, 0, NULL, NULL, NULL, 0, 0, 12, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785476423681, 1531185233027706885, NULL, 'teamMember', '团队成员', 1, 0, 1608439433763061760, 0, NULL, NULL, NULL, 0, 0, 14, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785489006592, 1532173846016286720, NULL, 'dataId', '数据ID', 1, 0, 1608439761891852288, 0, NULL, NULL, NULL, 0, 0, 16, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785505783808, 1532171956620746755, NULL, 'updateTime', '更新时间', 13, 0, 1608439768413995008, 0, NULL, NULL, NULL, 0, 0, 15, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785514172416, 1532171631604129792, NULL, 'fieldRxrbad', '备注', 2, 1, 1608439765649948672, 0, NULL, '', 800, 0, 0, 12, 255, 0, 100, NULL, '7,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785514172417, 1531193456040390659, NULL, 'updateTime', '更新时间', 13, 0, 1608439761141071872, 0, NULL, NULL, NULL, 0, 0, 10, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785514172418, 1531220443035381760, NULL, 'fieldRxrbad', '图片', 8, 1, 1608439746549088256, 0, NULL, '', NULL, 0, 0, 13, 255, 0, 100, NULL, '8,0', NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785518366720, 1531185233027706886, NULL, 'currentFlowId', '当前节点', 1, 0, 1608439433763061760, 0, NULL, NULL, NULL, 0, 0, 15, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785526755328, 1531191383995494401, NULL, 'createUserName', '创建人', 10, 0, 1608439764727201792, 0, NULL, NULL, NULL, 0, 0, 12, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785530949632, 1531221060046860292, NULL, 'ownerUserName', '负责人', 10, 0, 1608439767700963328, 0, NULL, NULL, NULL, 0, 0, 13, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785535143936, 1532173846016286721, NULL, 'createUserName', '创建人', 10, 0, 1608439761891852288, 0, NULL, NULL, NULL, 0, 0, 17, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785543532544, 1532171956620746756, NULL, 'ownerUserName', '负责人', 10, 0, 1608439768413995008, 0, NULL, NULL, NULL, 0, 0, 16, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785551921152, 1532169825641684992, NULL, 'dataId', '数据ID', 1, 0, 1608439765649948672, 0, NULL, NULL, NULL, 0, 0, 13, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785551921153, 1531193456040390660, NULL, 'ownerUserName', '负责人', 10, 0, 1608439761141071872, 0, NULL, NULL, NULL, 0, 0, 11, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785556115456, 1531185233027706887, NULL, 'flowType', '节点类型', 1, 0, 1608439433763061760, 0, NULL, NULL, NULL, 0, 0, 16, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785564504064, 1531191383995494402, NULL, 'createTime', '创建时间', 13, 0, 1608439764727201792, 0, NULL, NULL, NULL, 0, 0, 13, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785568698368, 1531217779065139200, NULL, 'dataId', '数据ID', 1, 0, 1608439746549088256, 0, NULL, NULL, NULL, 0, 0, 14, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785568698369, 1531221060046860293, NULL, 'teamMember', '团队成员', 1, 0, 1608439767700963328, 0, NULL, NULL, NULL, 0, 0, 14, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785577086976, 1532173846016286722, NULL, 'createTime', '创建时间', 13, 0, 1608439761891852288, 0, NULL, NULL, NULL, 0, 0, 18, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785585475584, 1532171956620746757, NULL, 'teamMember', '团队成员', 1, 0, 1608439768413995008, 0, NULL, NULL, NULL, 0, 0, 17, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785593864192, 1532169825641684993, NULL, 'createUserName', '创建人', 10, 0, 1608439765649948672, 0, NULL, NULL, NULL, 0, 0, 14, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785593864193, 1531185233027706888, NULL, 'flowStatus', '节点状态', 1, 0, 1608439433763061760, 0, NULL, NULL, NULL, 0, 0, 17, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785598058496, 1531193456040390661, NULL, 'teamMember', '团队成员', 1, 0, 1608439761141071872, 0, NULL, NULL, NULL, 0, 0, 12, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785606447104, 1531221060046860294, NULL, 'currentFlowId', '当前节点', 1, 0, 1608439767700963328, 0, NULL, NULL, NULL, 0, 0, 15, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785610641408, 1531217779065139201, NULL, 'createUserName', '创建人', 10, 0, 1608439746549088256, 0, NULL, NULL, NULL, 0, 0, 15, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785614835712, 1532173846020481024, NULL, 'updateTime', '更新时间', 13, 0, 1608439761891852288, 0, NULL, NULL, NULL, 0, 0, 19, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785614835713, 1531191383995494403, NULL, 'updateTime', '更新时间', 13, 0, 1608439764727201792, 0, NULL, NULL, NULL, 0, 0, 14, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785623224320, 1532171956620746758, NULL, 'currentFlowId', '当前节点', 1, 0, 1608439768413995008, 0, NULL, NULL, NULL, 0, 0, 18, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785627418624, 1532169825641684994, NULL, 'createTime', '创建时间', 13, 0, 1608439765649948672, 0, NULL, NULL, NULL, 0, 0, 15, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785635807232, 1531185233027706889, NULL, 'categoryId', '分类ID', 1, 0, 1608439433763061760, 0, NULL, NULL, NULL, 0, 0, 18, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785640001536, 1531193456040390662, NULL, 'currentFlowId', '当前节点', 1, 0, 1608439761141071872, 0, NULL, NULL, NULL, 0, 0, 13, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785648390144, 1531221060046860295, NULL, 'flowType', '节点类型', 1, 0, 1608439767700963328, 0, NULL, NULL, NULL, 0, 0, 16, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785656778752, 1532173846020481025, NULL, 'ownerUserName', '负责人', 10, 0, 1608439761891852288, 0, NULL, NULL, NULL, 0, 0, 20, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785660973056, 1532171956620746759, NULL, 'flowType', '节点类型', 1, 0, 1608439768413995008, 0, NULL, NULL, NULL, 0, 0, 19, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785669361664, 1532169825641684995, NULL, 'updateTime', '更新时间', 13, 0, 1608439765649948672, 0, NULL, NULL, NULL, 0, 0, 16, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785673555968, 1531185233027706890, NULL, 'stageId', '阶段ID', 1, 0, 1608439433763061760, 0, NULL, NULL, NULL, 0, 0, 19, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785673555969, 1531217779065139202, NULL, 'createTime', '创建时间', 13, 0, 1608439746549088256, 0, NULL, NULL, NULL, 0, 0, 16, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785677750272, 1531193456040390663, NULL, 'flowType', '节点类型', 1, 0, 1608439761141071872, 0, NULL, NULL, NULL, 0, 0, 14, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785686138880, 1531221060046860296, NULL, 'flowStatus', '节点状态', 1, 0, 1608439767700963328, 0, NULL, NULL, NULL, 0, 0, 17, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785690333184, 1532173846020481026, NULL, 'teamMember', '团队成员', 1, 0, 1608439761891852288, 0, NULL, NULL, NULL, 0, 0, 21, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785694527488, 1531191383995494404, NULL, 'ownerUserName', '负责人', 10, 0, 1608439764727201792, 0, NULL, NULL, NULL, 0, 0, 15, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785698721792, 1532171956620746760, NULL, 'flowStatus', '节点状态', 1, 0, 1608439768413995008, 0, NULL, NULL, NULL, 0, 0, 20, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785715499008, 1531185233027706891, NULL, 'stageName', '阶段名称', 1, 0, 1608439433763061760, 0, NULL, NULL, NULL, 0, 0, 20, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785715499009, 1532169825641684996, NULL, 'ownerUserName', '负责人', 10, 0, 1608439765649948672, 0, NULL, NULL, NULL, 0, 0, 17, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785715499010, 1531217779065139203, NULL, 'updateTime', '更新时间', 13, 0, 1608439746549088256, 0, NULL, NULL, NULL, 0, 0, 17, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785715499011, 1531193456040390664, NULL, 'flowStatus', '节点状态', 1, 0, 1608439761141071872, 0, NULL, NULL, NULL, 0, 0, 15, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785723887616, 1531221060046860297, NULL, 'categoryId', '分类ID', 1, 0, 1608439767700963328, 0, NULL, NULL, NULL, 0, 0, 18, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785736470528, 1532173846020481027, NULL, 'currentFlowId', '当前节点', 1, 0, 1608439761891852288, 0, NULL, NULL, NULL, 0, 0, 22, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785736470529, 1532171956620746761, NULL, 'categoryId', '分类ID', 1, 0, 1608439768413995008, 0, NULL, NULL, NULL, 0, 0, 21, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785740664832, 1531191383995494405, NULL, 'teamMember', '团队成员', 1, 0, 1608439764727201792, 0, NULL, NULL, NULL, 0, 0, 16, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785749053440, 1531185233027706892, NULL, 'stageStatus', '阶段状态', 1, 0, 1608439433763061760, 0, NULL, NULL, NULL, 0, 0, 21, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785753247744, 1532169825641684997, NULL, 'teamMember', '团队成员', 1, 0, 1608439765649948672, 0, NULL, NULL, NULL, 0, 0, 18, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785757442048, 1531193456040390665, NULL, 'categoryId', '分类ID', 1, 0, 1608439761141071872, 0, NULL, NULL, NULL, 0, 0, 16, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785757442049, 1531217779065139204, NULL, 'ownerUserName', '负责人', 10, 0, 1608439746549088256, 0, NULL, NULL, NULL, 0, 0, 18, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785761636352, 1531221060046860298, NULL, 'stageId', '阶段ID', 1, 0, 1608439767700963328, 0, NULL, NULL, NULL, 0, 0, 19, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785774219264, 1532173846020481028, NULL, 'flowType', '节点类型', 1, 0, 1608439761891852288, 0, NULL, NULL, NULL, 0, 0, 23, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785774219265, 1532171956620746762, NULL, 'stageId', '阶段ID', 1, 0, 1608439768413995008, 0, NULL, NULL, NULL, 0, 0, 22, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785782607872, 1531191383995494406, NULL, 'currentFlowId', '当前节点', 1, 0, 1608439764727201792, 0, NULL, NULL, NULL, 0, 0, 17, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785790996480, 1531185233027706893, NULL, 'moduleId', '模块ID', 1, 0, 1608439433763061760, 0, NULL, NULL, NULL, 0, 0, 22, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785790996481, 1532169825641684998, NULL, 'currentFlowId', '当前节点', 1, 0, 1608439765649948672, 0, NULL, NULL, NULL, 0, 0, 19, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785799385088, 1531217779065139205, NULL, 'teamMember', '团队成员', 1, 0, 1608439746549088256, 0, NULL, NULL, NULL, 0, 0, 19, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785799385089, 1531221060046860299, NULL, 'stageName', '阶段名称', 1, 0, 1608439767700963328, 0, NULL, NULL, NULL, 0, 0, 20, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785799385090, 1531193456040390666, NULL, 'stageId', '阶段ID', 1, 0, 1608439761141071872, 0, NULL, NULL, NULL, 0, 0, 17, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785811968000, 1532173846020481029, NULL, 'flowStatus', '节点状态', 1, 0, 1608439761891852288, 0, NULL, NULL, NULL, 0, 0, 24, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785816162304, 1532171956620746763, NULL, 'stageName', '阶段名称', 1, 0, 1608439768413995008, 0, NULL, NULL, NULL, 0, 0, 23, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785820356608, 1531191383995494407, NULL, 'flowType', '节点类型', 1, 0, 1608439764727201792, 0, NULL, NULL, NULL, 0, 0, 18, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785828745216, 1532169825641684999, NULL, 'flowType', '节点类型', 1, 0, 1608439765649948672, 0, NULL, NULL, NULL, 0, 0, 20, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785837133824, 1531221060046860300, NULL, 'stageStatus', '阶段状态', 1, 0, 1608439767700963328, 0, NULL, NULL, NULL, 0, 0, 21, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785845522432, 1531193456040390667, NULL, 'stageName', '阶段名称', 1, 0, 1608439761141071872, 0, NULL, NULL, NULL, 0, 0, 18, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785845522433, 1531217779065139206, NULL, 'currentFlowId', '当前节点', 1, 0, 1608439746549088256, 0, NULL, NULL, NULL, 0, 0, 20, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785853911040, 1532171956620746764, NULL, 'stageStatus', '阶段状态', 1, 0, 1608439768413995008, 0, NULL, NULL, NULL, 0, 0, 24, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785858105344, 1532173846020481030, NULL, 'categoryId', '分类ID', 1, 0, 1608439761891852288, 0, NULL, NULL, NULL, 0, 0, 25, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785874882560, 1531221060046860301, NULL, 'moduleId', '模块ID', 1, 0, 1608439767700963328, 0, NULL, NULL, NULL, 0, 0, 22, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785874882561, 1532169825641685000, NULL, 'flowStatus', '节点状态', 1, 0, 1608439765649948672, 0, NULL, NULL, NULL, 0, 0, 21, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785879076864, 1531193456040390668, NULL, 'stageStatus', '阶段状态', 1, 0, 1608439761141071872, 0, NULL, NULL, NULL, 0, 0, 19, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785883271168, 1531191383995494408, NULL, 'flowStatus', '节点状态', 1, 0, 1608439764727201792, 0, NULL, NULL, NULL, 0, 0, 19, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785891659776, 1531217779065139207, NULL, 'flowType', '节点类型', 1, 0, 1608439746549088256, 0, NULL, NULL, NULL, 0, 0, 21, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785895854080, 1532171956620746765, NULL, 'moduleId', '模块ID', 1, 0, 1608439768413995008, 0, NULL, NULL, NULL, 0, 0, 25, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785912631296, 1532173846020481031, NULL, 'stageId', '阶段ID', 1, 0, 1608439761891852288, 0, NULL, NULL, NULL, 0, 0, 26, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785925214208, 1531191383995494409, NULL, 'categoryId', '分类ID', 1, 0, 1608439764727201792, 0, NULL, NULL, NULL, 0, 0, 20, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785958768640, 1532169825641685001, NULL, 'categoryId', '分类ID', 1, 0, 1608439765649948672, 0, NULL, NULL, NULL, 0, 0, 22, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785958768641, 1531193456040390669, NULL, 'moduleId', '模块ID', 1, 0, 1608439761141071872, 0, NULL, NULL, NULL, 0, 0, 20, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439785975545856, 1531191383995494410, NULL, 'stageId', '阶段ID', 1, 0, 1608439764727201792, 0, NULL, NULL, NULL, 0, 0, 21, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439786109763584, 1531217779065139208, NULL, 'flowStatus', '节点状态', 1, 0, 1608439746549088256, 0, NULL, NULL, NULL, 0, 0, 22, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439786109763585, 1531191383995494411, NULL, 'stageName', '阶段名称', 1, 0, 1608439764727201792, 0, NULL, NULL, NULL, 0, 0, 22, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439786176872448, 1532173846020481032, NULL, 'stageName', '阶段名称', 1, 0, 1608439761891852288, 0, NULL, NULL, NULL, 0, 0, 27, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439786202038272, 1532169825641685002, NULL, 'stageId', '阶段ID', 1, 0, 1608439765649948672, 0, NULL, NULL, NULL, 0, 0, 23, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439786411753472, 1532173846020481033, NULL, 'stageStatus', '阶段状态', 1, 0, 1608439761891852288, 0, NULL, NULL, NULL, 0, 0, 28, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439786449502208, 1531217779065139209, NULL, 'categoryId', '分类ID', 1, 0, 1608439746549088256, 0, NULL, NULL, NULL, 0, 0, 23, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439786462085120, 1532169825641685003, NULL, 'stageName', '阶段名称', 1, 0, 1608439765649948672, 0, NULL, NULL, NULL, 0, 0, 24, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439786524999680, 1532173846020481034, NULL, 'moduleId', '模块ID', 1, 0, 1608439761891852288, 0, NULL, NULL, NULL, 0, 0, 29, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439786533388288, 1531191383995494412, NULL, 'stageStatus', '阶段状态', 1, 0, 1608439764727201792, 0, NULL, NULL, NULL, 0, 0, 23, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439786571137024, 1531191383995494413, NULL, 'moduleId', '模块ID', 1, 0, 1608439764727201792, 0, NULL, NULL, NULL, 0, 0, 24, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439786579525632, 1532169825641685004, NULL, 'stageStatus', '阶段状态', 1, 0, 1608439765649948672, 0, NULL, NULL, NULL, 0, 0, 25, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439786592108544, 1531217779065139210, NULL, 'stageId', '阶段ID', 1, 0, 1608439746549088256, 0, NULL, NULL, NULL, 0, 0, 24, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439786625662976, 1532169825641685005, NULL, 'moduleId', '模块ID', 1, 0, 1608439765649948672, 0, NULL, NULL, NULL, 0, 0, 26, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439786638245888, 1531217779065139211, NULL, 'stageName', '阶段名称', 1, 0, 1608439746549088256, 0, NULL, NULL, NULL, 0, 0, 25, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439786671800320, 1531217779065139212, NULL, 'stageStatus', '阶段状态', 1, 0, 1608439746549088256, 0, NULL, NULL, NULL, 0, 0, 26, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439786722131968, 1531217779065139213, NULL, 'moduleId', '模块ID', 1, 0, 1608439746549088256, 0, NULL, NULL, NULL, 0, 0, 27, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:23');
INSERT INTO `wk_module_field` VALUES (1608439790400536576, 1532189498936467456, NULL, 'fieldYlzjop', '油卡号码', 1, 1, 1608439763607322624, 0, NULL, '', NULL, 1, 1, 0, 255, 0, 50, NULL, '0,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439790438285312, 1529021021966905344, NULL, 'fieldTwjaes', '发卡日期', 4, 1, 1608439763607322624, 0, NULL, '', NULL, 0, 0, 1, 255, 0, 50, NULL, '0,1', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439790476034048, 1532246067208331264, NULL, 'fieldWbogqz', '余额', 5, 1, 1608439763607322624, 0, NULL, '', NULL, 0, 1, 2, 255, 0, 50, 4, '1,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439790509588480, 1529021022021431296, NULL, 'fieldMhlqon', '经手人', 10, 1, 1608439763607322624, 0, NULL, '', NULL, 0, 0, 3, 255, 0, 50, NULL, '1,1', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439790551531520, 1529022535615713280, NULL, 'fieldWisysx', '加油站', 3, 1, 1608439763607322624, 0, NULL, '', NULL, 0, 0, 4, 255, 0, 50, 2, '2,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439790589280256, 1529022535762513920, NULL, 'fieldZoywvv', '备注', 2, 1, 1608439763607322624, 0, NULL, '', 800, 0, 0, 5, 255, 0, 100, NULL, '3,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439790622834688, 1529019174740897792, NULL, 'dataId', '数据ID', 1, 0, 1608439763607322624, 0, NULL, NULL, NULL, 0, 0, 6, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439790639611904, 1534014966169935872, NULL, 'fieldMygrvj', '驾驶员', 1, 1, 1608439771190624256, 0, NULL, '', NULL, 0, 1, 0, 255, 0, 50, NULL, '0,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439790660583424, 1529019174740897793, NULL, 'createUserName', '创建人', 10, 0, 1608439763607322624, 0, NULL, NULL, NULL, 0, 0, 7, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439790668972032, 1531113641056784384, NULL, 'fieldMhlqon', '申请单号', 63, 1, 1608439774743199744, 0, NULL, '', NULL, 1, 0, 0, 245, 0, 50, NULL, '0,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439790677360640, 1531111169563152384, NULL, 'fieldZoywvv', '所属车队', 52, 1, 1608439771190624256, 0, NULL, '', NULL, 0, 1, 1, 251, 0, 50, NULL, '0,1', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439790694137856, 1529019174740897794, NULL, 'createTime', '创建时间', 13, 0, 1608439763607322624, 0, NULL, NULL, NULL, 0, 0, 8, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439790706720768, 1531113641098727424, NULL, 'fieldTwjaes', '申请日期', 4, 1, 1608439774743199744, 0, NULL, '', NULL, 0, 1, 1, 255, 0, 50, NULL, '0,1', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439790715109376, 1531111169617678336, NULL, 'fieldWisysx', '性别', 3, 1, 1608439771190624256, 0, NULL, '', NULL, 0, 1, 2, 255, 0, 50, 2, '1,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439790731886592, 1529019174740897795, NULL, 'updateTime', '更新时间', 13, 0, 1608439763607322624, 0, NULL, NULL, NULL, 0, 0, 9, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439790744469504, 1531113641140670464, NULL, 'fieldFowfyg', '用车人', 10, 1, 1608439774743199744, 0, NULL, '', NULL, 0, 1, 2, 255, 0, 50, NULL, '1,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439790752858112, 1531111169730924544, NULL, 'fieldCvwjri', '文化程度', 3, 1, 1608439771190624256, 0, NULL, '', NULL, 0, 0, 3, 255, 0, 50, 2, '1,1', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439790769635328, 1529019174740897796, NULL, 'ownerUserName', '负责人', 10, 0, 1608439763607322624, 0, NULL, NULL, NULL, 0, 0, 10, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439790782218240, 1531113641182613504, NULL, 'fieldZoywvv', '用车部门', 12, 1, 1608439774743199744, 0, NULL, '', NULL, 0, 1, 3, 255, 0, 50, NULL, '1,1', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439790790606848, 1534014966471925760, NULL, 'fieldUedvna', '身份证号', 1, 1, 1608439771190624256, 0, NULL, '', NULL, 0, 1, 4, 255, 0, 50, NULL, '2,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439790807384064, 1529019174740897797, NULL, 'teamMember', '团队成员', 1, 0, 1608439763607322624, 0, NULL, NULL, NULL, 0, 0, 11, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439790819966976, 1532228091981668352, NULL, 'fieldMygrvj', '驾驶员', 52, 1, 1608439774743199744, 0, NULL, '', NULL, 0, 0, 4, 251, 0, 50, NULL, '2,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439790828355584, 1531111169928056832, NULL, 'fieldYlzjop', '手机号码', 7, 1, 1608439771190624256, 0, NULL, '', NULL, 0, 1, 5, 255, 0, 50, NULL, '2,1', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439790853521408, 1529019174740897798, NULL, 'currentFlowId', '当前节点', 1, 0, 1608439763607322624, 0, NULL, NULL, NULL, 0, 0, 12, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439790853521409, 1531113641224556544, NULL, 'fieldYlzjop', '随行人员', 10, 1, 1608439774743199744, 0, NULL, '', NULL, 0, 0, 5, 255, 0, 50, NULL, '2,1', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439790870298624, 1531111169982582784, NULL, 'fieldOfgvdu', '住址', 43, 1, 1608439771190624256, 0, NULL, '', NULL, 0, 0, 6, 255, 0, 100, 1, '3,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439790895464449, 1531184225996288000, NULL, 'fieldWisysx', '申请车型', 3, 1, 1608439774743199744, 0, NULL, '', NULL, 0, 1, 6, 255, 0, 50, 2, '3,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439790908047360, 1529019174740897799, NULL, 'flowType', '节点类型', 1, 0, 1608439763607322624, 0, NULL, NULL, NULL, 0, 0, 13, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439790991933440, 1531111170037108736, NULL, 'fieldXgxxqa', '驾驶员状态', 3, 1, 1608439771190624256, 0, NULL, '', NULL, 0, 1, 7, 255, 0, 100, 2, '4,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439790996127744, 1532199205797437440, NULL, 'fieldPzuvea', '申请车辆', 52, 1, 1608439774743199744, 0, NULL, '', NULL, 0, 0, 7, 251, 0, 50, NULL, '3,1', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791021293568, 1529019174740897800, NULL, 'flowStatus', '节点状态', 1, 0, 1608439763607322624, 0, NULL, NULL, NULL, 0, 0, 14, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791029682176, 1531111170171326464, NULL, 'fieldDomlvu', '驾驶证信息', 60, 1, 1608439771190624256, 0, NULL, '', NULL, 0, 0, 8, 224, 0, 100, NULL, '5,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791038070784, 1534082233297608704, NULL, 'fieldDqxojy', '计划用车时间', 13, 1, 1608439774743199744, 0, NULL, '', NULL, 0, 1, 8, 255, 0, 50, NULL, '4,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791054848000, 1529019174740897801, NULL, 'categoryId', '分类ID', 1, 0, 1608439763607322624, 0, NULL, NULL, NULL, 0, 0, 15, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791067430912, 1534014966723584000, NULL, 'fieldAzqvyl', '驾驶证号码', 1, 1, 1608439771190624256, 0, NULL, '', NULL, 0, 0, 9, 255, 0, 50, NULL, '6,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791071625216, 1534082233356328960, NULL, 'fieldJbegdq', '预计返回时间', 13, 1, 1608439774743199744, 0, NULL, '', NULL, 0, 1, 9, 255, 0, 50, NULL, '4,1', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791100985344, 1529019174740897802, NULL, 'stageId', '阶段ID', 1, 0, 1608439763607322624, 0, NULL, NULL, NULL, 0, 0, 16, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791105179648, 1531111170276184064, NULL, 'fieldSexbax', '准驾车型', 3, 1, 1608439771190624256, 0, NULL, '', NULL, 0, 0, 10, 255, 0, 50, 2, '6,1', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791109373952, 1531184226164060160, NULL, 'fieldDomlvu', '用车事由', 2, 1, 1608439774743199744, 0, NULL, '', 800, 0, 1, 10, 255, 0, 100, NULL, '5,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791134539776, 1529019174740897803, NULL, 'stageName', '阶段名称', 1, 0, 1608439763607322624, 0, NULL, NULL, NULL, 0, 0, 17, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791134539777, 1531184226210197504, NULL, 'fieldCvwjri', '目的地', 43, 1, 1608439774743199744, 0, NULL, '', NULL, 0, 1, 11, 255, 0, 100, 1, '6,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791138734080, 1531111170443956224, NULL, 'fieldTwjaes', '初次领驾驶证日期', 4, 1, 1608439771190624256, 0, NULL, '', NULL, 0, 0, 11, 255, 0, 50, NULL, '7,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791201648640, 1529019174740897804, NULL, 'stageStatus', '阶段状态', 1, 0, 1608439763607322624, 0, NULL, NULL, NULL, 0, 0, 18, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791201648641, 1531184226252140544, NULL, 'fieldPavmpj', '备注', 2, 1, 1608439774743199744, 0, NULL, '', 800, 0, 0, 12, 255, 0, 100, NULL, '7,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791210037251, 1531111170494287872, NULL, 'fieldWkshct', '驾驶证到期日期', 4, 1, 1608439771190624256, 0, NULL, '', NULL, 0, 0, 12, 255, 0, 50, NULL, '7,1', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791239397376, 1529019174740897805, NULL, 'moduleId', '模块ID', 1, 0, 1608439763607322624, 0, NULL, NULL, NULL, 0, 0, 19, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791243591680, 1531111804064878592, NULL, 'dataId', '数据ID', 1, 0, 1608439774743199744, 0, NULL, NULL, NULL, 0, 0, 13, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791260368896, 1531111170548813824, NULL, 'fieldRmsiyy', '驾龄', 5, 1, 1608439771190624256, 0, NULL, '', NULL, 0, 0, 13, 255, 0, 50, 4, '8,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791293923328, 1531111804064878593, NULL, 'createUserName', '创建人', 10, 0, 1608439774743199744, 0, NULL, NULL, NULL, 0, 0, 14, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791331672064, 1531111804064878594, NULL, 'createTime', '创建时间', 13, 0, 1608439774743199744, 0, NULL, NULL, NULL, 0, 0, 15, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791365226496, 1531111170603339776, NULL, 'fieldPavmpj', '驾驶证图片', 8, 1, 1608439771190624256, 0, NULL, '', NULL, 0, 0, 14, 255, 0, 100, NULL, '9,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791382003712, 1531111804064878595, NULL, 'updateTime', '更新时间', 13, 0, 1608439774743199744, 0, NULL, NULL, NULL, 0, 0, 16, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791402975232, 1531111170657865728, NULL, 'fieldRxrbad', '从业资格证信息', 60, 1, 1608439771190624256, 0, NULL, '', NULL, 0, 0, 15, 224, 0, 100, NULL, '10,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791419752448, 1531111804064878596, NULL, 'ownerUserName', '负责人', 10, 0, 1608439774743199744, 0, NULL, NULL, NULL, 0, 0, 17, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791440723968, 1534014967063322624, NULL, 'fieldQyghpc', '从业资格证号码', 1, 1, 1608439771190624256, 0, NULL, '', NULL, 0, 0, 16, 255, 0, 50, NULL, '11,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791457501184, 1531111804064878597, NULL, 'teamMember', '团队成员', 1, 0, 1608439774743199744, 0, NULL, NULL, NULL, 0, 0, 18, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791478472704, 1531111170762723328, NULL, 'fieldWasmoq', '从业资格证类别', 3, 1, 1608439771190624256, 0, NULL, '', NULL, 0, 0, 17, 255, 0, 50, 2, '11,1', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791495249920, 1531111804064878598, NULL, 'currentFlowId', '当前节点', 1, 0, 1608439774743199744, 0, NULL, NULL, NULL, 0, 0, 19, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791516221440, 1531111170896941056, NULL, 'fieldKpcmww', '初次领证日期', 4, 1, 1608439771190624256, 0, NULL, '', NULL, 0, 0, 18, 255, 0, 50, NULL, '12,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791537192960, 1531111804064878599, NULL, 'flowType', '节点类型', 1, 0, 1608439774743199744, 0, NULL, NULL, NULL, 0, 0, 20, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791553970176, 1531111170951467008, NULL, 'fieldAuwwnq', '有效期限', 4, 1, 1608439771190624256, 0, NULL, '', NULL, 0, 0, 19, 255, 0, 50, NULL, '12,1', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791574941696, 1531111804064878600, NULL, 'flowStatus', '节点状态', 1, 0, 1608439774743199744, 0, NULL, NULL, NULL, 0, 0, 21, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791587524608, 1531111171005992960, NULL, 'fieldYyoira', '附件', 60, 1, 1608439771190624256, 0, NULL, '', NULL, 0, 0, 20, 224, 0, 100, NULL, '13,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791604301824, 1531111804064878601, NULL, 'categoryId', '分类ID', 1, 0, 1608439774743199744, 0, NULL, NULL, NULL, 0, 0, 22, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791625273344, 1531111171060518912, NULL, 'fieldEilney', '身份证照片', 8, 1, 1608439771190624256, 0, NULL, '', NULL, 0, 0, 21, 255, 0, 100, NULL, '14,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791642050560, 1531111804064878602, NULL, 'stageId', '阶段ID', 1, 0, 1608439774743199744, 0, NULL, NULL, NULL, 0, 0, 23, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791658827776, 1531111171110850560, NULL, 'fieldHsnfao', '驾驶证照片', 8, 1, 1608439771190624256, 0, NULL, '', NULL, 0, 0, 22, 255, 0, 100, NULL, '15,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791679799296, 1531111804064878603, NULL, 'stageName', '阶段名称', 1, 0, 1608439774743199744, 0, NULL, NULL, NULL, 0, 0, 24, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791696576512, 1531111171165376512, NULL, 'fieldPzuvea', '从业资格证照片', 8, 1, 1608439771190624256, 0, NULL, '', NULL, 0, 0, 23, 255, 0, 100, NULL, '16,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791717548032, 1531111804064878604, NULL, 'stageStatus', '阶段状态', 1, 0, 1608439774743199744, 0, NULL, NULL, NULL, 0, 0, 25, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791734325248, 1531111171219902464, NULL, 'fieldCefggt', '驾驶员聘用合同', 8, 1, 1608439771190624256, 0, NULL, '', NULL, 0, 0, 24, 255, 0, 100, NULL, '17,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791742713856, 1531101048367861760, NULL, 'fieldFowfyg', '配件名称', 1, 1, 1608439767134732288, 0, NULL, '', NULL, 0, 1, 0, 255, 0, 50, NULL, '0,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791763685376, 1531111804064878605, NULL, 'moduleId', '模块ID', 1, 0, 1608439774743199744, 0, NULL, NULL, NULL, 0, 0, 26, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791772073984, 1531101362722557952, NULL, 'dataId', '数据ID', 1, 0, 1608439771190624256, 0, NULL, NULL, NULL, 0, 0, 25, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791780462592, 1531101048313335808, NULL, 'fieldMhlqon', '配件编码', 1, 1, 1608439767134732288, 0, NULL, '', NULL, 0, 1, 1, 255, 0, 50, NULL, '0,1', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791814017024, 1531101362722557953, NULL, 'createUserName', '创建人', 10, 0, 1608439771190624256, 0, NULL, NULL, NULL, 0, 0, 26, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791826599936, 1531101048422387712, NULL, 'fieldZoywvv', '规格型号', 1, 1, 1608439767134732288, 0, NULL, '', NULL, 0, 0, 2, 255, 0, 50, NULL, '1,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791860154368, 1531101362722557954, NULL, 'createTime', '创建时间', 13, 0, 1608439771190624256, 0, NULL, NULL, NULL, 0, 0, 27, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791868542976, 1531101048472719360, NULL, 'fieldYlzjop', '单位', 1, 1, 1608439767134732288, 0, NULL, '', NULL, 0, 0, 3, 255, 0, 50, NULL, '1,1', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791897903104, 1531101362722557955, NULL, 'updateTime', '更新时间', 13, 0, 1608439771190624256, 0, NULL, NULL, NULL, 0, 0, 28, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791910486016, 1531101048527245312, NULL, 'fieldOjkodf', '单价', 6, 1, 1608439767134732288, 0, NULL, '', NULL, 0, 0, 4, 255, 0, 50, 2, '2,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791948234752, 1531101362722557956, NULL, 'ownerUserName', '负责人', 10, 0, 1608439771190624256, 0, NULL, NULL, NULL, 0, 0, 29, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439791981789184, 1531101362722557957, NULL, 'teamMember', '团队成员', 1, 0, 1608439771190624256, 0, NULL, NULL, NULL, 0, 0, 30, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792023732224, 1531101362722557958, NULL, 'currentFlowId', '当前节点', 1, 0, 1608439771190624256, 0, NULL, NULL, NULL, 0, 0, 31, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792061480960, 1531101362722557959, NULL, 'flowType', '节点类型', 1, 0, 1608439771190624256, 0, NULL, NULL, NULL, 0, 0, 32, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792099229696, 1531929289777340416, NULL, 'fieldMhlqon', '关联维保申请单', 52, 1, 1608439769529679872, 0, NULL, '', NULL, 0, 1, 0, 251, 0, 50, NULL, '0,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792099229697, 1531101362722557960, NULL, 'flowStatus', '节点状态', 1, 0, 1608439771190624256, 0, NULL, NULL, NULL, 0, 0, 33, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792136978432, 1531929289827672064, NULL, 'fieldDqxojy', '取车日期', 13, 1, 1608439769529679872, 0, NULL, '', NULL, 0, 0, 1, 255, 0, 50, NULL, '0,1', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792145367041, 1531101362722557961, NULL, 'categoryId', '分类ID', 1, 0, 1608439771190624256, 0, NULL, NULL, NULL, 0, 0, 34, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792174727168, 1531929289878003712, NULL, 'fieldFowfyg', '车辆', 52, 1, 1608439769529679872, 0, NULL, '', NULL, 0, 1, 2, 251, 0, 100, NULL, '1,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792178921472, 1531101362722557962, NULL, 'stageId', '阶段ID', 1, 0, 1608439771190624256, 0, NULL, NULL, NULL, 0, 0, 35, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792183115776, 1531094470235377664, NULL, 'fieldZoywvv', '车辆号牌', 1, 1, 1608439777943453696, 0, NULL, '', NULL, 1, 1, 0, 255, 0, 50, NULL, '0,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792216670208, 1531929289928335360, 1, 'fieldTghtjb', '配件明细', 45, 1, 1608439769529679872, 0, '添加明细表格', '', NULL, 0, 0, 3, 233, 0, 100, 1, '2,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792220864512, 1531101362722557963, NULL, 'stageName', '阶段名称', 1, 0, 1608439771190624256, 0, NULL, NULL, NULL, 0, 0, 36, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792225058816, 1531094470285709312, NULL, 'fieldYlzjop', '车辆品牌', 1, 1, 1608439777943453696, 0, NULL, '', NULL, 0, 0, 1, 255, 0, 50, NULL, '0,1', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792258613248, 1531101362722557964, NULL, 'stageStatus', '阶段状态', 1, 0, 1608439771190624256, 0, NULL, NULL, NULL, 0, 0, 37, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792267001856, 1531929289978667008, 1, 'fieldWisysx', '维修项目', 3, 1, 1608439769529679872, 0, NULL, '', NULL, 0, 1, 4, 171, 0, 50, 2, '3,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792267001857, 1531094470331846656, NULL, 'fieldDomlvu', '车辆型号', 1, 1, 1608439777943453696, 0, NULL, '', NULL, 0, 0, 2, 255, 0, 50, NULL, '1,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792275390464, 1531101048581771264, NULL, 'fieldDomlvu', '备注', 2, 1, 1608439767134732288, 0, NULL, '', 800, 0, 0, 5, 255, 0, 100, NULL, '3,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792292167680, 1531101362722557965, NULL, 'moduleId', '模块ID', 1, 0, 1608439771190624256, 0, NULL, NULL, NULL, 0, 0, 38, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792304750592, 1531094470382178304, NULL, 'fieldWisysx', '车辆类型', 3, 1, 1608439777943453696, 0, NULL, '', NULL, 0, 0, 3, 255, 0, 50, 2, '1,1', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792304750593, 1531929290104496128, 1, 'fieldZoywvv', '配件名称', 52, 1, 1608439769529679872, 0, NULL, '', NULL, 0, 1, 5, 171, 0, 50, NULL, '3,1', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792313139200, 1531098805463797760, NULL, 'dataId', '数据ID', 1, 0, 1608439767134732288, 0, NULL, NULL, NULL, 0, 0, 6, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792342499328, 1531094470499618816, NULL, 'fieldOjkodf', '购置价格', 6, 1, 1608439777943453696, 0, NULL, '', NULL, 0, 0, 4, 255, 0, 50, 2, '2,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792342499329, 1531929290154827776, 1, 'fieldYlzjop', '配件编码', 1, 1, 1608439769529679872, 0, NULL, '', NULL, 0, 0, 6, 171, 0, 50, NULL, '3,2', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792359276544, 1531098805463797761, NULL, 'createUserName', '创建人', 10, 0, 1608439767134732288, 0, NULL, NULL, NULL, 0, 0, 7, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792388636672, 1532256280493760512, 1, 'fieldPzuvea', '规格型号', 1, 1, 1608439769529679872, 0, NULL, '', NULL, 0, 0, 7, 171, 0, 50, NULL, '3,3', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792422191104, 1531094470549950464, NULL, 'fieldTwjaes', '购置日期', 4, 1, 1608439777943453696, 0, NULL, '', NULL, 0, 0, 5, 255, 0, 50, NULL, '2,1', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792438968320, 1531929290318405632, 1, 'fieldDomlvu', '单位', 1, 1, 1608439769529679872, 0, NULL, '', NULL, 0, 0, 8, 171, 0, 50, NULL, '3,4', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792472522752, 1531094470596087808, NULL, 'fieldPavmpj', '驾驶员', 10, 1, 1608439777943453696, 0, NULL, '', NULL, 0, 0, 6, 255, 0, 50, NULL, '3,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792485105664, 1531929290364542976, 1, 'fieldOjkodf', '单价', 6, 1, 1608439769529679872, 0, NULL, '', NULL, 0, 0, 9, 171, 0, 50, 2, '3,5', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792514465792, 1531094470646419456, NULL, 'fieldRxrbad', '所属部门', 12, 1, 1608439777943453696, 0, NULL, '', NULL, 0, 0, 7, 255, 0, 50, NULL, '3,1', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792527048705, 1531929290414874624, 1, 'fieldPfmdbh', '数量', 5, 1, 1608439769529679872, 0, NULL, '', NULL, 0, 1, 10, 171, 0, 50, 4, '3,6', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792556408832, 1531094470692556800, NULL, 'fieldYyoira', '所属车队', 52, 1, 1608439777943453696, 0, NULL, '', NULL, 0, 0, 8, 251, 0, 50, NULL, '4,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792568991744, 1531929290465206272, 1, 'fieldRmsiyy', '材料费', 6, 1, 1608439769529679872, 0, NULL, '', NULL, 0, 0, 11, 171, 0, 50, 2, '3,7', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792594157568, 1531094470742888448, NULL, 'fieldWkshct', '出厂日期', 4, 1, 1608439777943453696, 0, NULL, '', NULL, 0, 0, 9, 255, 0, 50, NULL, '4,1', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792606740480, 1531929290515537920, 1, 'fieldWbogqz', '工时费', 6, 1, 1608439769529679872, 0, NULL, '', NULL, 0, 0, 12, 171, 0, 50, 2, '3,8', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792631906304, 1531094470789025792, NULL, 'fieldEilney', '车架号', 1, 1, 1608439777943453696, 0, NULL, '', NULL, 0, 0, 10, 255, 0, 50, NULL, '5,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792648683520, 1532262166243123200, 1, 'fieldRqwfhn', '金额小计', 6, 1, 1608439769529679872, 0, NULL, '', NULL, 0, 0, 13, 171, 0, 50, 2, '3,9', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792673849344, 1531094470839357440, NULL, 'fieldHsnfao', '发动机号', 1, 1, 1608439777943453696, 0, NULL, '', NULL, 0, 0, 11, 255, 0, 50, NULL, '5,1', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792690626560, 1531951270681763840, NULL, 'fieldRxehqc', '其他费用', 6, 1, 1608439769529679872, 0, NULL, '', NULL, 0, 0, 14, 255, 0, 50, 2, '4,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792719986688, 1531094470885494784, NULL, 'fieldPzuvea', '车身颜色', 1, 1, 1608439777943453696, 0, NULL, '', NULL, 0, 0, 12, 255, 0, 50, NULL, '6,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792740958208, 1532167214012162048, NULL, 'fieldYyoira', '维保总费用', 64, 1, 1608439769529679872, 0, NULL, '', NULL, 0, 0, 15, 242, 0, 50, 2, '4,1', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792740958209, 1531098805463797762, NULL, 'createTime', '创建时间', 13, 0, 1608439767134732288, 0, NULL, NULL, NULL, 0, 0, 8, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792757735425, 1531094470931632128, NULL, 'fieldPfmdbh', '乘坐人数（座）', 5, 1, 1608439777943453696, 0, NULL, '', NULL, 0, 0, 13, 255, 0, 50, 4, '6,1', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792770318336, 1532178371364069376, NULL, 'fieldMhlqon', '车辆', 52, 1, 1608439776668385280, 0, NULL, '', NULL, 0, 1, 0, 251, 0, 50, NULL, '0,0', NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439792808067073, 1531098805463797763, NULL, 'updateTime', '更新时间', 13, 0, 1608439767134732288, 0, NULL, NULL, NULL, 0, 0, 9, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792812261376, 1532169180264128512, NULL, 'fieldEilney', '备注', 2, 1, 1608439769529679872, 0, NULL, '', 800, 0, 0, 16, 255, 0, 100, NULL, '5,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792829038593, 1531094470981963776, NULL, 'fieldRmsiyy', '载重（吨）', 5, 1, 1608439777943453696, 0, NULL, '', NULL, 0, 0, 14, 255, 0, 50, 4, '7,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792841621504, 1532178371464732672, NULL, 'fieldZoywvv', '所属车队', 1, 1, 1608439776668385280, 0, NULL, '', NULL, 0, 0, 1, 255, 0, 50, NULL, '0,1', NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439792845815808, 1531098805463797764, NULL, 'ownerUserName', '负责人', 10, 0, 1608439767134732288, 0, NULL, NULL, NULL, 0, 0, 10, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792875175936, 1532178371414401024, NULL, 'fieldFowfyg', '车辆所属部门', 12, 1, 1608439776668385280, 0, NULL, '', NULL, 0, 0, 2, 255, 0, 50, NULL, '1,0', NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439792879370241, 1532169180318654464, NULL, 'fieldHsnfao', '附件', 8, 1, 1608439769529679872, 0, NULL, '', NULL, 0, 0, 17, 255, 0, 100, NULL, '6,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792883564544, 1531094471028101120, NULL, 'fieldWbogqz', '排气量（升）', 5, 1, 1608439777943453696, 0, NULL, '', NULL, 0, 0, 15, 255, 0, 50, 4, '7,1', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792912924672, 1532178371313737728, NULL, 'fieldTwjaes', '违章日期', 4, 1, 1608439776668385280, 0, NULL, '', NULL, 0, 0, 3, 255, 0, 50, NULL, '1,1', NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439792925507584, 1531098805463797765, NULL, 'teamMember', '团队成员', 1, 0, 1608439767134732288, 0, NULL, NULL, NULL, 0, 0, 11, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792933896192, 1531094471078432768, NULL, 'fieldRxehqc', '百公里耗油量（升）', 5, 1, 1608439777943453696, 0, NULL, '', NULL, 0, 0, 16, 255, 0, 50, 4, '8,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792938090496, 1531923992505606144, NULL, 'dataId', '数据ID', 1, 0, 1608439769529679872, 0, NULL, NULL, NULL, 0, 0, 18, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792950673409, 1532178371515064320, NULL, 'fieldYlzjop', '驾驶员', 52, 1, 1608439776668385280, 0, NULL, '', NULL, 0, 1, 4, 251, 0, 50, NULL, '2,0', NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439792963256320, 1531098805463797766, NULL, 'currentFlowId', '当前节点', 1, 0, 1608439767134732288, 0, NULL, NULL, NULL, 0, 0, 12, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792980033536, 1531094471124570112, NULL, 'fieldRqwfhn', '初始里程数/KM', 5, 1, 1608439777943453696, 0, NULL, '', NULL, 0, 0, 17, 255, 0, 50, 4, '9,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439792988422144, 1531923992505606145, NULL, 'createUserName', '创建人', 10, 0, 1608439769529679872, 0, NULL, NULL, NULL, 0, 0, 19, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793001005056, 1531098805463797767, NULL, 'flowType', '节点类型', 1, 0, 1608439767134732288, 0, NULL, NULL, NULL, 0, 0, 13, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793001005057, 1532178371561201664, NULL, 'fieldWisysx', '违章项目', 3, 1, 1608439776668385280, 0, NULL, '', NULL, 0, 1, 5, 255, 0, 50, 2, '3,0', NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439793030365184, 1531094471170707456, NULL, 'fieldPizhki', '已行驶里程/KM', 5, 1, 1608439777943453696, 0, NULL, '', NULL, 0, 1, 18, 255, 0, 50, 4, '9,1', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793034559488, 1531923992505606146, NULL, 'createTime', '创建时间', 13, 0, 1608439769529679872, 0, NULL, NULL, NULL, 0, 0, 20, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793034559489, 1531098805463797768, NULL, 'flowStatus', '节点状态', 1, 0, 1608439767134732288, 0, NULL, NULL, NULL, 0, 0, 14, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793038753792, 1532178371695419392, NULL, 'fieldCvwjri', '违章地点', 43, 1, 1608439776668385280, 0, NULL, '', NULL, 0, 0, 6, 255, 0, 100, 1, '4,0', NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439793068113920, 1531098805463797769, NULL, 'categoryId', '分类ID', 1, 0, 1608439767134732288, 0, NULL, NULL, NULL, 0, 0, 15, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793072308224, 1532178371796082688, NULL, 'fieldOjkodf', '罚款', 6, 1, 1608439776668385280, 0, NULL, '', NULL, 0, 0, 7, 255, 0, 50, 2, '5,0', NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439793076502528, 1531923992505606147, NULL, 'updateTime', '更新时间', 13, 0, 1608439769529679872, 0, NULL, NULL, NULL, 0, 0, 21, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793084891136, 1531094471221039104, NULL, 'fieldKpcmww', '注册日期', 4, 1, 1608439777943453696, 0, NULL, '', NULL, 0, 0, 19, 255, 0, 50, NULL, '10,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793110056960, 1531098805463797770, NULL, 'stageId', '阶段ID', 1, 0, 1608439767134732288, 0, NULL, NULL, NULL, 0, 0, 16, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793118445568, 1531923992505606148, NULL, 'ownerUserName', '负责人', 10, 0, 1608439769529679872, 0, NULL, NULL, NULL, 0, 0, 22, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793131028480, 1531094471267176448, NULL, 'fieldAuwwnq', '发证日期', 4, 1, 1608439777943453696, 0, NULL, '', NULL, 0, 0, 20, 255, 0, 50, NULL, '10,1', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793131028481, 1532178371846414336, NULL, 'fieldPfmdbh', '扣分', 5, 1, 1608439776668385280, 0, NULL, '', NULL, 0, 0, 8, 255, 0, 50, 4, '5,1', NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439793147805696, 1531098805463797771, NULL, 'stageName', '阶段名称', 1, 0, 1608439767134732288, 0, NULL, NULL, NULL, 0, 0, 17, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793160388608, 1531094471313313792, NULL, 'fieldCvwjri', '车辆状态', 3, 1, 1608439777943453696, 0, NULL, '', NULL, 0, 1, 21, 255, 0, 100, 2, '11,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793164582912, 1531923992505606149, NULL, 'teamMember', '团队成员', 1, 0, 1608439769529679872, 0, NULL, NULL, NULL, 0, 0, 23, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793168777216, 1532178371896745984, NULL, 'fieldDomlvu', '备注', 2, 1, 1608439776668385280, 0, NULL, '', 800, 0, 0, 9, 255, 0, 100, NULL, '6,0', NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439793185554432, 1531098805463797772, NULL, 'stageStatus', '阶段状态', 1, 0, 1608439767134732288, 0, NULL, NULL, NULL, 0, 0, 18, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793202331648, 1531094471430754304, NULL, 'fieldOfgvdu', '随车证件', 9, 1, 1608439777943453696, 0, NULL, '', NULL, 0, 0, 22, 255, 0, 100, 1, '12,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793202331649, 1531923992505606150, NULL, 'currentFlowId', '当前节点', 1, 0, 1608439769529679872, 0, NULL, NULL, NULL, 0, 0, 24, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793206525952, 1532176832020946944, NULL, 'dataId', '数据ID', 1, 0, 1608439776668385280, 0, NULL, NULL, NULL, 0, 0, 10, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439793223303168, 1531098805463797773, NULL, 'moduleId', '模块ID', 1, 0, 1608439767134732288, 0, NULL, NULL, NULL, 0, 0, 19, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793235886080, 1531923992505606151, NULL, 'flowType', '节点类型', 1, 0, 1608439769529679872, 0, NULL, NULL, NULL, 0, 0, 25, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793244274688, 1531094471560777728, NULL, 'fieldCefggt', '附件', 8, 1, 1608439777943453696, 0, NULL, '', NULL, 0, 0, 23, 255, 0, 100, NULL, '13,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793244274689, 1532176832033529856, NULL, 'createUserName', '创建人', 10, 0, 1608439776668385280, 0, NULL, NULL, NULL, 0, 0, 11, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439793273634816, 1532176832033529857, NULL, 'createTime', '创建时间', 13, 0, 1608439776668385280, 0, NULL, NULL, NULL, 0, 0, 12, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439793277829120, 1531923992505606152, NULL, 'flowStatus', '节点状态', 1, 0, 1608439769529679872, 0, NULL, NULL, NULL, 0, 0, 26, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793282023424, 1531094471611109376, NULL, 'fieldXgxxqa', '其他信息', 9, 1, 1608439777943453696, 0, NULL, '', NULL, 0, 0, 24, 255, 0, 100, 1, '14,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793315577856, 1532176832037724160, NULL, 'updateTime', '更新时间', 13, 0, 1608439776668385280, 0, NULL, NULL, NULL, 0, 0, 13, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439793328160768, 1531094471753715712, NULL, 'fieldMygrvj', '备注', 2, 1, 1608439777943453696, 0, NULL, '', 800, 0, 0, 25, 255, 0, 100, NULL, '15,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793332355072, 1531923992505606153, NULL, 'categoryId', '分类ID', 1, 0, 1608439769529679872, 0, NULL, NULL, NULL, 0, 0, 27, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793349132288, 1532176832037724161, NULL, 'ownerUserName', '负责人', 10, 0, 1608439776668385280, 0, NULL, NULL, NULL, 0, 0, 14, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439793370103808, 1529016058993782784, NULL, 'fieldMhlqon', '车队编号', 1, 1, 1608439776207011840, 0, NULL, '', NULL, 1, 1, 0, 255, 0, 100, NULL, '0,0', NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439793374298112, 1531094471799853056, NULL, 'fieldUedvna', '加油信息', 60, 1, 1608439777943453696, 0, NULL, '', NULL, 0, 0, 26, 224, 0, 100, NULL, '16,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793378492416, 1531923992505606154, NULL, 'stageId', '阶段ID', 1, 0, 1608439769529679872, 0, NULL, NULL, NULL, 0, 0, 28, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793386881024, 1532176832037724162, NULL, 'teamMember', '团队成员', 1, 0, 1608439776668385280, 0, NULL, NULL, NULL, 0, 0, 15, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439793407852544, 1529016059039920128, NULL, 'fieldFowfyg', '车队名称', 1, 1, 1608439776207011840, 0, NULL, '', NULL, 0, 1, 1, 255, 0, 100, NULL, '1,0', NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439793416241152, 1531094471850184704, NULL, 'fieldSexbax', '油品类型', 3, 1, 1608439777943453696, 0, NULL, '', NULL, 0, 0, 27, 255, 0, 50, 2, '17,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793424629760, 1531923992505606155, NULL, 'stageName', '阶段名称', 1, 0, 1608439769529679872, 0, NULL, NULL, NULL, 0, 0, 29, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793424629761, 1532176832037724163, NULL, 'currentFlowId', '当前节点', 1, 0, 1608439776668385280, 0, NULL, NULL, NULL, 0, 0, 16, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439793441406976, 1529015943390375936, NULL, 'dataId', '数据ID', 1, 0, 1608439776207011840, 0, NULL, NULL, NULL, 0, 0, 2, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439793453989888, 1531923992505606156, NULL, 'stageStatus', '阶段状态', 1, 0, 1608439769529679872, 0, NULL, NULL, NULL, 0, 0, 30, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793458184192, 1531094842077204480, NULL, 'fieldAzqvyl', '油卡号码', 52, 1, 1608439777943453696, 0, NULL, '', NULL, 0, 0, 28, 251, 0, 50, NULL, '17,1', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793462378496, 1532176832037724164, NULL, 'flowType', '节点类型', 1, 0, 1608439776668385280, 0, NULL, NULL, NULL, 0, 0, 17, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439793474961408, 1529015943390375937, NULL, 'createUserName', '创建人', 10, 0, 1608439776207011840, 0, NULL, NULL, NULL, 0, 0, 3, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439793495932928, 1532176832037724165, NULL, 'flowStatus', '节点状态', 1, 0, 1608439776668385280, 0, NULL, NULL, NULL, 0, 0, 18, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439793495932929, 1531097605070770176, NULL, 'fieldQyghpc', '保养信息', 60, 1, 1608439777943453696, 0, NULL, '', NULL, 0, 0, 29, 224, 0, 100, NULL, '18,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793508515840, 1531923992505606157, NULL, 'moduleId', '模块ID', 1, 0, 1608439769529679872, 0, NULL, NULL, NULL, 0, 0, 31, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793512710144, 1529015943390375938, NULL, 'createTime', '创建时间', 13, 0, 1608439776207011840, 0, NULL, NULL, NULL, 0, 0, 4, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439793533681664, 1532176832037724166, NULL, 'categoryId', '分类ID', 1, 0, 1608439776668385280, 0, NULL, NULL, NULL, 0, 0, 19, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439793537875968, 1531097605125296128, NULL, 'fieldZhjtju', '保养里程（KM）', 5, 1, 1608439777943453696, 0, NULL, '', NULL, 0, 0, 30, 255, 0, 50, 4, '19,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793550458880, 1529015943390375939, NULL, 'updateTime', '更新时间', 13, 0, 1608439776207011840, 0, NULL, NULL, NULL, 0, 0, 5, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439793567236096, 1532176832037724167, NULL, 'stageId', '阶段ID', 1, 0, 1608439776668385280, 0, NULL, NULL, NULL, 0, 0, 20, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439793575624704, 1531097605179822080, NULL, 'fieldLnxruf', '保养间隔（月）', 5, 1, 1608439777943453696, 0, NULL, '', NULL, 0, 0, 31, 255, 0, 50, 4, '19,1', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793592401920, 1532176832037724168, NULL, 'stageName', '阶段名称', 1, 0, 1608439776668385280, 0, NULL, NULL, NULL, 0, 0, 21, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439793592401921, 1529015943390375940, NULL, 'ownerUserName', '负责人', 10, 0, 1608439776207011840, 0, NULL, NULL, NULL, 0, 0, 6, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439793617567744, 1531097605230153728, NULL, 'fieldVjmdue', '上次保养里程（KM）', 5, 1, 1608439777943453696, 0, NULL, '', NULL, 0, 0, 32, 255, 0, 50, 4, '20,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793625956352, 1532176832037724169, NULL, 'stageStatus', '阶段状态', 1, 0, 1608439776668385280, 0, NULL, NULL, NULL, 0, 0, 22, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439793630150656, 1529015943390375941, NULL, 'teamMember', '团队成员', 1, 0, 1608439776207011840, 0, NULL, NULL, NULL, 0, 0, 7, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439793655316480, 1531097605284679680, NULL, 'fieldRjtgmq', '上次保养日期', 4, 1, 1608439777943453696, 0, NULL, '', NULL, 0, 0, 33, 255, 0, 50, NULL, '20,1', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793659510784, 1532176832037724170, NULL, 'moduleId', '模块ID', 1, 0, 1608439776668385280, 0, NULL, NULL, NULL, 0, 0, 23, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439793663705088, 1529015943390375942, NULL, 'currentFlowId', '当前节点', 1, 0, 1608439776207011840, 0, NULL, NULL, NULL, 0, 0, 8, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439793693065216, 1531097605339205632, NULL, 'fieldMxylkv', '报废信息', 60, 1, 1608439777943453696, 0, NULL, '', NULL, 0, 0, 34, 224, 0, 100, NULL, '21,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793701453824, 1529015943390375943, NULL, 'flowType', '节点类型', 1, 0, 1608439776207011840, 0, NULL, NULL, NULL, 0, 0, 9, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439793735008256, 1531097605393731584, NULL, 'fieldWasmoq', '是否报废', 3, 1, 1608439777943453696, 0, NULL, '', NULL, 0, 1, 35, 255, 0, 50, 1, '22,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793735008257, 1529015943390375944, NULL, 'flowStatus', '节点状态', 1, 0, 1608439776207011840, 0, NULL, NULL, NULL, 0, 0, 10, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439793760174080, 1529015943390375945, NULL, 'categoryId', '分类ID', 1, 0, 1608439776207011840, 0, NULL, NULL, NULL, 0, 0, 11, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439793768562688, 1531097605502783488, NULL, 'fieldRrfspm', '报废日期', 4, 1, 1608439777943453696, 0, NULL, '', NULL, 0, 0, 36, 255, 0, 50, NULL, '22,1', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793785339904, 1529015943390375946, NULL, 'stageId', '阶段ID', 1, 0, 1608439776207011840, 0, NULL, NULL, NULL, 0, 0, 12, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439793802117120, 1531097605557309440, NULL, 'fieldHgzwjo', '报废原因', 2, 1, 1608439777943453696, 0, NULL, '', 800, 0, 0, 37, 255, 0, 100, NULL, '23,0', NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793810505728, 1529015943390375947, NULL, 'stageName', '阶段名称', 1, 0, 1608439776207011840, 0, NULL, NULL, NULL, 0, 0, 13, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439793839865856, 1529015943390375948, NULL, 'stageStatus', '阶段状态', 1, 0, 1608439776207011840, 0, NULL, NULL, NULL, 0, 0, 14, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439793839865857, 1531086705811116032, NULL, 'dataId', '数据ID', 1, 0, 1608439777943453696, 0, NULL, NULL, NULL, 0, 0, 38, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793860837376, 1529015943390375949, NULL, 'moduleId', '模块ID', 1, 0, 1608439776207011840, 0, NULL, NULL, NULL, 0, 0, 15, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:25');
INSERT INTO `wk_module_field` VALUES (1608439793869225984, 1531086705823698944, NULL, 'createUserName', '创建人', 10, 0, 1608439777943453696, 0, NULL, NULL, NULL, 0, 0, 39, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793898586112, 1531086705823698945, NULL, 'createTime', '创建时间', 13, 0, 1608439777943453696, 0, NULL, NULL, NULL, 0, 0, 40, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793919557632, 1531086705823698946, NULL, 'updateTime', '更新时间', 13, 0, 1608439777943453696, 0, NULL, NULL, NULL, 0, 0, 41, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793944723456, 1531086705823698947, NULL, 'ownerUserName', '负责人', 10, 0, 1608439777943453696, 0, NULL, NULL, NULL, 0, 0, 42, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793969889280, 1531086705823698948, NULL, 'teamMember', '团队成员', 1, 0, 1608439777943453696, 0, NULL, NULL, NULL, 0, 0, 43, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439793990860800, 1531086705823698949, NULL, 'currentFlowId', '当前节点', 1, 0, 1608439777943453696, 0, NULL, NULL, NULL, 0, 0, 44, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439794028609536, 1531086705823698950, NULL, 'flowType', '节点类型', 1, 0, 1608439777943453696, 0, NULL, NULL, NULL, 0, 0, 45, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439794070552577, 1531086705823698951, NULL, 'flowStatus', '节点状态', 1, 0, 1608439777943453696, 0, NULL, NULL, NULL, 0, 0, 46, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439794112495616, 1531086705823698952, NULL, 'categoryId', '分类ID', 1, 0, 1608439777943453696, 0, NULL, NULL, NULL, 0, 0, 47, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439794146050048, 1531086705823698953, NULL, 'stageId', '阶段ID', 1, 0, 1608439777943453696, 0, NULL, NULL, NULL, 0, 0, 48, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439794183798784, 1531086705823698954, NULL, 'stageName', '阶段名称', 1, 0, 1608439777943453696, 0, NULL, NULL, NULL, 0, 0, 49, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439794225741824, 1531086705823698955, NULL, 'stageStatus', '阶段状态', 1, 0, 1608439777943453696, 0, NULL, NULL, NULL, 0, 0, 50, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');
INSERT INTO `wk_module_field` VALUES (1608439794259296257, 1531086705823698956, NULL, 'moduleId', '模块ID', 1, 0, 1608439777943453696, 0, NULL, NULL, NULL, 0, 0, 51, 255, 0, 50, NULL, NULL, NULL, NULL, '2022-12-29 20:28:24');

-- ----------------------------
-- Table structure for wk_module_field_config
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_field_config`;
CREATE TABLE `wk_module_field_config`  (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `field_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字段名称',
  `field_type` int(11) NOT NULL DEFAULT 1 COMMENT '字段类型 1 keyword 2 date 3 number 4 nested 5 datetime',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `field_name`(`field_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字段配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_field_config
-- ----------------------------
INSERT INTO `wk_module_field_config` VALUES (1608392367011532801, 'fieldMhlqon', 1, '2022-12-29 17:19:57');
INSERT INTO `wk_module_field_config` VALUES (1608392381528018945, 'fieldFowfyg', 1, '2022-12-29 17:20:01');
INSERT INTO `wk_module_field_config` VALUES (1608392381620293633, 'fieldZoywvv', 1, '2022-12-29 17:20:01');
INSERT INTO `wk_module_field_config` VALUES (1608392381687402498, 'fieldYlzjop', 1, '2022-12-29 17:20:01');
INSERT INTO `wk_module_field_config` VALUES (1608392381767094273, 'fieldDomlvu', 1, '2022-12-29 17:20:01');
INSERT INTO `wk_module_field_config` VALUES (1608392381850980354, 'fieldPavmpj', 1, '2022-12-29 17:20:01');
INSERT INTO `wk_module_field_config` VALUES (1608392381934866433, 'fieldRxrbad', 1, '2022-12-29 17:20:01');
INSERT INTO `wk_module_field_config` VALUES (1608392455150637057, 'fieldYyoira', 1, '2022-12-29 17:20:18');
INSERT INTO `wk_module_field_config` VALUES (1608392455364546562, 'fieldWisysx', 4, '2022-12-29 17:20:19');
INSERT INTO `wk_module_field_config` VALUES (1608392455691702273, 'fieldPizhki', 3, '2022-12-29 17:20:19');
INSERT INTO `wk_module_field_config` VALUES (1608392456077578242, 'fieldLnxruf', 3, '2022-12-29 17:20:19');
INSERT INTO `wk_module_field_config` VALUES (1608392456148881410, 'fieldVjmdue', 3, '2022-12-29 17:20:19');
INSERT INTO `wk_module_field_config` VALUES (1608392498796564482, 'fieldDqxojy', 5, '2022-12-29 17:20:29');
INSERT INTO `wk_module_field_config` VALUES (1608392499056611329, 'fieldTghtjb', 6, '2022-12-29 17:20:29');
INSERT INTO `wk_module_field_config` VALUES (1608392499467653122, 'fieldQyghpc', 1, '2022-12-29 17:20:29');
INSERT INTO `wk_module_field_config` VALUES (1608392500377817089, 'fieldOjkodf', 3, '2022-12-29 17:20:29');
INSERT INTO `wk_module_field_config` VALUES (1608392500591726593, 'fieldZhjtju', 3, '2022-12-29 17:20:29');
INSERT INTO `wk_module_field_config` VALUES (1608392505796857858, 'fieldTwjaes', 2, '2022-12-29 17:20:31');
INSERT INTO `wk_module_field_config` VALUES (1608392506258231298, 'fieldRqwfhn', 3, '2022-12-29 17:20:31');
INSERT INTO `wk_module_field_config` VALUES (1608392506392449026, 'fieldYymhyq', 1, '2022-12-29 17:20:31');
INSERT INTO `wk_module_field_config` VALUES (1608392506589581314, 'fieldHsnfao', 1, '2022-12-29 17:20:31');
INSERT INTO `wk_module_field_config` VALUES (1608392508036616193, 'fieldHevxte', 1, '2022-12-29 17:20:31');
INSERT INTO `wk_module_field_config` VALUES (1608392508829339649, 'fieldHgzwjo', 1, '2022-12-29 17:20:31');
INSERT INTO `wk_module_field_config` VALUES (1608392509089386498, 'fieldCvwjri', 4, '2022-12-29 17:20:31');
INSERT INTO `wk_module_field_config` VALUES (1608392509282324482, 'fieldPfmdbh', 3, '2022-12-29 17:20:31');
INSERT INTO `wk_module_field_config` VALUES (1608392510934880258, 'fieldHofwzx', 1, '2022-12-29 17:20:32');
INSERT INTO `wk_module_field_config` VALUES (1608392511064903682, 'fieldAuwcxa', 1, '2022-12-29 17:20:32');
INSERT INTO `wk_module_field_config` VALUES (1608392511404642305, 'fieldEilney', 1, '2022-12-29 17:20:32');
INSERT INTO `wk_module_field_config` VALUES (1608392511580803074, 'fieldCefggt', 1, '2022-12-29 17:20:32');
INSERT INTO `wk_module_field_config` VALUES (1608392511715020801, 'fieldRxehqc', 3, '2022-12-29 17:20:32');
INSERT INTO `wk_module_field_config` VALUES (1608439744225443840, 'fieldJbegdq', 5, '2022-12-29 20:28:13');
INSERT INTO `wk_module_field_config` VALUES (1608439760235102208, 'fieldPzuvea', 1, '2022-12-29 20:28:17');
INSERT INTO `wk_module_field_config` VALUES (1608439760327376896, 'fieldRmsiyy', 3, '2022-12-29 20:28:17');
INSERT INTO `wk_module_field_config` VALUES (1608439762315476992, 'fieldMygrvj', 1, '2022-12-29 20:28:17');
INSERT INTO `wk_module_field_config` VALUES (1608439763951255552, 'fieldWbogqz', 3, '2022-12-29 20:28:18');
INSERT INTO `wk_module_field_config` VALUES (1608439766476226560, 'fieldWkshct', 2, '2022-12-29 20:28:18');
INSERT INTO `wk_module_field_config` VALUES (1608439766543335424, 'fieldKpcmww', 2, '2022-12-29 20:28:18');
INSERT INTO `wk_module_field_config` VALUES (1608439772012707840, 'fieldUedvna', 1, '2022-12-29 20:28:20');
INSERT INTO `wk_module_field_config` VALUES (1608439772218228736, 'fieldOfgvdu', 4, '2022-12-29 20:28:20');
INSERT INTO `wk_module_field_config` VALUES (1608439772323086336, 'fieldXgxxqa', 4, '2022-12-29 20:28:20');
INSERT INTO `wk_module_field_config` VALUES (1608439772507635712, 'fieldAzqvyl', 1, '2022-12-29 20:28:20');
INSERT INTO `wk_module_field_config` VALUES (1608439772595716096, 'fieldSexbax', 4, '2022-12-29 20:28:20');
INSERT INTO `wk_module_field_config` VALUES (1608439773254221824, 'fieldWasmoq', 4, '2022-12-29 20:28:20');
INSERT INTO `wk_module_field_config` VALUES (1608439773451354112, 'fieldAuwwnq', 2, '2022-12-29 20:28:20');
INSERT INTO `wk_module_field_config` VALUES (1608439781705744384, 'fieldRjtgmq', 2, '2022-12-29 20:28:22');
INSERT INTO `wk_module_field_config` VALUES (1608439781810601984, 'fieldMxylkv', 1, '2022-12-29 20:28:22');
INSERT INTO `wk_module_field_config` VALUES (1608439782011928576, 'fieldRrfspm', 2, '2022-12-29 20:28:22');

-- ----------------------------
-- Table structure for wk_module_field_data
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_field_data`;
CREATE TABLE `wk_module_field_data`  (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `data_id` bigint(20) NOT NULL COMMENT '数据ID',
  `module_id` bigint(20) NULL DEFAULT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `field_id` bigint(20) NOT NULL COMMENT '字段ID',
  `field_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '自定义字段英文标识',
  `value` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '值',
  `is_main` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否是主字段',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `module_field_data_index_module_id_version`(`module_id`, `version`) USING BTREE,
  INDEX `module_field_data_index_data_id`(`data_id`) USING BTREE,
  INDEX `wk_module_field_data_module_id_field_id_index`(`module_id`, `field_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '模块字段值表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_field_data
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_field_data_common
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_field_data_common`;
CREATE TABLE `wk_module_field_data_common`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `data_id` bigint(20) NOT NULL COMMENT '数据ID',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `owner_user_id` bigint(20) NOT NULL COMMENT '负责人ID',
  `team_member` json NULL COMMENT '团队成员',
  `type` int(11) NULL DEFAULT 0 COMMENT '类型 0 审批 1 其他',
  `current_flow_id` bigint(20) NULL DEFAULT NULL COMMENT '当前节点',
  `flow_type` int(11) NULL DEFAULT NULL COMMENT '节点类型 0 条件 1 审批节点 2 填写节点 3 抄送节点 4 添加数据 5 更新节点',
  `flow_status` int(11) NULL DEFAULT NULL COMMENT '节点状态 0 待处理 1 已处理 2 拒绝 3 处理中 4 撤回 5 未提交 8 作废 9 忽略',
  `category_id` bigint(20) NULL DEFAULT NULL COMMENT '分类ID',
  `stage_id` bigint(20) NULL DEFAULT NULL COMMENT '阶段ID',
  `stage_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '阶段名称',
  `stage_status` int(11) NULL DEFAULT NULL COMMENT '阶段状态 0 未开始 1 完成 2 草稿 3 成功 4 失败',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次id，导入使用',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `data_id`(`data_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通用模块字段值表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_field_data_common
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_field_default
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_field_default`;
CREATE TABLE `wk_module_field_default`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `target_module_id` bigint(20) NULL DEFAULT NULL COMMENT '目标模块ID',
  `target_field_id` bigint(20) NULL DEFAULT NULL COMMENT '目标字段ID',
  `field_id` bigint(20) NULL DEFAULT NULL COMMENT '字段ID',
  `key` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '选项ID',
  `value` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '默认值',
  `type` int(11) NOT NULL DEFAULT 1 COMMENT '默认值类型 1 固定值2 自定义筛选 3 公式',
  `search` json NULL COMMENT '筛选条件',
  `formula` json NULL COMMENT '公式',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字段默认值配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_field_default
-- ----------------------------
INSERT INTO `wk_module_field_default` VALUES (1608392516433612804, 1608392498486185984, 0, 0, NULL, 1522619338986717184, NULL, '', 1, NULL, '{\"expression\": \"NOW()\"}');
INSERT INTO `wk_module_field_default` VALUES (1608392516433612807, 1608392498486185984, 0, 0, NULL, 1523903289806077952, NULL, '', 3, NULL, '{\"expression\": \"#{1608392498486185984-1522622016991100928}*#{1608392498486185984-1523903289730580480}\"}');
INSERT INTO `wk_module_field_default` VALUES (1608392516433612809, 1608392498486185984, 0, 0, NULL, 1524053824723017728, NULL, '', 3, NULL, '{\"expression\": \"SUM(#{1608392498486185984-1523903289806077952})\"}');
INSERT INTO `wk_module_field_default` VALUES (1608392517603823617, 1608392505129963520, 0, 0, NULL, 1522635492618764288, NULL, '', 3, NULL, '{\"expression\": \"TODAY()\"}');
INSERT INTO `wk_module_field_default` VALUES (1608392517666738177, 1608392505129963520, 0, 0, NULL, 1523903534577270784, NULL, '', 3, NULL, '{\"expression\": \"#{1608392505129963520-1522635492962697216}*#{1608392505129963520-1523903534505967616}\"}');
INSERT INTO `wk_module_field_default` VALUES (1608392517670932482, 1608392505129963520, 0, 0, NULL, 1523906876086992896, NULL, '', 3, NULL, '{\"expression\": \"SUM(#{1608392505129963520-1523903534577270784})\"}');
INSERT INTO `wk_module_field_default` VALUES (1608392517670932484, 1608392509865332736, 0, 0, NULL, 1523690294299688960, NULL, '', 3, NULL, '{\"expression\": \"TODAY()\"}');
INSERT INTO `wk_module_field_default` VALUES (1608439787007344640, 1608439765649948672, 0, 0, NULL, 1532171630983372800, NULL, '', 3, NULL, '{\"expression\": \"TODAY()\"}');
INSERT INTO `wk_module_field_default` VALUES (1608439787007344641, 1608439433763061760, 0, 0, NULL, 1534083811840688128, NULL, '', 3, NULL, '{\"expression\": \"TODAY()\"}');
INSERT INTO `wk_module_field_default` VALUES (1608439787007344642, 1608439767700963328, 0, 0, NULL, 1531922301278347264, NULL, NULL, 3, NULL, '{\"expression\": \"TODAY()\"}');
INSERT INTO `wk_module_field_default` VALUES (1608439787007344643, 1608439761141071872, 0, 1608439763607322624, 1532246067208331264, 1532248972401385472, NULL, '', 2, '[{\"type\": 1, \"model\": 0, \"search\": {\"type\": 1, \"values\": [], \"formType\": \"text\", \"fieldName\": \"fieldYlzjop\", \"searchEnum\": \"IS\", \"currentFieldId\": 1531209525480173568, \"tempCurrentFieldId\": \"1531209525480173568\"}, \"groupId\": 1}]', NULL);
INSERT INTO `wk_module_field_default` VALUES (1608439787024121856, 1608439768413995008, 0, 0, NULL, 1532173423826034688, NULL, NULL, 3, NULL, '{\"expression\": \"TODAY()\"}');
INSERT INTO `wk_module_field_default` VALUES (1608439787032510464, 1608439761141071872, 0, 0, NULL, 1532245858373935104, NULL, '', 3, NULL, '{\"expression\": \"#{1608439761141071872-1532248972401385472}+#{1608439761141071872-1531209525610196992}\"}');
INSERT INTO `wk_module_field_default` VALUES (1608439787036704768, 1608439768413995008, 0, 0, NULL, 1532173424245465088, NULL, '', 3, NULL, '{\"expression\": \"SUM(#{1608439768413995008-1532173424190939136})\"}');
INSERT INTO `wk_module_field_default` VALUES (1608439787091230720, 1608439746549088256, 0, 1608439777943453696, 1531094471850184704, 1532252179328475136, NULL, '', 2, '[{\"type\": 1, \"model\": 0, \"search\": {\"type\": 1, \"values\": [], \"formType\": \"text\", \"fieldName\": \"fieldZoywvv\", \"searchEnum\": \"IS\", \"currentFieldId\": 1531220096699117568, \"tempCurrentFieldId\": \"1531220096699117568\"}, \"groupId\": 1}]', NULL);
INSERT INTO `wk_module_field_default` VALUES (1608439787108007936, 1608439746549088256, 0, 0, NULL, 1534102235975319552, NULL, '', 3, NULL, '{\"expression\": \"#{1608439763607322624-1532246067208331264}-#{1608439746549088256-1531220097043050496}*#{1608439746549088256-1531220097093382144}\"}');
INSERT INTO `wk_module_field_default` VALUES (1608439791973400576, 1608439774743199744, 0, 1608439777943453696, 1531094470382178304, 1531184225996288000, NULL, '', 2, NULL, NULL);
INSERT INTO `wk_module_field_default` VALUES (1608439794024415232, 1608439769529679872, 0, 0, NULL, 1531929289827672064, NULL, '', 3, NULL, '{\"expression\": \"TODAY()\"}');
INSERT INTO `wk_module_field_default` VALUES (1608439794041192448, 1608439769529679872, 0, 0, NULL, 1532262166243123200, NULL, '', 3, NULL, '{\"expression\": \"#{1608439769529679872-1531929290364542976}*#{1608439769529679872-1531929290414874624}+#{1608439769529679872-1531929290465206272}+#{1608439769529679872-1531929290515537920}\"}');
INSERT INTO `wk_module_field_default` VALUES (1608439794078941184, 1608439776668385280, 0, 0, NULL, 1532178371313737728, NULL, '', 3, NULL, '{\"expression\": \"TODAY()\"}');
INSERT INTO `wk_module_field_default` VALUES (1608439794959745024, 1608439777943453696, 0, 0, NULL, 1531094470382178304, '1608439777943453696-1', '', 1, NULL, NULL);
INSERT INTO `wk_module_field_default` VALUES (1608439794968133632, 1608439777943453696, 0, 0, NULL, 1531094471313313792, '1608439777943453696-1', '', 1, NULL, NULL);
INSERT INTO `wk_module_field_default` VALUES (1608439794989105152, 1608439777943453696, 0, 0, NULL, 1531094471850184704, '1608439777943453696-3', '', 1, NULL, NULL);
INSERT INTO `wk_module_field_default` VALUES (1608439795010076672, 1608439777943453696, 0, 0, NULL, 1531097605393731584, '1608439777943453696-2', '', 1, NULL, NULL);

-- ----------------------------
-- Table structure for wk_module_field_formula
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_field_formula`;
CREATE TABLE `wk_module_field_formula`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `field_id` bigint(20) NULL DEFAULT NULL COMMENT '字段ID',
  `type` int(11) NOT NULL COMMENT '数值类型 1 数字 2 金额 3 百分比 4 日期 5 日期时间 6 文本 7 布尔值',
  `formula` json NOT NULL COMMENT '公式',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_module_field_formula_field_id_version_index`(`field_id`, `version`) USING BTREE,
  INDEX `wk_module_field_formula_module_id_version_index`(`module_id`, `version`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字段公式' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_field_formula
-- ----------------------------
INSERT INTO `wk_module_field_formula` VALUES (1608376867284160514, 1608376662409187328, 0, 1547825640620761088, 1, '{\"expression\": \"#{1608376662409187328-1547825385271533568}-#{1608376662409187328-1547825385321865216}+#{1608376662409187328-1547825385368002560}\"}');
INSERT INTO `wk_module_field_formula` VALUES (1608376867703590920, 1608376854185349120, 0, 1522635895833985024, 1, '{\"expression\": \"SUM(#{1608376854185349120-1522635492962697216})\"}');
INSERT INTO `wk_module_field_formula` VALUES (1608392023594504193, 1608391604776472576, 0, 1547825640620761088, 1, '{\"expression\": \"#{1608391604776472576-1547825385271533568}-#{1608391604776472576-1547825385321865216}+#{1608391604776472576-1547825385368002560}\"}');
INSERT INTO `wk_module_field_formula` VALUES (1608392023800025100, 1608391975947210752, 0, 1522635895833985024, 1, '{\"expression\": \"SUM(#{1608391975947210752-1522635492962697216})\"}');
INSERT INTO `wk_module_field_formula` VALUES (1608392516689465377, 1608392204931043328, 0, 1547825640620761088, 1, '{\"expression\": \"#{1608392204931043328-1547825385271533568}-#{1608392204931043328-1547825385321865216}+#{1608392204931043328-1547825385368002560}\"}');
INSERT INTO `wk_module_field_formula` VALUES (1608392517670932483, 1608392505129963520, 0, 1522635895833985024, 1, '{\"expression\": \"SUM(#{1608392505129963520-1522635492962697216})\"}');
INSERT INTO `wk_module_field_formula` VALUES (1608439787397414912, 1608439764727201792, 0, 1531193080780206080, 2, '{\"expression\": \"SUM(#{1608439764727201792-1531192798142836736}+#{1608439764727201792-1531192798188974080}+#{1608439764727201792-1531192798235111424}+#{1608439764727201792-1531192798281248768}+#{1608439764727201792-1531192798327386112}+#{1608439764727201792-1531192798373523456})\"}');
INSERT INTO `wk_module_field_formula` VALUES (1608439787426775040, 1608439433763061760, 0, 1531187608413057024, 1, '{\"expression\": \"#{1608439433763061760-1531187321963065344}-#{1608439433763061760-1531187321879179264}\"}');
INSERT INTO `wk_module_field_formula` VALUES (1608439787426775041, 1608439746549088256, 0, 1531220265255612416, 2, '{\"expression\": \"#{1608439746549088256-1531220097093382144}*#{1608439746549088256-1531220097043050496}\"}');
INSERT INTO `wk_module_field_formula` VALUES (1608439794070552576, 1608439769529679872, 0, 1532167214012162048, 2, '{\"expression\": \"SUM(#{1608439769529679872-1532262166243123200})+#{1608439769529679872-1531951270681763840}\"}');

-- ----------------------------
-- Table structure for wk_module_field_options
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_field_options`;
CREATE TABLE `wk_module_field_options`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `field_id` bigint(20) NULL DEFAULT NULL COMMENT '字段ID',
  `key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '选项ID',
  `value` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '选项值',
  `type` int(11) NOT NULL DEFAULT 1 COMMENT '选项类型：0 普通 1 其他',
  `sorting` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_module_field_options_module_id_version_index`(`module_id`, `version`) USING BTREE,
  INDEX `wk_module_field_options_field_id_version_index`(`field_id`, `version`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字段选项表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_field_options
-- ----------------------------
INSERT INTO `wk_module_field_options` VALUES (1608392515737358367, 1608392444123811840, 0, 1522614679714758656, '1608392444123811840-1', '一般消耗品', 0, 0);
INSERT INTO `wk_module_field_options` VALUES (1608392515737358369, 1608392444123811840, 0, 1522614679714758656, '1608392444123811840-2', '耐用品', 0, 1);
INSERT INTO `wk_module_field_options` VALUES (1608392516580413442, 1608392507768180736, 0, 1523704530035900416, '1608392507768180736-1', '一般消耗品', 0, 0);
INSERT INTO `wk_module_field_options` VALUES (1608392516630745089, 1608392507768180736, 0, 1523704530035900416, '1608392507768180736-2', '耐用品', 0, 1);
INSERT INTO `wk_module_field_options` VALUES (1608392516689465371, 1608392498486185984, 0, 1522622016840105984, '1608392498486185984-1', '一般消耗品', 0, 0);
INSERT INTO `wk_module_field_options` VALUES (1608392516689465373, 1608392498486185984, 0, 1522622016840105984, '1608392498486185984-2', '耐用品', 0, 1);
INSERT INTO `wk_module_field_options` VALUES (1608392516689465385, 1608392513568903168, 0, 1547827576845705216, '1608392513568903168-1', '一般消耗品', 0, 0);
INSERT INTO `wk_module_field_options` VALUES (1608392516689465388, 1608392513568903168, 0, 1547827576845705216, '1608392513568903168-2', '耐用品', 0, 1);
INSERT INTO `wk_module_field_options` VALUES (1608392517733847041, 1608392509865332736, 0, 1523696647734919168, '1608392509865332736-1', '一般消耗品', 0, 0);
INSERT INTO `wk_module_field_options` VALUES (1608392517733847042, 1608392505129963520, 0, 1522635492832673792, '1608392505129963520-1', '一般消耗品', 0, 0);
INSERT INTO `wk_module_field_options` VALUES (1608392517805150209, 1608392509865332736, 0, 1523696647734919168, '1608392509865332736-2', '耐用品', 0, 1);
INSERT INTO `wk_module_field_options` VALUES (1608392517805150210, 1608392505129963520, 0, 1522635492832673792, '1608392505129963520-2', '耐用品', 0, 1);
INSERT INTO `wk_module_field_options` VALUES (1608439787372249088, 1608439767700963328, 0, 1531922301118963712, '1608439767700963328-1', '正常维修', 0, 0);
INSERT INTO `wk_module_field_options` VALUES (1608439787372249089, 1608439765649948672, 0, 1532171631188893696, '1608439765649948672-1', '合格', 0, 0);
INSERT INTO `wk_module_field_options` VALUES (1608439787372249090, 1608439761891852288, 0, 1532176550008528896, '1608439761891852288-1', '车辆相撞', 0, 0);
INSERT INTO `wk_module_field_options` VALUES (1608439787384832000, 1608439767700963328, 0, 1531922301118963712, '1608439767700963328-2', '事故维修', 0, 1);
INSERT INTO `wk_module_field_options` VALUES (1608439787384832001, 1608439761891852288, 0, 1532176550008528896, '1608439761891852288-2', '车辆本身', 0, 1);
INSERT INTO `wk_module_field_options` VALUES (1608439787384832002, 1608439765649948672, 0, 1532171631188893696, '1608439765649948672-2', '不合格', 0, 1);
INSERT INTO `wk_module_field_options` VALUES (1608439787393220608, 1608439767700963328, 0, 1531922301118963712, '1608439767700963328-3', '保养', 0, 2);
INSERT INTO `wk_module_field_options` VALUES (1608439787393220609, 1608439761891852288, 0, 1532176550008528896, '1608439761891852288-3', '人车相撞', 0, 2);
INSERT INTO `wk_module_field_options` VALUES (1608439787481300992, 1608439746549088256, 0, 1532252179328475136, '1608439746549088256-1', '92#汽油', 0, 0);
INSERT INTO `wk_module_field_options` VALUES (1608439787489689600, 1608439746549088256, 0, 1531220096841723904, '1608439746549088256-2', '油卡', 0, 0);
INSERT INTO `wk_module_field_options` VALUES (1608439787502272512, 1608439746549088256, 0, 1532252179328475136, '1608439746549088256-2', '89#汽油', 0, 1);
INSERT INTO `wk_module_field_options` VALUES (1608439787510661120, 1608439746549088256, 0, 1532252179328475136, '1608439746549088256-3', '95#汽油', 0, 2);
INSERT INTO `wk_module_field_options` VALUES (1608439787519049728, 1608439746549088256, 0, 1532252179328475136, '1608439746549088256-4', '98#汽油', 0, 3);
INSERT INTO `wk_module_field_options` VALUES (1608439787527438336, 1608439746549088256, 0, 1532252179328475136, '1608439746549088256-5', '0#柴油', 0, 4);
INSERT INTO `wk_module_field_options` VALUES (1608439791906291712, 1608439763607322624, 0, 1529022535615713280, '1608439763607322624-1', '中石化', 0, 0);
INSERT INTO `wk_module_field_options` VALUES (1608439791965011968, 1608439763607322624, 0, 1529022535615713280, '1608439763607322624-2', '中石油', 0, 1);
INSERT INTO `wk_module_field_options` VALUES (1608439792015343616, 1608439774743199744, 0, 1531184225996288000, '1608439774743199744-2', '商务车', 0, 0);
INSERT INTO `wk_module_field_options` VALUES (1608439792023732225, 1608439774743199744, 0, 1531184225996288000, '1608439774743199744-1', '客车', 0, 1);
INSERT INTO `wk_module_field_options` VALUES (1608439792032120832, 1608439774743199744, 0, 1531184225996288000, '1608439774743199744-3', '轿车', 0, 2);
INSERT INTO `wk_module_field_options` VALUES (1608439792631906305, 1608439771190624256, 0, 1531111169617678336, '1608439771190624256-1', '男', 0, 0);
INSERT INTO `wk_module_field_options` VALUES (1608439792644489216, 1608439771190624256, 0, 1531111170276184064, '1608439771190624256-1', 'A1', 0, 0);
INSERT INTO `wk_module_field_options` VALUES (1608439792652877824, 1608439771190624256, 0, 1531111170037108736, '1608439771190624256-1', '空闲', 0, 0);
INSERT INTO `wk_module_field_options` VALUES (1608439792665460736, 1608439771190624256, 0, 1531111170762723328, '1608439771190624256-1', '旅客', 0, 0);
INSERT INTO `wk_module_field_options` VALUES (1608439792673849345, 1608439771190624256, 0, 1531111169730924544, '1608439771190624256-1', '本科', 0, 0);
INSERT INTO `wk_module_field_options` VALUES (1608439792682237952, 1608439771190624256, 0, 1531111169617678336, '1608439771190624256-2', '女', 0, 1);
INSERT INTO `wk_module_field_options` VALUES (1608439792694820864, 1608439771190624256, 0, 1531111170037108736, '1608439771190624256-2', '出车', 0, 1);
INSERT INTO `wk_module_field_options` VALUES (1608439792703209472, 1608439771190624256, 0, 1531111170276184064, '1608439771190624256-2', 'A2', 0, 1);
INSERT INTO `wk_module_field_options` VALUES (1608439792715792384, 1608439771190624256, 0, 1531111170762723328, '1608439771190624256-2', '普通货物', 0, 1);
INSERT INTO `wk_module_field_options` VALUES (1608439792724180992, 1608439771190624256, 0, 1531111169730924544, '1608439771190624256-2', '大专', 0, 1);
INSERT INTO `wk_module_field_options` VALUES (1608439792736763904, 1608439771190624256, 0, 1531111170037108736, '1608439771190624256-3', '请假', 0, 2);
INSERT INTO `wk_module_field_options` VALUES (1608439792745152512, 1608439771190624256, 0, 1531111170276184064, '1608439771190624256-3', 'A3', 0, 2);
INSERT INTO `wk_module_field_options` VALUES (1608439792757735424, 1608439771190624256, 0, 1531111170762723328, '1608439771190624256-3', '出租车驾驶员', 0, 2);
INSERT INTO `wk_module_field_options` VALUES (1608439792766124032, 1608439771190624256, 0, 1531111169730924544, '1608439771190624256-3', '高中/中专', 0, 2);
INSERT INTO `wk_module_field_options` VALUES (1608439792808067072, 1608439771190624256, 0, 1531111170037108736, '1608439771190624256-4', '离职', 0, 3);
INSERT INTO `wk_module_field_options` VALUES (1608439792820649984, 1608439771190624256, 0, 1531111170276184064, '1608439771190624256-4', 'B1', 0, 3);
INSERT INTO `wk_module_field_options` VALUES (1608439792829038592, 1608439771190624256, 0, 1531111170762723328, '1608439771190624256-4', '危险货物运输驾驶员', 0, 3);
INSERT INTO `wk_module_field_options` VALUES (1608439792841621505, 1608439771190624256, 0, 1531111169730924544, '1608439771190624256-4', '初中', 0, 3);
INSERT INTO `wk_module_field_options` VALUES (1608439792850010112, 1608439771190624256, 0, 1531111170276184064, '1608439771190624256-5', 'B2', 0, 4);
INSERT INTO `wk_module_field_options` VALUES (1608439792858398720, 1608439771190624256, 0, 1531111169730924544, '1608439771190624256-5', '小学', 0, 4);
INSERT INTO `wk_module_field_options` VALUES (1608439792870981632, 1608439771190624256, 0, 1531111170276184064, '1608439771190624256-6', 'C1', 0, 5);
INSERT INTO `wk_module_field_options` VALUES (1608439792879370240, 1608439771190624256, 0, 1531111170276184064, '1608439771190624256-7', 'C2', 0, 6);
INSERT INTO `wk_module_field_options` VALUES (1608439794120884224, 1608439769529679872, 0, 1531929289978667008, '1608439769529679872-1', '发动机系统', 0, 0);
INSERT INTO `wk_module_field_options` VALUES (1608439794125078528, 1608439776668385280, 0, 1532178371561201664, '1608439776668385280-1', '闯红灯', 0, 0);
INSERT INTO `wk_module_field_options` VALUES (1608439794133467136, 1608439769529679872, 0, 1531929289978667008, '1608439769529679872-2', '底盘系统', 0, 1);
INSERT INTO `wk_module_field_options` VALUES (1608439794137661440, 1608439776668385280, 0, 1532178371561201664, '1608439776668385280-2', '无证驾驶', 0, 1);
INSERT INTO `wk_module_field_options` VALUES (1608439794146050049, 1608439769529679872, 0, 1531929289978667008, '1608439769529679872-3', '空调电器系统', 0, 2);
INSERT INTO `wk_module_field_options` VALUES (1608439794146050050, 1608439776668385280, 0, 1532178371561201664, '1608439776668385280-3', '超载', 0, 2);
INSERT INTO `wk_module_field_options` VALUES (1608439794158632960, 1608439769529679872, 0, 1531929289978667008, '1608439769529679872-4', '车身系统', 0, 3);
INSERT INTO `wk_module_field_options` VALUES (1608439794158632961, 1608439776668385280, 0, 1532178371561201664, '1608439776668385280-4', '酒后驾驶', 0, 3);
INSERT INTO `wk_module_field_options` VALUES (1608439794167021568, 1608439776668385280, 0, 1532178371561201664, '1608439776668385280-5', '超载行驶', 0, 4);
INSERT INTO `wk_module_field_options` VALUES (1608439795114934272, 1608439777943453696, 0, 1531094471430754304, '1608439777943453696-1', '行驶证', 0, 0);
INSERT INTO `wk_module_field_options` VALUES (1608439795119128576, 1608439777943453696, 0, 1531097605393731584, '1608439777943453696-1', '是', 0, 0);
INSERT INTO `wk_module_field_options` VALUES (1608439795123322880, 1608439777943453696, 0, 1531094471611109376, '1608439777943453696-1', '私车公用', 0, 0);
INSERT INTO `wk_module_field_options` VALUES (1608439795131711488, 1608439777943453696, 0, 1531094470382178304, '1608439777943453696-1', '轿车', 0, 0);
INSERT INTO `wk_module_field_options` VALUES (1608439795135905792, 1608439777943453696, 0, 1531094471850184704, '1608439777943453696-2', '89#汽油', 0, 0);
INSERT INTO `wk_module_field_options` VALUES (1608439795144294400, 1608439777943453696, 0, 1531094471313313792, '1608439777943453696-1', '可用', 0, 0);
INSERT INTO `wk_module_field_options` VALUES (1608439795148488704, 1608439777943453696, 0, 1531094471430754304, '1608439777943453696-2', '环保标', 0, 1);
INSERT INTO `wk_module_field_options` VALUES (1608439795156877312, 1608439777943453696, 0, 1531094471611109376, '1608439777943453696-2', '领导专用', 0, 1);
INSERT INTO `wk_module_field_options` VALUES (1608439795161071616, 1608439777943453696, 0, 1531094471850184704, '1608439777943453696-3', '92#汽油', 0, 1);
INSERT INTO `wk_module_field_options` VALUES (1608439795165265920, 1608439777943453696, 0, 1531097605393731584, '1608439777943453696-2', '否', 0, 1);
INSERT INTO `wk_module_field_options` VALUES (1608439795173654528, 1608439777943453696, 0, 1531094470382178304, '1608439777943453696-2', '货车', 0, 1);
INSERT INTO `wk_module_field_options` VALUES (1608439795182043136, 1608439777943453696, 0, 1531094471313313792, '1608439777943453696-2', '出差', 0, 1);
INSERT INTO `wk_module_field_options` VALUES (1608439795194626048, 1608439777943453696, 0, 1531094471430754304, '1608439777943453696-3', '年检合格证', 0, 2);
INSERT INTO `wk_module_field_options` VALUES (1608439795203014656, 1608439777943453696, 0, 1531094471611109376, '1608439777943453696-3', '接送客用', 0, 2);
INSERT INTO `wk_module_field_options` VALUES (1608439795211403264, 1608439777943453696, 0, 1531094471850184704, '1608439777943453696-1', '95#汽油', 0, 2);
INSERT INTO `wk_module_field_options` VALUES (1608439795219791872, 1608439777943453696, 0, 1531094470382178304, '1608439777943453696-3', '商务车', 0, 2);
INSERT INTO `wk_module_field_options` VALUES (1608439795228180480, 1608439777943453696, 0, 1531094471313313792, '1608439777943453696-3', '维保', 0, 2);
INSERT INTO `wk_module_field_options` VALUES (1608439795240763392, 1608439777943453696, 0, 1531094471430754304, '1608439777943453696-4', '机动车交通事故责任强制保险', 0, 3);
INSERT INTO `wk_module_field_options` VALUES (1608439795249152000, 1608439777943453696, 0, 1531094471850184704, '1608439777943453696-4', '98#汽油', 0, 3);
INSERT INTO `wk_module_field_options` VALUES (1608439795257540608, 1608439777943453696, 0, 1531094470382178304, '1608439777943453696-4', '客车', 0, 3);
INSERT INTO `wk_module_field_options` VALUES (1608439795265929216, 1608439777943453696, 0, 1531094471313313792, '1608439777943453696-5', '已报废', 0, 3);
INSERT INTO `wk_module_field_options` VALUES (1608439795274317824, 1608439777943453696, 0, 1531094471430754304, '1608439777943453696-5', '灭火器', 0, 4);
INSERT INTO `wk_module_field_options` VALUES (1608439795282706432, 1608439777943453696, 0, 1531094471850184704, '1608439777943453696-5', '0#柴油', 0, 4);
INSERT INTO `wk_module_field_options` VALUES (1608439795286900736, 1608439777943453696, 0, 1531094471313313792, '1608439777943453696-4', '其他', 0, 4);

-- ----------------------------
-- Table structure for wk_module_field_serial_number
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_field_serial_number`;
CREATE TABLE `wk_module_field_serial_number`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `module_id` bigint(20) NOT NULL COMMENT '模块id',
  `version` int(11) NOT NULL COMMENT '版本号',
  `field_id` bigint(20) NOT NULL COMMENT '自定义字段id',
  `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字段值',
  `field_number` int(11) NULL DEFAULT 0 COMMENT '自动计数类型数据值',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建人id',
  `update_user_id` bigint(20) NULL DEFAULT NULL COMMENT '更新人id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_module_field_serial_number_field_id_version_index`(`field_id`, `version`) USING BTREE,
  INDEX `wk_module_field_serial_number_module_id_version_index`(`module_id`, `version`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '自定义编号字段值表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_field_serial_number
-- ----------------------------
INSERT INTO `wk_module_field_serial_number` VALUES (1608410347585994752, 1608398882170810368, 0, 1606112760111673344, 'CP-20221229-1', 1, '2022-12-29 18:31:24', NULL, 1, NULL);
INSERT INTO `wk_module_field_serial_number` VALUES (1608430753332146176, 1608398882170810368, 0, 1606112760111673344, 'CP-20221229-2', 2, '2022-12-29 19:52:29', NULL, 1, NULL);

-- ----------------------------
-- Table structure for wk_module_field_serial_number_rules
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_field_serial_number_rules`;
CREATE TABLE `wk_module_field_serial_number_rules`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `field_id` bigint(20) NULL DEFAULT NULL COMMENT '字段ID',
  `text_format` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'type为3，并且为日期和日期时间类型时需要，为需要格式化的日期格式',
  `start_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '对应type的值，1起始编号，2固定值，3字段id',
  `step_number` int(11) NULL DEFAULT NULL COMMENT 'stepNumber  当type为1时需要，递增数;',
  `reset_type` int(11) NULL DEFAULT NULL COMMENT ' 当type为1时需要，1 每天 2 每月 3 每年 4 从不;',
  `type` int(11) NULL DEFAULT NULL COMMENT '类型 1 自动计数 2 固定值 3 表单内字段',
  `sorting` int(11) NULL DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_module_field_serial_number_rules_module_id_version_index`(`module_id`, `version`) USING BTREE,
  INDEX `wk_module_field_serial_number_rules_field_id_version_index`(`field_id`, `version`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '自定义编码规则表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_field_serial_number_rules
-- ----------------------------
INSERT INTO `wk_module_field_serial_number_rules` VALUES (1608392516672688129, 1608392507768180736, 0, 1534356260369063936, NULL, '1', 1, 4, 1, 0);
INSERT INTO `wk_module_field_serial_number_rules` VALUES (1608392516689465346, 1608392507768180736, 0, 1534356260369063936, 'yyyyMMdd', '1523701952950665218', NULL, NULL, 3, 1);
INSERT INTO `wk_module_field_serial_number_rules` VALUES (1608392516689465374, 1608392498486185984, 0, 1522623630405632000, NULL, '1', 1, 4, 1, 0);
INSERT INTO `wk_module_field_serial_number_rules` VALUES (1608392516689465376, 1608392498486185984, 0, 1522619338940579840, NULL, '1', 1, 4, 1, 0);
INSERT INTO `wk_module_field_serial_number_rules` VALUES (1608392516689465378, 1608392498486185984, 0, 1522623630405632000, 'yyyyMMdd', '1522618449823629314', NULL, NULL, 3, 1);
INSERT INTO `wk_module_field_serial_number_rules` VALUES (1608392516689465379, 1608392498486185984, 0, 1522619338940579840, 'yyyyMMdd', '1522618449823629314', NULL, NULL, 3, 1);
INSERT INTO `wk_module_field_serial_number_rules` VALUES (1608392516689465380, 1608392472255008768, 0, 1523690993800544256, NULL, '1', 1, 4, 1, 0);
INSERT INTO `wk_module_field_serial_number_rules` VALUES (1608392516689465381, 1608392204931043328, 0, 1547822901325012992, NULL, '1', 1, 4, 1, 0);
INSERT INTO `wk_module_field_serial_number_rules` VALUES (1608392516689465382, 1608392472255008768, 0, 1523690993800544256, 'yyyyMMdd', '1523690674521735170', NULL, NULL, 3, 1);
INSERT INTO `wk_module_field_serial_number_rules` VALUES (1608392516689465383, 1608392204931043328, 0, 1547822901325012992, 'yyyyMMdd', '1547820594801745922', NULL, NULL, 3, 1);
INSERT INTO `wk_module_field_serial_number_rules` VALUES (1608392517066952707, 1608392513568903168, 0, 1547821988896120832, NULL, '1', 1, 4, 1, 0);
INSERT INTO `wk_module_field_serial_number_rules` VALUES (1608392517066952708, 1608392513568903168, 0, 1547821988896120832, 'yyyyMMdd', '1547821744326254594', NULL, NULL, 3, 1);
INSERT INTO `wk_module_field_serial_number_rules` VALUES (1608392517805150211, 1608392505129963520, 0, 1522635492488740864, NULL, '1', 1, 4, 1, 0);
INSERT INTO `wk_module_field_serial_number_rules` VALUES (1608392517805150212, 1608392505129963520, 0, 1522635492488740864, 'yyyyMMdd', '1522630040778539009', NULL, NULL, 3, 1);
INSERT INTO `wk_module_field_serial_number_rules` VALUES (1608392518186831874, 1608392509865332736, 0, 1523690294253551616, NULL, '1', 1, 4, 1, 0);
INSERT INTO `wk_module_field_serial_number_rules` VALUES (1608392518186831875, 1608392509865332736, 0, 1523690294253551616, 'yyyyMMdd', '1522640460302090242', NULL, NULL, 3, 1);
INSERT INTO `wk_module_field_serial_number_rules` VALUES (1608439787678433280, 1608439761141071872, 0, 1532231649967808512, NULL, '1', 1, 4, 1, 0);
INSERT INTO `wk_module_field_serial_number_rules` VALUES (1608439787682627584, 1608439767700963328, 0, 1531924677582249984, NULL, '1', 1, 4, 1, 0);
INSERT INTO `wk_module_field_serial_number_rules` VALUES (1608439787682627585, 1608439746549088256, 0, 1532252179148120064, NULL, '1', 1, 4, 1, 0);
INSERT INTO `wk_module_field_serial_number_rules` VALUES (1608439787695210496, 1608439767700963328, 0, 1531924677582249984, 'yyyyMMdd', '1531221060046860290', NULL, NULL, 3, 1);
INSERT INTO `wk_module_field_serial_number_rules` VALUES (1608439787695210497, 1608439761141071872, 0, 1532231649967808512, 'yyyyMMdd', '1531193456040390658', NULL, NULL, 3, 1);
INSERT INTO `wk_module_field_serial_number_rules` VALUES (1608439792078258176, 1608439774743199744, 0, 1531113641056784384, NULL, '1', 1, 4, 1, 0);
INSERT INTO `wk_module_field_serial_number_rules` VALUES (1608439792095035392, 1608439774743199744, 0, 1531113641056784384, 'yyyyMMdd', '1531111804064878594', NULL, NULL, 3, 1);

-- ----------------------------
-- Table structure for wk_module_field_sort
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_field_sort`;
CREATE TABLE `wk_module_field_sort`  (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `field_id` bigint(20) NULL DEFAULT NULL COMMENT '字段ID',
  `field_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字段名称',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `category_id` bigint(20) NULL DEFAULT NULL COMMENT '分类ID',
  `type` int(11) NULL DEFAULT NULL COMMENT '字段类型',
  `style` int(11) NULL DEFAULT NULL COMMENT '字段宽度',
  `sort` int(11) NOT NULL DEFAULT 0 COMMENT '字段排序',
  `user_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '用户id',
  `is_hide` int(11) NOT NULL DEFAULT 1 COMMENT '是否隐藏 0、不隐藏 1、隐藏',
  `is_lock` tinyint(1) NULL DEFAULT 0 COMMENT '字段锁定',
  `is_null` tinyint(4) NULL DEFAULT 0 COMMENT '是否必填 1 是 0 否',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字段排序表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_field_sort
-- ----------------------------
INSERT INTO `wk_module_field_sort` VALUES (1608395317037576194, 1547822901325012992, 'fieldMhlqon', '产品库存编号', 1608392204931043328, NULL, 63, NULL, 0, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395317079519234, 1547822901371150336, 'fieldFowfyg', '产品名称', 1608392204931043328, NULL, 52, NULL, 1, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395317142433794, 1547822901417287680, 'fieldZoywvv', '所属仓库', 1608392204931043328, NULL, 52, NULL, 2, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395317142433795, 1547825385271533568, 'fieldYlzjop', '入库数量', 1608392204931043328, NULL, 56, NULL, 3, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395317142433796, 1547825385321865216, 'fieldDomlvu', '领用数量', 1608392204931043328, NULL, 56, NULL, 4, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395317142433797, 1547825385368002560, 'fieldPavmpj', '归还数量', 1608392204931043328, NULL, 56, NULL, 5, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395317196959746, 1547825640620761088, 'fieldRxrbad', '当前库存', 1608392204931043328, NULL, 64, NULL, 6, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395317196959747, 1547820594801745920, 'dataId', '数据ID', 1608392204931043328, NULL, 1, NULL, 200, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395317196959748, 1547820594801745921, 'createUserName', '创建人', 1608392204931043328, NULL, 10, NULL, 201, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395317255680002, 1547820594801745922, 'createTime', '创建时间', 1608392204931043328, NULL, 13, NULL, 202, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395317255680003, 1547820594801745923, 'updateTime', '更新时间', 1608392204931043328, NULL, 13, NULL, 203, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395317255680004, 1547820594801745924, 'ownerUserName', '负责人', 1608392204931043328, NULL, 10, NULL, 204, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395317255680005, 1547820594801745925, 'teamMember', '团队成员', 1608392204931043328, NULL, 1, NULL, 205, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395317255680006, 1547820594801745926, 'currentFlowId', '当前节点', 1608392204931043328, NULL, 1, NULL, 206, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395317335371778, 1547820594801745927, 'flowType', '节点类型', 1608392204931043328, NULL, 1, NULL, 207, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395317394092034, 1547820594805940224, 'flowStatus', '节点状态', 1608392204931043328, NULL, 1, NULL, 208, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395317394092035, 1547820594805940225, 'categoryId', '分类ID', 1608392204931043328, NULL, 1, NULL, 209, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395317394092036, 1547820594805940226, 'stageId', '阶段ID', 1608392204931043328, NULL, 1, NULL, 210, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395317394092037, 1547820594805940227, 'stageName', '阶段名称', 1608392204931043328, NULL, 1, NULL, 211, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395317473783809, 1547820594805940228, 'stageStatus', '阶段状态', 1608392204931043328, NULL, 1, NULL, 212, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395317473783810, 1547820594805940229, 'moduleId', '模块ID', 1608392204931043328, NULL, 1, NULL, 213, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395365716668418, 1547821988896120832, 'fieldMhlqon', '产品编号', 1608392513568903168, NULL, 63, NULL, 0, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395365779582977, 1547821988942258176, 'fieldFowfyg', '产品名称', 1608392513568903168, NULL, 1, NULL, 1, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395365779582978, 1547827576711487488, 'fieldZoywvv', '规格', 1608392513568903168, NULL, 1, NULL, 2, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395365855080449, 1547827576753430528, 'fieldYlzjop', '单位', 1608392513568903168, NULL, 1, NULL, 3, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395365855080450, 1547827576799567872, 'fieldOjkodf', '单价', 1608392513568903168, NULL, 6, NULL, 4, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395365855080451, 1547827576845705216, 'fieldWisysx', '分类', 1608392513568903168, NULL, 3, NULL, 5, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395365913800705, 1547821744326254592, 'dataId', '数据ID', 1608392513568903168, NULL, 1, NULL, 200, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395365913800706, 1547821744326254593, 'createUserName', '创建人', 1608392513568903168, NULL, 10, NULL, 201, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395365976715266, 1547821744326254594, 'createTime', '创建时间', 1608392513568903168, NULL, 13, NULL, 202, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395365976715267, 1547821744326254595, 'updateTime', '更新时间', 1608392513568903168, NULL, 13, NULL, 203, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395365976715268, 1547821744326254596, 'ownerUserName', '负责人', 1608392513568903168, NULL, 10, NULL, 204, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395365976715269, 1547821744326254597, 'teamMember', '团队成员', 1608392513568903168, NULL, 1, NULL, 205, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395365976715270, 1547821744326254598, 'currentFlowId', '当前节点', 1608392513568903168, NULL, 1, NULL, 206, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395366035435522, 1547821744326254599, 'flowType', '节点类型', 1608392513568903168, NULL, 1, NULL, 207, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395366035435523, 1547821744326254600, 'flowStatus', '节点状态', 1608392513568903168, NULL, 1, NULL, 208, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395366035435524, 1547821744326254601, 'categoryId', '分类ID', 1608392513568903168, NULL, 1, NULL, 209, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395366035435525, 1547821744326254602, 'stageId', '阶段ID', 1608392513568903168, NULL, 1, NULL, 210, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395366035435526, 1547821744326254603, 'stageName', '阶段名称', 1608392513568903168, NULL, 1, NULL, 211, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395366035435527, 1547821744326254604, 'stageStatus', '阶段状态', 1608392513568903168, NULL, 1, NULL, 212, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608395366035435528, 1547821744326254605, 'moduleId', '模块ID', 1608392513568903168, NULL, 1, NULL, 213, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439910189858816, 1522614130734891008, 'fieldMhlqon', '用品名称', 1608392444123811840, NULL, 1, NULL, 0, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439910202441728, 1523706973259923456, 'fieldYyoira', '仓库', 1608392444123811840, NULL, 52, NULL, 1, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439910215024640, 1522614130785222656, 'fieldFowfyg', '规格', 1608392444123811840, NULL, 1, NULL, 2, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439910231801856, 1522614130827165696, 'fieldZoywvv', '单位', 1608392444123811840, NULL, 1, NULL, 3, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439910244384768, 1522614679714758656, 'fieldWisysx', '分类', 1608392444123811840, NULL, 3, NULL, 4, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439910261161984, 1547484787855400960, 'fieldPizhki', '入库数量', 1608392444123811840, NULL, 5, NULL, 5, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439910273744896, 1547823290552229888, 'fieldLnxruf', '领用数量', 1608392444123811840, NULL, 5, NULL, 6, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439910290522112, 1547823290770333696, 'fieldVjmdue', '归还数量', 1608392444123811840, NULL, 5, NULL, 7, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439910303105024, 1522601270784917504, 'dataId', '数据ID', 1608392444123811840, NULL, 1, NULL, 200, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439910315687936, 1522601270789111808, 'createUserName', '创建人', 1608392444123811840, NULL, 10, NULL, 201, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439910332465152, 1522601270789111809, 'createTime', '创建时间', 1608392444123811840, NULL, 13, NULL, 202, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439910345048064, 1522601270789111810, 'updateTime', '更新时间', 1608392444123811840, NULL, 13, NULL, 203, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439910357630976, 1522601270789111811, 'ownerUserName', '负责人', 1608392444123811840, NULL, 10, NULL, 204, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439910370213888, 1522601270789111812, 'currentFlowId', '当前节点', 1608392444123811840, NULL, 1, NULL, 205, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439910382796800, 1522601270789111813, 'flowType', '节点类型', 1608392444123811840, NULL, 1, NULL, 206, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439910437322752, 1522601270789111814, 'flowStatus', '节点状态', 1608392444123811840, NULL, 1, NULL, 207, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439910449905664, 1522601270789111815, 'categoryId', '分类ID', 1608392444123811840, NULL, 1, NULL, 208, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439910462488576, 1522601270789111816, 'stageId', '阶段ID', 1608392444123811840, NULL, 1, NULL, 209, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439910475071488, 1522601270789111817, 'stageName', '阶段名称', 1608392444123811840, NULL, 1, NULL, 210, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439910487654400, 1522601270789111818, 'stageStatus', '阶段状态', 1608392444123811840, NULL, 1, NULL, 211, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439910500237312, 1522601270789111819, 'moduleId', '模块ID', 1608392444123811840, NULL, 1, NULL, 212, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439920784670720, 1531185823434715136, 'fieldMhlqon', '关联用车申请单', 1608439433763061760, NULL, 52, NULL, 0, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439920793059328, 1531185823489241088, 'fieldFowfyg', '用车人', 1608439433763061760, NULL, 10, NULL, 1, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439920801447936, 1547094601317294080, 'fieldHsnfao', '归还车辆', 1608439433763061760, NULL, 1, NULL, 2, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439920809836544, 1532228759043776512, 'fieldEilney', '驾驶员', 1608439433763061760, NULL, 1, NULL, 3, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439920822419456, 1534083811756802048, 'fieldDqxojy', '出车日期', 1608439433763061760, NULL, 13, NULL, 4, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439920830808064, 1531187321879179264, 'fieldOjkodf', '出车里程', 1608439433763061760, NULL, 5, NULL, 5, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439920839196672, 1534083811840688128, 'fieldJbegdq', '回车日期', 1608439433763061760, NULL, 13, NULL, 6, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439920847585280, 1531187321963065344, 'fieldPfmdbh', '回车里程', 1608439433763061760, NULL, 5, NULL, 7, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439920855973888, 1531187608413057024, 'fieldPavmpj', '行驶里程', 1608439433763061760, NULL, 64, NULL, 8, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439920864362496, 1531185233027706880, 'dataId', '数据ID', 1608439433763061760, NULL, 1, NULL, 200, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439920876945408, 1531185233027706881, 'createUserName', '创建人', 1608439433763061760, NULL, 10, NULL, 201, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439920893722624, 1531185233027706882, 'createTime', '创建时间', 1608439433763061760, NULL, 13, NULL, 202, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439920906305536, 1531185233027706883, 'updateTime', '更新时间', 1608439433763061760, NULL, 13, NULL, 203, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439920918888448, 1531185233027706884, 'ownerUserName', '负责人', 1608439433763061760, NULL, 10, NULL, 204, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439920935665664, 1531185233027706885, 'teamMember', '团队成员', 1608439433763061760, NULL, 1, NULL, 205, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439920944054272, 1531185233027706886, 'currentFlowId', '当前节点', 1608439433763061760, NULL, 1, NULL, 206, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439920952442880, 1531185233027706887, 'flowType', '节点类型', 1608439433763061760, NULL, 1, NULL, 207, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439920960831488, 1531185233027706888, 'flowStatus', '节点状态', 1608439433763061760, NULL, 1, NULL, 208, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439920973414400, 1531185233027706889, 'categoryId', '分类ID', 1608439433763061760, NULL, 1, NULL, 209, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439920981803008, 1531185233027706890, 'stageId', '阶段ID', 1608439433763061760, NULL, 1, NULL, 210, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439920990191616, 1531185233027706891, 'stageName', '阶段名称', 1608439433763061760, NULL, 1, NULL, 211, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439920998580224, 1531185233027706892, 'stageStatus', '阶段状态', 1608439433763061760, NULL, 1, NULL, 212, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608439921002774528, 1531185233027706893, 'moduleId', '模块ID', 1608439433763061760, NULL, 1, NULL, 213, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645774365720576, 1522619338940579840, 'fieldMhlqon', '采购单号', 1608392498486185984, NULL, 63, NULL, 0, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645774374109184, 1522619338986717184, 'fieldDqxojy', '申请时间', 1608392498486185984, NULL, 13, NULL, 1, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645774382497792, 1522622016580059136, 'fieldZoywvv', '申请人', 1608392498486185984, NULL, 10, NULL, 2, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645774390886400, 1522622016626196480, 'fieldYlzjop', '部门', 1608392498486185984, NULL, 12, NULL, 3, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645774403469312, 1522619339032854528, 'fieldFowfyg', '用途', 1608392498486185984, NULL, 1, NULL, 4, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645774411857920, 1524053824723017728, 'fieldLnxruf', '预计金额', 1608392498486185984, NULL, 6, NULL, 5, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645774420246528, 1522618449823629312, 'dataId', '数据ID', 1608392498486185984, NULL, 1, NULL, 200, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645774428635136, 1522618449823629313, 'createUserName', '创建人', 1608392498486185984, NULL, 10, NULL, 201, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645774437023744, 1522618449823629314, 'createTime', '创建时间', 1608392498486185984, NULL, 13, NULL, 202, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645774445412352, 1522618449823629315, 'updateTime', '更新时间', 1608392498486185984, NULL, 13, NULL, 203, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645774457995264, 1522618449823629316, 'ownerUserName', '负责人', 1608392498486185984, NULL, 10, NULL, 204, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645774466383872, 1522618449823629317, 'currentFlowId', '当前节点', 1608392498486185984, NULL, 1, NULL, 205, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645774478966784, 1522618449823629318, 'flowType', '节点类型', 1608392498486185984, NULL, 1, NULL, 206, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645774495744000, 1522618449823629319, 'flowStatus', '节点状态', 1608392498486185984, NULL, 1, NULL, 207, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645774508326912, 1522618449823629320, 'categoryId', '分类ID', 1608392498486185984, NULL, 1, NULL, 208, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645774558658560, 1522618449823629321, 'stageId', '阶段ID', 1608392498486185984, NULL, 1, NULL, 209, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645774575435776, 1522618449823629322, 'stageName', '阶段名称', 1608392498486185984, NULL, 1, NULL, 210, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645774588018688, 1522618449823629323, 'stageStatus', '阶段状态', 1608392498486185984, NULL, 1, NULL, 211, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645774604795904, 1522618449823629324, 'moduleId', '模块ID', 1608392498486185984, NULL, 1, NULL, 212, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645777167515648, 1522635492488740864, 'fieldMhlqon', '入库编号', 1608392505129963520, NULL, 63, NULL, 0, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645777180098560, 1522635492660707328, 'fieldYlzjop', '关联采购申请单', 1608392505129963520, NULL, 52, NULL, 1, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645777196875776, 1522635492534878208, 'fieldFowfyg', '签收人', 1608392505129963520, NULL, 10, NULL, 2, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645777209458688, 1522635492576821248, 'fieldZoywvv', '部门', 1608392505129963520, NULL, 12, NULL, 3, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645777222041600, 1522635492618764288, 'fieldTwjaes', '签收日期', 1608392505129963520, NULL, 4, NULL, 4, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645777238818816, 1522635895833985024, 'fieldYyoira', '总数量', 1608392505129963520, NULL, 64, NULL, 5, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645777251401728, 1523906876086992896, 'fieldZhjtju', '采购金额合计', 1608392505129963520, NULL, 6, NULL, 6, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645777268178944, 1522636123425308672, 'fieldHsnfao', '备注', 1608392505129963520, NULL, 2, NULL, 7, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645777276567552, 1522630040774344704, 'dataId', '数据ID', 1608392505129963520, NULL, 1, NULL, 200, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645777289150464, 1522630040778539008, 'createUserName', '创建人', 1608392505129963520, NULL, 10, NULL, 201, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645777301733376, 1522630040778539009, 'createTime', '创建时间', 1608392505129963520, NULL, 13, NULL, 202, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645777314316288, 1522630040778539010, 'updateTime', '更新时间', 1608392505129963520, NULL, 13, NULL, 203, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645777326899200, 1522630040778539011, 'ownerUserName', '负责人', 1608392505129963520, NULL, 10, NULL, 204, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645777339482112, 1522630040778539012, 'currentFlowId', '当前节点', 1608392505129963520, NULL, 1, NULL, 205, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645777356259328, 1522630040778539013, 'flowType', '节点类型', 1608392505129963520, NULL, 1, NULL, 206, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645777364647936, 1522630040778539014, 'flowStatus', '节点状态', 1608392505129963520, NULL, 1, NULL, 207, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645777377230848, 1522630040778539015, 'categoryId', '分类ID', 1608392505129963520, NULL, 1, NULL, 208, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645777385619456, 1522630040778539016, 'stageId', '阶段ID', 1608392505129963520, NULL, 1, NULL, 209, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645777394008064, 1522630040778539017, 'stageName', '阶段名称', 1608392505129963520, NULL, 1, NULL, 210, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645777402396672, 1522630040778539018, 'stageStatus', '阶段状态', 1608392505129963520, NULL, 1, NULL, 211, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645777410785280, 1522630040778539019, 'moduleId', '模块ID', 1608392505129963520, NULL, 1, NULL, 212, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645780023836672, 1534356260369063936, 'fieldHevxte', '物品归还编号', 1608392507768180736, NULL, 63, NULL, 0, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645780032225280, 1527563081086976000, 'fieldHsnfao', '关联领用单号', 1608392507768180736, NULL, 52, NULL, 1, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645780040613888, 1523704529641635840, 'fieldTwjaes', '归还日期', 1608392507768180736, NULL, 4, NULL, 2, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645780049002496, 1523704529738104832, 'fieldFowfyg', '归还人员', 1608392507768180736, NULL, 10, NULL, 3, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645780057391104, 1527570189740195840, 'fieldQyghpc', '归还部门', 1608392507768180736, NULL, 12, NULL, 4, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645780065779712, 1523704530329501696, 'fieldRxrbad', '备注', 1608392507768180736, NULL, 2, NULL, 5, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645780074168320, 1523701952950665216, 'dataId', '数据ID', 1608392507768180736, NULL, 1, NULL, 200, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645780082556928, 1523701952950665217, 'createUserName', '创建人', 1608392507768180736, NULL, 10, NULL, 201, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645780095139840, 1523701952950665218, 'createTime', '创建时间', 1608392507768180736, NULL, 13, NULL, 202, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645780111917056, 1523701952950665219, 'updateTime', '更新时间', 1608392507768180736, NULL, 13, NULL, 203, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645780124499968, 1523701952950665220, 'ownerUserName', '负责人', 1608392507768180736, NULL, 10, NULL, 204, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645780137082880, 1523701952950665221, 'currentFlowId', '当前节点', 1608392507768180736, NULL, 1, NULL, 205, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645780149665792, 1523701952950665222, 'flowType', '节点类型', 1608392507768180736, NULL, 1, NULL, 206, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645780162248704, 1523701952954859520, 'flowStatus', '节点状态', 1608392507768180736, NULL, 1, NULL, 207, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645780170637312, 1523701952954859521, 'categoryId', '分类ID', 1608392507768180736, NULL, 1, NULL, 208, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645780179025920, 1523701952954859522, 'stageId', '阶段ID', 1608392507768180736, NULL, 1, NULL, 209, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645780187414528, 1523701952954859523, 'stageName', '阶段名称', 1608392507768180736, NULL, 1, NULL, 210, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645780199997440, 1523701952954859524, 'stageStatus', '阶段状态', 1608392507768180736, NULL, 1, NULL, 211, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645780208386048, 1523701952954859525, 'moduleId', '模块ID', 1608392507768180736, NULL, 1, NULL, 212, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645782104211456, 1523690294253551616, 'fieldMhlqon', '领用单号', 1608392509865332736, NULL, 63, NULL, 0, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645782112600064, 1523690294299688960, 'fieldTwjaes', '领用日期', 1608392509865332736, NULL, 4, NULL, 1, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645782120988672, 1523690294341632000, 'fieldFowfyg', '领用人', 1608392509865332736, NULL, 10, NULL, 2, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645782133571584, 1523690294387769344, 'fieldZoywvv', '领用部门', 1608392509865332736, NULL, 12, NULL, 3, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645782146154496, 1523690294433906688, 'fieldYlzjop', '领用用途', 1608392509865332736, NULL, 1, NULL, 4, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645782158737408, 1523701602042609664, 'fieldQyghpc', '备注', 1608392509865332736, NULL, 2, NULL, 5, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645782167126016, 1522640460302090240, 'dataId', '数据ID', 1608392509865332736, NULL, 1, NULL, 200, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645782175514624, 1522640460302090241, 'createUserName', '创建人', 1608392509865332736, NULL, 10, NULL, 201, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645782183903232, 1522640460302090242, 'createTime', '创建时间', 1608392509865332736, NULL, 13, NULL, 202, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645782192291840, 1522640460302090243, 'updateTime', '更新时间', 1608392509865332736, NULL, 13, NULL, 203, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645782204874752, 1522640460302090244, 'ownerUserName', '负责人', 1608392509865332736, NULL, 10, NULL, 204, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645782217457664, 1522640460302090245, 'currentFlowId', '当前节点', 1608392509865332736, NULL, 1, NULL, 205, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645782234234880, 1522640460302090246, 'flowType', '节点类型', 1608392509865332736, NULL, 1, NULL, 206, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645782246817792, 1522640460302090247, 'flowStatus', '节点状态', 1608392509865332736, NULL, 1, NULL, 207, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645782259400704, 1522640460302090248, 'categoryId', '分类ID', 1608392509865332736, NULL, 1, NULL, 208, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645782271983616, 1522640460302090249, 'stageId', '阶段ID', 1608392509865332736, NULL, 1, NULL, 209, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645782284566528, 1522640460302090250, 'stageName', '阶段名称', 1608392509865332736, NULL, 1, NULL, 210, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645782292955136, 1522640460302090251, 'stageStatus', '阶段状态', 1608392509865332736, NULL, 1, NULL, 211, 1, 0, 0, 0);
INSERT INTO `wk_module_field_sort` VALUES (1608645782305538048, 1522640460302090252, 'moduleId', '模块ID', 1608392509865332736, NULL, 1, NULL, 212, 1, 0, 0, 0);

-- ----------------------------
-- Table structure for wk_module_field_tags
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_field_tags`;
CREATE TABLE `wk_module_field_tags`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `field_id` bigint(20) NULL DEFAULT NULL COMMENT '字段ID',
  `key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '选项ID',
  `value` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '选项值',
  `color` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '颜色',
  `sorting` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `group_id` int(11) NOT NULL COMMENT '分组ID',
  `group_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分组名字',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_module_field_tags_field_id_version_index`(`field_id`, `version`) USING BTREE,
  INDEX `wk_module_field_tags_module_id_version_index`(`module_id`, `version`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字段标签选项表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_field_tags
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_field_tree
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_field_tree`;
CREATE TABLE `wk_module_field_tree`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `field_id` bigint(20) NULL DEFAULT NULL COMMENT '字段ID',
  `show_field` bigint(20) NOT NULL COMMENT '展示字段',
  `sorting` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
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
  `id` bigint(20) NOT NULL,
  `type` int(11) NOT NULL DEFAULT 0 COMMENT '0 字段关联 1 模块关联',
  `relate_field_id` bigint(20) NOT NULL COMMENT '数据关联字段ID',
  `field_id` bigint(20) NULL DEFAULT NULL COMMENT '当前字段ID',
  `module_id` bigint(20) NOT NULL COMMENT '当前模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `target_field_id` bigint(20) NULL DEFAULT NULL COMMENT '目标字段ID',
  `target_module_id` bigint(20) NOT NULL COMMENT '目标模块ID',
  `target_category_ids` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '目标分类ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `union_field_id`(`relate_field_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字段关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_field_union
-- ----------------------------
INSERT INTO `wk_module_field_union` VALUES (1608392515737358372, 1, 1523706973259923456, NULL, 1608392444123811840, 0, NULL, 1608392472255008768, NULL, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_field_union` VALUES (1608392516689465352, 0, 1527563081086976000, 1523704529738104832, 1608392507768180736, 0, 1523690294341632000, 1608392509865332736, NULL, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_field_union` VALUES (1608392516689465356, 0, 1527563081086976000, 1527570189740195840, 1608392507768180736, 0, 1523690294387769344, 1608392509865332736, NULL, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_field_union` VALUES (1608392516689465358, 0, 1527563081086976000, 1523704529989763072, 1608392507768180736, 0, 1523696647688781824, 1608392509865332736, NULL, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_field_union` VALUES (1608392516689465360, 0, 1527563081086976000, 1523704530035900416, 1608392507768180736, 0, 1523696647734919168, 1608392509865332736, NULL, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_field_union` VALUES (1608392516689465363, 0, 1527563081086976000, 1523704530140758016, 1608392507768180736, 0, 1523696647835582464, 1608392509865332736, NULL, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_field_union` VALUES (1608392516689465365, 0, 1527563081086976000, 1523704530191089664, 1608392507768180736, 0, 1523696647881719808, 1608392509865332736, NULL, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_field_union` VALUES (1608392516689465367, 0, 1527563081086976000, 1527573799052034048, 1608392507768180736, 0, 1527551818508320768, 1608392509865332736, NULL, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_field_union` VALUES (1608392516689465369, 1, 1527563081086976000, NULL, 1608392507768180736, 0, NULL, 1608392509865332736, NULL, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_field_union` VALUES (1608392516689465370, 1, 1547842611416449024, NULL, 1608392507768180736, 0, NULL, 1608392472255008768, NULL, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_field_union` VALUES (1608392516689465384, 0, 1547099620317929472, 1522622016793968640, 1608392498486185984, 0, 1547827576711487488, 1608392513568903168, NULL, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_field_union` VALUES (1608392516689465387, 0, 1547099620317929472, 1522622016944963584, 1608392498486185984, 0, 1547827576753430528, 1608392513568903168, NULL, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_field_union` VALUES (1608392516689465389, 0, 1547099620317929472, 1523903289730580480, 1608392498486185984, 0, 1547827576799567872, 1608392513568903168, NULL, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_field_union` VALUES (1608392516689465391, 1, 1547822901417287680, NULL, 1608392204931043328, 0, NULL, 1608392472255008768, NULL, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_field_union` VALUES (1608392516689465392, 0, 1547099620317929472, 1522622016840105984, 1608392498486185984, 0, 1547827576845705216, 1608392513568903168, NULL, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_field_union` VALUES (1608392517062758401, 1, 1547822901371150336, NULL, 1608392204931043328, 0, NULL, 1608392513568903168, NULL, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_field_union` VALUES (1608392517066952706, 1, 1547099620317929472, NULL, 1608392498486185984, 0, NULL, 1608392513568903168, NULL, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_field_union` VALUES (1608392517863870465, 0, 1522635492660707328, 1522635492790730752, 1608392505129963520, 0, 1522622016793968640, 1608392498486185984, NULL, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_field_union` VALUES (1608392517926785025, 0, 1522635492660707328, 1522635492832673792, 1608392505129963520, 0, 1522622016840105984, 1608392498486185984, NULL, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_field_union` VALUES (1608392517926785026, 0, 1522635492660707328, 1522635492962697216, 1608392505129963520, 0, 1522622016991100928, 1608392498486185984, NULL, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_field_union` VALUES (1608392517926785027, 0, 1522635492660707328, 1523903534505967616, 1608392505129963520, 0, 1523903289730580480, 1608392498486185984, NULL, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_field_union` VALUES (1608392517926785028, 0, 1522635492660707328, 1523903534577270784, 1608392505129963520, 0, 1523903289806077952, 1608392498486185984, NULL, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_field_union` VALUES (1608392517926785029, 0, 1522635492660707328, 1522635492920754176, 1608392505129963520, 0, 1522622016944963584, 1608392498486185984, NULL, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_field_union` VALUES (1608392517989699585, 0, 1522635492660707328, 1522635492744593408, 1608392505129963520, 0, 1547099620317929472, 1608392498486185984, NULL, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_field_union` VALUES (1608392517989699586, 1, 1522635492660707328, NULL, 1608392505129963520, 0, NULL, 1608392498486185984, NULL, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_field_union` VALUES (1608392517989699587, 1, 1528919053848096768, NULL, 1608392505129963520, 0, NULL, 1608392472255008768, NULL, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_field_union` VALUES (1608392518186831877, 0, 1527551818508320768, 1523696647688781824, 1608392509865332736, 0, 1547827576711487488, 1608392513568903168, NULL, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_field_union` VALUES (1608392518186831879, 0, 1527551818508320768, 1523696647835582464, 1608392509865332736, 0, 1547827576753430528, 1608392513568903168, NULL, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_field_union` VALUES (1608392518253940737, 0, 1527551818508320768, 1523696647734919168, 1608392509865332736, 0, 1547827576845705216, 1608392513568903168, NULL, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_field_union` VALUES (1608392518253940738, 0, 1527551818508320768, 1547833333272092672, 1608392509865332736, 0, 1547827576799567872, 1608392513568903168, NULL, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_field_union` VALUES (1608392518253940740, 1, 1527551818508320768, NULL, 1608392509865332736, 0, NULL, 1608392513568903168, NULL, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_field_union` VALUES (1608392518253940742, 1, 1547831583253274624, NULL, 1608392509865332736, 0, NULL, 1608392472255008768, NULL, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_field_union` VALUES (1608439787783290880, 0, 1531185823434715136, 1531185823489241088, 1608439433763061760, 0, 1531113641140670464, 1608439774743199744, NULL, '2022-12-29 20:28:23', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439787783290881, 1, 1534102235916599296, NULL, 1608439746549088256, 0, NULL, 1608439763607322624, NULL, '2022-12-29 20:28:23', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439787783290882, 1, 1531209525480173568, NULL, 1608439761141071872, 0, NULL, 1608439763607322624, NULL, '2022-12-29 20:28:23', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439787783290883, 0, 1532171630933041152, 1532171631033704448, 1608439765649948672, 0, 1531094470646419456, 1608439777943453696, NULL, '2022-12-29 20:28:23', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439787783290884, 0, 1531221281732603904, 1531221281791324160, 1608439767700963328, 0, 1531094470646419456, 1608439777943453696, NULL, '2022-12-29 20:28:23', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439787795873792, 0, 1532176550532816896, 1532174514697396224, 1608439761891852288, 0, 1531113641182613504, 1608439774743199744, NULL, '2022-12-29 20:28:23', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439787795873793, 0, 1531220096699117568, 1531220096795586560, 1608439746549088256, 0, 1531094470646419456, 1608439777943453696, NULL, '2022-12-29 20:28:23', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439787800068096, 0, 1531185823434715136, 1532228759043776512, 1608439433763061760, 0, 1532228091981668352, 1608439774743199744, NULL, '2022-12-29 20:28:23', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439787804262400, 1, 1531221281732603904, NULL, 1608439767700963328, 0, NULL, 1608439777943453696, NULL, '2022-12-29 20:28:23', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439787812651008, 0, 1531220096699117568, 1532252179328475136, 1608439746549088256, 0, 1531094471850184704, 1608439777943453696, NULL, '2022-12-29 20:28:23', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439787812651009, 0, 1532176550532816896, 1532176549903671296, 1608439761891852288, 0, 1532228091981668352, 1608439774743199744, NULL, '2022-12-29 20:28:23', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439787812651010, 0, 1531185823434715136, 1534083811756802048, 1608439433763061760, 0, 1534082233297608704, 1608439774743199744, NULL, '2022-12-29 20:28:23', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439787816845312, 1, 1532171630933041152, NULL, 1608439765649948672, 0, NULL, 1608439777943453696, NULL, '2022-12-29 20:28:23', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439787825233920, 1, 1531220096699117568, NULL, 1608439746549088256, 0, NULL, 1608439777943453696, NULL, '2022-12-29 20:28:23', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439787829428224, 0, 1532176550532816896, 1532174514651258880, 1608439761891852288, 0, 1532199205797437440, 1608439774743199744, NULL, '2022-12-29 20:28:23', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439787850399744, 0, 1531185823434715136, 1547094601317294080, 1608439433763061760, 0, 1532199205797437440, 1608439774743199744, NULL, '2022-12-29 20:28:23', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439787850399745, 1, 1532176550532816896, NULL, 1608439761891852288, 0, NULL, 1608439774743199744, NULL, '2022-12-29 20:28:23', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439787871371264, 1, 1531185823434715136, NULL, 1608439433763061760, 0, NULL, 1608439774743199744, NULL, '2022-12-29 20:28:23', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439787875565568, 1, 1532176549903671296, NULL, 1608439761891852288, 0, NULL, 1608439771190624256, NULL, '2022-12-29 20:28:23', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439787875565569, 1, 1531192798050562048, NULL, 1608439764727201792, 0, NULL, 1608439777943453696, NULL, '2022-12-29 20:28:23', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439787888148480, 0, 1532174514651258880, 1532174514697396224, 1608439761891852288, 0, 1531094470646419456, 1608439777943453696, NULL, '2022-12-29 20:28:23', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439787892342784, 0, 1531192798000230400, 1531192798050562048, 1608439764727201792, 0, 1532199205797437440, 1608439774743199744, NULL, '2022-12-29 20:28:23', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439787900731392, 0, 1532173423880560640, 1532173423930892288, 1608439768413995008, 0, 1531094470646419456, 1608439777943453696, NULL, '2022-12-29 20:28:23', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439787913314304, 1, 1532173423880560640, NULL, 1608439768413995008, 0, NULL, 1608439777943453696, NULL, '2022-12-29 20:28:23', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439787917508608, 1, 1531192798000230400, NULL, 1608439764727201792, 0, NULL, 1608439774743199744, NULL, '2022-12-29 20:28:23', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439787946868736, 1, 1532174514651258880, NULL, 1608439761891852288, 0, NULL, 1608439777943453696, NULL, '2022-12-29 20:28:23', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439792145367040, 1, 1532199205797437440, NULL, 1608439774743199744, 0, NULL, 1608439777943453696, NULL, '2022-12-29 20:28:24', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439792157949952, 1, 1532228091981668352, NULL, 1608439774743199744, 0, NULL, 1608439771190624256, NULL, '2022-12-29 20:28:24', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439792959062016, 1, 1531111169563152384, NULL, 1608439771190624256, 0, NULL, 1608439776207011840, NULL, '2022-12-29 20:28:25', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439794217353216, 0, 1532178371364069376, 1532178371414401024, 1608439776668385280, 0, 1531094470646419456, 1608439777943453696, NULL, '2022-12-29 20:28:25', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439794221547520, 0, 1531929289777340416, 1531929289878003712, 1608439769529679872, 0, 1531221281732603904, 1608439767700963328, NULL, '2022-12-29 20:28:25', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439794234130432, 0, 1532178371364069376, 1532178371464732672, 1608439776668385280, 0, 1531094470692556800, 1608439777943453696, NULL, '2022-12-29 20:28:25', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439794242519040, 1, 1531929289777340416, NULL, 1608439769529679872, 0, NULL, 1608439767700963328, NULL, '2022-12-29 20:28:25', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439794246713344, 1, 1532178371364069376, NULL, 1608439776668385280, 0, NULL, 1608439777943453696, NULL, '2022-12-29 20:28:25', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439794259296256, 0, 1531929290104496128, 1531929290154827776, 1608439769529679872, 0, 1531101048313335808, 1608439767134732288, NULL, '2022-12-29 20:28:25', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439794259296258, 1, 1532178371515064320, NULL, 1608439776668385280, 0, NULL, 1608439771190624256, NULL, '2022-12-29 20:28:25', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439794271879168, 0, 1531929290104496128, 1531929290318405632, 1608439769529679872, 0, 1531101048472719360, 1608439767134732288, NULL, '2022-12-29 20:28:25', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439794292850688, 0, 1531929290104496128, 1531929290364542976, 1608439769529679872, 0, 1531101048527245312, 1608439767134732288, NULL, '2022-12-29 20:28:25', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439794309627904, 0, 1531929290104496128, 1532256280493760512, 1608439769529679872, 0, 1531101048422387712, 1608439767134732288, NULL, '2022-12-29 20:28:25', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439794322210816, 1, 1531929290104496128, NULL, 1608439769529679872, 0, NULL, 1608439767134732288, NULL, '2022-12-29 20:28:25', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439794338988032, 1, 1531929289878003712, NULL, 1608439769529679872, 0, NULL, 1608439777943453696, NULL, '2022-12-29 20:28:25', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439795349815296, 1, 1531094842077204480, NULL, 1608439777943453696, 0, NULL, 1608439763607322624, NULL, '2022-12-29 20:28:25', 1);
INSERT INTO `wk_module_field_union` VALUES (1608439795358203904, 1, 1531094470692556800, NULL, 1608439777943453696, 0, NULL, 1608439776207011840, NULL, '2022-12-29 20:28:25', 1);

-- ----------------------------
-- Table structure for wk_module_field_union_condition
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_field_union_condition`;
CREATE TABLE `wk_module_field_union_condition`  (
  `id` bigint(20) NOT NULL,
  `model` int(11) NOT NULL COMMENT '模式：0 简单 1 高级',
  `type` int(11) NULL DEFAULT NULL COMMENT '类型：0 自定义 1 匹配字段',
  `search` json NULL COMMENT '筛选条件',
  `group_id` int(11) NOT NULL COMMENT '分组ID',
  `target_module_id` bigint(20) NOT NULL COMMENT '目标模块ID',
  `relate_field_id` bigint(20) NOT NULL COMMENT '数据关联字段ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建人',
  `module_id` bigint(20) NOT NULL COMMENT '当前模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据关联筛选条件表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_field_union_condition
-- ----------------------------
INSERT INTO `wk_module_field_union_condition` VALUES (1608392516689465375, 0, 0, '{\"type\": 1, \"values\": [\"1\"], \"formType\": \"text\", \"fieldName\": \"flowStatus\", \"searchEnum\": \"IS\"}', 1, 1608392509865332736, 1527563081086976000, '2022-12-29 17:20:33', 0, 1608392507768180736, 0);
INSERT INTO `wk_module_field_union_condition` VALUES (1608392517066952711, 0, 1, '{\"type\": 1, \"values\": [], \"formType\": \"text\", \"fieldName\": \"fieldMhlqon\", \"searchEnum\": \"IS\", \"currentFieldId\": 1547822901371150336, \"tempCurrentFieldId\": \"1547822901371150336\"}', 1, 1608392444123811840, 1547825385271533568, '2022-12-29 17:20:33', 0, 1608392204931043328, 0);
INSERT INTO `wk_module_field_union_condition` VALUES (1608392517066952713, 0, 1, '{\"type\": 3, \"values\": [], \"formType\": \"data_union\", \"fieldName\": \"fieldYyoira\", \"searchEnum\": \"CONTAINS\", \"currentFieldId\": 1547822901417287680, \"tempCurrentFieldId\": \"1547822901417287680\"}', 1, 1608392444123811840, 1547825385271533568, '2022-12-29 17:20:33', 0, 1608392204931043328, 0);
INSERT INTO `wk_module_field_union_condition` VALUES (1608392517066952716, 0, 1, '{\"type\": 1, \"values\": [], \"formType\": \"text\", \"fieldName\": \"fieldMhlqon\", \"searchEnum\": \"IS\", \"currentFieldId\": 1547822901371150336, \"tempCurrentFieldId\": \"1547822901371150336\"}', 1, 1608392444123811840, 1547825385368002560, '2022-12-29 17:20:33', 0, 1608392204931043328, 0);
INSERT INTO `wk_module_field_union_condition` VALUES (1608392517066952718, 0, 1, '{\"type\": 3, \"values\": [], \"formType\": \"data_union\", \"fieldName\": \"fieldYyoira\", \"searchEnum\": \"CONTAINS\", \"currentFieldId\": 1547822901417287680, \"tempCurrentFieldId\": \"1547822901417287680\"}', 1, 1608392444123811840, 1547825385368002560, '2022-12-29 17:20:33', 0, 1608392204931043328, 0);
INSERT INTO `wk_module_field_union_condition` VALUES (1608392517066952719, 0, 1, '{\"type\": 1, \"values\": [], \"formType\": \"text\", \"fieldName\": \"fieldMhlqon\", \"searchEnum\": \"IS\", \"currentFieldId\": 1547822901371150336, \"tempCurrentFieldId\": \"1547822901371150336\"}', 1, 1608392444123811840, 1547825385321865216, '2022-12-29 17:20:33', 0, 1608392204931043328, 0);
INSERT INTO `wk_module_field_union_condition` VALUES (1608392517066952722, 0, 1, '{\"type\": 3, \"values\": [], \"formType\": \"data_union\", \"fieldName\": \"fieldYyoira\", \"searchEnum\": \"CONTAINS\", \"currentFieldId\": 1547822901417287680, \"tempCurrentFieldId\": \"1547822901417287680\"}', 1, 1608392444123811840, 1547825385321865216, '2022-12-29 17:20:33', 0, 1608392204931043328, 0);
INSERT INTO `wk_module_field_union_condition` VALUES (1608392518056808450, 0, 0, '{\"type\": 1, \"values\": [\"1\"], \"formType\": \"text\", \"fieldName\": \"flowStatus\", \"searchEnum\": \"IS\"}', 1, 1608392498486185984, 1522635492660707328, '2022-12-29 17:20:33', 0, 1608392505129963520, 0);
INSERT INTO `wk_module_field_union_condition` VALUES (1608439788496322560, 0, 0, '{\"type\": 1, \"values\": [\"1\"], \"formType\": \"text\", \"fieldName\": \"flowStatus\", \"searchEnum\": \"IS\"}', 1, 1608439774743199744, 1532176550532816896, '2022-12-29 20:28:23', 1, 1608439761891852288, 0);
INSERT INTO `wk_module_field_union_condition` VALUES (1608439788525682688, 0, 0, '{\"type\": 1, \"values\": [\"1\"], \"formType\": \"text\", \"fieldName\": \"flowStatus\", \"searchEnum\": \"IS\"}', 1, 1608439774743199744, 1531185823434715136, '2022-12-29 20:28:23', 1, 1608439433763061760, 0);
INSERT INTO `wk_module_field_union_condition` VALUES (1608439788529876992, 0, 0, '{\"type\": 1, \"values\": [\"1\"], \"formType\": \"text\", \"fieldName\": \"flowStatus\", \"searchEnum\": \"IS\"}', 1, 1608439774743199744, 1531192798000230400, '2022-12-29 20:28:23', 1, 1608439764727201792, 0);
INSERT INTO `wk_module_field_union_condition` VALUES (1608439792195698688, 0, 1, '{\"type\": 1, \"values\": [], \"formType\": \"select\", \"fieldName\": \"fieldWisysx\", \"searchEnum\": \"IS\", \"currentFieldId\": 1531184225996288000, \"tempCurrentFieldId\": \"1531184225996288000\"}', 1, 1608439777943453696, 1532199205797437440, '2022-12-29 20:28:24', 1, 1608439774743199744, 0);
INSERT INTO `wk_module_field_union_condition` VALUES (1608439792208281600, 0, 0, '{\"type\": 1, \"values\": [\"{\\\"key\\\":\\\"1531086705781755904-1\\\",\\\"value\\\":\\\"可用\\\",\\\"type\\\":0}\"], \"formType\": \"select\", \"fieldName\": \"fieldCvwjri\", \"searchEnum\": \"IS\"}', 1, 1608439777943453696, 1532199205797437440, '2022-12-29 20:28:24', 1, 1608439774743199744, 0);
INSERT INTO `wk_module_field_union_condition` VALUES (1608439792220864513, 0, 0, '{\"type\": 1, \"values\": [\"{\\\"key\\\":\\\"1531101362701586432-1\\\",\\\"value\\\":\\\"空闲\\\",\\\"type\\\":0}\"], \"formType\": \"select\", \"fieldName\": \"fieldXgxxqa\", \"searchEnum\": \"IS\"}', 1, 1608439771190624256, 1532228091981668352, '2022-12-29 20:28:24', 1, 1608439774743199744, 0);
INSERT INTO `wk_module_field_union_condition` VALUES (1608439794401902592, 0, 0, '{\"type\": 1, \"values\": [\"1\"], \"formType\": \"text\", \"fieldName\": \"flowStatus\", \"searchEnum\": \"IS\"}', 1, 1608439767700963328, 1531929289777340416, '2022-12-29 20:28:25', 1, 1608439769529679872, 0);

-- ----------------------------
-- Table structure for wk_module_file
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_file`;
CREATE TABLE `wk_module_file`  (
  `id` bigint(20) NOT NULL,
  `data_id` bigint(20) NOT NULL COMMENT '数据ID',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `batch_id` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件批次ID',
  `create_user_id` bigint(20) NOT NULL,
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
  `id` bigint(20) NOT NULL COMMENT '主键-模块分组的标题的id',
  `application_id` bigint(20) NOT NULL COMMENT '应用id',
  `group_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分组标题的名称',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
  `icon_color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标颜色',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人id',
  `update_user_id` bigint(20) NULL DEFAULT NULL COMMENT '更新人id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_module_group_application_id_index`(`application_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '模块分组表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_group
-- ----------------------------
INSERT INTO `wk_module_group` VALUES (1608392204880711680, 1608392204842962944, '库存信息', 'wk wk-schedule', '#0052CC', '2022-12-29 17:19:19', '2022-12-29 17:19:19', 1, 1);
INSERT INTO `wk_module_group` VALUES (1608392204880711681, 1608392204842962944, '基础设置', 'wk wk-manage', '#0052CC', '2022-12-29 17:19:19', '2022-12-29 17:19:19', 1, 1);
INSERT INTO `wk_module_group` VALUES (1608439433247162368, 1608439432936783872, '基础设置', 'wk wk-icon-all-line', '#0052CC', '2022-12-29 20:26:59', '2022-12-29 20:26:59', 1, 1);
INSERT INTO `wk_module_group` VALUES (1608439433255550976, 1608439432936783872, '用车管理', 'wk wk-approval-13', '#0052CC', '2022-12-29 20:26:59', '2022-12-29 20:26:59', 1, 1);
INSERT INTO `wk_module_group` VALUES (1608439433255550977, 1608439432936783872, '加油管理', 'wk wk-invoice', '#0052CC', '2022-12-29 20:26:59', '2022-12-29 20:26:59', 1, 1);
INSERT INTO `wk_module_group` VALUES (1608439433255550978, 1608439432936783872, '维保管理', 'wk wk-icon-form', '#0052CC', '2022-12-29 20:26:59', '2022-12-29 20:26:59', 1, 1);
INSERT INTO `wk_module_group` VALUES (1608439433255550979, 1608439432936783872, '年检保险', 'wk wk-icon-all-line', '#0052CC', '2022-12-29 20:26:59', '2022-12-29 20:26:59', 1, 1);
INSERT INTO `wk_module_group` VALUES (1608439433255550980, 1608439432936783872, '违章事故', 'wk wk-approval-18', '#0052CC', '2022-12-29 20:26:59', '2022-12-29 20:26:59', 1, 1);

-- ----------------------------
-- Table structure for wk_module_group_sort
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_group_sort`;
CREATE TABLE `wk_module_group_sort`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `group_id` bigint(20) NULL DEFAULT NULL COMMENT '分组id',
  `module_id` bigint(20) NULL DEFAULT NULL COMMENT '模块id',
  `sort` int(11) NULL DEFAULT NULL COMMENT '分组、模块全部在一起的排序',
  `application_id` bigint(20) NULL DEFAULT NULL COMMENT '应用id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建者',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '分组与模块一起的排序表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_group_sort
-- ----------------------------
INSERT INTO `wk_module_group_sort` VALUES (1608392514613284865, NULL, 1608392498486185984, 0, 1608392204842962944, '2022-12-29 17:20:33', NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_group_sort` VALUES (1608392514613284866, NULL, 1608392505129963520, 1, 1608392204842962944, '2022-12-29 17:20:33', NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_group_sort` VALUES (1608392514613284867, NULL, 1608392509865332736, 2, 1608392204842962944, '2022-12-29 17:20:33', NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_group_sort` VALUES (1608392514701365249, NULL, 1608392507768180736, 3, 1608392204842962944, '2022-12-29 17:20:33', NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_group_sort` VALUES (1608392514701365250, 1608392204880711680, NULL, 4, 1608392204842962944, '2022-12-29 17:20:33', NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_group_sort` VALUES (1608392514734919681, 1608392204880711680, 1608392444123811840, 1, 1608392204842962944, '2022-12-29 17:20:33', NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_group_sort` VALUES (1608392514734919682, 1608392204880711680, 1608392204931043328, 2, 1608392204842962944, '2022-12-29 17:20:33', NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_group_sort` VALUES (1608392514797834242, 1608392204880711681, NULL, 7, 1608392204842962944, '2022-12-29 17:20:33', NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_group_sort` VALUES (1608392514797834243, 1608392204880711681, 1608392472255008768, 1, 1608392204842962944, '2022-12-29 17:20:33', NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_group_sort` VALUES (1608392514797834244, 1608392204880711681, 1608392513568903168, 2, 1608392204842962944, '2022-12-29 17:20:33', NULL, '2022-12-29 17:20:33');
INSERT INTO `wk_module_group_sort` VALUES (1608439783379271680, 1608439433247162368, NULL, 0, 1608439432936783872, '2022-12-29 20:28:22', 1, '2022-12-29 20:28:22');
INSERT INTO `wk_module_group_sort` VALUES (1608439783396048896, 1608439433247162368, 1608439776207011840, 0, 1608439432936783872, '2022-12-29 20:28:22', 1, '2022-12-29 20:28:22');
INSERT INTO `wk_module_group_sort` VALUES (1608439783412826112, 1608439433247162368, 1608439763607322624, 1, 1608439432936783872, '2022-12-29 20:28:22', 1, '2022-12-29 20:28:22');
INSERT INTO `wk_module_group_sort` VALUES (1608439783425409024, 1608439433247162368, 1608439777943453696, 2, 1608439432936783872, '2022-12-29 20:28:22', 1, '2022-12-29 20:28:22');
INSERT INTO `wk_module_group_sort` VALUES (1608439783437991936, 1608439433247162368, 1608439767134732288, 3, 1608439432936783872, '2022-12-29 20:28:22', 1, '2022-12-29 20:28:22');
INSERT INTO `wk_module_group_sort` VALUES (1608439783454769152, 1608439433247162368, 1608439771190624256, 4, 1608439432936783872, '2022-12-29 20:28:22', 1, '2022-12-29 20:28:22');
INSERT INTO `wk_module_group_sort` VALUES (1608439783479934976, 1608439433255550976, NULL, 1, 1608439432936783872, '2022-12-29 20:28:22', 1, '2022-12-29 20:28:22');
INSERT INTO `wk_module_group_sort` VALUES (1608439783492517888, 1608439433255550976, 1608439774743199744, 1, 1608439432936783872, '2022-12-29 20:28:22', 1, '2022-12-29 20:28:22');
INSERT INTO `wk_module_group_sort` VALUES (1608439783505100800, 1608439433255550976, 1608439433763061760, 2, 1608439432936783872, '2022-12-29 20:28:22', 1, '2022-12-29 20:28:22');
INSERT INTO `wk_module_group_sort` VALUES (1608439783517683712, 1608439433255550976, 1608439764727201792, 3, 1608439432936783872, '2022-12-29 20:28:22', 1, '2022-12-29 20:28:22');
INSERT INTO `wk_module_group_sort` VALUES (1608439783534460928, 1608439433255550977, NULL, 2, 1608439432936783872, '2022-12-29 20:28:22', 1, '2022-12-29 20:28:22');
INSERT INTO `wk_module_group_sort` VALUES (1608439783547043840, 1608439433255550977, 1608439761141071872, 1, 1608439432936783872, '2022-12-29 20:28:22', 1, '2022-12-29 20:28:22');
INSERT INTO `wk_module_group_sort` VALUES (1608439783559626752, 1608439433255550977, 1608439746549088256, 2, 1608439432936783872, '2022-12-29 20:28:22', 1, '2022-12-29 20:28:22');
INSERT INTO `wk_module_group_sort` VALUES (1608439783576403968, 1608439433255550978, NULL, 3, 1608439432936783872, '2022-12-29 20:28:22', 1, '2022-12-29 20:28:22');
INSERT INTO `wk_module_group_sort` VALUES (1608439783588986880, 1608439433255550978, 1608439767700963328, 1, 1608439432936783872, '2022-12-29 20:28:22', 1, '2022-12-29 20:28:22');
INSERT INTO `wk_module_group_sort` VALUES (1608439783643512832, 1608439433255550978, 1608439769529679872, 2, 1608439432936783872, '2022-12-29 20:28:22', 1, '2022-12-29 20:28:22');
INSERT INTO `wk_module_group_sort` VALUES (1608439783660290048, 1608439433255550979, NULL, 4, 1608439432936783872, '2022-12-29 20:28:22', 1, '2022-12-29 20:28:22');
INSERT INTO `wk_module_group_sort` VALUES (1608439783672872960, 1608439433255550979, 1608439765649948672, 1, 1608439432936783872, '2022-12-29 20:28:22', 1, '2022-12-29 20:28:22');
INSERT INTO `wk_module_group_sort` VALUES (1608439783685455872, 1608439433255550979, 1608439768413995008, 2, 1608439432936783872, '2022-12-29 20:28:22', 1, '2022-12-29 20:28:22');
INSERT INTO `wk_module_group_sort` VALUES (1608439783702233088, 1608439433255550980, NULL, 5, 1608439432936783872, '2022-12-29 20:28:22', 1, '2022-12-29 20:28:22');
INSERT INTO `wk_module_group_sort` VALUES (1608439783719010304, 1608439433255550980, 1608439761891852288, 1, 1608439432936783872, '2022-12-29 20:28:22', 1, '2022-12-29 20:28:22');
INSERT INTO `wk_module_group_sort` VALUES (1608439783735787520, 1608439433255550980, 1608439776668385280, 2, 1608439432936783872, '2022-12-29 20:28:22', 1, '2022-12-29 20:28:22');

-- ----------------------------
-- Table structure for wk_module_layout
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_layout`;
CREATE TABLE `wk_module_layout`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `module_id` bigint(20) NOT NULL COMMENT '模块Id',
  `version` int(11) NOT NULL COMMENT '版本号',
  `data` json NOT NULL COMMENT '布局数据',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建人',
  `update_user_id` bigint(20) NOT NULL COMMENT '修改人',
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
  `application_id` bigint(20) NOT NULL COMMENT '应用ID',
  `source_id` bigint(20) NULL DEFAULT NULL COMMENT '源应用 ID',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '应用名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '应用简介',
  `detail` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '应用详情描述',
  `big_picture` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '应用大图',
  `key_point` json NULL COMMENT '要点',
  `preview` json NULL COMMENT '预览',
  `is_featured` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否精选',
  `main_picture` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '主图',
  `detail_picture` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '详情图',
  `type` int(11) NOT NULL DEFAULT 0 COMMENT '0 自定义\r\n1 安装\r\n2 导入\r\n3 自用系统\r\n4 其他',
  `relate_application_id` bigint(20) NULL DEFAULT NULL COMMENT '关联的应用 ID',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
  `icon_color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标颜色',
  `status` int(10) UNSIGNED NOT NULL COMMENT '状态 1 正常 2 禁用',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`application_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '应用表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_metadata
-- ----------------------------
INSERT INTO `wk_module_metadata` VALUES (1608392204842962944, 1547891158883065856, '办公用品管理', '办公用品采购、入库、领用、归还等一站式办公用品管理，解决物资领用效率低，成本把控难的问题。', '【从产生采购需求、进行采购审批到采购完成入库的全流程管理】\n办公用品管理系统以行政人员实际业务场景为核心，按需采购办公用品，可发起采购申请单，包含申请时间、申请人、用途、采购物品明细、预估金额等，办公用品采购明细清晰可见，物品预估金额一键计算，企业精准把控物资成本；物品采购完成后签收入库，通过关联采购申请单，精准把控物资采购进度，洞悉物资入库明细。\n\n【规范办公物品领用消耗，物品归还状况实时查看】\n可批量申请领用资产，提交领用审批，通过规范化管理办公用品领用流程，多维度精细化管理，极大提升用户物资领用体验。耐用品归还时关联物资领用单，企业精确跟进归还进度，提高物资使用效率，降低企业运营成本，打造一体化资产生命周期管理。\n\n办公用品管理系统实现了办公用品采购、入库、领用、归还等一站式办公用品管理，降低企业运营成本，解决企业物资领用效率低，成本把控难的问题。\n\n', NULL, NULL, NULL, 0, NULL, '[\"https://file.72crm.com/20220717/1548616921835991040-1.png\",\"https://file.72crm.com/20220717/1548616932451774464-2.1.png\",\"https://file.72crm.com/20220717/1548616947412856832-2.2.png\",\"https://file.72crm.com/20220717/1548616957424660480-2.3.png\",\"https://file.72crm.com/20220717/1548616967545516032-2.4.png\",\"https://file.72crm.com/20220717/1548616977477627904-2.5.png\"]', 2, NULL, 'wk wk-icon-business-opportunity', '#EBECF0', 1, '2022-12-29 17:19:19');
INSERT INTO `wk_module_metadata` VALUES (1608439432936783872, 1547891195096686592, '车辆管理', '规范车辆流程管理，帮助车队降低车辆空置率，显著提升企业车辆管理水平和经济效益。', '1、基础设置\n将车辆信息、驾驶员信息、油卡信息、配件信息录入系统进行统一管理。\n2、用车管理\n用车管理包括对用车申请、还车登记以及用车期间产生的费用进行综合管理，当外出人员需要用车时，需要在系统上填写用车申请单，填写用车相关信息并交由车辆管理人员进行审批；归还车辆时车辆管理人员需进行车辆检查，并将车辆行驶里程情况录入系统。\n3、加油管理\n管理油卡充值和汽车加油记录。\n4、维保管理\n车辆维修保养时，需要在系统上录入维保申请单，维保结束后，也需要登记取车记录。\n5、年检保险\n需要定期安排车辆检修，结束后录入系统，并按实际情况对车辆购买保险，并进行年检。\n6、违章事故\n管理车辆违章、事故的记录。\n\n', NULL, NULL, NULL, 0, NULL, '[\"https://file.72crm.com/20220717/1548617058142482432-1.png\",\"https://file.72crm.com/20220717/1548617068808597504-3.1.png\",\"https://file.72crm.com/20220717/1548617080808501248-3.2.png\",\"https://file.72crm.com/20220717/1548617091248123904-3.3.png\"]', 2, NULL, 'wk wk-approval-13', '#0052CC', 1, '2022-12-29 20:26:59');

-- ----------------------------
-- Table structure for wk_module_print_record
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_print_record`;
CREATE TABLE `wk_module_print_record`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `template_id` bigint(20) NOT NULL COMMENT '模板id',
  `record_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '打印内容',
  `data_id` bigint(20) NOT NULL COMMENT '数据ID',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建人id',
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
  `id` bigint(20) NOT NULL COMMENT '主键',
  `template_id` bigint(20) NULL DEFAULT NULL COMMENT '模板id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板名称',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '模板内容',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建人id',
  `update_user_id` bigint(20) NULL DEFAULT NULL COMMENT '修改人id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_module_print_template_module_id_version_index`(`module_id`, `version`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '打印模板' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_print_template
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_publish_record
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_publish_record`;
CREATE TABLE `wk_module_publish_record`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `application_id` bigint(20) NOT NULL COMMENT '应用ID',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '模块版本号',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_module_publish_record_application_id_index`(`application_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '模块发布记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_publish_record
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_role
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_role`;
CREATE TABLE `wk_module_role`  (
  `role_id` bigint(20) NOT NULL,
  `role_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `application_id` bigint(20) NOT NULL COMMENT '应用ID',
  `range_type` int(11) NOT NULL DEFAULT 1 COMMENT '1-本人 2-本人及下属 3-本部门 4-本部门及下属部门 5-全部',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '1 启用 0 禁用',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建人',
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
  `id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL COMMENT '角色id',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `field_id` bigint(20) NULL DEFAULT NULL COMMENT '字段id',
  `auth_level` int(2) NOT NULL COMMENT '权限 1不可编辑不可查看 2可查看不可编辑 3可编辑可查看',
  `operate_type` int(2) NOT NULL COMMENT '操作权限 1都可以设置 2只有查看权限可设置 3只有编辑权限可设置 4都不能设置',
  `mask_type` int(1) NULL DEFAULT 0 COMMENT '掩码类型 0 都不隐藏 1 列表隐藏详情不隐藏 2 都隐藏',
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
  `id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `category_id` bigint(20) NULL DEFAULT NULL COMMENT '分类ID',
  `auth` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '权限\n1: \'index\', // 查看列表\n2: \'read\', // 查看详情\n3: \'save\', // 新建\n4: \'edit\', // 编辑\n5: \'transfer\', // 转移\n6: \'delete\', // 删除\n7: \'import\', // 导入\n8: \'export\', // 导出\n9: \'print\', // 打印\n10: \'moveCategory\', // 转移到分类\n11: \'teamSave\' // 编辑团队成员',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建人',
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
  `id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户',
  `application_id` bigint(20) NOT NULL COMMENT '应用ID',
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
  `scene_id` bigint(20) NOT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '场景名称',
  `module_id` bigint(20) NULL DEFAULT NULL,
  `data` json NULL,
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `sort` int(11) NOT NULL DEFAULT 999 COMMENT '排序ID',
  `is_hide` int(11) NOT NULL DEFAULT 0 COMMENT '1隐藏',
  `is_system` int(11) NOT NULL DEFAULT 0 COMMENT '1全部 2 我负责的 3 我下属负责的 0 自定义',
  `is_default` int(11) NULL DEFAULT 0 COMMENT '是否默认 0 否 1是',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`scene_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '模块场景表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_scene
-- ----------------------------
INSERT INTO `wk_module_scene` VALUES (1608350235966148610, '全部%s', 1608031970215678000, NULL, 1, 1, 0, 1, 1, '2022-12-29 14:32:32', '2022-12-29 14:32:32');
INSERT INTO `wk_module_scene` VALUES (1608350235966148611, '我负责的%s', 1608031970215678000, NULL, 1, 2, 0, 2, 0, '2022-12-29 14:32:32', '2022-12-29 14:32:32');
INSERT INTO `wk_module_scene` VALUES (1608350235966148612, '下属负责的%s', 1608031970215678000, NULL, 1, 3, 0, 3, 0, '2022-12-29 14:32:32', '2022-12-29 14:32:32');
INSERT INTO `wk_module_scene` VALUES (1608395314005094402, '全部%s', 1608392204931043328, NULL, 1, 1, 0, 1, 1, '2022-12-29 17:31:40', '2022-12-29 17:31:40');
INSERT INTO `wk_module_scene` VALUES (1608395314005094403, '我负责的%s', 1608392204931043328, NULL, 1, 2, 0, 2, 0, '2022-12-29 17:31:40', '2022-12-29 17:31:40');
INSERT INTO `wk_module_scene` VALUES (1608395314038648834, '下属负责的%s', 1608392204931043328, NULL, 1, 3, 0, 3, 0, '2022-12-29 17:31:40', '2022-12-29 17:31:40');
INSERT INTO `wk_module_scene` VALUES (1608395398201552897, '全部%s', 1608392513568903168, NULL, 1, 1, 0, 1, 1, '2022-12-29 17:32:00', '2022-12-29 17:32:00');
INSERT INTO `wk_module_scene` VALUES (1608395398201552898, '我负责的%s', 1608392513568903168, NULL, 1, 2, 0, 2, 0, '2022-12-29 17:32:00', '2022-12-29 17:32:00');
INSERT INTO `wk_module_scene` VALUES (1608395398201552899, '下属负责的%s', 1608392513568903168, NULL, 1, 3, 0, 3, 0, '2022-12-29 17:32:00', '2022-12-29 17:32:00');
INSERT INTO `wk_module_scene` VALUES (1608439908642160640, '全部%s', 1608392444123811840, NULL, 1, 1, 0, 1, 1, '2022-12-29 20:28:52', '2022-12-29 20:28:52');
INSERT INTO `wk_module_scene` VALUES (1608439908650549248, '我负责的%s', 1608392444123811840, NULL, 1, 2, 0, 2, 0, '2022-12-29 20:28:52', '2022-12-29 20:28:52');
INSERT INTO `wk_module_scene` VALUES (1608439908658937856, '下属负责的%s', 1608392444123811840, NULL, 1, 3, 0, 3, 0, '2022-12-29 20:28:52', '2022-12-29 20:28:52');
INSERT INTO `wk_module_scene` VALUES (1608439918687518720, '全部%s', 1608439433763061760, NULL, 1, 1, 0, 1, 1, '2022-12-29 20:28:55', '2022-12-29 20:28:55');
INSERT INTO `wk_module_scene` VALUES (1608439918695907328, '我负责的%s', 1608439433763061760, NULL, 1, 2, 0, 2, 0, '2022-12-29 20:28:55', '2022-12-29 20:28:55');
INSERT INTO `wk_module_scene` VALUES (1608439918704295936, '下属负责的%s', 1608439433763061760, NULL, 1, 3, 0, 3, 0, '2022-12-29 20:28:55', '2022-12-29 20:28:55');
INSERT INTO `wk_module_scene` VALUES (1608645772687998976, '全部%s', 1608392472255008768, NULL, 1, 1, 0, 1, 1, '2022-12-30 10:06:53', '2022-12-30 10:06:53');
INSERT INTO `wk_module_scene` VALUES (1608645772692193280, '全部%s', 1608392498486185984, NULL, 1, 1, 0, 1, 1, '2022-12-30 10:06:54', '2022-12-30 10:06:54');
INSERT INTO `wk_module_scene` VALUES (1608645772704776192, '我负责的%s', 1608392498486185984, NULL, 1, 2, 0, 2, 0, '2022-12-30 10:06:54', '2022-12-30 10:06:54');
INSERT INTO `wk_module_scene` VALUES (1608645772704776193, '我负责的%s', 1608392472255008768, NULL, 1, 2, 0, 2, 0, '2022-12-30 10:06:53', '2022-12-30 10:06:53');
INSERT INTO `wk_module_scene` VALUES (1608645772717359104, '下属负责的%s', 1608392498486185984, NULL, 1, 3, 0, 3, 0, '2022-12-30 10:06:54', '2022-12-30 10:06:54');
INSERT INTO `wk_module_scene` VALUES (1608645772721553408, '下属负责的%s', 1608392472255008768, NULL, 1, 3, 0, 3, 0, '2022-12-30 10:06:53', '2022-12-30 10:06:53');
INSERT INTO `wk_module_scene` VALUES (1608645775573680128, '全部%s', 1608392505129963520, NULL, 1, 1, 0, 1, 1, '2022-12-30 10:06:55', '2022-12-30 10:06:55');
INSERT INTO `wk_module_scene` VALUES (1608645775586263040, '我负责的%s', 1608392505129963520, NULL, 1, 2, 0, 2, 0, '2022-12-30 10:06:55', '2022-12-30 10:06:55');
INSERT INTO `wk_module_scene` VALUES (1608645775598845952, '下属负责的%s', 1608392505129963520, NULL, 1, 3, 0, 3, 0, '2022-12-30 10:06:55', '2022-12-30 10:06:55');
INSERT INTO `wk_module_scene` VALUES (1608645778018959360, '全部%s', 1608392507768180736, NULL, 1, 1, 0, 1, 1, '2022-12-30 10:06:55', '2022-12-30 10:06:55');
INSERT INTO `wk_module_scene` VALUES (1608645778031542272, '我负责的%s', 1608392507768180736, NULL, 1, 2, 0, 2, 0, '2022-12-30 10:06:55', '2022-12-30 10:06:55');
INSERT INTO `wk_module_scene` VALUES (1608645778044125184, '下属负责的%s', 1608392507768180736, NULL, 1, 3, 0, 3, 0, '2022-12-30 10:06:55', '2022-12-30 10:06:55');
INSERT INTO `wk_module_scene` VALUES (1608645780392935424, '全部%s', 1608392509865332736, NULL, 1, 1, 0, 1, 1, '2022-12-30 10:06:56', '2022-12-30 10:06:56');
INSERT INTO `wk_module_scene` VALUES (1608645780405518336, '我负责的%s', 1608392509865332736, NULL, 1, 2, 0, 2, 0, '2022-12-30 10:06:56', '2022-12-30 10:06:56');
INSERT INTO `wk_module_scene` VALUES (1608645780413906944, '下属负责的%s', 1608392509865332736, NULL, 1, 3, 0, 3, 0, '2022-12-30 10:06:56', '2022-12-30 10:06:56');

-- ----------------------------
-- Table structure for wk_module_statistic_field_union
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_statistic_field_union`;
CREATE TABLE `wk_module_statistic_field_union`  (
  `id` bigint(20) NOT NULL,
  `relate_field_id` bigint(20) NOT NULL COMMENT '统计字段ID',
  `module_id` bigint(20) NOT NULL COMMENT '当前模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `target_field_id` bigint(20) NULL DEFAULT NULL COMMENT '目标字段ID',
  `target_module_id` bigint(20) NOT NULL COMMENT '目标模块ID',
  `statistic_type` int(11) NOT NULL COMMENT '统计类型',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `relate_field_id`(`relate_field_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '统计字段关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_statistic_field_union
-- ----------------------------
INSERT INTO `wk_module_statistic_field_union` VALUES (1608392516068708361, 1524202586061725696, 1608392444123811840, 0, 1522635492962697216, 1608392505129963520, 1, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_statistic_field_union` VALUES (1608392516068708365, 1536896929075687424, 1608392444123811840, 0, 1522622016991100928, 1608392498486185984, 1, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_statistic_field_union` VALUES (1608392516068708368, 1536893534436716544, 1608392444123811840, 0, 1523704530237227008, 1608392507768180736, 1, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_statistic_field_union` VALUES (1608392516068708370, 1536888922849800192, 1608392444123811840, 0, 1523696647881719808, 1608392509865332736, 1, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_statistic_field_union` VALUES (1608392516068708372, 1536888922635890688, 1608392444123811840, 0, 1522635492962697216, 1608392505129963520, 1, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_statistic_field_union` VALUES (1608392516068708375, 1547124698610999296, 1608392444123811840, 0, 1522635492962697216, 1608392505129963520, 1, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_statistic_field_union` VALUES (1608392516068708376, 1547111610935484416, 1608392444123811840, 0, 1522635492962697216, 1608392505129963520, 1, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_statistic_field_union` VALUES (1608392516068708378, 1547112543438315520, 1608392444123811840, 0, 1523704530237227008, 1608392507768180736, 1, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_statistic_field_union` VALUES (1608392516068708381, 1547112543228600320, 1608392444123811840, 0, 1523696647881719808, 1608392509865332736, 1, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_statistic_field_union` VALUES (1608392516068708382, 1547789201929478144, 1608392444123811840, 0, 1522635492962697216, 1608392505129963520, 1, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_statistic_field_union` VALUES (1608392517066952727, 1547825385271533568, 1608392204931043328, 0, 1547484787855400960, 1608392444123811840, 1, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_statistic_field_union` VALUES (1608392517243113473, 1547825385368002560, 1608392204931043328, 0, 1547823290770333696, 1608392444123811840, 1, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_statistic_field_union` VALUES (1608392517251502081, 1547825385321865216, 1608392204931043328, 0, 1547823290552229888, 1608392444123811840, 1, '2022-12-29 17:20:33', 0);
INSERT INTO `wk_module_statistic_field_union` VALUES (1608392518325243906, 1523698997690204160, 1608392509865332736, 0, 1522614130873303040, 1608392444123811840, 1, '2022-12-29 17:20:34', 0);
INSERT INTO `wk_module_statistic_field_union` VALUES (1608439792929701888, 1532232616855543808, 1608439763607322624, 0, 1531209525656334336, 1608439761141071872, 1, '2022-12-29 20:28:24', 1);

-- ----------------------------
-- Table structure for wk_module_status
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_status`;
CREATE TABLE `wk_module_status`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `is_enable` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0 停用 1 启用',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_module_status_module_id_index`(`module_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '模块状态表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_status
-- ----------------------------
INSERT INTO `wk_module_status` VALUES (1608031970878377986, 1608031970215677952, '2022-12-28 17:27:52', 1);
INSERT INTO `wk_module_status` VALUES (1608031970945486850, 1608031970911932416, '2022-12-28 17:27:52', 1);
INSERT INTO `wk_module_status` VALUES (1608031970945486852, 1608031970958069760, '2022-12-28 17:27:52', 1);
INSERT INTO `wk_module_status` VALUES (1608031970945486854, 1608031970979041280, '2022-12-28 17:27:52', 1);
INSERT INTO `wk_module_status` VALUES (1608376663164162050, 1608376662409187328, '2022-12-29 16:17:33', 1);
INSERT INTO `wk_module_status` VALUES (1608376819028692993, 1608376818923835392, '2022-12-29 16:18:11', 1);
INSERT INTO `wk_module_status` VALUES (1608376830927933442, 1608376830856630272, '2022-12-29 16:18:13', 1);
INSERT INTO `wk_module_status` VALUES (1608376841044594689, 1608376840931348480, '2022-12-29 16:18:16', 1);
INSERT INTO `wk_module_status` VALUES (1608376854294401026, 1608376854185349120, '2022-12-29 16:18:19', 1);
INSERT INTO `wk_module_status` VALUES (1608376857339465729, 1608376857092001792, '2022-12-29 16:18:20', 1);
INSERT INTO `wk_module_status` VALUES (1608376860325810177, 1608376860195786752, '2022-12-29 16:18:20', 1);
INSERT INTO `wk_module_status` VALUES (1608376863744167938, 1608376863555424256, '2022-12-29 16:18:21', 1);
INSERT INTO `wk_module_status` VALUES (1608386586941906946, 1608386586396647424, '2022-12-29 16:56:59', 1);
INSERT INTO `wk_module_status` VALUES (1608386587734630401, 1608386587604606976, '2022-12-29 16:57:00', 1);
INSERT INTO `wk_module_status` VALUES (1608386588116312066, 1608386588011454464, '2022-12-29 16:57:00', 1);
INSERT INTO `wk_module_status` VALUES (1608386588439273474, 1608386588338610176, '2022-12-29 16:57:00', 1);
INSERT INTO `wk_module_status` VALUES (1608389866518536194, 1608389866422067200, '2022-12-29 17:10:01', 1);
INSERT INTO `wk_module_status` VALUES (1608389882964402178, 1608389882909876224, '2022-12-29 17:10:05', 1);
INSERT INTO `wk_module_status` VALUES (1608389883350278146, 1608389883299946496, '2022-12-29 17:10:05', 1);
INSERT INTO `wk_module_status` VALUES (1608389883677433857, 1608389883618713600, '2022-12-29 17:10:05', 1);
INSERT INTO `wk_module_status` VALUES (1608391605237846018, 1608391604776472576, '2022-12-29 17:16:56', 1);
INSERT INTO `wk_module_status` VALUES (1608391940048162818, 1608391939779727360, '2022-12-29 17:18:16', 1);
INSERT INTO `wk_module_status` VALUES (1608391957924286466, 1608391956376588288, '2022-12-29 17:18:20', 1);
INSERT INTO `wk_module_status` VALUES (1608391970360397825, 1608391970154876928, '2022-12-29 17:18:23', 1);
INSERT INTO `wk_module_status` VALUES (1608391976094011394, 1608391975947210752, '2022-12-29 17:18:24', 1);
INSERT INTO `wk_module_status` VALUES (1608391982041534465, 1608391981840207872, '2022-12-29 17:18:26', 1);
INSERT INTO `wk_module_status` VALUES (1608391989830356994, 1608391989733888000, '2022-12-29 17:18:28', 1);
INSERT INTO `wk_module_status` VALUES (1608392003759640578, 1608392003600257024, '2022-12-29 17:18:31', 1);
INSERT INTO `wk_module_status` VALUES (1608392205048483841, 1608392204931043328, '2022-12-29 17:19:19', 1);
INSERT INTO `wk_module_status` VALUES (1608392444253835265, 1608392444123811840, '2022-12-29 17:20:16', 1);
INSERT INTO `wk_module_status` VALUES (1608392472426975233, 1608392472255008768, '2022-12-29 17:20:23', 1);
INSERT INTO `wk_module_status` VALUES (1608392498582654978, 1608392498486185984, '2022-12-29 17:20:29', 1);
INSERT INTO `wk_module_status` VALUES (1608392505218043905, 1608392505129963520, '2022-12-29 17:20:30', 1);
INSERT INTO `wk_module_status` VALUES (1608392507835289601, 1608392507768180736, '2022-12-29 17:20:31', 1);
INSERT INTO `wk_module_status` VALUES (1608392509945024513, 1608392509865332736, '2022-12-29 17:20:32', 1);
INSERT INTO `wk_module_status` VALUES (1608392513757646850, 1608392513568903168, '2022-12-29 17:20:32', 1);
INSERT INTO `wk_module_status` VALUES (1608398876714020866, 1608398875837411328, '2022-12-29 17:45:49', 1);
INSERT INTO `wk_module_status` VALUES (1608398881449390081, 1608398881373892608, '2022-12-29 17:45:51', 1);
INSERT INTO `wk_module_status` VALUES (1608398881910763521, 1608398881805905920, '2022-12-29 17:45:51', 1);
INSERT INTO `wk_module_status` VALUES (1608398882288250882, 1608398882170810368, '2022-12-29 17:45:51', 1);
INSERT INTO `wk_module_status` VALUES (1608439743118147584, 1608439433763061760, '2022-12-29 20:28:13', 1);
INSERT INTO `wk_module_status` VALUES (1608439758830010368, 1608439746549088256, '2022-12-29 20:28:17', 1);
INSERT INTO `wk_module_status` VALUES (1608439761229152256, 1608439761141071872, '2022-12-29 20:28:17', 1);
INSERT INTO `wk_module_status` VALUES (1608439761979932672, 1608439761891852288, '2022-12-29 20:28:17', 1);
INSERT INTO `wk_module_status` VALUES (1608439763691208704, 1608439763607322624, '2022-12-29 20:28:18', 1);
INSERT INTO `wk_module_status` VALUES (1608439764857225216, 1608439764727201792, '2022-12-29 20:28:18', 1);
INSERT INTO `wk_module_status` VALUES (1608439765733834752, 1608439765649948672, '2022-12-29 20:28:18', 1);
INSERT INTO `wk_module_status` VALUES (1608439767231201280, 1608439767134732288, '2022-12-29 20:28:19', 1);
INSERT INTO `wk_module_status` VALUES (1608439767789043712, 1608439767700963328, '2022-12-29 20:28:19', 1);
INSERT INTO `wk_module_status` VALUES (1608439768535629824, 1608439768413995008, '2022-12-29 20:28:19', 1);
INSERT INTO `wk_module_status` VALUES (1608439769617760256, 1608439769529679872, '2022-12-29 20:28:19', 1);
INSERT INTO `wk_module_status` VALUES (1608439771316453376, 1608439771190624256, '2022-12-29 20:28:20', 1);
INSERT INTO `wk_module_status` VALUES (1608439774864834560, 1608439774743199744, '2022-12-29 20:28:20', 1);
INSERT INTO `wk_module_status` VALUES (1608439776337035264, 1608439776207011840, '2022-12-29 20:28:21', 1);
INSERT INTO `wk_module_status` VALUES (1608439776790020096, 1608439776668385280, '2022-12-29 20:28:21', 1);
INSERT INTO `wk_module_status` VALUES (1608439778081865728, 1608439777943453696, '2022-12-29 20:28:21', 1);

-- ----------------------------
-- Table structure for wk_module_team_member
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_team_member`;
CREATE TABLE `wk_module_team_member`  (
  `id` bigint(20) NOT NULL,
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `data_id` bigint(20) NOT NULL COMMENT '数据ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `power` int(1) NULL DEFAULT NULL COMMENT '1 只读 2 读写 3 负责人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `expires_time` datetime NULL DEFAULT NULL COMMENT '过期时间',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_module_team_member_module_id_data_id_index`(`module_id`, `data_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '团队成员表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_team_member
-- ----------------------------

-- ----------------------------
-- Table structure for wk_module_tree_data
-- ----------------------------
DROP TABLE IF EXISTS `wk_module_tree_data`;
CREATE TABLE `wk_module_tree_data`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `data_id` bigint(20) NOT NULL COMMENT '数据 ID',
  `parent_id` json NULL COMMENT '父级数据 ID',
  `child_id` json NULL COMMENT '子集数据 ID',
  `module_id` bigint(20) NOT NULL COMMENT '模块 ID',
  `field_id` bigint(20) NOT NULL COMMENT '字段 ID',
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
  `id` bigint(20) NOT NULL,
  `data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '配置数据',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_module_user_search_config_module_id_create_user_id_index`(`module_id`, `create_user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用戶搜索配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_module_user_search_config
-- ----------------------------

-- ----------------------------
-- Table structure for wk_stage
-- ----------------------------
DROP TABLE IF EXISTS `wk_stage`;
CREATE TABLE `wk_stage`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `stage_setting_id` bigint(20) NOT NULL COMMENT '阶段流程ID',
  `stage_id` bigint(20) NOT NULL COMMENT '阶段ID',
  `stage_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '阶段名称',
  `sort` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_stage_module_id_version_index`(`module_id`, `version`) USING BTREE,
  INDEX `wk_stage_module_id_stage_id_version_index`(`module_id`, `stage_id`, `version`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '阶段表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_stage
-- ----------------------------

-- ----------------------------
-- Table structure for wk_stage_comment
-- ----------------------------
DROP TABLE IF EXISTS `wk_stage_comment`;
CREATE TABLE `wk_stage_comment`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `comment_id` bigint(20) NOT NULL COMMENT '评论ID',
  `stage_setting_id` bigint(20) NOT NULL COMMENT '阶段流程ID',
  `stage_id` bigint(20) NOT NULL COMMENT '阶段ID',
  `content` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '内容',
  `main_id` bigint(20) NULL DEFAULT 0 COMMENT '主评论id',
  `reply_user_id` bigint(20) NULL DEFAULT NULL COMMENT '回复评论用户ID',
  `is_delete` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  `data_id` bigint(20) NULL DEFAULT NULL COMMENT '关联模块数据Id',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_stage_comment_data_id_index`(`data_id`) USING BTREE,
  INDEX `wk_stage_comment_module_id_version_index`(`module_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '阶段评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_stage_comment
-- ----------------------------

-- ----------------------------
-- Table structure for wk_stage_data
-- ----------------------------
DROP TABLE IF EXISTS `wk_stage_data`;
CREATE TABLE `wk_stage_data`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `stage_setting_id` bigint(20) NOT NULL COMMENT '阶段流程ID',
  `is_main` tinyint(1) NOT NULL DEFAULT 0 COMMENT '阶段流程主体',
  `stage_id` bigint(20) NOT NULL COMMENT '阶段ID',
  `stage_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '阶段名称',
  `sort` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `data_id` bigint(20) NULL DEFAULT NULL COMMENT '关联模块数据Id',
  `field_data` json NULL COMMENT '表单数据',
  `task_data` json NULL COMMENT '阶段工作数据',
  `status` int(11) NULL DEFAULT 0 COMMENT '0 未开始 1 完成 2 草稿 3 成功 4 失败',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_stage_comment_module_id_version_index`(`module_id`, `version`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '阶段数据表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_stage_data
-- ----------------------------

-- ----------------------------
-- Table structure for wk_stage_field
-- ----------------------------
DROP TABLE IF EXISTS `wk_stage_field`;
CREATE TABLE `wk_stage_field`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `stage_setting_id` bigint(20) NOT NULL COMMENT '阶段流程ID',
  `stage_id` bigint(20) NOT NULL COMMENT '阶段ID',
  `field_id` bigint(20) NOT NULL COMMENT '主键ID',
  `field_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '自定义字段英文标识',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '字段名称',
  `type` int(11) NOT NULL DEFAULT 1 COMMENT '字段类型',
  `field_type` int(11) NOT NULL DEFAULT 1 COMMENT '0 系统 1 自定义',
  `remark` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字段说明',
  `input_tips` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '输入提示',
  `max_length` int(11) NULL DEFAULT NULL COMMENT '最大长度',
  `is_unique` int(11) NULL DEFAULT 0 COMMENT '是否唯一 1 是 0 否',
  `is_null` int(11) NULL DEFAULT 0 COMMENT '是否必填 1 是 0 否',
  `sorting` int(11) NULL DEFAULT 1 COMMENT '排序 从小到大',
  `is_hidden` int(11) NOT NULL DEFAULT 0 COMMENT '是否隐藏  0不隐藏 1隐藏',
  `style_percent` int(11) NULL DEFAULT 50 COMMENT '样式百分比',
  `precisions` int(11) NULL DEFAULT NULL COMMENT '精度，允许的最大小数位',
  `form_position` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表单定位 坐标格式',
  `max_num_restrict` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '限制的最大数',
  `min_num_restrict` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '限制的最小数',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_stage_field_module_id_version_index`(`module_id`, `version`) USING BTREE,
  INDEX `wk_stage_field_stage_setting_id_version_index`(`stage_setting_id`, `version`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '阶段字段表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_stage_field
-- ----------------------------

-- ----------------------------
-- Table structure for wk_stage_setting
-- ----------------------------
DROP TABLE IF EXISTS `wk_stage_setting`;
CREATE TABLE `wk_stage_setting`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `stage_setting_id` bigint(20) NOT NULL COMMENT '阶段流程ID',
  `stage_setting_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '阶段流程名称',
  `success_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '成功阶段的名称',
  `failed_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '失败阶段的名称',
  `dept_ids` json NULL COMMENT '适用部门',
  `user_ids` json NULL COMMENT '适用员工',
  `status` int(11) NULL DEFAULT 1 COMMENT '状态 1 正常 0 停用',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_stage_flow_module_id_version_index`(`module_id`, `version`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '阶段配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_stage_setting
-- ----------------------------

-- ----------------------------
-- Table structure for wk_stage_task
-- ----------------------------
DROP TABLE IF EXISTS `wk_stage_task`;
CREATE TABLE `wk_stage_task`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `stage_setting_id` bigint(20) NOT NULL COMMENT '阶段流程ID',
  `stage_id` bigint(20) NOT NULL COMMENT '阶段ID',
  `task_id` bigint(20) NOT NULL COMMENT '任务ID',
  `task_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务名称',
  `sort` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `is_must` tinyint(1) NULL DEFAULT 1 COMMENT '是否必做',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `version` int(11) NOT NULL COMMENT '版本号',
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
  `id` bigint(20) NOT NULL COMMENT '主键',
  `application_id` bigint(20) NOT NULL COMMENT '应用 ID',
  `module_id` bigint(20) NOT NULL COMMENT '模块 ID',
  `module_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '模块名称',
  `object_type` int(11) NOT NULL DEFAULT 0 COMMENT '对象类型 0 自定义流程 1 自定义按钮',
  `object_id` bigint(20) NULL DEFAULT NULL COMMENT '对象 ID',
  `record_id` bigint(20) NULL DEFAULT NULL COMMENT '记录 ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `data_id` bigint(20) NOT NULL COMMENT '数据 ID',
  `field_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '字段值',
  `field_auth` json NULL COMMENT '字段授权',
  `type` int(11) NOT NULL DEFAULT 0 COMMENT '待办类型 0 节点通知',
  `type_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '类型名称',
  `type_id` bigint(20) NULL DEFAULT NULL COMMENT '类型 ID',
  `flow_type` int(11) NULL DEFAULT NULL COMMENT '节点类型 0 条件节点 1 审批节点 2 填写节点 3 抄送节点 4 添加数据 5 更新数据 6 发起人节点',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `owner_user_id` bigint(20) NULL DEFAULT NULL COMMENT '负责人',
  `viewed` tinyint(1) NOT NULL DEFAULT 0 COMMENT '已读',
  `view_time` datetime NULL DEFAULT NULL COMMENT '查看时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '变更时间',
  `status` int(11) NOT NULL DEFAULT 0 COMMENT '待办状态 0 待处理 1 已处理',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '待办' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_to_do
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
