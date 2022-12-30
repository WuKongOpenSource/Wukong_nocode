package com.kakarote.module.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 流程限时处理设置
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_flow_time_limit")
@ApiModel(value="FlowTimeLimit对象", description="流程限时处理设置")
public class FlowTimeLimit implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

	@ApiModelProperty(value = "模块ID")
	private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "流程ID")
    private Long flowId;

    @ApiModelProperty(value = "是否进入此节点时给处理人发消息通知 1 是 0 否")
    private Integer isSendMessage;

    @ApiModelProperty(value = "是否允许转交 1 是 0 否")
    private Integer allowTransfer;

    @ApiModelProperty(value = "允许转交的用户ID，空数组代表全部")
    private String transferUserIds;

    @ApiModelProperty(value = "反馈是否必填 1 是 0 否")
    private Integer openFeedback;

	@ApiModelProperty(value = "撤回之后重新审核操作 1 从第一层开始 2 从拒绝的层级开始")
	private Integer recheckType;

    @ApiModelProperty(value = "是否开启限时处理 1 是 0 否")
    private Integer openTimeLimit;

    @ApiModelProperty(value = "限时处理的值 d代表天，m代表分钟 h代表小时 例 10d代表10天")
    private String timeValue;

    @ApiModelProperty(value = "超时类型 1 自动提醒 2 自动转交 3 自动同意")
    private Integer overtimeType;

    @ApiModelProperty(value = "自动提醒和自动转交的人员列表")
    private String userIds;

    @ApiModelProperty(value = "自动转交的审批类型 1 依次审批 2 会签 3 或签")
    private Integer examineType;

    @ApiModelProperty(value = "批次ID")
    private String batchId;

    @ApiModelProperty(value = "流程元数据ID")
    private Long flowMetadataId;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

}
