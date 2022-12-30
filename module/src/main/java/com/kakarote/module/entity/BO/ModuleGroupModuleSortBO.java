package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wwl
 * @date 2022/3/24 19:52
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("模块分组排序 的参数")
public class ModuleGroupModuleSortBO {

    @ApiModelProperty(value = "应用id")
    private Long applicationId;

    @ApiModelProperty(value = "模块id")
    private Long moduleId;

    @ApiModelProperty(value = "分组id (移动模块时，这个参数表示要挪到的目标分组，为空表示挪出分组)")
    private Long groupId;
    @ApiModelProperty(value = "分组下的模块")
    List<String> moduleList;


}
