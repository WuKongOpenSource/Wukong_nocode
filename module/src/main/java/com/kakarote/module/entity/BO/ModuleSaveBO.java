package com.kakarote.module.entity.BO;

import com.kakarote.common.entity.SimpleUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel("模块保存BO")
public class ModuleSaveBO {

    @ApiModelProperty(value = "模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "应用ID")
    @NotNull
    private Long applicationId;

    @ApiModelProperty(value = "主字段ID")
    private Long mainFieldId;

    @ApiModelProperty(value = "主字段名称")
    private String mainFieldName;

    @ApiModelProperty(value = "模块图标")
    private String icon;

    @ApiModelProperty(value = "图标文件")
    private String iconFile;

    @ApiModelProperty(value = "图标颜色")
    private String iconColor;

    @ApiModelProperty(value = "模块名称")
    @NotNull
    private String name;

    @ApiModelProperty(value = "关联的模块")
    private Long relateModuleId;

    @ApiModelProperty(value = "已激活")
    private Boolean isActive;

    @ApiModelProperty(value = "0 停用 1 正常 2 草稿")
    private Integer status;

    @ApiModelProperty(value = "模块类型 1 无代码模块 2 自定义bi模块 3 自用系统模块")
    private Integer moduleType;

    @ApiModelProperty(value = "0 停用 1 启用")
    private Boolean isEnable;

    @ApiModelProperty(value = "流程管理员")
	private List<Long> manageUserIds;

	@ApiModelProperty(value = "流程管理员")
	private List<SimpleUser> manageUsers;
}
