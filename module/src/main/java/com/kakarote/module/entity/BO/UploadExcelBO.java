package com.kakarote.module.entity.BO;

import com.kakarote.common.entity.UserInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * @author wwl
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("上传文件业务对象")
public class UploadExcelBO extends FieldQueryBO {

    @ApiModelProperty("文件路径")
    private String filePath;

    @ApiModelProperty("关键字-跳过2 覆盖1")
    private Integer repeatHandling;

    @ApiModelProperty("messageId")
    private Long messageId;

    @ApiModelProperty("上传人ID")
    private UserInfo userInfo;

    /**
     * 导入时后台需要的参数
     */
    private Map<String, Object> param;
}
