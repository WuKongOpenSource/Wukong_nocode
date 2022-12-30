package com.kakarote.module.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.kakarote.common.constant.Const;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.module.constant.ModuleCodeEnum;
import com.kakarote.module.constant.ModuleFieldEnum;
import com.kakarote.module.entity.BO.ListHeadQueryBO;
import com.kakarote.module.entity.BO.ModuleFieldSortBO;
import com.kakarote.module.entity.BO.ModuleFieldStyleSaveBO;
import com.kakarote.module.entity.PO.CustomCategoryField;
import com.kakarote.module.entity.PO.ModuleEntity;
import com.kakarote.module.entity.PO.ModuleField;
import com.kakarote.module.entity.PO.ModuleFieldSort;
import com.kakarote.module.entity.VO.ModuleFieldSortVO;
import com.kakarote.module.mapper.ModuleFieldSortMapper;
import com.kakarote.module.service.ICustomCategoryFieldService;
import com.kakarote.module.service.IModuleFieldService;
import com.kakarote.module.service.IModuleFieldSortService;
import com.kakarote.module.service.IModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 字段排序表 服务实现类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-12-19
 */
@Service
public class ModuleFieldSortServiceImpl extends BaseServiceImpl<ModuleFieldSortMapper, ModuleFieldSort> implements IModuleFieldSortService {

    @Autowired
    private IModuleFieldService moduleFieldService;

    @Autowired
    private IModuleService moduleService;

    @Autowired
    private ICustomCategoryFieldService categoryFieldService;

    /**
     * 查询列表字段
     *
     * @param moduleId 模块ID
     * @return data
     */
    @Override
    public List<ModuleFieldSortVO> queryListHead(Long moduleId, Integer version) {
        Long userId = UserUtil.getUserId();
        if (ObjectUtil.isNull(version)) {
            ModuleEntity module = moduleService.getNormal(moduleId);
            if (ObjectUtil.isNull(module)) {
                throw new BusinessException(ModuleCodeEnum.MODULE_NOT_FOUND);
            }
            version = module.getVersion();
        }
        ModuleEntity module = moduleService.getByModuleIdAndVersion(moduleId, version);
        if (ObjectUtil.isNull(module)) {
            throw new BusinessException(ModuleCodeEnum.MODULE_NOT_FOUND);
        }
        Long number = lambdaQuery().eq(ModuleFieldSort::getUserId, userId)
                .eq(ModuleFieldSort::getModuleId, moduleId)
                .isNull(ModuleFieldSort::getCategoryId)
                .count();
        if (number == 0) {
            List<ModuleField> moduleFields = moduleFieldService.lambdaQuery()
                    .select(ModuleField::getFieldId, ModuleField::getFieldName, ModuleField::getName, ModuleField::getType, ModuleField::getFieldType)
                    .eq(ModuleField::getModuleId, moduleId)
                    .eq(ModuleField::getVersion, version)
                    .eq(ModuleField::getIsHidden, 0)
                    .isNull(ModuleField::getGroupId)
                    .orderByAsc(ModuleField::getSorting)
                    .list();
            int i = 0;
            AtomicInteger atomicInteger = new AtomicInteger(200);
            List<ModuleFieldSort> fieldSortList = new ArrayList<>(moduleFields.size());
            for (ModuleField moduleField : moduleFields) {
                ModuleFieldSort fieldSort = new ModuleFieldSort();
                fieldSort.setModuleId(moduleField.getModuleId());
                fieldSort.setFieldId(moduleField.getFieldId());
                fieldSort.setFieldName(moduleField.getFieldName());
                fieldSort.setName(moduleField.getName());
                fieldSort.setType(moduleField.getType());
                fieldSort.setStyle(null);
                if (ObjectUtil.equal(0, moduleField.getFieldType())) {
                    fieldSort.setSort(atomicInteger.getAndIncrement());
                } else {
                    fieldSort.setSort(i++);
                }
                fieldSort.setIsHide(0);
                fieldSort.setIsLock(false);
                fieldSort.setUserId(userId);
                fieldSort.setModuleId(moduleId);
                fieldSortList.add(fieldSort);
            }
            saveBatch(fieldSortList, Const.BATCH_SAVE_SIZE);
        }
        List<ModuleFieldSortVO> fieldSortVOS = getBaseMapper().queryListHead(moduleId,userId);
        fieldSortVOS.forEach(f -> f.setFormType(ModuleFieldEnum.parse(f.getType()).getFormType()));
        return fieldSortVOS;
    }

