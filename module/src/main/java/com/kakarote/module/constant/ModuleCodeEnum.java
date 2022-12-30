package com.kakarote.module.constant;


import com.kakarote.common.result.ResultCode;

/**
 * @author zhangzhiwei
 * module响应错误代码枚举类 4300 - 4500
 */

public enum ModuleCodeEnum implements ResultCode {

    MODULE_MAX_SIZE_ERROR(4300,"数量超过限制"),
    MODULE_FIELD_MAX_SIZE_ERROR(4301,"字段数量超过限制"),
	MODULE_PARAM_NULL_ERROR(4302, "参数为空"),
	MODULE_NOT_FOUND(4303, "模块未找到"),
	MODULE_FIELD_NOT_FOUND(4304, "模块字段未找到"),
	MODULE_MAIN_FIELD_ID_CAN_NOT_DELETE(4304, "模块主字段不能删除"),
	MODULE_FIELD_NOT_DATA_UNION(4305, "该字段非数据关联字段"),
	MODULE_FIELD_NOT_SET_UNION_FIELD(4306, "该字段未设置关联字段"),
	MODULE_NOT_SET_MAIN_FIELD(4306, "模块未设置主字段"),
	MODULE_ERROR_FIELD_TYPE(4307, "字段类型错误"),
	SEND_MSG_FAILED(4308, "发送消息失败"),
	EXAMINE_ROLE_NO_USER_ERROR(4309, "自选的角色下没有检测到人员，请核实！"),
	FLOW_COPY_NO_USER_ERROR(4410, "抄送节点无抄送人错误"),
	FLOW_NO_RULE_ERROR(4411, "数据节点无规则错误"),
	NO_TARGET_MODULE_ERROR(4412, "目标模块未设置"),
	MODULE_FIELD_DATA_CAN_NOT_EDIT_ERROR(4413, "不能编辑，请先撤回再编辑"),
	EXAMINE_RECHECK_PASS_ERROR(4414, "该审核已通过，不能撤回"),
	MODULE_ID_IS_NULL_ERROR(4414, "模块ID不能为空"),
	MODULE_INVALID_MAIN_FIELD_ID_ERROR(4415, "无效的主字段ID"),
	FIELD_NOT_DATA_UNION(4416, "该字段不是数据关联字段"),
	FILE_BATCH_ID_IS_NULL(4417, "文件批次ID为空"),
	DATA_ID_IS_NULL(4418, "数据ID为空"),
	CURRENT_USER_NO_AUTH(4419, "当前用户没有授权"),
	MODULE_DISABLED(4420, "模块已停用"),
	MODULE_IS_NOT_ACTIVE(4421, "模块未激活"),
	SOME_ONE_IS_FILLING_FIELD_DATA(4422, "其他用户正在填写字段值"),
	TEAM_OWNER_CAN_NOT_BE_REMOVED(4423, "团队负责人不能退出团队"),
    MODULE_MAIN_FIELD_NOT_SET(4424, "模块未配置主字段"),
    FIELD_DATA_VALUE_IS_UNIQUE(4425, "字段值唯一，不能重复"),
    MODULE_SCENE_CAN_NOT_HIDE_ALL(4426, "禁止隐藏所有场景"),
	MODULE_SERIAL_NUMBER_FIELD_LENGTH_ERROR(4427, "自定义编号类型字段不能超过200字符"),
	MODULE_GROUP_NAME_EXIST_ERROR(4428, "分组名称已存在"),
	EXPRESSION_PARSE_ERROR(4429, "表达式解析错误"),
	DIGIT_ERROR(4430, "输入金额有误"),
    EXPRESSION_ARG_ERROR(4431, "表达式参数错误"),
	MODULE_PRINT_TEMPLATE_CONTENT_EMPTY_ERROR(4432,"使用的打印模板不能为空"),
	MODULE_PRINT_PRE_VIEW_ERROR(4433, "仅支持pdf和word格式预览"),
	FLOW_CONFIG_IS_NULL(4434, "未配置节点"),
	CUSTOM_BUTTON_NOT_FOUND(4435, "按钮未找到"),
	FLOW_NOT_FOUND(4436, "节点未找到"),
    CUSTOM_NOTICE_NOT_FOUND(4437, "提醒未找到"),
	MODULE_GROUP_NOT_EXIST_ERROR(4438, "模块分组不存在"),
	DATA_CHECK_RULE_NOT_FOUND(4439, "校验规则未找到"),
	GROUP_CAN_NOT_MOVE_IN_GROUP(4439, "分组不能放入分组"),
	CUSTOM_CATEGORY_NOT_FOUND(4440, "自定义分组未找到"),
	CATEGORY_AND_DATA_NOT_OF_SAME_MODULE(4441, "分組和数据不属于同一个模块"),
	STAGE_SETTING_NOT_FOUND(4442, "阶段流程未找到"),
	IMPORT_ERROR_TEMPLATE_NOT_EXIST(4443, "导入数据错误文件不存在"),
	DATA_NOT_EXIST_OR_DELETE(4444, "数据不存在或已被删除"),
	CUSTOM_BUTTON_DEALING(4445, "自定义按钮处理中"),
	HAS_EXAMINED_ERROR(4446, "已审核，无需再次审核"),
	MODULE_IS_NOT_LATEST_VERSION(4447, "模块不是最新版本"),
	FIELD_CAN_NOT_IN_RULE(4448, "单选、多选、标签字段不能作为编码规则"),
	PRINT_TEMPLATE_NOT_FOUND(4449, "打印模板未找到"),
	PRINT_TEMPLATE_NOT_LATEST(4450, "打印模板配置错误，请重新编辑"),
	STAGE_NOT_FOUND(4451, "阶段未找到"),
	FORMULA_VALUE_NOT_MATCH_TYPE(4452, "计算公式结果和字段类型不匹配"),
	IMPORT_APP_PARSE_ERROR(4453, "应用导入解析错误"),
	SOURCE_APP_CATEGORY_PARAM_ERROR(4454, "应用分类参数错误"),
	APP_NOT_PUBLISHED(4455, "应用未发布"),
	APP_HAS_INSTALLED(4456, "应用已安装"),
	PERMISSION_DENIED(4457, "没有权限"),
	EXTERNAL_MODULE_EXISTED(4458, "外部应用已存在"),
	;


    ModuleCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private int code;
    private String msg;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
