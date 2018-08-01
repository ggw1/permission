package com.mmall.service;

import com.google.common.base.Preconditions;
import com.mmall.common.RequestHolder;
import com.mmall.dao.SysAclModuleMapper;
import com.mmall.exception.ParamException;
import com.mmall.model.SysAclModule;
import com.mmall.model.SysDept;
import com.mmall.param.AclModuleParam;
import com.mmall.util.BeanValidator;
import com.mmall.util.IpUtil;
import com.mmall.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SysAclModuleService {

    @Resource
    private SysAclModuleMapper sysAclModuleMapper;

    public void save(AclModuleParam param){
        BeanValidator.check(param);
        if(checkExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("同一层级下存在相同名称的权限模块");
        }
        SysAclModule aclModule = SysAclModule.builder().name(param.getName()).parentId(param.getParentId())
                .seq(param.getSeq()).status(param.getStatus()).remark(param.getRemark()).build();
        aclModule.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()), param.getParentId()));
        aclModule.setOperator(RequestHolder.getCurrentUser().getUsername());
        aclModule.setOperateTime(new Date());
        aclModule.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysAclModuleMapper.insertSelective(aclModule);
    }

    public void update(AclModuleParam param){
        BeanValidator.check(param);
        if(checkExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("同一层级下存在相同名称的权限模块");
        }
        SysAclModule before=sysAclModuleMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新的权限模块不存在");

        SysAclModule after= SysAclModule.builder().id(param.getId()).name(param.getName()).parentId(param.getParentId())
                .seq(param.getSeq()).status(param.getStatus()).remark(param.getRemark()).build();
        after.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()), param.getParentId()));
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateTime(new Date());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));

        updateWithChild(before,after);

    }

    @Transactional
    public void updateWithChild(SysAclModule before, SysAclModule after){
        String newLevelPrefix=after.getLevel();
        String oldLevelPrefix=before.getLevel();
        if(!after.getLevel().equals(before.getLevel())){
            List<SysAclModule> aclModulesist=sysAclModuleMapper.getChildAclModuleListByLevel(before.getLevel());
            if(CollectionUtils.isNotEmpty(aclModulesist)){
                for(SysAclModule aclModule:aclModulesist){
                    String level=aclModule.getLevel();
                    if(level.indexOf(oldLevelPrefix)==0){
                        level=newLevelPrefix + level.substring(oldLevelPrefix.length());
                        aclModule.setLevel(level);
                    }
                }
                sysAclModuleMapper.batchUpdateLevel(aclModulesist);
            }
        }
        sysAclModuleMapper.updateByPrimaryKeySelective(after);
    }

    private boolean checkExist(Integer parentId,String aclModuleName,Integer deptId){
        return sysAclModuleMapper.countByNameAndParentId(parentId,aclModuleName,deptId)>0;
    }

    private String getLevel(Integer aclModuleId){
        SysAclModule aclModule=sysAclModuleMapper.selectByPrimaryKey(aclModuleId);
        if(aclModule==null){
            return null;
        }
        return aclModule.getLevel();
    }
}
