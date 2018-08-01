package com.mmall.controller;

import com.mmall.common.JsonData;
import com.mmall.dao.SysAclModuleMapper;
import com.mmall.param.AclModuleParam;
import com.mmall.param.DeptParam;
import com.mmall.service.SysAclModuleService;
import com.mmall.service.SysTreeService;
import com.mmall.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

@Controller
@RequestMapping("/sys/aclModule")
@Slf4j
public class SysAclModuleController {

    @Resource
    private SysAclModuleService sysAclModuleService;
    @Resource
    private SysTreeService sysTreeService;

    @RequestMapping(value = "/acl.page")
    public ModelAndView page(DeptParam param){
        return new ModelAndView("acl");
    }

    @RequestMapping(value = "/save.json")
    @ResponseBody
    public JsonData saveAclModule(AclModuleParam param){
        sysAclModuleService.save(param);
        return JsonData.success();
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData updateAclModule(AclModuleParam param){
        sysAclModuleService.update(param);
        return JsonData.success();
    }

    @RequestMapping(value = "/tree.json")
    @ResponseBody
    public JsonData tree(){
        return JsonData.success(sysTreeService.aclModuleTree());
    }
}
