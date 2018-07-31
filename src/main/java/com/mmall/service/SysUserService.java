package com.mmall.service;

import com.google.common.base.Preconditions;
import com.mmall.dao.SysUserMapper;
import com.mmall.exception.ParamException;
import com.mmall.model.SysUser;
import com.mmall.param.UserParam;
import com.mmall.util.BeanValidator;
import com.mmall.util.MD5Util;
import com.mmall.util.PasswordUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class SysUserService {

    @Resource
    private SysUserMapper sysUserMapper;

    public void save(UserParam param){
        BeanValidator.check(param);
        if(checkEmailExist(param.getMail(),param.getId())){
            throw new ParamException("邮箱已被占用");
        }
        if(checkTelephoneExist(param.getTelephone(),param.getId())){
            throw new ParamException("电话已被占用");
        }
        String password= PasswordUtil.randomPassword();
        //TODO
        password="12345678";
        String encryptedPassword= MD5Util.encrypt(password);
        SysUser user=SysUser.builder().username(param.getUsername()).telephone(param.getTelephone())
                .mail(param.getMail()).password(encryptedPassword).deptId(param.getDeptId()).status(param.getStatus())
                .remark(param.getRemark()).build();
        user.setOperator("system");//TODO
        user.setOperateIp("127.0.0.1");//TODO
        user.setOperateTime(new Date());

        //TODO:sendEmail

        sysUserMapper.insertSelective(user);
    }

    public boolean checkTelephoneExist(String telephone, Integer id){
        return sysUserMapper.countByTelephone(telephone,id)>0;
    }

    public boolean checkEmailExist(String mail,Integer id){
        return sysUserMapper.countByMail(mail,id)>0;
    }

    public void update(UserParam param){
        BeanValidator.check(param);
        if(checkEmailExist(param.getMail(),param.getId())){
            throw new ParamException("邮箱已被占用");
        }
        if(checkTelephoneExist(param.getTelephone(),param.getId())){
            throw new ParamException("电话已被占用");
        }
        SysUser before=sysUserMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新的用户不存在");
        SysUser after=SysUser.builder().id(param.getId()).username(param.getUsername()).telephone(param.getTelephone())
                .mail(param.getMail()).deptId(param.getDeptId()).status(param.getStatus())
                .remark(param.getRemark()).build();
        sysUserMapper.updateByPrimaryKeySelective(after);
    }

    public SysUser findByKeyword(String keyword) {
        return sysUserMapper.findByKeyword(keyword);
    }
}
