package com.kakarote.module.service;

import com.kakarote.common.result.BasePage;
import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.ToDoBO;
import com.kakarote.module.entity.BO.ToDoListQueryBO;
import com.kakarote.module.entity.BO.ToDoSaveBO;
import com.kakarote.module.entity.BO.ToDoUpdateBO;
import com.kakarote.module.entity.PO.ToDo;

public interface IToDoService extends BaseService<ToDo> {

    /**
     * 查询待办列表
     *
     * @param queryBO
     * @return
     */
    BasePage<ToDoBO> queryList(ToDoListQueryBO queryBO);

    /**
     * 保存待办信息
     *
     * @param saveBO
     */
    void save(ToDoSaveBO saveBO);

    /**
     * 设置已读
     *
     * @param id
     */
    void setViewed(Long id);

    /**
     * 更新待办信息
     *
     * @param updateBO
     */
    void update(ToDoUpdateBO updateBO);

}
