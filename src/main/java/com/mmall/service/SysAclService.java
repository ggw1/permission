package com.mmall.service;

import com.google.common.base.Preconditions;
import com.mmall.beans.PageQuery;
import com.mmall.beans.PageResult;
import com.mmall.common.RequestHolder;
import com.mmall.dao.SysAclMapper;
import com.mmall.exception.ParamException;
import com.mmall.model.SysAcl;
import com.mmall.param.AclParam;
import com.mmall.util.BeanValidator;
import com.mmall.util.IpUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class SysAclService {

    @Resource
    private SysAclMapper sysAclMapper;

    public void save(AclParam aclParam){
        BeanValidator.check(aclParam);
        if(checkExist(aclParam.getAclModuleId(),aclParam.getName(),aclParam.getId())){
            throw new ParamException("当前权限模块下存在相同名称的权限点");
        }
        SysAcl acl=SysAcl.builder().name(aclParam.getName()).aclModuleId(aclParam.getAclModuleId()).url(aclParam.getUrl())
                .status(aclParam.getStatus()).type(aclParam.getType()).seq(aclParam.getSeq()).remark(aclParam.getRemark()).build();
        acl.setCode(generateCode());
        acl.setOperator(RequestHolder.getCurrentUser().getUsername());
        acl.setOperateTime(new Date());
        acl.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));

        sysAclMapper.insertSelective(acl);
    }

    public void update(AclParam aclParam){
        BeanValidator.check(aclParam);
        if(checkExist(aclParam.getAclModuleId(),aclParam.getName(),aclParam.getId())){
            throw new ParamException("当前权限模块下存在相同名称的权限点");
        }
        SysAcl before=sysAclMapper.selectByPrimaryKey(aclParam.getId());
        Preconditions.checkNotNull(before,"待更新的权限点不存在");

        SysAcl after=SysAcl.builder().id(aclParam.getId()).name(aclParam.getName()).aclModuleId(aclParam.getAclModuleId()).url(aclParam.getUrl())
                .status(aclParam.getStatus()).type(aclParam.getType()).seq(aclParam.getSeq()).remark(aclParam.getRemark()).build();
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateTime(new Date());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));

        sysAclMapper.updateByPrimaryKeySelective(after);
    }

    public boolean checkExist(int aclModuleId,String name,Integer id){
        return sysAclMapper.countByNameAndAclModuleId(aclModuleId,name,id)>0;
    }

    public String generateCode(){
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(new Date())+""+(int)(Math.random()*100);
    }

    public PageResult<SysAcl> getpageByAclModuleId(int aclModuleId,PageQuery page){
        BeanValidator.check(page);
        int count=sysAclMapper.countByAclModuleId(aclModuleId);
        if(count>0){
            List<SysAcl> aclList=sysAclMapper.getPageByAclModuleId(aclModuleId,page);
            return PageResult.<SysAcl>builder().data(aclList).total(count).build();
        }
        return PageResult.<SysAcl>builder().build();
    }
}
