package com.kakarote.module.entity.PO;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kakarote.common.constant.Const;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 自定义字段表
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-12-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_module_field")
@ApiModel(value = "ModuleField对象", description = "自定义字段表")
public class ModuleField implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "主键ID")
	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;

	@ApiModelProperty(value = "字段ID")
	private Long fieldId;

	@ApiModelProperty(value = "分组ID")
	private Integer groupId;

	@ApiModelProperty(value = "自定义字段英文标识")
	private String fieldName;

	@ApiModelProperty(value = "字段名称")
	private String name;

	@ApiModelProperty(value = "字段类型")
	private Integer type;

	@ApiModelProperty(value = "0 系统 1 自定义")
	private Integer fieldType;

	@ApiModelProperty(value = "模块ID")
	private Long moduleId;

	@ApiModelProperty(value = "版本号")
	private Integer version;

	@ApiModelProperty(value = "字段说明")
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

	@ApiModelProperty(value = "样式百分比")
	private Integer stylePercent;

	@ApiModelProperty(value = "精度，允许的最大小数位/地图精度/明细表格、逻辑表单展示方式")
	@TableField(updateStrategy = FieldStrategy.IGNORED)
	private Integer precisions;

	@ApiModelProperty(value = "表单定位 坐标格式： 1,1")
	private String formPosition;

	@ApiModelProperty(value = "限制的最大数值")
	@TableField(updateStrategy = FieldStrategy.IGNORED)
	private String maxNumRestrict;

	@ApiModelProperty(value = "限制的最小数值")
	@TableField(updateStrategy = FieldStrategy.IGNORED)
	private String minNumRestrict;

	@ApiModelProperty(value = "最后修改时间")
	@TableField(fill = FieldFill.UPDATE)
	private Date updateTime;

	@TableField(exist = false)
	@ApiModelProperty(value = "x轴")
	@JsonIgnore
	private Integer xAxis;

	@TableField(exist = false)
	@ApiModelProperty(value = "y轴")
	@JsonIgnore
	private Integer yAxis;

	@TableField(exist = false)
	@ApiModelProperty(value = "类型")
	private String formType;

	public void setFormPosition(String formPosition) {
		this.formPosition = formPosition;
		if (StrUtil.isNotEmpty(formPosition)) {
			if (formPosition.contains(Const.SEPARATOR)) {
				String[] axisArr = formPosition.split(Const.SEPARATOR);
				int two=2;
				if (axisArr.length == two) {
					String regex="[0-9]+";
					if (axisArr[0].matches(regex) && axisArr[1].matches(regex)) {
						this.xAxis = Integer.valueOf(axisArr[0]);
						this.yAxis = Integer.valueOf(axisArr[1]);
						return;
					}
				}
			}
		}
		this.xAxis = -1;
		this.yAxis = -1;
	}
}
