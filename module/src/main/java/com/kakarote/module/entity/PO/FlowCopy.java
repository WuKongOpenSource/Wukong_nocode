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
 * 流程抄送节点配置
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_flow_copy")
@ApiModel(value="FlowCopy对象", description="流程抄送节点配置")
public class FlowCopy implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

	@ApiModelProperty(value = "模块ID")
	private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "流程ID")
    private Long flowId;

    @ApiModelProperty(value = "用户列表")
    private String userIds;

    @ApiModelProperty(value = "上级列表")
    private String parentLevels;

    @ApiModelProperty(value = "角色列表")
    private String roleIds;

    @ApiModelProperty(value = "1是0否，是否给发起人本人发送")
    private Integer isSelf;

    @ApiModelProperty(value = "是否允许发起人添加抄送人")
    private Integer isAdd;

    @ApiModelProperty(value = "批次ID")
    private String batchId;

    @ApiModelProperty(value = "流程元数据ID")
    private Long flowMetadataId;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

}
