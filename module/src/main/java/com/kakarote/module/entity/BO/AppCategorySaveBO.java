package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zjj
 * @description: AppCategorySaveBO
 * @date 2022/6/9
 */
@Data
@ApiModel(value = "应用分类关系保存 BO", description = "应用分类关系保存 BO")
public class AppCategorySaveBO {

    @ApiModelProperty("分类ID")
    @NotNull
    private Long categoryId;

    @ApiModelProperty(value = "应用ID列表")
    private List<String> appIds;

    @ApiModelProperty(value = "应用ID")
    private String applicationId;

}
