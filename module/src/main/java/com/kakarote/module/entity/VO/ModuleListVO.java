package com.kakarote.module.entity.VO;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@ApiModel("模块列表VO")
@Accessors(chain = true)
public class ModuleListVO {

	@ApiModelProperty("模块Id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "应用ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long applicationId;

	@ApiModelProperty(value = "主字段ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long mainFieldId;

    @ApiModelProperty(value = "字段排序")
    private Integer sort;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "已激活")
    private Boolean isActive;

    @ApiModelProperty(value = "0 停用 1 正常 2 草稿")
    private Integer status;

    @ApiModelProperty(value = "模块类型 1 无代码模块 2 自定义bi模块")
    private Integer moduleType;

    @ApiModelProperty(value = "0 停用 1 启用")
    private Boolean isEnable;

    @ApiModelProperty(value = "模块图标")
    private String icon;

    @ApiModelProperty(value = "图标文件")
    private String iconFile;

    @ApiModelProperty(value = "图标颜色")
    private String iconColor;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "创建人ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;

    @ApiModelProperty(value = "创建人名称")
    private String createUserName;

    @ApiModelProperty(value = "分组下模块")
    private List<ModuleListVO> childList;

}