    @Override
    public List<ModuleFieldSortVO> queryListHead(Long moduleId, Integer version, Long categoryId) {
        Long userId = UserUtil.getUserId();
        List<CustomCategoryField> categoryFields = categoryFieldService.getByCategoryIdAndVersion(categoryId, version);
        Map<Long, CustomCategoryField> fieldIdCategoryMap = categoryFields.stream().collect(Collectors.toMap(CustomCategoryField::getFieldId, Function.identity()));
        Long number = lambdaQuery()
                .eq(ModuleFieldSort::getUserId, userId)
                .eq(ModuleFieldSort::getModuleId, moduleId)
                .eq(ModuleFieldSort::getCategoryId, categoryId)
                .count();
        if (number == 0) {
            List<ModuleField> moduleFields = moduleFieldService.lambdaQuery()
                    .select(ModuleField::getFieldId, ModuleField::getFieldName, ModuleField::getName, ModuleField::getType, ModuleField::getFieldType)
                    .eq(ModuleField::getModuleId, moduleId)
                    .eq(ModuleField::getVersion, version)
                    .eq(ModuleField::getIsHidden, 0)
                    .isNull(ModuleField::getGroupId)
                    .orderByAsc(ModuleField::getSorting)
                    .list();
            int i = 0;
            AtomicInteger atomicInteger = new AtomicInteger(200);
            List<ModuleFieldSort> fieldSortList = new ArrayList<>(moduleFields.size());
            for (ModuleField moduleField : moduleFields) {
                Long fieldId = moduleField.getFieldId();
                ModuleFieldSort fieldSort = new ModuleFieldSort();
                fieldSort.setModuleId(moduleField.getModuleId());
                fieldSort.setCategoryId(categoryId);
                fieldSort.setFieldId(fieldId);
                fieldSort.setFieldName(moduleField.getFieldName());
                fieldSort.setName(moduleField.getName());
                fieldSort.setType(moduleField.getType());
                fieldSort.setStyle(null);
                if (ObjectUtil.equal(0, moduleField.getFieldType())) {
                    fieldSort.setSort(atomicInteger.getAndIncrement());
                } else {
                    fieldSort.setSort(i++);
                }
                fieldSort.setIsHide(0);
                fieldSort.setIsLock(false);
                fieldSort.setUserId(userId);
                fieldSort.setModuleId(moduleId);
                CustomCategoryField categoryField = fieldIdCategoryMap.get(fieldId);
                if (ObjectUtil.isNotNull(categoryField)) {
                    fieldSort.setName(categoryField.getName());
                    fieldSort.setIsHide(categoryField.getIsHide());
                    fieldSort.setIsNull(categoryField.getIsNull());
                }
                fieldSortList.add(fieldSort);
            }
            saveBatch(fieldSortList, Const.BATCH_SAVE_SIZE);
        }
        List<ModuleFieldSortVO> fieldSortVOS = getBaseMapper().queryListHeadByCategoryId(moduleId, categoryId, userId);
        fieldSortVOS.forEach(f -> f.setFormType(ModuleFieldEnum.parse(f.getType()).getFormType()));
        return fieldSortVOS;
    }

    @Override
    public List<ModuleFieldSortVO> queryListHead(ListHeadQueryBO queryBO) {
        List<ModuleFieldSortVO> fieldSortVOS;
        if (ObjectUtil.isNull(queryBO.getCategoryId())) {
            fieldSortVOS = this.queryListHead(queryBO.getModuleId(), queryBO.getVersion());

        } else {
            fieldSortVOS = this.queryListHead(queryBO.getModuleId(), queryBO.getVersion(), queryBO.getCategoryId());
        }
        return fieldSortVOS.stream().filter(f -> !CollUtil.contains(Arrays.asList("dataId", "moduleId"), f.getFieldName())).collect(Collectors.toList());
    }

