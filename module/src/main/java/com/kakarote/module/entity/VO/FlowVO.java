package com.kakarote.module.entity.VO;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.kakarote.module.entity.BO.FlowConditionBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-28 10:05
 */
@Data
@ApiModel("审批数据VO")
public class FlowVO {

	@ApiModelProperty(value = "流程Id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long flowId;

	@ApiModelProperty(value = "流程类型  0 条件节点 1 审批节点 2 填写节点 3 抄送节点 4 添加数据 5 更新数据 6 发起人节点")
	private Integer flowType;

	@ApiModelProperty(value = "流程名称")
	private String flowName;

	@ApiModelProperty(value = "流程下属类型 如审批节点类型")
	private Integer type;

	@ApiModelProperty(value = "描述文本")
	private String content;

	@ApiModelProperty(value = "排序")
	private Integer sort;

	@ApiModelProperty(value = "对应类型配置")
	private JSONObject data;

	@ApiModelProperty(value = "高级设置，审批节点、填写节点需要")
	private FlowTimeLimitConfig timeLimitConfig;

	@ApiModelProperty(value = "字段授权")
	private String fieldAuth;

	@ApiModelProperty(value = "节点状态 0 待处理 1 已处理 2 拒绝 3 处理中 4 撤回 5 未提交 8 作废 9 忽略")
	private Integer flowStatus;

	@ApiModelProperty(value = "用戶列表")
	private List<FlowVO.User> users;

	@ApiModelProperty(value = "创建人")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long createUserId;


	@ApiModel("添加数据节点")
	@Data
	public static class FlowSaveData {

		@ApiModelProperty("负责人")
		private User ownerUser;

		@ApiModelProperty("目标模块ID")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long targetModuleId;

		@ApiModelProperty("添加规则")
		private List<FlowConditionBO> insertRules;
	}

	@ApiModel("更新数据节点")
	@Data
	public static class FlowUpdateData {

		@ApiModelProperty("目标模块ID")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long targetModuleId;

		@ApiModelProperty("更新数据筛选")
		private List<FlowConditionBO> searchRules;

		@ApiModelProperty("更新规则")
		private List<FlowConditionBO> updateRules;

		@ApiModelProperty("若找不到符合筛选条件的数据,是否添加 1 是 0 否")
		private Integer isInsert;

		@ApiModelProperty("添加规则")
		private List<FlowConditionBO> insertRules;
	}

	@ApiModel("抄送数据节点")
	@Data
	public static class FlowCopyData {

		@ApiModelProperty("用户列表")
		private List<User> userList;

		@ApiModelProperty("是否允许发起人添加抄送人")
		private Integer isAdd;

		// region 抄送新需求 20220324 wwl
		@ApiModelProperty("1是0否，是否抄送给发起者本人")
		private Integer isSelf;
		@ApiModelProperty("指定角色列表")
		private List<Long> roleList;
		@ApiModelProperty("指定上级从直属上级到最高第20级上级均可以选择")
		private List<Integer> parentLevelList;
		@ApiModelProperty("配置时选的用户id列表")
		private List<Long> userIdList;
		// endregion
	}

	@ApiModel("填写数据节点")
	@Data
	public static class FlowFillData extends FlowVO.FlowExamineData{

		@ApiModelProperty("字段ID列表")
		public List<Long> fieldIds;
	}

	@ApiModel("审批数据节点")
	@Data
	public static class FlowExamineData {

		@ApiModelProperty("1 指定成员 2 主管 3 角色 4 发起人自选 5 连续多级主管")
		private Integer examineType;

		@ApiModelProperty("找不到人员时的处理方案 1 自动通过 2 管理员审批")
		private Integer examineErrorHandling;

		@ApiModelProperty("多人审批类型 1 依次审批 2 会签 3 或签  <br/>" +
				"当审批类型为主管时 找不到上级时，是否由上一级上级代审批 0 否 1 是 <br/>" +
				"当审批类型为连续多级主管时 1 指定角色 2 组织架构的最上级 ")
		private Integer type;

		@ApiModelProperty("选择范围，只有发起人自选需要 1 全公司 2 指定成员 3 指定角色 ")
		private Integer rangeType;

		@ApiModelProperty("选择类型，只有发起人自选需要 1 自选一人 2 自选多人")
		private Integer chooseType;

		@ApiModelProperty("用户列表")
		private List<User> userList;

		@ApiModelProperty("角色ID")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long roleId;

		@ApiModelProperty("直属上级级别 1 代表直属上级 2 代表 直属上级的上级\n" +
				"连续多级审批的最高级别")
		private Integer parentLevel;
	}

	@ApiModel("条件节点")
	@Data
	public static class FlowCondition {

		@ApiModelProperty("排序，从小到大")
		private Integer sort;

		@ApiModelProperty(value = "条件ID")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long conditionId;

		@ApiModelProperty("条件名称")
		private String conditionName;

		@ApiModelProperty("条件筛选列表")
		private List<FlowConditionBO> conditionDataList;

		@ApiModelProperty("该条件下属流程")
		private List<FlowVO> flowDataList = new ArrayList<>();
	}


	@ApiModel("高级设置")
	@Data
	public static class FlowTimeLimitConfig {
		@ApiModelProperty("是否发送消息通知 1 是 0 否")
		private Integer isSendMessage;

		@ApiModelProperty("是否允许转交 1 是 0 否")
		private Integer allowTransfer;

		@ApiModelProperty("允许转交的用户，空数组代表全部")
		private List<User> transferUsers;

		@ApiModelProperty("反馈是否必填 1 是 0 否")
		private Integer openFeedback;

		@ApiModelProperty("是否开启限时处理 1 是 0 否")
		private Integer openTimeLimit;

		@ApiModelProperty("限时处理的值 d代表天，m代表分钟 h代表小时 <br/>" +
				"例 10d代表10天")
		private String timeValue;

		@ApiModelProperty("超时类型 1 自动提醒 2 自动转交 3 自动同意")
		private Integer overtimeType;

		@ApiModelProperty("自动提醒和自动转交的人员列表")
		private List<User> users;

		@ApiModelProperty("自动转交的审批类型 1 依次审批 2 会签 3 或签")
		private Integer examineType;
	}

	@ApiModel("用户对象")
	@Data
	public static class User implements Serializable {
		private static final long serialVersionUID=1L;

		@ApiModelProperty("用户ID")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long userId;

		@ApiModelProperty("头像")
		private String img;

		@ApiModelProperty("昵称")
		private String realname;
	}
}
