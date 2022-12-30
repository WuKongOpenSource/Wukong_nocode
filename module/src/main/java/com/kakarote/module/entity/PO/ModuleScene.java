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
 * 模块场景表
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_module_scene")
@ApiModel(value="ModuleScene对象", description="模块场景表")
public class ModuleScene implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty("场景ID")
    @TableId(type = IdType.ASSIGN_ID)
    private Long sceneId;

    @ApiModelProperty(value = "场景名称")
    private String name;

    @ApiModelProperty(value = "模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "场景数据")
    private String data;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "排序ID")
    private Integer sort;

    @ApiModelProperty(value = "1隐藏")
    private Integer isHide;

    @ApiModelProperty(value = "1全部 2 我负责的 3 我下属负责的 0 自定义")
    private Integer isSystem;

    @ApiModelProperty(value = "是否默认 0 否 1是")
    private Integer isDefault;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    public ModuleScene() {
    }

    public ModuleScene(String name, Long moduleId, Long userId, Integer sort, Integer isHide, Integer isSystem, Integer isDefault, Date createTime, Date updateTime) {
        this.name = name;
        this.moduleId = moduleId;
        this.userId = userId;
        this.sort = sort;
        this.isHide = isHide;
        this.isSystem = isSystem;
        this.isDefault = isDefault;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }
}
