package com.kakarote.module.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author wwl
 * @date 2022/3/11 9:50
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_module_print_record")
@ApiModel(value = "ModulePrintRecord对象", description = "打印记录表")
public class ModulePrintRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "打印记录id")
    @TableId(value = "id ", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "打印模板Id")
    private Long templateId;

    @ApiModelProperty(value = "模板内容")
    private String recordContent;

    @ApiModelProperty(value = "数据id")
    private Long dataId;

    @ApiModelProperty(value = "模块id")
    private Long moduleId;

    @ApiModelProperty(value = "创建人id")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty("创建人名称")
    @TableField(exist = false)
    private String createUserName;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}

