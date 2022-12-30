package com.kakarote.module.entity.BO;

import com.kakarote.module.entity.PO.ModuleFieldSerialNumberRules;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel("模块字段BO")
public class ModuleFieldBO {

	@NotNull
	@ApiModelProperty(value = "本地临时字段ID")
	private String tempFieldId;

    @ApiModelProperty(value = "主键ID")
    private Long fieldId;

    @ApiModelProperty(value = "分组ID")
    private Integer groupId;

	@ApiModelProperty(value = "自定义字段英文标识")
	private String fieldName;

    @ApiModelProperty(value = "字段名称")
    private String name;

    /**
     * 20220316
     * TODO wwl,导入尝试使用，无用就删
     */
    @ApiModelProperty(value = "importValue")
    private Object importValue;

    @ApiModelProperty(value = "字段类型")
    private Integer type;

    @ApiModelProperty(value = "0 系统 1 自定义")
    private Integer fieldType;

	@ApiModelProperty(value = "类型")
	private String formType;

    @ApiModelProperty(value = "字段提示")
    private String remark;

    @ApiModelProperty(value = "输入提示")
    private String inputTips;

    @ApiModelProperty(value = "最大长度")
    private Integer maxLength;

    @ApiModelProperty(value = "是否唯一 1 是 0 否")
    private Integer isUnique;

    @ApiModelProperty(value = "是否必填 1 是 0 否")
    private Integer isNull;

    @ApiModelProperty(value = "排序 从小到大")
    private Integer sorting;

    @ApiModelProperty(value = "操作权限")
    private Integer operating;

    @ApiModelProperty(value = "是否隐藏  0不隐藏 1隐藏")
    private Integer isHidden;

    @ApiModelProperty(value = "样式百分比%")
    private Integer stylePercent;

    @ApiModelProperty(value = "精度，允许的最大小数位")
    private Integer precisions;

    @ApiModelProperty(value = "表单定位 坐标格式： 1,1")
    private String formPosition;

    @ApiModelProperty(value = "限制的最大数值")
    private String maxNumRestrict;

    @ApiModelProperty(value = "限制的最小数值")
    private String minNumRestrict;

    @ApiModelProperty(value = "选项列表")
    private List<ModuleOptionsBO> optionsList;

    @ApiModelProperty(value = "标签列表")
    private List<ModuleTagsBO> tagList;

    @ApiModelProperty(value = "自定义编码规则list")
    private List<ModuleFieldSerialNumberRules> serialNumberRules;

    @ApiModelProperty(value = "公式字段")
    private ModuleFieldFormulaBO formulaBO;

    @ApiModelProperty(value = "树字段配置")
    private List<ModuleTreeBO> treeBO;
}
