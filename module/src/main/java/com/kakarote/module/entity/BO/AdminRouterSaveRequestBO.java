package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : zjj
 * @since : 2022/8/11
 * 路由保存请求
 */
@Data
public class AdminRouterSaveRequestBO {

    @ApiModelProperty("应用 ID")
    private Long applicationId;

    @ApiModelProperty("路由信息")
    private List<RouterBO> routerList;

}
