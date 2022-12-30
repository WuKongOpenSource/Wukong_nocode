package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.TeamMemberRemoveBO;
import com.kakarote.module.entity.BO.TeamMemberSaveBO;
import com.kakarote.module.entity.PO.ModuleTeamMember;
import com.kakarote.module.entity.VO.TeamMemberVO;

import java.util.List;

public interface IModuleTeamMemberService extends BaseService<ModuleTeamMember> {

    /**
     * 保存团队成员
     *
     * @param memberSaveBO
     */
    void saveTeamMember(TeamMemberSaveBO memberSaveBO);

    /**
     * 移除团队成员
     *
     * @param memberRemoveBO
     */
    void removeTeamMember(TeamMemberRemoveBO memberRemoveBO);

    /**
     * 获取数据的团队成员
     *
     * @param moduleId
     * @param dataId
     * @return
     */
    List<TeamMemberVO> getTeamMember(Long moduleId, Long dataId);

    /**
     *  获取当前用户的团队成员信息
     *
     * @param moduleId
     * @param dataId
     * @return
     */
    TeamMemberVO getTeamMemberInfo(Long moduleId, Long dataId);

    List<ModuleTeamMember> getTeamMemberByDataId(Long moduleId, Long dataId);
}
