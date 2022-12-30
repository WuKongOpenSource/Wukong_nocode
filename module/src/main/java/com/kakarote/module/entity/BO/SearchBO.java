package com.kakarote.module.entity.BO;

import com.kakarote.common.result.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangzhiwei
 * 通用搜索对象
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "高级筛选BO", description = "高级筛选表")
public class SearchBO extends PageEntity {

    @ApiModelProperty(value = "搜索条件")
    private String search;

    @ApiModelProperty(value = "场景ID")
    private Long sceneId;

    @ApiModelProperty(value = "moduleId")
    private Long moduleId;

    @ApiModelProperty(value = "排序字段")
    private String sortField;

    @ApiModelProperty(value = "排序字段 1 倒序 2 正序")
    private Integer order;

    @ApiModelProperty(value = "高级筛选列表")
    private List<SearchEntityBO> searchList = new ArrayList<>();

    @ApiModelProperty(value = "搜索的字段")
	private List<String> fetchFieldNameList = new ArrayList<>();

    @ApiModelProperty(value = "是否根据auth进行筛选")
    private Boolean authFilter = false;

    /**
     * es的一页一页的向后翻
     */
    @ApiModelProperty(value = "searchAfter搜索所需key")
    @Setter(AccessLevel.NONE)
    private Object[] searchAfterKey;
    /**
     * 设置afterKey
     * @param objects data
     */
    public void searchAfter(Object[] objects) {
        searchAfterKey = objects;
    }
}
