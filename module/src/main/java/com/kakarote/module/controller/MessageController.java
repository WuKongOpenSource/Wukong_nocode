package com.kakarote.module.controller;

import com.kakarote.common.constant.Const;
import com.kakarote.common.redis.Redis;
import com.kakarote.common.result.BasePage;
import com.kakarote.common.result.Result;
import com.kakarote.module.constant.ModuleConst;
import com.kakarote.module.entity.BO.MessageDeleteBO;
import com.kakarote.module.entity.BO.MessageSetReadBO;
import com.kakarote.module.entity.PO.Message;
import com.kakarote.module.entity.VO.ModuleMessageVO;
import com.kakarote.module.service.IMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author zjj
 * @title: MessageController
 * @description: TODO
 * @date 2021/11/2614:05
 */
@RestController
@RequestMapping("/moduleMessage")
@Api(tags = "系统消息")
public class MessageController {
    @Autowired
    private IMessageService messageService;

    @Autowired
    private Redis redis;


    @PostMapping("/list")
    @ApiOperation("获取消息列表")
    public Result<List<ModuleMessageVO>> queryMessageList() {
        List<ModuleMessageVO> result = messageService.queryMessageList();
        return Result.ok(result);
    }

    @ApiOperation("设置已读")
    @PostMapping("/setRead")
    public Result setMessageRead(@RequestBody MessageSetReadBO setReadBO) {
        messageService.setMessageRead(setReadBO);
        return Result.ok();
    }

    @ApiOperation("删除消息")
    @PostMapping("/delete")
    public Result deleteMessage(@RequestBody MessageDeleteBO deleteBO) {
        messageService.deleteMessage(deleteBO);
        return Result.ok();
    }

    @PostMapping("/queryList/{moduleId}/{type}")
    @ApiOperation("获取某个类型的消息列表")
    public Result<BasePage<Message>> queryMsgList(@PathVariable("moduleId") Long moduleId, @PathVariable("type") Integer type) {
        BasePage<Message> result = messageService.queryMsgList(moduleId, type);
        return Result.ok(result);
    }

    @PostMapping("/queryImportNum")
    @ApiOperation("查询导入数量")
    public Result<Integer> queryImportNum(Long messageId, HttpServletResponse response) {
        if (messageId == null) {
            return Result.ok(null);
        }
        boolean exists = redis.exists(ModuleConst.UPLOAD_EXCEL_MESSAGE_PREFIX + messageId);
        Integer num = null;
        if (exists) {
            num = redis.get(ModuleConst.UPLOAD_EXCEL_MESSAGE_PREFIX + messageId);
        }
        return Result.ok(num);
    }
    @PostMapping("/queryImportInfo")
    @ApiOperation("查询导入信息")
    public Result<Message> queryImportInfo(@RequestParam("messageId") Long messageId) {
        Message msg = messageService.getById(messageId);
        if (msg != null && msg.getValue() != null) {
            return Result.ok(msg);
        } else {
            Message message = new Message();
            message.setValue("导入总数据" + 0 + "条"
                    + Const.SEPARATOR + " 导入成功"+ 0 +"条"
                    + Const.SEPARATOR + " 导入失败"+ 0 +"条"
                    + Const.SEPARATOR + " 覆盖数量"+ 0 +"条");
            return Result.ok(msg);
        }
    }

}
