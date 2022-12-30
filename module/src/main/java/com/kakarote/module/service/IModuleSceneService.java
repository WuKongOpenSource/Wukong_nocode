package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.ModuleSceneConfigBO;
import com.kakarote.module.entity.PO.ModuleScene;
import com.kakarote.module.entity.VO.ModuleSceneVO;

import java.util.List;

/**
 * <p>
 * 模块场景表 服务类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-03-22
 */
public interface IModuleSceneService extends BaseService<ModuleScene> {

    /**
     *
     * @param moduleId 模块ID
     * @return data
     */
    public List<ModuleSceneVO> queryList(Long moduleId);


    /**
     * 新增场景
     * @param sceneVO 场景data
     */
    public void saveScene(ModuleSceneVO sceneVO);

    /**
     * 修改场景
     * @param sceneVO 场景data
     */
    public void updateScene(ModuleSceneVO sceneVO);


    /**
     * 删除场景
     * @param sceneId 场景Id
     */
    public void deleteScene(Long sceneId);

    void setDefault(Long sceneId);

    /**
     *  场景设置
     *
     * @param sceneConfigBO
     */
    void sceneConfig(ModuleSceneConfigBO sceneConfigBO);

    ModuleSceneVO getBySceneId(Long sceneId);
}
