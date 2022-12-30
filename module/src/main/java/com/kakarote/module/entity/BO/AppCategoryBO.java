package com.kakarote.module.entity.BO;

import com.kakarote.module.entity.PO.Category;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zjj
 * @description: AppCategoryBO
 * @date 2022/6/9
 */
@Data
@ApiModel(value = "应用分类关系 BO", description = "应用分类关系 BO")
public class AppCategoryBO extends Category {

    @ApiModelProperty(value = "应用ID")
    private List<String> applicationIds;
}
