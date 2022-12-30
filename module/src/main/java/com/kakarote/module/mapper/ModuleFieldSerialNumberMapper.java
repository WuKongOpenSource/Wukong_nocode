package com.kakarote.module.mapper;

import com.kakarote.common.servlet.BaseMapper;
import com.kakarote.module.entity.PO.ModuleFieldSerialNumber;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * 自定义编号字段
 *
 * @author wwl
 */
public interface ModuleFieldSerialNumberMapper extends BaseMapper<ModuleFieldSerialNumber> {


    Integer queryMaxNumber(@Param("moduleId") Long moduleId
            , @Param("version") int version
            , @Param("fieldId") Long fieldId
            , @Param("startDate")  Date startDate
            , @Param("endDate")  Date endDate);
}
