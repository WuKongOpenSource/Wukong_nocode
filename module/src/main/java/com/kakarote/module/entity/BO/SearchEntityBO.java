package com.kakarote.module.entity.BO;

import cn.hutool.core.util.ObjectUtil;
import com.kakarote.module.constant.FieldSearchEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zjj
 */
@Data
@ApiModel(value = "高级筛选子查询")
@Accessors(chain = true)
public class SearchEntityBO {

    @ApiModelProperty(value = "明細表格字段")
    private ModuleSimpleFieldBO detailTableField;

    @ApiModelProperty(value = "字段ID")
    private Long fieldId;

    @ApiModelProperty(value = "字段名称")
    private String fieldName;

	@ApiModelProperty(value = "格式")
	private String formType;

    /**
     * 给search枚举赋值时，记得一并赋值type字段，否则get本字段时会为null(因为重写了getter方法)
     * 比如
     * SearchEntityBO entity = new SearchEntityBO();
     *         entity.setSearchEnum(FieldSearchEnum.ID);
     *         entity.setType(11);
     */
    @ApiModelProperty(value = "高级筛选列表")
    private FieldSearchEnum searchEnum;

    @ApiModelProperty(value = "高级筛选列表")
    private Integer type;

    @ApiModelProperty(value = "表达式")
    private String expression;

    @ApiModelProperty(value = "值列表")
    private List<String> values = new ArrayList<>();

    @ApiModelProperty(value = "筛选条件类型为1(匹配字段)时，传入当前模块字段id")
	private Long currentFieldId;

    @NotNull
    @ApiModelProperty(value = "当前模块临时字段ID")
    private String tempCurrentFieldId;

	public SearchEntityBO() {
	}

    public FieldSearchEnum getSearchEnum() {
        if (ObjectUtil.isNotNull(searchEnum) && ObjectUtil.notEqual(FieldSearchEnum.NULL, searchEnum)) {
            return searchEnum;
        }
        return FieldSearchEnum.parse(this.type);
    }
}
