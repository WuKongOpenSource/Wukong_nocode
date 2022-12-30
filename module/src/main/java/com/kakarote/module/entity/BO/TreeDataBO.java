package com.kakarote.module.entity.BO;

import com.kakarote.module.entity.PO.ModuleFieldData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : zjj
 * @since : 2022/12/3
 */
@Data
@ApiModel(value = "树数据展示 BO", description = "树数据展示 BO")
public class TreeDataBO {

    @ApiModelProperty(value = "数据 ID")
    private Long dataId;

    @ApiModelProperty(value = "父级树")
    private List<TreeDataBO> parent = new ArrayList<>();

    @ApiModelProperty(value = "子集")
    private List<TreeDataBO> children = new ArrayList<>();

    @ApiModelProperty(value = "字段值列表")
    private List<ModuleFieldData> fieldDataList;

    @ApiModelProperty(value = "父级 ID")
    private List<Long> parentIds = new ArrayList<>();

    @ApiModelProperty(value = "子集 ID")
    private List<Long> childIds = new ArrayList<>();

    public void addChild(TreeDataBO child) {
        this.getChildren().add(child);
    }
}
