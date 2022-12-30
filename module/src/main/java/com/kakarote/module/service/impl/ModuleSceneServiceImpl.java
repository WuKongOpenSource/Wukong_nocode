package com.kakarote.module.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.module.common.ModuleCacheUtil;
import com.kakarote.module.constant.ModuleCodeEnum;
import com.kakarote.module.entity.BO.ModuleSceneConfigBO;
import com.kakarote.module.entity.PO.ModuleEntity;
import com.kakarote.module.entity.PO.ModuleScene;
import com.kakarote.module.entity.VO.ModuleSceneVO;
import com.kakarote.module.mapper.ModuleSceneMapper;
import com.kakarote.module.service.IModuleSceneService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 模块场景表 服务实现类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-03-22
 */
@Service
public class ModuleSceneServiceImpl extends BaseServiceImpl<ModuleSceneMapper, ModuleScene> implements IModuleSceneService {

    /**
     * @param moduleId 模块ID
     * @return data
     */
    @Override
    public List<ModuleSceneVO> queryList(Long moduleId) {
        ModuleEntity module = ModuleCacheUtil.getActiveById(moduleId);
        if (ObjectUtil.isNull(module)) {
            throw new BusinessException(ModuleCodeEnum.MODULE_NOT_FOUND);
        }
        Long userId = UserUtil.getUserId();
        Long count = lambdaQuery()
                .ne(ModuleScene::getIsSystem, 0)
                .eq(ModuleScene::getUserId, userId)
                .eq(ModuleScene::getModuleId, moduleId)
                .count();
        if (count == 0) {
            initConfig(moduleId, userId);
        }
        List<ModuleScene> moduleScenes = lambdaQuery()
                .eq(ModuleScene::getModuleId, moduleId)
                .eq(ModuleScene::getUserId, userId)
                .orderByAsc(ModuleScene::getSort)
                .list();
        List<ModuleSceneVO> moduleSceneVOS = JSON.parseArray(JSON.toJSONString(moduleScenes), ModuleSceneVO.class);
        moduleSceneVOS.forEach(s -> s.setName(String.format(s.getName(), module.getName())));
        return moduleSceneVOS;
    }

    /**
     * 新增场景
     *
     * @param sceneVO 场景data
     */
    @Override
    public void saveScene(ModuleSceneVO sceneVO) {
        if (ObjectUtil.isNotNull(sceneVO.getSceneId())) {
            return;
        }
        if (ObjectUtil.isNull(sceneVO.getModuleId())) {
            throw new BusinessException(ModuleCodeEnum.MODULE_ID_IS_NULL_ERROR);
        }

        ModuleScene moduleScene = new ModuleScene();
        if (Objects.equals(1, sceneVO.getIsDefault())) {
            lambdaUpdate().set(ModuleScene::getIsDefault, 0).eq(ModuleScene::getUserId, UserUtil.getUserId()).update();
            moduleScene.setIsDefault(1);
        }
        moduleScene.setName(sceneVO.getName());
        moduleScene.setData(sceneVO.getData());
        moduleScene.setModuleId(sceneVO.getModuleId());
        moduleScene.setCreateTime(new Date());
        moduleScene.setUpdateTime(new Date());
        moduleScene.setUserId(UserUtil.getUserId());
        save(moduleScene);
    }

    /**
     * 修改场景
     *
     * @param sceneVO 场景data
     */
    @Override
    public void updateScene(ModuleSceneVO sceneVO) {
        if (sceneVO.getSceneId() == null) {
            return;
        }
        ModuleScene moduleScene = getById(sceneVO.getSceneId());
        if (moduleScene == null || moduleScene.getIsSystem() != 0) {
            return;
        }
        if (Objects.equals(1, sceneVO.getIsDefault())) {
            lambdaUpdate().set(ModuleScene::getIsDefault, 0).eq(ModuleScene::getUserId, UserUtil.getUserId()).update();
        }
        lambdaUpdate()
                .set(ModuleScene::getName, sceneVO.getName())
                .set(ModuleScene::getData, sceneVO.getData())
                .set(ModuleScene::getIsDefault, sceneVO.getIsDefault())
                .eq(ModuleScene::getSceneId, sceneVO.getSceneId())
                .eq(ModuleScene::getUserId, UserUtil.getUserId())
                .update();
    }

    /**
     * 删除场景
     *
     * @param sceneId 场景Id
     */
    @Override
    public void deleteScene(Long sceneId) {
        ModuleScene moduleScene = getById(sceneId);
        if (ObjectUtil.isNotNull(moduleScene) && ObjectUtil.equal(0, moduleScene.getIsSystem())) {
            removeById(moduleScene.getSceneId());
        }
    }

    private void initConfig(Long moduleId, Long userId) {
        List<ModuleScene> list = new ArrayList<>();
        list.add(new ModuleScene("全部%s", moduleId, userId, 1, 0, 1, 1, new Date(), new Date()));
        list.add(new ModuleScene("我负责的%s", moduleId, userId, 2, 0, 2, 0, new Date(), new Date()));
        list.add(new ModuleScene("下属负责的%s", moduleId, userId, 3, 0, 3, 0, new Date(), new Date()));
        saveBatch(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefault(Long sceneId) {
        ModuleScene moduleScene = getById(sceneId);
        if (ObjectUtil.isNotNull(moduleScene)) {
            lambdaUpdate().set(ModuleScene::getIsDefault, 0).eq(ModuleScene::getUserId, UserUtil.getUserId()).update();
            moduleScene.setIsDefault(1);
            updateById(moduleScene);
        }
    }

    @Override
    public void sceneConfig(ModuleSceneConfigBO sceneConfigBO) {
        if(CollUtil.isEmpty(sceneConfigBO.getNoHideIds())) {
            throw new BusinessException(ModuleCodeEnum.MODULE_SCENE_CAN_NOT_HIDE_ALL);
        }
        List<ModuleScene> sceneList = lambdaQuery()
                .eq(ModuleScene::getUserId, UserUtil.getUserId())
                .eq(ModuleScene::getModuleId, sceneConfigBO.getModuleId())
                .list();
        Map<Long, ModuleScene> sceneIdEntityMap = sceneList.stream().collect(Collectors.toMap(ModuleScene::getSceneId, Function.identity()));
        List<ModuleScene> scenes = new ArrayList<>();
        AtomicInteger sort = new AtomicInteger(0);
        for (Long sceneId : sceneConfigBO.getNoHideIds()) {
            ModuleScene scene = sceneIdEntityMap.get(sceneId);
            scene.setSort(sort.getAndIncrement());
            scene.setIsHide(0);
            scenes.add(scene);
        }
        for (Long sceneId : sceneConfigBO.getHideIds()) {
            ModuleScene scene = sceneIdEntityMap.get(sceneId);
            scene.setSort(sort.getAndIncrement());
            scene.setIsHide(1);
            scenes.add(scene);
        }
        saveOrUpdateBatch(scenes);
    }

    @Override
    public ModuleSceneVO getBySceneId(Long sceneId) {
        ModuleScene scene = getById(sceneId);
        return JSON.parseObject(JSON.toJSONString(scene), ModuleSceneVO.class);
    }
}