    @Override
    public void setFieldStyle(ModuleFieldStyleSaveBO saveBO) {
        if (ObjectUtil.isNull(saveBO.getFieldId()) || ObjectUtil.isNull(saveBO.getModuleId())) {
            return;
        }
        LambdaUpdateWrapper<ModuleFieldSort> wrapper = new LambdaUpdateWrapper<>();
        if (ObjectUtil.isNotNull(saveBO.getStyle())) {
            wrapper.set(ModuleFieldSort::getStyle, saveBO.getStyle());
        }
        if (ObjectUtil.isNotNull(saveBO.getIsLock())) {
            wrapper.set(ModuleFieldSort::getIsLock, saveBO.getIsLock());
        }
        if (ObjectUtil.isNull(saveBO.getCategoryId())) {
            wrapper.isNull(ModuleFieldSort::getCategoryId);
        }else {
            wrapper.eq(ModuleFieldSort::getCategoryId, saveBO.getCategoryId());
        }
        wrapper.eq(ModuleFieldSort::getModuleId, saveBO.getModuleId())
                .eq(ModuleFieldSort::getFieldId, saveBO.getFieldId())
                .eq(ModuleFieldSort::getUserId, UserUtil.getUserId());
        update(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setFieldSort(ModuleFieldSortBO fieldSortBO) {
        if (ObjectUtil.isNotNull(fieldSortBO.getCategoryId())) {
            lambdaUpdate()
                    .eq(ModuleFieldSort::getModuleId, fieldSortBO.getModuleId())
                    .eq(ModuleFieldSort::getUserId, UserUtil.getUserId())
                    .eq(ModuleFieldSort::getCategoryId, fieldSortBO.getCategoryId())
                    .remove();
        } else {
            lambdaUpdate()
                    .eq(ModuleFieldSort::getModuleId, fieldSortBO.getModuleId())
                    .eq(ModuleFieldSort::getUserId, UserUtil.getUserId())
                    .isNull(ModuleFieldSort::getCategoryId)
                    .remove();
        }

        List<ModuleField> moduleFields = moduleFieldService.getByModuleId(fieldSortBO.getModuleId(), null);
        int i = 0;
        List<ModuleFieldSort> fieldSortList = new ArrayList<>(moduleFields.size());
        for (ModuleField moduleField : moduleFields) {
            if (ObjectUtil.isNotNull(moduleField.getGroupId())) {
                continue;
            }
            ModuleFieldSort fieldSort = new ModuleFieldSort();
            fieldSort.setFieldId(moduleField.getFieldId());
            fieldSort.setFieldName(moduleField.getFieldName());
            fieldSort.setName(moduleField.getName());
            fieldSort.setType(moduleField.getType());
            fieldSort.setStyle(null);
            fieldSort.setSort(i++);
            fieldSort.setUserId(UserUtil.getUserId());
            fieldSort.setModuleId(fieldSortBO.getModuleId());
            ModuleFieldSort toHideField = fieldSortBO.getHideFields().stream()
                    .filter(f -> ObjectUtil.equal(moduleField.getFieldName(), f.getFieldName()))
                    .findFirst().orElse(null);
            if (ObjectUtil.isNotNull(toHideField)) {
                fieldSort.setIsHide(1);
            } else {
                int index = this.getFieldSortIndex(fieldSortBO.getNoHideFields(), fieldSort);
                if (index >= 0 ) {
                    fieldSort.setSort(index);
                }
                fieldSort.setIsHide(0);
            }
            fieldSort.setCategoryId(fieldSortBO.getCategoryId());
            fieldSortList.add(fieldSort);
        }
        saveBatch(fieldSortList, Const.BATCH_SAVE_SIZE);
    }

    private int getFieldSortIndex(List<ModuleFieldSort> fieldSorts, ModuleFieldSort fieldSort){
        AtomicInteger index = new AtomicInteger(0);
        ModuleFieldSort sort = fieldSorts.stream().filter(f -> {
            index.getAndIncrement();
            return ObjectUtil.equal(fieldSort.getFieldName(), f.getFieldName());
        }).findFirst().orElse(null);
        if (ObjectUtil.isNull(sort)) {
            return -1;
        }
        return index.get();
    }
}
