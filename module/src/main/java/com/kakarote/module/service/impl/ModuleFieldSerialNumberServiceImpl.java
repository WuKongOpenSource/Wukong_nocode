package com.kakarote.module.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.util.TypeUtils;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.common.redis.Redis;
import com.kakarote.common.result.SystemCodeEnum;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.module.constant.ModuleCodeEnum;
import com.kakarote.module.entity.PO.ModuleField;
import com.kakarote.module.entity.PO.ModuleFieldSerialNumber;
import com.kakarote.module.entity.PO.ModuleFieldSerialNumberRules;
import com.kakarote.module.mapper.ModuleFieldSerialNumberMapper;
import com.kakarote.module.service.IModuleFieldSerialNumberRulesService;
import com.kakarote.module.service.IModuleFieldSerialNumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 自定义编号字段
 * @author wwl
 * @date 20220304
 */
@Service
public class ModuleFieldSerialNumberServiceImpl extends BaseServiceImpl<ModuleFieldSerialNumberMapper, ModuleFieldSerialNumber> implements IModuleFieldSerialNumberService {
    @Autowired
    private Redis redis;
    @Autowired
    private IModuleFieldSerialNumberRulesService moduleFieldSerialNumberRulesService;

    private Integer getSerialNumber(Integer depth, Long fieldId, Integer startNumber, ModuleFieldSerialNumberRules numberSetting) {
        //最多重试100次
        if (depth < 0) {
            throw new BusinessException(SystemCodeEnum.SYSTEM_ERROR);
        }
        int initNumber = NumberUtil.parseInt(numberSetting.getStartNumber());
        if (ObjectUtil.isNull(startNumber) || initNumber > startNumber) {
            startNumber = initNumber;
        } else {
            startNumber = startNumber + numberSetting.getStepNumber();
        }
        //防止并发生成
        String SERIAL_NUMBER = "SERIAL_NUMBER:";
        String str = ":";
        long five = 5L;
        if (redis.setNx(SERIAL_NUMBER + fieldId + str + startNumber, five, 1)) {
            return startNumber;
        } else {
            return getSerialNumber(--depth, fieldId, startNumber, numberSetting);
        }
    }

    @Override
    public String generateNumber(Map<String, Object> map, List<ModuleField> fields, Long fieldId, Long moduleId, int version) {
        // 查到这个字段的规则
        List<ModuleFieldSerialNumberRules> fieldNumberSettingList = moduleFieldSerialNumberRulesService.querySerialNumberRuleList(moduleId, fieldId, version);
        Integer fieldNumber = null;
        StringBuilder builder = new StringBuilder();
        for (ModuleFieldSerialNumberRules numberSetting : fieldNumberSettingList) {
            Integer type = numberSetting.getType();
            switch (type) {
                case 1:
                    if (ObjectUtil.isNull(numberSetting.getResetType())) {
                        numberSetting.setResetType(4);
                    }
                    if (ObjectUtil.isNull(numberSetting.getStepNumber())) {
                        numberSetting.setStepNumber(1);
                    }
                    if (!NumberUtil.isNumber(numberSetting.getStartNumber())) {
                        numberSetting.setStartNumber("0001");
                    }
                    Date startDate, endDate;
                    Date date = new Date();
                    switch (numberSetting.getResetType()) {
                        case 1:
                            startDate = DateUtil.beginOfDay(date);
                            endDate = DateUtil.endOfDay(date);
                            break;
                        case 2:
                            startDate = DateUtil.beginOfMonth(date);
                            endDate = DateUtil.endOfMonth(date);
                            break;
                        case 3:
                            startDate = DateUtil.beginOfYear(date);
                            endDate = DateUtil.endOfYear(date);
                            break;
                        case 4:
                            startDate = null;
                            endDate = null;
                            break;
                        default:
                            throw new BusinessException(SystemCodeEnum.SYSTEM_NO_VALID);
                    }
                    Integer queryMaxNumber = getBaseMapper().queryMaxNumber(moduleId, version, fieldId, startDate, endDate);
                    fieldNumber = getSerialNumber(100, fieldId, queryMaxNumber, numberSetting);
                    String number = fieldNumber.toString();
                    if (number.length() < numberSetting.getStartNumber().length()) {
                        number = String.format("%0" + numberSetting.getStartNumber().length() + "d", fieldNumber);
                    }
                    builder.append(number);
                    break;
                case 2:
                    builder.append(numberSetting.getStartNumber().replace("-", ""));
                    break;
                case 3:
                    // 查到这个 模块-版本 下的fields
                    ModuleField moduleField = fields
                            .stream()
                            .filter(field -> ObjectUtil.equal(TypeUtils.castToLong(numberSetting.getStartNumber()), field.getFieldId()))
                            .findFirst()
                            .orElse(null);
                    if (ObjectUtil.isNull(moduleField)) {
                        break;
                    }
                    Object object = map.get(moduleField.getFieldName());
                    if (ObjectUtil.isEmpty(object)) {
                        break;
                    }
                    //时间类型字段
                    if (Arrays.asList("yyyyMMdd", "yyyyMM", "yyyy").contains(numberSetting.getTextFormat())) {
                        if (object instanceof Date) {
                            builder.append(DateUtil.format((Date) object, numberSetting.getTextFormat()));
                        } else if (object instanceof LocalDate) {
                            builder.append(LocalDateTimeUtil.format((LocalDate) object, numberSetting.getTextFormat()));
                        } else if (object instanceof LocalDateTime) {
                            builder.append(LocalDateTimeUtil.format((LocalDateTime) object, numberSetting.getTextFormat()));
                        } else {
                            builder.append(DateUtil.parseDate((String) object).toString(numberSetting.getTextFormat()));
                        }
                    } else {
                        builder.append(object.toString().replace("-", ""));
                    }
                    break;
                default:
                    break;
            }
            builder.append("-");
        }
        builder.deleteCharAt(builder.length() - 1);
        // 自定义编号类型字段不能超过200字符
        int index = 200;
        if (builder.length() > index) {
            throw new BusinessException(ModuleCodeEnum.MODULE_SERIAL_NUMBER_FIELD_LENGTH_ERROR);
        }
        if (ObjectUtil.isNull(fieldNumber)) {
            throw new BusinessException(SystemCodeEnum.SYSTEM_ERROR);
        }
        ModuleFieldSerialNumber data = new ModuleFieldSerialNumber();
        data.setModuleId(moduleId);
        data.setVersion(version);
        data.setFieldNumber(fieldNumber);
        data.setFieldId(fieldId);
        data.setValue(builder.toString());
        data.setCreateTime(LocalDateTimeUtil.now());
        save(data);
        return data.getValue();
    }
}
