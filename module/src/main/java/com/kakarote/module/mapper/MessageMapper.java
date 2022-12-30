package com.kakarote.module.mapper;

import com.kakarote.common.result.BasePage;
import com.kakarote.common.servlet.BaseMapper;
import com.kakarote.module.entity.PO.Message;
import org.apache.ibatis.annotations.Param;

/**
 * @author zjj
 * MessageMapper
 *  系统消息
 * @date 2021/11/269:45
 */
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 导入信息list
     * @param parse a
     * @param moduleId a
     * @param type a
     * @param userId a
     * @return a
     */
    BasePage<Message> getImportHistoryList(BasePage<Object> parse, @Param("moduleId") Long moduleId, @Param("type") Integer type, @Param("userId") Long userId);

}
