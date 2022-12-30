package com.kakarote.module.entity.BO;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel("审批数据保存BO")
public class FlowSaveBO {

    @ApiModelProperty(value = "流程类型  0 条件节点 1 审批节点 2 填写节点 3 抄送节点 4 添加数据 5 更新数据 6 发起人节点")
    @NotNull
    private Integer flowType;

    @ApiModelProperty(value = "流程名称")
    private String flowName;

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


    @ApiModel("添加数据节点")
    @Data
    public static class FlowSaveData {

        @ApiModelProperty("负责人ID")
        private Long ownerUserId;

        @ApiModelProperty("目标模块ID")
        private Long targetModuleId;

        @ApiModelProperty("添加规则")
        private List<FlowConditionBO> insertRules;
    }

    @ApiModel("更新数据节点")
    @Data
    public static class FlowUpdateData {

        @ApiModelProperty("目标模块ID")
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
        private List<Long> userList;

        @ApiModelProperty("是否允许发起人添加抄送人")
        private Integer isAdd;

        // region wwl
        @ApiModelProperty("发起人自己，1是0否，是否抄送给发起者本人")
        private Integer isSelf;
        @ApiModelProperty("指定角色列表")
        private List<Long> roleList;
        @ApiModelProperty("指定上级从直属上级到最高第20级上级均可以选择")
        private List<Integer> parentLevelList;
        // endregion

    }

    @ApiModel("填写数据节点")
    @Data
    public static class FlowFillData extends FlowSaveBO.FlowExamineData{

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
        private List<Long> userList;

        @ApiModelProperty("角色ID")
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

        @ApiModelProperty("条件名称")
        private String conditionName;

        @ApiModelProperty("条件筛选列表")
        private List<FlowConditionBO> conditionDataList;

        @ApiModelProperty("该条件下属流程")
        private List<FlowSaveBO> flowDataList;
    }

    @ApiModel("高级设置")
    @Data
    public static class FlowTimeLimitConfig {
        @ApiModelProperty("是否发送消息通知 1 是 0 否")
        private Integer isSendMessage;

        @ApiModelProperty("是否允许转交 1 是 0 否")
        private Integer allowTransfer;

        @ApiModelProperty("允许转交的用户ID，空数组代表全部")
        private List<Long> transferUserIds;

        @ApiModelProperty("反馈是否必填 1 是 0 否")
        private Integer openFeedback;

		@ApiModelProperty(value = "撤回之后重新审核操作 1 从第一层开始 2 从拒绝的层级开始")
		private Integer recheckType;

        @ApiModelProperty("是否开启限时处理 1 是 0 否")
        private Integer openTimeLimit;

        @ApiModelProperty("限时处理的值 d代表天，m代表分钟 h代表小时 <br/>" +
                "例 10d代表10天")
        private String timeValue;

        @ApiModelProperty("超时类型 1 自动提醒 2 自动转交 3 自动同意")
        private Integer overtimeType;

        @ApiModelProperty("自动提醒和自动转交的人员列表")
        private List<Long> userIds;

        @ApiModelProperty("自动转交的审批类型 1 依次审批 2 会签 3 或签")
        private Integer examineType;
    }
}
